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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
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
 * Preferences Page for Debugger.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class PreferencesDebugger extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  /**
   * The constructor.
   */
  public PreferencesDebugger() {
    super(FieldEditorPreferencePage.GRID); // use GRID-Layout

    noDefaultAndApplyButton(); // disable Default and Apply

    setPreferenceStore(Activator.getDefault().getPreferenceStore()); // set
  }

  /**
   * {@inheritDoc}
   */
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    // Field for Debugger-Executable
    FileFieldEditor linker = new FileFieldEditor(Constants.PREFERENCES_DEBUGGER_NAME, Messages.DEBUGGER_NAME + ": ",
                                                 true, parent);
    addField(linker);

    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 3;

    Label label = new Label(parent, SWT.LEFT);
    label.setText(" \n" + Messages.PARAMS_TEMPLATE_DEBUGGER + "\n ");
    label.setLayoutData(gd);

    // Field for parameters to use Compiler-Executable
    MultiLineStringFieldEditor params = new MultiLineStringFieldEditor(Constants.PREFERENCES_DEBUGGER_PARAMS,
                                                                       Messages.PARAMS_NAME + ": ", parent){
    	 
    	  @Override
    		protected boolean doCheckState() {
    		  return true;
    		}
    };
    addField(params);
  }

  /**
   * {@inheritDoc}
   */
  public void init(IWorkbench workbench) {
  }
}
