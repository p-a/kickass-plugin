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
package org.lyllo.kickassplugin.editor;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.Messages;


/**
 * Action to toggle a comment.
 * 
 * @author Andy Reek
 * @since 13.02.2006
 */
public class ToggleCommentAction extends Action {

  /** Refer to SourceViewer */
  private SourceViewer viewer;

  /**
   * The constructor.
   * 
   * @param viewer The SourceViewer for the corresponding ASM-Editor.
   */
  public ToggleCommentAction(SourceViewer viewer) {
    this.viewer = viewer;
  }

  /**
   * {@inheritDoc}
   */
  public void run() {
    IDocument document = viewer.getDocument();
    ISelection selection = viewer.getSelection();
    TextSelection textSelection;
    if (selection instanceof TextSelection) {
      textSelection = (TextSelection) selection;
      boolean isCommented = isCommented(document, textSelection);
      if (isCommented) {
        viewer.doOperation(ITextOperationTarget.STRIP_PREFIX);
      } else {
        viewer.doOperation(ITextOperationTarget.PREFIX);
      }
    }
  }

  /**
   * Check, if the selection in the given document is commented.
   * 
   * @param document The document.
   * @param selection The selection.
   * 
   * @return true, if commented. Otherwise false.
   */
  private boolean isCommented(IDocument document, TextSelection selection) {
    try {
      int startLine = selection.getStartLine();
      int endLine = selection.getEndLine();
      String firstChar;
      for (int line = startLine; line <= endLine; line++) {
        firstChar = document.get(document.getLineOffset(line), 2);
        if (!firstChar.equals("//")) {
          return false;
        }
      }
    } catch (BadLocationException e) {
      Activator.getDefault().getLog().log(
                                          new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
                                                     Messages.BADLOCATION_ERROR, e));
    }

    return true;
  }
}
