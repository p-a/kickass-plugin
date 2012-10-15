package org.lyllo.kickassplugin.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;


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

public class TriggerBuildAction extends Action {

	private IFileEditorInput input;

	public TriggerBuildAction(SourceViewer viewer, IEditorInput iEditorInput) {
		if (iEditorInput instanceof IFileEditorInput){
			input = (IFileEditorInput) iEditorInput;
		}
	}

	public void run() {
		if (input != null){
			try {
				input.getFile().touch(new NullProgressMonitor());
			} catch (CoreException e) {
			}
		}
	}

}

