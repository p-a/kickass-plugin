package org.lyllo.kickassplugin.popup.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class BuildpathAction implements IObjectActionDelegate {

	private Shell shell;
	private IProject project;
	
	/**
	 * Constructor for Action1.
	 */
	public BuildpathAction() {
		super();
	}
	
	private PreferenceDialog getPropertyDialog(String id, IAdaptable element) {
         return PreferencesUtil.createPropertyDialogOn(shell, element, id, null, null);
	}


	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
		shell = targetPart.getSite().getShell();
		ISelection selection = targetPart.getSite().getSelectionProvider().getSelection();
		Object element = ((IStructuredSelection)selection).getFirstElement();
		project= ((IResource)element).getProject();
		
	}

	public void run(IAction action) {
		PreferenceDialog propertyDialog = getPropertyDialog("org.lyllo.kickassplugin.buildPathPropertyPage", project);
		propertyDialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
