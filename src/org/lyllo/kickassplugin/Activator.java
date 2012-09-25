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
package org.lyllo.kickassplugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.lyllo.kickassplugin.editor.ASMEditor;
import org.lyllo.kickassplugin.ui.OutputConsole;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class Activator extends AbstractUIPlugin {

	/**
	 * The shared instance.
	 */
	private static Activator plugin;

	/**
	 * The TemplateStore.
	 */
	private static TemplateStore templateStore;

	/**
	 * The Output-Console.
	 */
	private static OutputConsole console = new OutputConsole();

	private static AutocompletionCollector autocompletionCollector = new AutocompletionCollector();

	/**
	 * The constructor.
	 */
	public Activator() {
		super();
		plugin = this;
	}

	public AutocompletionCollector getAutoCompletionCollector(){
		return autocompletionCollector;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this; 

		ResourcesPlugin.getWorkspace().addResourceChangeListener(autocompletionCollector);
		
		Job job = new Job("Autocollector init") {
		     protected IStatus run(IProgressMonitor monitor) {
		    	 autocompletionCollector.init();  
		           return Status.OK_STATUS;
		        }

		    };
		  job.setPriority(Job.LONG);
		  job.schedule(); 		
		
		 ISaveParticipant saveParticipant = new KickassSaveParticipant();
		 ISavedState lastState =
		      ResourcesPlugin.getWorkspace().addSaveParticipant(Constants.PLUGIN_ID, saveParticipant);
		   if (lastState != null) {
		      lastState.processResourceChangeEvents(autocompletionCollector);
		   }		
		
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null; 
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(autocompletionCollector);
		
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return The plug-in.
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Loads and returns the TemplateStore.
	 * 
	 * @return The TemplateStore.
	 */
	public static TemplateStore getTemplateStore() {
		if (templateStore == null) {
			templateStore = new TemplateStore(ASMEditor.getContextTypeRegistry(), plugin.getPreferenceStore(),
					Constants.PROPERTY_TEMPLATES);
		}

		try {
			templateStore.load();
		} catch (IOException e) {
			plugin.getLog().log(new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK, Messages.LOADTEMPSTORE_ERROR, e));
		}

		return templateStore;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path The path.
	 * 
	 * @return The image descriptor.
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Constants.PLUGIN_ID, path);
	}

	/**
	 * Returns the absolut path of a entrie from the plugin's directory.
	 * 
	 * @param entrie a file or directory (don't use "dir1\dir2" or "dir1\file1")
	 * 
	 * @return Returns the path from the plugin.
	 */
	public static String getFilePathFromPlugin(String entrie) {
		URL url = null;
		IPath path = null;
		String result = "";

		Enumeration<URL> enu = Activator.getDefault().getBundle().findEntries("/", entrie, true);
		if (enu.hasMoreElements()) {
			url = (URL) enu.nextElement();
		}

		if (url == null) {
			return "";
		}

		try {
			path = new Path(FileLocator.toFileURL(url).getPath());
			result = path.makeAbsolute().toOSString();
		} catch (Exception e) {
			result = "";
		}

		return result;
	}

	/**
	 * Return the Output-Console object for text output.
	 * 
	 * @return Output-Console object
	 */
	public static OutputConsole getConsole() {
		return console;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		Display display = Display.getCurrent();

		String textAttribute = TextAttributeConverter.textAttributesToPreferenceData(new Color(display, 0, 0, 255), false,
				false);
		store.setDefault(Constants.PREFERENCES_TEXTCOLOR_STRING, textAttribute);

		textAttribute = TextAttributeConverter.textAttributesToPreferenceData(new Color(display, 0, 128, 0), false, true);
		store.setDefault(Constants.PREFERENCES_TEXTCOLOR_COMMENT, textAttribute);

		textAttribute = TextAttributeConverter.textAttributesToPreferenceData(new Color(display, 0, 0, 128), true, false);
		store.setDefault(Constants.PREFERENCES_TEXTCOLOR_INSTRUCTION, textAttribute);

		textAttribute = TextAttributeConverter.textAttributesToPreferenceData(new Color(display, 128, 64, 0), true, false);
		store.setDefault(Constants.PREFERENCES_TEXTCOLOR_SEGMENT, textAttribute);

		textAttribute = TextAttributeConverter.textAttributesToPreferenceData(new Color(display, 64, 64, 255), true, true);
		store.setDefault(Constants.PREFERENCES_TEXTCOLOR_CONSTANT, textAttribute);

		textAttribute = TextAttributeConverter.textAttributesToPreferenceData(new Color(display, 255, 64, 255), true, false);
		store.setDefault(Constants.PREFERENCES_TEXTCOLOR_CLASS, textAttribute);

		textAttribute = TextAttributeConverter.textAttributesToPreferenceData(new Color(display, 50, 168, 0), true, false);
		store.setDefault(Constants.PREFERENCES_TEXTCOLOR_FUNCTION, textAttribute);

		store.setDefault(Constants.PREFERENCES_COMPILER_NAME,"kickass.jar");
		store.setDefault(Constants.PREFERENCES_COMPILER_AFO,false);
		store.setDefault(Constants.PREFERENCES_COMPILER_SYMBOLS, true);
		store.setDefault(Constants.PREFERENCES_COMPILER_VICESYMBOLS, true);

		store.setDefault(Constants.PROPERTY_TEMPLATES,
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<templates>"
						+ "<template autoinsert=\"true\" context=\"asm.editor.context\" deleted=\"false\" description=\"Basic Upstart.\" enabled=\"true\" name=\"BasicUpstart\">.pc = $$0801 \"Basic Upstart\"&#10;:BasicUpstart(start)&#10;.pc = $$0810 \"Program\"&#10;start:&#10;</template>"
						+ "</templates>");

		store.setDefault(Constants.PREFERENCES_COMPILER_NAME, "path/to/kickass.jar");
		store.setDefault(Constants.PREFERENCES_COMPILER_PARAMS, "");
		store.setDefault(Constants.PREFERENCES_LINKER_NAME, "");
		store.setDefault(Constants.PREFERENCES_LINKER_PARAMS, "");
		store.setDefault(Constants.PREFERENCES_LINKER_EXT, "");
		store.setDefault(Constants.PREFERENCES_DEBUGGER_NAME, "x64");
		store.setDefault(Constants.PREFERENCES_DEBUGGER_PARAMS, "-truedrive");
	}

	public static List<String> getGlobalLibdirs() {
		List<String> dirs = new ArrayList<String>();
		IPreferenceStore store = getDefault().getPreferenceStore();
		String libdirsGlobal = store.getString(Constants.PREFERENCES_COMPILER_LIBDIRS);
		if (libdirsGlobal != null && !"".equals(libdirsGlobal)){
			String[] split = libdirsGlobal.split(File.pathSeparator);
			List<String> splitArray = Arrays.asList(split);
			dirs.addAll(splitArray);
		}
		
		return dirs;
	}
}
