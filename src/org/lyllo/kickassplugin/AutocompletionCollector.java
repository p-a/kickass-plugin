package org.lyllo.kickassplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.lyllo.kickassplugin.prefs.ProjectPrefenceHelper;

public class AutocompletionCollector implements IResourceChangeListener, IResourceDeltaVisitor, IResourceVisitor{

	public void resourceChanged(IResourceChangeEvent event) {

		switch (event.getType()){
		case IResourceChangeEvent.PRE_DELETE:
			break;
		case IResourceChangeEvent.POST_CHANGE:
		case IResourceChangeEvent.POST_BUILD:
		case IResourceChangeEvent.PRE_REFRESH:
			try {
				event.getDelta().accept(this);
			} catch (CoreException ex){
				//error
				ex.printStackTrace();
			}
			break;
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {

		if (delta.getResource() == null || delta.getResource().getProject() == null){
			return true;
		}

		IResource resource = delta.getResource();

		if (delta.getKind() == IResourceDelta.REMOVED){
			return true;
		}  

		return visit(resource);

	}


	private void scanfile(final IFile file, final String project) throws CoreException {

		WorkspaceJob scanFileJob = 
				new WorkspaceJob("Autocompletion collector job: " + file.getName()) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {


				if (monitor == null){
					monitor = new NullProgressMonitor();
				}

				BufferedReader reader = null;
				List<String> labels = new ArrayList<String>();
				List<String> constig = new ArrayList<String>();
				List<String> macros = new ArrayList<String>();
				List<String> functions = new ArrayList<String>();

				Set<String> imports = new HashSet<String>();

				try {
					reader = new BufferedReader(new InputStreamReader(file.getContents(true)),8192);
					String line = null;
					monitor.beginTask("Scanning file: " + file.getName(), 1);
					while ((line = reader.readLine()) != null){

						if(monitor.isCanceled())
							throw new OperationCanceledException();

						String lowerLine = line.toLowerCase();
						if (line.indexOf(":") > -1 || lowerLine.indexOf(".label") > -1){
							Matcher matcher = Constants.LABEL_PATTERN.matcher(line);
							if (matcher.matches()){
								String group = matcher.group(1);
								group = Constants.SPACES_EQUALS_SIGN_SPACES.matcher(group).replaceAll("");
								labels.add(group);
							} else {
								matcher =  Constants.LABEL_PATTERN_ALT.matcher(line);
								if (matcher.find()){
									labels.add(matcher.group(1));
								}
							}
						}

						if (lowerLine.indexOf(".macro") > -1) {
							Matcher matcher = Constants.MACRO_PATTERN.matcher(line);

							if (matcher.find()) {
								macros.add(":"+matcher.group(1));
							}
						}

						//FIXME, there is already a Pattern for this in Constants.
						if (lowerLine.indexOf(".pseudocommand") > -1) {
							Matcher matcher = Constants.PSEUDOCOMMAND_PATTERN_LINE.matcher(line);

							if (matcher.matches()) {
								String group = matcher.group(1);
								group = Constants.BEGIN_BLOCK_PATTERN.matcher(group).replaceAll("").trim();
								macros.add(":"+group);
							}
						}

						if (lowerLine.indexOf(".var") > -1 || lowerLine.indexOf(".const") > -1) {
							Pattern pattern = Constants.CONSTVAR_PATTERN;
							Matcher matcher = pattern.matcher(line);

							if (matcher.matches()) {
								constig.add(matcher.group(2));
							}
						}

						if (lowerLine.indexOf(".function") > -1) {
							Pattern pattern = Constants.FUNCTION_PATTERN;
							Matcher matcher = pattern.matcher(line);

							if (matcher.find()) {
								functions.add(matcher.group(1));
							}
						}


						if (lowerLine.indexOf(".import") > -1) {
							Pattern pattern = Constants.IMPORT_SOURCE_PATTERN;
							Matcher matcher = pattern.matcher(line);

							if (matcher.matches()) {
								List<String> split = new ArrayList<String>();
								split.addAll(Arrays.asList(ProjectPrefenceHelper.getSourceDirs(file.getProject())));
								if (split.isEmpty()){
									split.add(Constants.DEFAULT_SRC_DIRECTORY);
								}

								split.addAll(Arrays.asList(ProjectPrefenceHelper.getLibDirs(file.getProject())));
								split.add(file.getParent().getProjectRelativePath().toString());
								for (int i = 0; i < split.size(); i++ ){
									String folder = split.get(i);
									if (!"".equals(folder)){
										IPath importPath = file.getProject().getFolder(folder).getProjectRelativePath().append(matcher.group(1));
										IFile importedFile = file.getProject().getFile(importPath);
										if (importedFile.exists()){
											imports.add(importPath.toString());
										} 
									}

								}
							}
						}

					}

					Collections.sort(labels);
					Collections.sort(macros);
					Collections.sort(constig);
					Collections.sort(functions);

					synchronized (file) {
						file.setSessionProperty(Constants.LABELS_SESSION_KEY, labels);
						file.setSessionProperty(Constants.MACROS_SESSION_KEY, macros);
						file.setSessionProperty(Constants.CONST_SESSION_KEY, constig);
						file.setSessionProperty(Constants.FUNCTIONS_SESSION_KEY, functions);
						file.setSessionProperty(Constants.IMPORTS_SESSION_KEY, imports);
					}

					monitor.worked(1);

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null){
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
					monitor.done();
				}

				return Status.OK_STATUS;
			}
		};

		//scanFileJob.setRule(file.getProject());
		scanFileJob.setPriority(Job.SHORT);

		scanFileJob.schedule();
	}

	public void init() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.getRoot().accept(this);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public boolean visit(IResource resource) throws CoreException {

		if (resource.getType() == IResource.ROOT){
			return true;
		}
		
		if (resource.getProject() == null 
				|| resource.isVirtual()
				|| !resource.getProject().isAccessible() 
				|| !resource.getProject().hasNature(Constants.NATURE_ID)){

			return false;
		}

		if (resource.getType() == IResource.PROJECT){
			IProject project = (IProject) resource;
			
			return project.isOpen();
		}

		if (resource.getType() == IResource.FOLDER){
			// on project's build path?
			return true;
		}

		if (resource.getType() == IResource.FILE){
			IFile file = (IFile) resource;

			String project = resource.getProject().getName();
			String ext = file.getFileExtension();
			if (ext != null && Constants.EXTENSION_PATTERN_ALL.matcher(ext).matches())
				scanfile(file, project);
		}

		return resource.getType() == IResource.ROOT;
	}

}