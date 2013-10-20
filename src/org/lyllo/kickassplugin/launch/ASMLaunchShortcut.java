/*
 Kick Assembler plugin - An Eclipse plugin for convenient Kick Assembling
 Copyright (c) 2012 - P-a Backstrom <pa.backstrom@gmail.com>

 Based on ASMPlugin - http://sourceforge.net/projects/asmplugin/
 Copyright (c) 2006 - Andy Reek, D. Mitte

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */ 
package org.lyllo.kickassplugin.launch;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.Messages;
import org.lyllo.kickassplugin.prefs.ProjectPrefenceHelper;


/**
 * LaunchShortcut for exexutable files.
 * 
 * @author Andy Reek
 * @since 13.02.2006
 */
public class ASMLaunchShortcut implements ILaunchShortcut {

	/**
	 * {@inheritDoc}
	 */
	public void launch(ISelection selection, String mode) {
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}

		Object firstSelection = ((IStructuredSelection) selection).getFirstElement();

		if (!(firstSelection instanceof IFile)) {
			Activator.getDefault().getLog().log(
					new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
							Messages.LAUNCH_ERROR, new FileNotFoundException("Selection is not an IFile " + firstSelection)));

			return;
		}
		IFile file = (IFile) firstSelection;
		if (file.getFileExtension() == null || !Constants.EXTENSION_PATTERN_ALL.matcher(file.getFileExtension()).matches()){
			Activator.getDefault().getLog().log(
					new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
							Messages.LAUNCH_ERROR, new FileNotFoundException("Selection does not have the right extension "  + file.getName())));

			return;
		}

		if (Constants.EXTENSION_PATTERN_INCLUDES.matcher(file.getFileExtension()).matches()){
			IContainer parent = file.getParent();
			file = null;

			try {
				IResource[] members = parent.members();
				for (int i =0; i < members.length && file == null; i++){
					if (members[i] instanceof IFile && members[i].getFileExtension() != null){
						if (Constants.EXTENSION_PATTERN_MAINFILES.matcher(members[i].getFileExtension()).matches()){
							file = (IFile) members[i];
						}
					}
				}
			} catch (CoreException e) {
				Activator.getDefault().getLog().log(
						new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
								Messages.LAUNCH_ERROR, e));

			}

			if (file == null){
				Activator.getDefault().getLog().log(
						new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
								Messages.LAUNCH_ERROR, new FileNotFoundException("Could not find any file to launch")));

				return;
			}
		}


		String buildDir = ProjectPrefenceHelper.getBuildDir(file.getProject());
		String destdir = file.getProject().getLocationURI().getRawPath() + File.separator + buildDir;

		IFolder destFolder = file.getProject().getFolder(buildDir);

		if (!destFolder.exists()){
			try {
				file.touch(new NullProgressMonitor());
			} catch (CoreException e) {
			}
			return;
		}

		String dest = destdir + File.separator + file.getName();
		String destName = file.getName().substring(0,file.getName().lastIndexOf('.')+1)+"prg";

		IFile destFile = destFolder.getFile(destName);
		if (!destFile.exists()){
			try {
				file.touch(new NullProgressMonitor());
			} catch (CoreException e) {
			}
			Activator.getDefault().getLog().log(
					new Status(Status.ERROR, Constants.PLUGIN_ID, "Dest file does not exist : " +destFile.getLocation().toOSString()));
			return;
		}

		dest = dest.substring(0,dest.lastIndexOf(File.separatorChar)+1) + destName;

		String wfile = dest;
		int pos = wfile.lastIndexOf(File.separator);

		if (pos > 0) {
			wfile = wfile.substring(0, pos);
		}

		try {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(Constants.LAUNCH_CONFIGURATION_TYPE_ID);
			ILaunchConfiguration[] configs = manager.getLaunchConfigurations(type);

			for (ILaunchConfiguration config : configs) {
				if (config.getAttribute(Constants.LAUNCH_FILE, "").equalsIgnoreCase(destName) &&
					config.getAttribute(Constants.LAUNCH_WORKING_DIRECTORY,"").equalsIgnoreCase(wfile)
						) {
					DebugUITools.launch(config, mode);
					return;
				}
			}

			ILaunchConfigurationWorkingCopy copy = type.newInstance(
					null,
					manager.generateUniqueLaunchConfigurationNameFrom(Messages.EXECUTABLE_NAME));
			copy.setAttribute(Constants.LAUNCH_FILE, destName);
			copy.setAttribute(Constants.LAUNCH_WORKING_DIRECTORY, wfile);
			copy.setAttribute(Constants.LAUNCH_ARGUMENTS, "");
			ILaunchConfiguration config = copy.doSave();
			DebugUITools.launch(config, mode);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(
					new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
							Messages.LAUNCH_ERROR, e));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		ISelection selection = new StructuredSelection(input.getAdapter(IFile.class));
		launch(selection, mode);
	}
}
