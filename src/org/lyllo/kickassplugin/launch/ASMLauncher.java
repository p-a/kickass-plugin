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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.preference.IPreferenceStore;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.SimpleExecutor;


/**
 * Launcher for executable files.
 * 
 * @author Andy Reek
 * @since 13.02.2006
 */
public class ASMLauncher extends LaunchConfigurationDelegate {

	/**
	 * {@inheritDoc}
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String debugger = store.getString(Constants.PREFERENCES_DEBUGGER_NAME);
		String params = store.getString(Constants.PREFERENCES_DEBUGGER_PARAMS);

		if (mode.equalsIgnoreCase("debug") && ((debugger == null) || (debugger.trim().length() < 1))) {
			return;
		}

		if (params == null) {
			params = "";
		}

		String launchWorkingDirectory = configuration.getAttribute(Constants.LAUNCH_WORKING_DIRECTORY, "");
		Activator.getDefault().getLog().log(
				new Status(Status.INFO, Constants.PLUGIN_ID, "Launch working directory: " + launchWorkingDirectory));
		if (launchWorkingDirectory == null || launchWorkingDirectory.trim().length() == 0){
			return;
		}

		String launchFile = configuration.getAttribute(Constants.LAUNCH_FILE, "");
		Activator.getDefault().getLog().log(
				new Status(Status.INFO, Constants.PLUGIN_ID, "Launch file: " + launchFile));
		if ((launchFile == null) || (launchFile.trim().length() < 1)) {
			return;
		}

		String filename;
		if (launchFile.lastIndexOf(File.separatorChar) > -1){
			filename = launchWorkingDirectory + launchFile.substring(launchFile.lastIndexOf(File.separatorChar));
		} else if (launchFile.lastIndexOf('/') > -1) {
			filename = launchWorkingDirectory + launchFile.substring(launchFile.lastIndexOf('/'));
		} else {
			filename = launchWorkingDirectory + File.separator + launchFile;
		}

		String sep = File.separator;
		if (sep.charAt(0) != '/') {
			sep = "\\" + sep;
		}
		filename = filename.replaceAll("/", sep);

		List<String> cmds = new ArrayList<String>();
		cmds.add(debugger.trim());

		for (String param: params.trim().split("/n")){
			if (!"".equals(param.trim())){
				cmds.add(param.trim());
			}
		}

		String vs =  filename.replace(".prg", ".vs").replace(".PRG", ".VS");
		File vsFile = new File(vs);
		if (vsFile.exists() || mode.equalsIgnoreCase("debug")){

			File file = new File(launchWorkingDirectory,"moncommands.vice");
			if (file.exists()){
				file.delete();
			}
			BufferedWriter writer = null;
			try {

				writer = new BufferedWriter(new FileWriter(file));

				if (vsFile.exists()){
					writer.write("ll \"");
					writer.write(vs);
					writer.write("\"");
					writer.newLine();
				}

				if (mode.equalsIgnoreCase("debug")){
					writer.write("break .breakpoint");
					writer.newLine();
					for (int i = 1; i < 9; i++){
						writer.write("break .breakpoint"+i);
						writer.newLine();
					}
				}
				cmds.add("-moncommands");
				cmds.add(file.getCanonicalPath());
			} catch (IOException e) {
				Activator.getDefault().getLog().log(
						new Status(Status.INFO, Constants.PLUGIN_ID, "Error" + e.getMessage()));

			} finally {
				if (writer != null){
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}

		cmds.add(filename);

		SimpleExecutor.exec(cmds.toArray(new String[]{}), launchWorkingDirectory);

	}


}
