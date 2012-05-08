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

import org.eclipse.osgi.util.NLS;

/**
 * Class which holds the message constants.
 * 
 * @author Andy Reek
 * @since 06.10.2005
 */
public class Messages extends NLS {

	/**
	 * Name of the message bundle.
	 */
	private static final String BUNDLE_NAME = "org.lyllo.kickassplugin.messages";

	public static String TEXTCOLOR_FUNCTION_NAME;
	
	public static String TEXTCOLOR_CLASS_NAME;

	public static String TEXTCOLOR_CONSTANT_NAME;

	/**
	 * Name of the string text style.
	 */
	public static String TEXTCOLOR_STRING_NAME;

	/**
	 * Name of the comment text style.
	 */
	public static String TEXTCOLOR_COMMENT_NAME;

	/**
	 * Name of the instruction text style.
	 */
	public static String TEXTCOLOR_INSTRUCTION_NAME;

	/**
	 * Name of the segment text style.
	 */
	public static String TEXTCOLOR_SEGMENT_NAME;

	/**
	 * Syntax Highlight Title Text.
	 */
	public static String SYNTAXHIGHLIGHT_TITLE;

	/**
	 * Foreground-Color-Text.
	 */
	public static String FOREGROUNDCOLOR_TEXT;

	/**
	 * Color-Text.
	 */
	public static String COLOR_TEXT;

	/**
	 * Bold-Text.
	 */
	public static String BOLD_TEXT;

	/**
	 * Italic-Text.
	 */
	public static String ITALIC_TEXT;

	/**
	 * The compiler choose label in the preferences.
	 */
	public static String COMPILER_NAME;

	public static String COMPILER_SYMBOLS;

	public static String COMPILER_VICESYMBOLS;
	
	public static String COMPILER_AFO;

	public static String COMPILER_LIBDIRS;

	public static String COMPILER_LIBDIRS_CHOOSETEXT;

	/**
	 * The debugger choose label in the preferences.
	 */
	public static String DEBUGGER_NAME;

	/**
	 * The linker choose label in the preferences.
	 */
	public static String LINKER_NAME;

	/**
	 * The label for all argument labels in the preferences.
	 */
	public static String PARAMS_NAME;

	/**
	 * The label for all input-file-extension labels in the preferences.
	 */
	public static String INPUT_EXT_NAME;

	/**
	 * Description for compiler parameter templates.
	 */
	public static String PARAMS_TEMPLATE_COMPILER;

	/**
	 * Description for linker parameter templates.
	 */
	public static String PARAMS_TEMPLATE_LINKER;

	/**
	 * Description for debugger parameter templates.
	 */
	public static String PARAMS_TEMPLATE_DEBUGGER;

	/**
	 * The title for the new ASM projet wizard.
	 */
	public static String WIZARD_NEW_PROJECT_TITLE;

	/**
	 * The title for the first page in the new ASM projet wizard.
	 */
	public static String WIZARD_NEW_PROJECT_PAGE1_TITLE;

	/**
	 * The title for the new ASM file wizard.
	 */
	public static String WIZARD_NEW_FILE_TITLE;

	/**
	 * The title for the first page in the new ASM file wizard.
	 */
	public static String WIZARD_NEW_FILE_PAGE1_TITLE;

	/**
	 * Error Message for an invalid file.
	 */
	public static String WIZARD_NEW_FILE_PAGE1_INVALID_FILE;

	/**
	 * The title for the second page in the new ASM file wizard.
	 */
	public static String WIZARD_NEW_FILE_PAGE2_TITLE;

	/**
	 * The description for the first page in the new ASM project wizard.
	 */
	public static String WIZARD_NEW_PROJECT_DESCRIPTION;

	/**
	 * The description for the first page in the new ASM file wizard.
	 */
	public static String WIZARD_NEW_FILE_PAGE1_DESCRIPTION;

	/**
	 * The description for the second page in the new ASM file wizard.
	 */
	public static String WIZARD_NEW_FILE_PAGE2_DESCRIPTION;

	/**
	 * The title for template table.
	 */
	public static String WIZARD_NEW_FILE_PAGE2_TABLE_TITLE;

	/**
	 * Text for use template checkbox.
	 */
	public static String USE_TEMPLATE;

	/**
	 * Text for console title.
	 */
	public static String CONSOLE_TITLE;

	/**
	 * Text for root procedure name.
	 */
	public static String TREEOBJECT_PROCEDURE_NAME;

	/**
	 * Text for root macro name.
	 */
	public static String TREEOBJECT_MACRO_NAME;

	/**
	 * Text for root label name.
	 */
	public static String TREEOBJECT_LABEL_NAME;

	/**
	 * Text for root segment name.
	 */
	public static String TREEOBJECT_SEGMENT_NAME;

	/**
	 * Text for build monitor title.
	 */
	public static String BUILDING_TITLE;

	/**
	 * Text compile build progress.
	 */
	public static String BUILDING_TEXT_COMPILE;

	/**
	 * Text link build progress.
	 */
	public static String BUILDING_TEXT_LINKING;

	/**
	 * Text in console of building start.
	 */
	public static String BUILDING_TEXT_CONSOLE;

	/**
	 * Text for error for loading asm instruction set.
	 */
	public static String LOAD_ASMISET_ERROR;

	/**
	 * Text for working directory.
	 */
	public static String LAUNCH_WORKDIR;

	/**
	 * Text for browse.
	 */
	public static String BROWSE;

	/**
	 * Text for select working directory.
	 */
	public static String LAUNCH_SELWORKDIR;

	/**
	 * Text for error on loading launch configuration.
	 */
	public static String LOAD_LAUNCH_CONFIG_ERROR;

	/**
	 * Text for error on launching.
	 */
	public static String LAUNCH_ERROR;

	/**
	 * Text for execute file.
	 */
	public static String LAUNCH_EXECFILE;

	/**
	 * Text for select execute file.
	 */
	public static String LAUNCH_SELFILE;

	/**
	 * Text for select execute file description.
	 */
	public static String LAUNCH_SELFILE_DESC;

	/**
	 * Text for OK.
	 */
	public static String OK_LABEL;

	/**
	 * Text for COPY.
	 */
	public static String COPY_LABEL;

	/**
	 * Text for error on open terminal.
	 */
	public static String OPEN_CONSOLE_ERROR;

	/**
	 * Text for information message box.
	 */
	public static String MSGBOX_INFORMATION;

	/**
	 * Text for function not supported.
	 */
	public static String FUNCTION_NOT_SUPPORTED;

	/**
	 * Text for executable.
	 */
	public static String EXECUTABLE_NAME;

	/**
	 * Text for main launch-tab.
	 */
	public static String MAINLAUNCHTAB;

	/**
	 * Text for arguments launch-tab.
	 */
	public static String ARGLAUNCHTAB;

	/**
	 * Text for error on loading TemplateStore.
	 */
	public static String LOADTEMPSTORE_ERROR;

	/**
	 * Text for error on bad location.
	 */
	public static String BADLOCATION_ERROR;

	/**
	 * Text for error on creating as file.
	 */
	public static String ASMCREATEFROMTEMP_ERROR;

	/**
	 * Text for Run.
	 */
	public static String RUN;

	/**
	 * Text for From.
	 */
	public static String FROM;

	// Initialize the constants.
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
