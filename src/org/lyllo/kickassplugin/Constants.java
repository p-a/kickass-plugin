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

import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Class which holds several constants.
 * 
 * @author Andy Reek
 * @since 06.10.2005
 */
public class Constants {

	// Important IDs.

	/**
	 * The Plug-In ID.
	 */
	public static final String PLUGIN_ID = "org.lyllo.kickassplugin"; //$NON-NLS-1$

	/**
	 * The Builder ID.
	 */
	public static final String BUILDER_ID = "org.lyllo.kickassplugin.builder"; //$NON-NLS-1$

	/**
	 * The Nature ID.
	 */
	public static final String NATURE_ID = "org.lyllo.kickassplugin.nature"; //$NON-NLS-1$

	/**
	 * The LaunchConfigurationType ID.
	 */
	public static final String LAUNCH_CONFIGURATION_TYPE_ID = "org.lyllo.kickassplugin.launch"; //$NON-NLS-1$

	// Names of properties in the PreferenceStore.

	/**
	 * Template Property.
	 */
	public static final String PROPERTY_TEMPLATES = "templates"; //$NON-NLS-1$

	/**
	 * Preference key for string text style preference keys.
	 */
	public static final String PREFERENCES_TEXTCOLOR_STRING = "preferences.textcolor.string"; //$NON-NLS-1$

	/**
	 * Preference key for comment text style preference keys.
	 */
	public static final String PREFERENCES_TEXTCOLOR_COMMENT = "preferences.textcolor.comment"; //$NON-NLS-1$

	/**
	 * Preference key for instruction text style preference keys.
	 */
	public static final String PREFERENCES_TEXTCOLOR_INSTRUCTION = "preferences.textcolor.instruction"; //$NON-NLS-1$

	public static final String PREFERENCES_TEXTCOLOR_FUNCTION = "preferences.textcolor.function"; //$NON-NLS-1$

	public static final String PREFERENCES_TEXTCOLOR_CLASS = "preferences.textcolor.class";

	public static final String PREFERENCES_TEXTCOLOR_CONSTANT = "preferences.textcolor.constant";


	/**
	 * Preference key for segment text style preference keys.
	 */
	public static final String PREFERENCES_TEXTCOLOR_SEGMENT = "preferences.textcolor.segment"; //$NON-NLS-1$

	/**
	 * Compiler name Property.
	 */
	public static final String PREFERENCES_COMPILER_NAME = "preferences.compiler.name"; //$NON-NLS-1$

	/**
	 * Compiler paramameter Property.
	 */
	public static final String PREFERENCES_COMPILER_PARAMS = "preferences.compiler.params"; //$NON-NLS-1$

	public static final String PREFERENCES_COMPILER_SYMBOLS = "preferences.compiler.symbols"; //$NON_NLS-1$

	public static final String PREFERENCES_COMPILER_VICESYMBOLS = "preferences.compiler.vicesymbols"; //$NON_NLS-1$

	public static final String PREFERENCES_COMPILER_LIBDIRS = "preferencs.compiler.libdirs";

	public static final String PREFERENCES_COMPILER_AFO = "preferences.compiler.afo";


	/**
	 * Linker name Property.
	 */
	public static final String PREFERENCES_LINKER_NAME = "preferences.linker.name"; //$NON-NLS-1$

	/**
	 * Linker paramameter Property.
	 */
	public static final String PREFERENCES_LINKER_PARAMS = "preferences.linker.params"; //$NON-NLS-1$


	/**
	 * Linker file-input-extension Property.
	 */
	public static final String PREFERENCES_LINKER_EXT = "preferences.linker.ext"; //$NON-NLS-1$

	/**
	 * Debugger name Property.
	 */
	public static final String PREFERENCES_DEBUGGER_NAME = "preferences.debugger.name"; //$NON-NLS-1$

	/**
	 * Debugger paramameter Property.
	 */
	public static final String PREFERENCES_DEBUGGER_PARAMS = "preferences.debugger.params"; //$NON-NLS-1$

	// Partition names.

	/**
	 * Name for the string partition.
	 */
	public static final String PARTITION_STRING = "partion.string";

	/**
	 * Name for the single line comment partition.
	 */
	public static final String PARTITION_COMMENT_SINGLE = "partion.single";

	/**
	 * Name for the multi line comment partition.
	 */
	public static final String PARTITION_COMMENT_MULTI = "partion.multi";

	// PropertyChangeEvent names.

	/**
	 * Event name for single line comments.
	 */
	public static final String TEXT_COMMENTS_SINGLE = "text.comments.single";

	/**
	 * Event name for multi line comments.
	 */
	public static final String TEXT_COMMENTS_MULTI = "text.comments.multi";

	/**
	 * Event name for strings.
	 */
	public static final String TEXT_STRINGS = "text.strings";

	/**
	 * Event name for instructions.
	 */
	public static final String TEXT_INSTRUCTIONS = "text.instructions";

	/**
	 * Event name for segments.
	 */
	public static final String TEXT_SEGMENTS = "text.segments";

	// Wizard constants.

	/**
	 * Name for the multi line comment partition.
	 */
	public static final ImageDescriptor WIZARD_NEW = Activator.getImageDescriptor("icons/wizards/newWizard.gif");

	// TreeObject types

	/**
	 * Default-type
	 */
	public static final int TREEOBJECT_TYPE_NULL = 0;

	/**
	 * Type for procedure root
	 */
	public static final int TREEOBJECT_TYPE_ROOT_PROCEDURE = 1;

	/**
	 * Type for macro root
	 */
	public static final int TREEOBJECT_TYPE_ROOT_MACRO = 2;

	/**
	 * Type for label root
	 */
	public static final int TREEOBJECT_TYPE_ROOT_LABEL = 3;

	/**
	 * Type for segment root
	 */
	public static final int TREEOBJECT_TYPE_ROOT_SEGMENT = 4;

	/**
	 * Type for procedure tree
	 */
	public static final int TREEOBJECT_TYPE_PROCEDURE = 5;

	/**
	 * Type for macro tree
	 */
	public static final int TREEOBJECT_TYPE_MACRO = 6;

	/**
	 * Type for label tree
	 */
	public static final int TREEOBJECT_TYPE_LABEL = 7;

	/**
	 * Type for segment tree
	 */
	public static final int TREEOBJECT_TYPE_SEGMENT = 8;

	// Launch

	/**
	 * Name of the argument attribute in the LaunchConfiguration.
	 */
	public static final String LAUNCH_ARGUMENTS = "launch.arguments";

	/**
	 * Name of the working directory attribute in the LaunchConfiguration.
	 */
	public static final String LAUNCH_WORKING_DIRECTORY = "launch.working.directory";

	/**
	 * Name of the file attribute in the LaunchConfiguration.
	 */
	public static final String LAUNCH_FILE = "launch.file";

	// Other

	/**
	 * The context type for the ASM Editor.
	 */
	public static final String ASM_EDITOR_CONTEXT = "asm.editor.context";

	/**
	 * Build output-directory.
	 */
	public static final String DEFAULT_BUILD_DIRECTORY = "build";

	public static final String DEFAULT_SRC_DIRECTORY = "src";

	public static final String PROJECT_PREFS_BUILD_DIRECTORY_KEY = "org.lyllo.kickassplugin.project.builddir";
	public static final String PROJECT_PREFS_SRC_DIRECTORY_KEY = "org.lyllo.kickassplugin.project.srcdir";
	public static final String PROJECT_PREFS_LIBDIR_DIRECTORY_KEY = "org.lyllo.kickassplugin.project.libdir";


	public final static Pattern EXTENSION_PATTERN_ALL = Pattern.compile("(asm|inc|sym|s)",Pattern.CASE_INSENSITIVE);
	public final static Pattern EXTENSION_PATTERN_INCLUDES = Pattern.compile("(inc|sym)",Pattern.CASE_INSENSITIVE);
	public final static Pattern EXTENSION_PATTERN_MAINFILES = Pattern.compile("(asm|s)",Pattern.CASE_INSENSITIVE);

	public final static Pattern LABEL_PATTERN = Pattern.compile("\\A\\s*\\.label\\s+(\\w+\\s*=\\s*\\S+).*$", Pattern.CASE_INSENSITIVE);
	public final static Pattern LABEL_PATTERN_ALT = Pattern.compile("\\A\\s*(\\w+):.*$", Pattern.CASE_INSENSITIVE);
	
	public final static Pattern MACRO_PATTERN = Pattern.compile("^\\s*\\.macro\\s*(\\w+\\s*\\((\\w+\\s*,?\\s*)*\\)).*$", Pattern.CASE_INSENSITIVE);

	public static final Pattern FUNCTION_PATTERN = Pattern.compile("\\A\\s*.function\\s*(\\w+\\s*\\((\\w+\\s*,?\\s*)*\\)).*$", Pattern.CASE_INSENSITIVE);
	
	public static final Pattern FILENAMESPACE_PATTERN = Pattern.compile("\\A\\s*.filenamespace\\s+(\\w+).*$", Pattern.CASE_INSENSITIVE);
	public static final Pattern NAMESPACE_PATTERN = Pattern.compile("\\A\\s*.namespace\\s+(\\w+).*$", Pattern.CASE_INSENSITIVE);



}
