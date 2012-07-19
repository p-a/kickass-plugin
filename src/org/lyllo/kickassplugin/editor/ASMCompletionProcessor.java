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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;


/**
 * Class for content assist. Create the content assist list.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class ASMCompletionProcessor implements IContentAssistProcessor {

	private static Image labelImage;

	static {
		labelImage = new Image(Display.getCurrent(),  Activator.getFilePathFromPlugin("tree_label.gif"));
	}

	private ASMEditor editor;


	public ASMCompletionProcessor(ASMEditor editor) {
		this.editor = editor;
	}

	private static IFile getCurrentIFile(){
		IWorkbenchWindow win = PlatformUI.getWorkbench
				().getActiveWorkbenchWindow();

		IWorkbenchPage page = win.getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null) {
				IEditorInput input = editor.getEditorInput();
				if (input instanceof IFileEditorInput) {
					return ((IFileEditorInput)input).getFile();
				}
			}
		}
		return null;
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
		String smprefix = prefix.toLowerCase();
		String item = "";

		count = addToProposalList(region, proposalList, offset, count,
				smprefix, item, editor.getOutline().getLabels(),null);

		List<String> macros = new ArrayList<String>();
		{
			List<String> tempMacros = editor.getOutline().getMacros();
			for (String m: tempMacros)
				macros.add(":"+m);
		}
		count = addToProposalList(region, proposalList, offset, count,
				smprefix, item, macros ,null);

		IFile currentIFile = getCurrentIFile();
		String projectName = currentIFile.getProject().getName();
		if (currentIFile != null){
			
			Map<String,List<String>> temp = Activator.getDefault().getAutoCompletionCollector().getLabelsForProject(
					currentIFile.getProject());
			if (temp != null){
				for (String key: temp.keySet()){
					if (!key.equals(currentIFile.getLocation().toOSString())){
						int dot = key.indexOf('.');
						if (dot > -1 && !key.endsWith(".")){
							String ext = key.substring(dot+1);
							if (Constants.EXTENSION_PATTERN_INCLUDES.matcher(ext).matches()){
								List<String> list = temp.get(key);
								key = " [" + key.substring(key.indexOf(projectName)+projectName.length()+1)+"]";
								count += addToProposalList(region, proposalList, offset, count, smprefix, item, list, key);
							}
						}

					}
				}
			}

		}

		if (count == 0) {
			return null;
		}


		return proposalList.toArray(new ICompletionProposal[0]);
	}

	protected int addToProposalList(Region region,
			ArrayList<ICompletionProposal> proposalList, int offset, int count,
			String smprefix, String item, List<String> labels, String key) {
		for (String label: labels){

			if (label.toLowerCase().startsWith(smprefix)){
				proposalList.add(new CompletionProposal(label, offset, region.getLength(), item.length(), labelImage,
						label + (key == null ? "" :  key), null, null));
				count++;

			}
		}
		return count;
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

				if (ch <= ' ' || ch == '<' || ch == '>' || ch == '#') {
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
