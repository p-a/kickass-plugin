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
import org.eclipse.core.runtime.Status;
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
				new WorkspaceJob("Scanning file " + file.getName()) {

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
					reader = new BufferedReader(new InputStreamReader(file.getContents(true)),32768);
					String line = null;
					while ( (line = reader.readLine()) != null){

						String lowerLine = line.toLowerCase();
						
						if (line.indexOf(":") > -1 || lowerLine.indexOf(".label") > -1){
							Matcher matcher = Constants.LABEL_PATTERN.matcher(line);
							if (matcher.matches()){
								labels.add(matcher.group(1).replaceAll("\\s*=\\s*\\S+", ""));
							} else {
								matcher =  Constants.LABEL_PATTERN_ALT.matcher(line);
								if (matcher.find()){
									labels.add(matcher.group(1));
								}
							}
						}
						
						if (lowerLine.indexOf(".macro") > -1) {
							Pattern pattern = Constants.MACRO_PATTERN;
							Matcher matcher = pattern.matcher(line);

							if (matcher.find()) {
								macros.add(":"+matcher.group(1));
							}
						}
						
						if (lowerLine.indexOf(".pseudocommand") > -1) {
							Pattern pattern = Pattern.compile("^\\s*\\.pseudocommand\\s+(.*)$", Pattern.CASE_INSENSITIVE);
							Matcher matcher = pattern.matcher(line);

							if (matcher.matches()) {
								macros.add(":"+matcher.group(1).replaceAll("\\{.*$", "").trim());
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
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null){
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				}
				Collections.sort(labels);
				file.setSessionProperty(Constants.LABELS_SESSION_KEY, labels);

				Collections.sort(macros);
				file.setSessionProperty(Constants.MACROS_SESSION_KEY, macros);
				
				Collections.sort(constig);
				file.setSessionProperty(Constants.CONST_SESSION_KEY, constig);
				
				Collections.sort(functions);
				file.setSessionProperty(Constants.FUNCTIONS_SESSION_KEY, functions);
				
				file.setSessionProperty(Constants.IMPORTS_SESSION_KEY, imports);
				
				return Status.OK_STATUS;
			}
		};

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

		if (resource.getProject() == null){
			return true;
		}
		
		if (!resource.getProject().isAccessible() || !resource.getProject().hasNature(Constants.NATURE_ID)){
			return false;
		}

		String project = resource.getProject().getName();

		if (resource.getType() != IResource.FILE)
			return true;

		IFile file = (IFile) resource;
		
		String ext = file.getFileExtension();
		if (ext != null && Constants.EXTENSION_PATTERN_ALL.matcher(ext).matches())
			scanfile(file, project);

		return true;
	}

}