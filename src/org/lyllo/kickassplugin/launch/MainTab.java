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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.Messages;


/**
 * Tab for main.
 * 
 * @author Andy Reek
 * @since 13.02.2006
 */
public class MainTab extends AbstractLaunchConfigurationTab {

  private Text textFile = null;

  private ModifyListener listener = null;

  /**
   * {@inheritDoc}
   */
  public void createControl(Composite parent) {
    listener = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        updateLaunchConfigurationDialog();
      }
    };

    Composite composite = new Composite(parent, parent.getStyle());
    composite.setLayout(new GridLayout(3, false));
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    setControl(composite);

    Label label = new Label(composite, SWT.NONE);
    label.setText(Messages.LAUNCH_EXECFILE + ":");

    textFile = new Text(composite, SWT.SINGLE | SWT.BORDER);
    textFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    textFile.addModifyListener(listener);

    Button button = new Button(composite, SWT.PUSH);
    button.setText(Messages.BROWSE);
    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
                                                                           new WorkbenchContentProvider());

        dialog.setTitle(Messages.LAUNCH_SELFILE);
        dialog.setMessage(Messages.LAUNCH_SELFILE_DESC);
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
        dialog.setAllowMultiple(false);
        dialog.setValidator(new ISelectionStatusValidator() {
          public IStatus validate(Object[] selection) {
            if ((selection != null) && (selection.length != 0) && (selection[0] instanceof IFile)) {
              return new Status(IStatus.OK, Constants.PLUGIN_ID, IStatus.OK, "", null);
            }

            return new Status(IStatus.ERROR, Constants.PLUGIN_ID, IStatus.ERROR, "", null);
          }
        });

        if (dialog.open() == Window.OK) {
          textFile.setText(((IFile) dialog.getFirstResult()).toString().substring(2));
        }
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(Constants.LAUNCH_FILE, "");
  }

  /**
   * {@inheritDoc}
   */
  public void initializeFrom(ILaunchConfiguration configuration) {
    try {
      textFile.removeModifyListener(listener);
      textFile.setText(configuration.getAttribute(Constants.LAUNCH_FILE, ""));
      textFile.addModifyListener(listener);
    } catch (CoreException e) {
      Activator.getDefault().getLog().log(
                                          new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
                                                     Messages.LOAD_LAUNCH_CONFIG_ERROR, e));
    }
  }

  /**
   * {@inheritDoc}
   */
  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(Constants.LAUNCH_FILE, textFile.getText());
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return Messages.MAINLAUNCHTAB;
  }
}
