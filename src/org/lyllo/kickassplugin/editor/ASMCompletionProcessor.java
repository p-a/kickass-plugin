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

import java.util.ArrayList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;


/**
 * Class for content assist. Create the content assist list.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class ASMCompletionProcessor implements IContentAssistProcessor {

  private static Image instructionImage;
  private static Image segmentImage;
  private static Image templateImage;

  static {
    instructionImage = new Image(Display.getCurrent(), Activator.getFilePathFromPlugin("comp_asm.gif"));
    segmentImage = new Image(Display.getCurrent(), Activator.getFilePathFromPlugin("comp_sgm.gif"));
    templateImage = new Image(Display.getCurrent(), Activator.getFilePathFromPlugin("comp_tmp.gif"));
  }


  /**
   * {@inheritDoc}
   */
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();

    int selectionOffset = offset;

    if (selection.getOffset() != offset) {
      selectionOffset = selection.getOffset();
    }

    String prefix = getPrefix(viewer, selectionOffset);
    Region region = new Region(selectionOffset - prefix.length(), prefix.length() + selection.getLength());

    ICompletionProposal[] keyWordsProposals = computeWordProposals(viewer, region, prefix, offset);

    return keyWordsProposals;
  }

  /**
   * Computes the word proposals.
   * 
   * @param viewer The viewer.
   * @param region The region.
   * @param prefix The prefix.
   * @param cursorOffset The offset of the cursor.
   * 
   * @return A list of the word proposal.
   */
  private ICompletionProposal[] computeWordProposals(ITextViewer viewer, Region region, String prefix, int cursorOffset) {
    ArrayList<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();

    int offset = region.getOffset();
    int count = 0;
    char lastchar = (prefix.length() < 1) ? ' ' : prefix.charAt(prefix.length() - 1);
    boolean small = ((lastchar < 'a') || (lastchar > 'z')) ? false : true;
    String smprefix = prefix.toLowerCase();
    String item = "";
    int i = 0;

    String[][] instructions = ASMInstructionSet.getInstructionArray();

    if (instructions != null) {
      for (i = 0; i < instructions.length; i++) {
        if (instructions[i][1].indexOf(smprefix) > -1) {
          item = small ? instructions[i][1] : instructions[i][0];
          proposalList.add(new CompletionProposal(item, offset, region.getLength(), item.length(), instructionImage,
                                                  item + " - " + instructions[i][2], null, null));
          count++;
        }
      }
    }

    String[][] segments = ASMInstructionSet.getSegmentArray();

    if (segments != null) {
      for (i = 0; i < segments.length; i++) {
        if (segments[i][1].indexOf(smprefix) > -1) {
          item = small ? segments[i][1] : segments[i][0];
          proposalList.add(new CompletionProposal(item, offset, region.getLength(), item.length(), segmentImage,
                                                  item + " - " + segments[i][2], null, null));
          count++;
        }
      }
    }

    TemplateContextType type = ASMEditor.getContextTypeRegistry().getContextType(Constants.ASM_EDITOR_CONTEXT);
    TemplateContext context = new DocumentTemplateContext(type, viewer.getDocument(), cursorOffset, 0);
    Region templateRegion = new Region(cursorOffset, 0);

    for (Template template : Activator.getTemplateStore().getTemplates()) {
      proposalList.add(new TemplateProposal(template, context, templateRegion, templateImage));
    }

    if (count < 1) {
      return null;
    }


    return proposalList.toArray(new ICompletionProposal[0]);
  }

  /**
   * Resturn the prefix.
   * 
   * @param viewer The viewer.
   * @param offset The offset.
   * 
   * @return The prefix.
   */
  private String getPrefix(ITextViewer viewer, int offset) {
    int i = offset;
    IDocument document = viewer.getDocument();

    if (i > document.getLength()) {
      return "";
    }

    try {
      while (i > 0) {
        char ch = document.getChar(i - 1);

        if (ch <= ' ') {
          break;
        }

        i--;
      }

      return document.get(i, offset - i);
    } catch (BadLocationException e) {
      return "";
    }
  }

  /**
   * {@inheritDoc}
   */
  public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public char[] getCompletionProposalAutoActivationCharacters() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public char[] getContextInformationAutoActivationCharacters() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public String getErrorMessage() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public IContextInformationValidator getContextInformationValidator() {
    return null;
  }
}
