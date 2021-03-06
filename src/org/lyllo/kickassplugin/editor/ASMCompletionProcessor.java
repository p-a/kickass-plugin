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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
	private static Image macroImage;
	private static Image defaultImage;
	private static Image procedureImage;
	private static List<String> kickassKeywords;
	private static List<String> asminstructions;

	static {
		labelImage = new Image(Display.getCurrent(),  Activator.getFilePathFromPlugin("tree_label.gif"));
		macroImage = new Image(Display.getCurrent(), Activator.getFilePathFromPlugin("tree_macro.gif"));
		defaultImage = new Image(Display.getCurrent(), Activator.getFilePathFromPlugin("tree_default.gif"));
		procedureImage = new Image(Display.getCurrent(), Activator.getFilePathFromPlugin("tree_procedure.gif"));
		kickassKeywords = new ArrayList<String>();
		for (String key : ASMInstructionSet.getSegments().keySet()){
			kickassKeywords.add(key.substring(1)+" ");
		}
		asminstructions = new ArrayList<String>();
		for (String key : ASMInstructionSet.getInstructions().keySet()){
			asminstructions.add(key+" ");
		}
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

		Prefix prefixType = getPrefix(viewer, selectionOffset);

		String prefix = prefixType.prefix;

		Region region = new Region(selectionOffset - prefix.length(), prefix.length() + selection.getLength());

		ICompletionProposal[] keyWordsProposals = computeWordProposals(viewer, region, prefixType, offset);

		return keyWordsProposals;
	}

	/**
	 * Computes the word proposals.
	 * 
	 * @param viewer The viewer.
	 * @param region The region.
	 * @param prefixType The prefix.
	 * @param cursorOffset The offset of the cursor.
	 * 
	 * @return A list of the word proposal.
	 */
	private ICompletionProposal[] computeWordProposals(ITextViewer viewer, Region region, Prefix prefixType, int cursorOffset) {
		ArrayList<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();

		int offset = region.getOffset();
		String smprefix = prefixType.prefix.toLowerCase();

		ASMContentOutlinePage outline = editor != null ? editor.getOutline() : null;
		/* Add things from outline, which is constantly updated */
		if (prefixType.type != PrefixType.KickAssKeyword && outline != null){
			addToProposalList(region, proposalList, offset,
					smprefix, outline.getLabels(),null,labelImage);
		}

		if (prefixType.type == PrefixType.Unknown) {
			if (outline != null){
				List<String> macros = new ArrayList<String>();
				List<String> tempMacros = outline.getMacros();
				for (String m: tempMacros)
					macros.add(":"+m);

				addToProposalList(region, proposalList, offset,
					smprefix, macros ,null,macroImage);
			}

			addToProposalList(region, proposalList, offset,
					smprefix, asminstructions
					,null,defaultImage);

			if (outline != null){
				addToProposalList(region, proposalList, offset,
					smprefix, outline.getConsts() ,null,defaultImage);
			}
			
			addToProposalList(region, proposalList, offset,
					smprefix, ASMInstructionSet.getConstants().keySet()
					,null,defaultImage);

			if (outline != null){
				addToProposalList(region, proposalList, offset,
					smprefix, outline.getFunctions() ,null,procedureImage);
			}
			
			addToProposalList(region, proposalList, offset,
					smprefix, ASMInstructionSet.getFunctions().keySet()
					,null,defaultImage);

			IFile currentIFile = getCurrentIFile();
			if (currentIFile == null)
				return null;

			IProject project = currentIFile.getProject();

			addDependencyItems(region, proposalList, offset, smprefix,prefixType,
					currentIFile, project, new HashSet<String>());

		} else if (prefixType.type == PrefixType.KickAssKeyword) {
			addToProposalList(region, proposalList, offset,
					smprefix, kickassKeywords
					,null,defaultImage);

		}


		if (proposalList.isEmpty()) {
			return null;
		}

		return proposalList.toArray(new ICompletionProposal[0]);
	}

	@SuppressWarnings("unchecked")
	protected void addDependencyItems(Region region,
			ArrayList<ICompletionProposal> proposalList, int offset,
			String smprefix, Prefix prefixType, IFile currentIFile, IProject project, HashSet<String> visited) {

		Set<String> imports = null;
		try {
			imports = (Set<String>) currentIFile.getSessionProperty(Constants.IMPORTS_SESSION_KEY);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if (imports != null){

			Iterator<String> it = imports.iterator();
			while (it.hasNext()){
				String filename = it.next();
				IResource member = project.findMember(filename);
				if (member.getType() == IResource.FILE && member != null && member.exists() && member.isAccessible()){
					IFile file = (IFile) member;
					if (!visited.contains(file.getLocation().toOSString())){
						visited.add(file.getLocation().toOSString());
						addToProposalList(region, proposalList, offset, smprefix, prefixType, file );
						//Recurse
						addDependencyItems(region, proposalList, offset, smprefix, prefixType, file, project, visited);
					}
				} else {
					it.remove();
				}
			}
		}
	}

	private void addToProposalList(Region region,
			ArrayList<ICompletionProposal> proposalList, int offset, String smprefix, Prefix prefixType, IFile file) {

		try {
			@SuppressWarnings("unchecked")
			List<String> labels = (List<String>) file.getSessionProperty(Constants.LABELS_SESSION_KEY);
			addToProposalList(region, proposalList, offset, smprefix, labels, " ["+file.getName()+"]",labelImage);

			if (prefixType.type == PrefixType.Label)
				return;
			@SuppressWarnings("unchecked")
			List<String> macros = (List<String>) file.getSessionProperty(Constants.MACROS_SESSION_KEY);
			addToProposalList(region, proposalList, offset, smprefix, macros, " ["+file.getName()+"]",macroImage);

			@SuppressWarnings("unchecked")
			List<String> constig = (List<String>) file.getSessionProperty(Constants.CONST_SESSION_KEY);
			addToProposalList(region, proposalList, offset, smprefix, constig, " ["+file.getName()+"]",defaultImage);

			@SuppressWarnings("unchecked")
			List<String> functions = (List<String>) file.getSessionProperty(Constants.FUNCTIONS_SESSION_KEY);
			addToProposalList(region, proposalList, offset, smprefix, functions, " ["+file.getName()+"]",procedureImage);


		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	protected void addToProposalList(Region region,
			ArrayList<ICompletionProposal> proposalList, int offset, 
			String smprefix, Collection<String> labels, String key, Image image) {

		if (labels == null)
			return;

		for (String label: labels){

			if (label.toLowerCase().startsWith(smprefix)){

				int cursorpos = label.indexOf('(')+1;
				if (cursorpos == 0 || label.indexOf(')') == cursorpos){
					cursorpos = label.length();
				} 

				proposalList.add(new CompletionProposal(label, offset, region.getLength(), cursorpos, image,
						label + (key == null ? "" :  key), null, null) );
			}
		}
	}

	/**
	 * Resturn the prefix.
	 * 
	 * @param viewer The viewer.
	 * @param offset The offset.
	 * 
	 * @return The prefix.
	 */
	private Prefix getPrefix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();

		Prefix prefix = new Prefix();
		prefix.prefix = "";
		prefix.type = PrefixType.Unknown;

		if (i > document.getLength()) {
			return prefix;
		}

		try {
			while (i > 0) {
				char ch = document.getChar(i - 1);

				if (ch <= ' ' || ch == '<' || ch == '>' || ch == '#' || ch == '!' || ch == '(' || ch == '[' || ch == '\t' || ch == '&' || ch == '|' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^')
				{
					break;
				}

				if (i >1 && ch == '.' && Character.isLetterOrDigit(document.getChar(i-2)) ) {

					prefix.type = PrefixType.Label;
					break;
				}
				if (ch =='.' ) {
					prefix.type = PrefixType.KickAssKeyword;
					break;
				}
				i--;
			}

			prefix.prefix = document.get(i, offset - i);
		} catch (BadLocationException e) {
		}

		return prefix;
	}

	enum PrefixType {
		Label,
		Unknown,
		KickAssKeyword
	}	

	private final class Prefix {

		String prefix = "";
		PrefixType type = PrefixType.Unknown;

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
