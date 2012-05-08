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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchViewerSorter;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Messages;


/**
 * WizardPage of creating new ASM-Files with Templates.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class TemplateFileWizardPage extends WizardPage {

  private final List<TemplateFileItem> templateFileList = new ArrayList<TemplateFileItem>();

  private Button fUseTemplatesCheckBox;
  private TableViewer templateFileViewer;

  /**
   * The constructor.
   * 
   * @param pageName The name of the page.
   */
  public TemplateFileWizardPage(String pageName) {
    super(pageName);
    setPageComplete(true);
  }

  /**
   * {@inheritDoc}
   */
  public void createControl(Composite parent) {
    Composite composite = new Composite(parent, SWT.NULL);
    GridLayout gl = new GridLayout(1, false);
    composite.setLayout(gl);

    createCheckBox(composite);
    createTableData(composite);

    setControl(composite);
  }

  /**
   * Creates the CheckBox.
   * 
   * @param composite The parent Composite.
   */
  private void createCheckBox(Composite composite) {
    fUseTemplatesCheckBox = new Button(composite, SWT.CHECK);
    fUseTemplatesCheckBox.setText(Messages.USE_TEMPLATE);
    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
    fUseTemplatesCheckBox.setLayoutData(gd);
    fUseTemplatesCheckBox.setSelection(false);
    fUseTemplatesCheckBox.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        if (templateFileViewer != null) {
          templateFileViewer.getControl().setEnabled(fUseTemplatesCheckBox.getSelection());
        }
      }
    });
  }

  /**
   * Creates the TableViewer.
   * 
   * @param composite The parent Composite.
   */
  private void createTableData(Composite composite) {
    templateFileViewer = new TableViewer(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
    templateFileViewer.setLabelProvider(new FileListLabelProvider());
    templateFileViewer.setContentProvider(new FileListContentProvider());
    templateFileViewer.setSorter(new WorkbenchViewerSorter());
    GridData gd = new GridData(GridData.FILL_BOTH);
    templateFileViewer.getControl().setLayoutData(gd);

    loadFileTemplates();

    templateFileViewer.setInput(templateFileList);

    if (templateFileList.size() > 0) {
      templateFileViewer.setSelection(new StructuredSelection(templateFileViewer.getElementAt(0)));
    }

    templateFileViewer.getControl().setEnabled(false);
  }

  /**
   * Loads the templates.
   */
  private void loadFileTemplates() {
    String path = Activator.getFilePathFromPlugin("templates");
    File file = new File(path);

    if (file == null) {
      return;
    }

    File[] files = file.listFiles();

    if (files == null) {
      return;
    }

    String item;

    for (int i = 0; i < files.length; i++) {
      item = files[i].getName();
      if (files[i].isFile() && item.toLowerCase().endsWith(".txt")) {
        item = item.substring(0, item.length() - 4);
        templateFileList.add(new TemplateFileItem(item, files[i]));
      }
    }
  }

  /**
   * Checks if templates should be used.
   * 
   * @return true to use templates. Otherwise false.
   */
  public boolean isTemplateFile() {
    return (isPageComplete() && fUseTemplatesCheckBox.getSelection()) ? true : false;
  }

  /**
   * Returns the current highlighting color list item.
   * 
   * @return the current highlighting color list item
   */
  private TemplateFileItem getTemplateFileItem() {
    IStructuredSelection selection = (IStructuredSelection) templateFileViewer.getSelection();
    return (TemplateFileItem) selection.getFirstElement();
  }

  /**
   * Returns the selected file.
   * 
   * @return The selected file.
   */
  public File getSelectedFile() {
    TemplateFileItem tfi = getTemplateFileItem();
    return tfi.getFile();
  }

  /**
   * Item in the highlighting color list.
   */
  private static class TemplateFileItem {

    /** Display name */
    private String fDisplayName;

    /** Filename */
    private File fFile;

    /**
     * Initialize the item with the given values.
     * 
     * @param displayName the display name
     * @param file Name of file
     */
    public TemplateFileItem(String displayName, File file) {
      fDisplayName = displayName;
      fFile = file;
    }

    /**
     * @return the name of file
     */
    public File getFile() {
      return fFile;
    }

    /**
     * @return the display name
     */
    public String getDisplayName() {
      return fDisplayName;
    }
  }

  /**
   * Color list label provider.
   */
  private class FileListLabelProvider extends LabelProvider {

    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
      return ((TemplateFileItem) element).getDisplayName();
    }
  }

  /**
   * Color list content provider.
   */
  private class FileListContentProvider implements IStructuredContentProvider {

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
      return ((java.util.List) inputElement).toArray();
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
  }
}
