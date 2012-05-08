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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.Messages;


/**
 * Wizard for creating new files.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class WizardNewFile extends Wizard implements INewWizard {

  /**
   * The first page of the wizard.
   */
  private WizardNewFileCreationPage page1;

  /**
   * The second page of the wizard.
   */
  private TemplateFileWizardPage page2;

  private IStructuredSelection selection;

  /**
   * The constructor.
   */
  public WizardNewFile() {
    super();
    setWindowTitle(Messages.WIZARD_NEW_FILE_TITLE);
  }

  /**
   * {@inheritDoc}
   */
  public void addPages() {
    super.addPages();

    page1 = new WizardNewFileCreationPage(Messages.WIZARD_NEW_FILE_PAGE1_TITLE, selection) {
      protected boolean validatePage() {
        if (!(getFileName().toLowerCase().endsWith(".asm") || getFileName().toLowerCase().endsWith(".s") || getFileName().toLowerCase().endsWith(".inc"))) {
          setErrorMessage(Messages.WIZARD_NEW_FILE_PAGE1_INVALID_FILE);
          return false;
        }
        return super.validatePage();
      }
    };
    page1.setTitle(Messages.WIZARD_NEW_FILE_PAGE1_TITLE);
    page1.setImageDescriptor(Constants.WIZARD_NEW);
    page1.setDescription(Messages.WIZARD_NEW_FILE_PAGE1_DESCRIPTION);

    page2 = new TemplateFileWizardPage(Messages.WIZARD_NEW_FILE_PAGE2_TITLE);
    page2.setTitle(Messages.WIZARD_NEW_FILE_PAGE2_TITLE);
    page2.setImageDescriptor(Constants.WIZARD_NEW);
    page2.setDescription(Messages.WIZARD_NEW_FILE_PAGE2_DESCRIPTION);

    addPage(page1);
    addPage(page2);
  }

  /**
   * {@inheritDoc}
   */
  public boolean performFinish() {
    boolean template = page2.isTemplateFile();
    File tfile = template ? page2.getSelectedFile() : null;
    IFile file = page1.createNewFile();

    if (file == null) {
      return false;
    }

    IEditorPart editpart = null;
    IDE.setDefaultEditor(file, "ASMPlugin.editor1");

    try {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      editpart = IDE.openEditor(page, file, true);
    } catch (PartInitException ex) {
      return false;
    }

    if ((editpart != null) && template && (tfile != null)) {
      if (editpart instanceof AbstractDecoratedTextEditor) {
        AbstractDecoratedTextEditor adedit = (AbstractDecoratedTextEditor) editpart;

        if (adedit != null) {
          IDocument doc = adedit.getDocumentProvider().getDocument(adedit.getEditorInput());

          if (doc != null) {
            String line;
            StringBuffer text = new StringBuffer("");

            try {
              FileInputStream fis = new FileInputStream(tfile.getPath());
              BufferedReader br = new BufferedReader(new InputStreamReader(fis));

              while ((line = br.readLine()) != null) {
                text.append(line);
                if ((line.length() < 1) || ((line.length() > 0) && (line.charAt(line.length() - 1) != '\n'))) {
                  text.append("\n");
                }
              }

              fis.close();
              br.close();
            } catch (Exception e) {
              Activator.getDefault().getLog().log(
                                                  new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
                                                             Messages.ASMCREATEFROMTEMP_ERROR, e));
            }

            doc.set(text.toString());
          }
        }
      }
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
  }
}
