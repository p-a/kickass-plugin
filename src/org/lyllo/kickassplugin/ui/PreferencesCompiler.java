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
package org.lyllo.kickassplugin.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.Messages;


/**
 * Preferences Page for Compiler.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class PreferencesCompiler extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  /**
   * The constructor.
   */
  public PreferencesCompiler() {
    super(FieldEditorPreferencePage.GRID); // use GRID-Layout

    noDefaultAndApplyButton(); // disable Default and Apply

    setPreferenceStore(Activator.getDefault().getPreferenceStore()); // set
  }

  /**
   * {@inheritDoc}
   */
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    // Field for Compiler-Executable
    FileFieldEditor compiler = new FileFieldEditor(Constants.PREFERENCES_COMPILER_NAME, Messages.COMPILER_NAME + ": ",
                                                   true, parent);
    compiler.setEmptyStringAllowed(false);
    compiler.setFileExtensions(new String[]{"jar"});
    
    addField(compiler);

    BooleanFieldEditor symbols = new BooleanFieldEditor(Constants.PREFERENCES_COMPILER_SYMBOLS, Messages.COMPILER_SYMBOLS, parent);
    addField(symbols);
    
    BooleanFieldEditor vicesymbols = new BooleanFieldEditor(Constants.PREFERENCES_COMPILER_VICESYMBOLS, Messages.COMPILER_VICESYMBOLS, parent);
    addField(vicesymbols);
    
    BooleanFieldEditor afo = new BooleanFieldEditor(Constants.PREFERENCES_COMPILER_AFO, Messages.COMPILER_AFO, parent);
    addField(afo);
    
    PathEditor pathEditor = new PathEditor(Constants.PREFERENCES_COMPILER_LIBDIRS, Messages.COMPILER_LIBDIRS,Messages.COMPILER_LIBDIRS_CHOOSETEXT, parent);
    addField(pathEditor);
    
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 3;

    Label label = new Label(parent, SWT.LEFT);
    label.setText(Messages.PARAMS_TEMPLATE_COMPILER);
    label.setLayoutData(gd);

    // Field for parameters to use Compiler-Executable
    MultiLineStringFieldEditor params = new MultiLineStringFieldEditor(Constants.PREFERENCES_COMPILER_PARAMS,
                                                                       Messages.PARAMS_NAME, parent);
    params.setErrorMessage("Invalid parameter. Must be in 'key=value' format.");
    
    addField(params);

  }

  /**
   * {@inheritDoc}
   */
  public void init(IWorkbench workbench) {
  }
}
