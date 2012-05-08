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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
      return;
    }

    IFile file = (IFile) firstSelection;
    String workspaceFilename = file.toString().substring(2);

    try {
      ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
      ILaunchConfigurationType type = manager.getLaunchConfigurationType(Constants.LAUNCH_CONFIGURATION_TYPE_ID);
      ILaunchConfiguration[] configs = manager.getLaunchConfigurations(type);

      for (ILaunchConfiguration config : configs) {
        if (config.getAttribute(Constants.LAUNCH_FILE, "").equalsIgnoreCase(workspaceFilename)) {
          DebugUITools.launch(config, mode);
          return;
        }
      }

      ILaunchConfigurationWorkingCopy copy = type.newInstance(
        null,
        manager.generateUniqueLaunchConfigurationNameFrom(Messages.EXECUTABLE_NAME));
      copy.setAttribute(Constants.LAUNCH_FILE, workspaceFilename);

      String sep = System.getProperty("file.separator");
      if (sep.charAt(0) != '/') {
        sep = "\\" + sep;
      }

      String wfile = workspaceFilename.replaceAll("/", sep);
      int pos = wfile.lastIndexOf(System.getProperty("file.separator"));

      if (pos > 0) {
        wfile = wfile.substring(0, pos);
      }

      String wdir = ResourcesPlugin.getWorkspace().getRoot().getLocation().makeAbsolute().toOSString()
                    + System.getProperty("file.separator") + wfile;

      copy.setAttribute(Constants.LAUNCH_WORKING_DIRECTORY, wdir);
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
