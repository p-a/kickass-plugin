package org.lyllo.kickassplugin.ui;


import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.prefs.ProjectPrefenceHelper;

public class BuildPathPropertyPage extends PropertyPage {

	private ScopedPreferenceStore store;
	private PathEditor srcPath;
	private PathEditor libPath;
	private DirectoryFieldEditor buildPath;

	@Override
	protected Control createContents(Composite parent) {

		final IProject project = (IProject) super.getElement();

		store = ProjectPrefenceHelper.getStore(project); 

		Composite buildPathComp = new Composite(parent, SWT.NONE);
		buildPathComp.setLayout(new GridLayout(3, false));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		buildPathComp.setLayoutData(gridData);
		
		final String projectPath = project.getLocation().toOSString().toLowerCase();

		buildPath = new DirectoryFieldEditor(Constants.PROJECT_PREFS_BUILD_DIRECTORY_KEY, "Output folder",  buildPathComp){
			
			@Override
			public void setStringValue(String val) {
				if (val.toLowerCase().startsWith(projectPath)){
					val = val.substring(projectPath.length());
					if (val.startsWith(File.separator)){
						val = val.substring(File.separator.length());
					}
				} 
				super.setStringValue(val);
			}
			
		};
		
		buildPath.setEmptyStringAllowed(false);
		buildPath.setPreferenceStore(store);
		buildPath.load();

		Composite comp = new Composite(parent, SWT.NONE);
		org.eclipse.swt.layout.GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		srcPath = new PathEditor(Constants.PROJECT_PREFS_SRC_DIRECTORY_KEY, "Source folders", "Choose", comp){
			@Override
			protected String getNewInputObject() {

				String val = super.getNewInputObject();
				if (val.toLowerCase().startsWith(projectPath)){
					val = val.substring(projectPath.length());
					if (val.startsWith(File.separator)){
						val = val.substring(File.separator.length());
					}
				} else {
					return null;
				}
				
				String[] items = this.getList().getItems();
				for (String item: items){
					if (item.equals(val)){
						return null;
					}
				}
				
				return val;
			}
			
		};

		srcPath.setPreferenceStore(store);
		srcPath.load();

		libPath = new PathEditor(Constants.PROJECT_PREFS_LIBDIR_DIRECTORY_KEY, "Library paths", "Choose", comp) {
			@Override
			protected String getNewInputObject() {

				String val = super.getNewInputObject();

				if (val.toLowerCase().startsWith(projectPath)){
					val = val.substring(projectPath.length());
					if (val.startsWith(File.separator)){
						val = val.substring(File.separator.length());
					}
				} else {
					return null;
				}
				
				String[] items = this.getList().getItems();
				for (String item: items){
					if (item.equals(val)){
						return null;
					}
				}
				
				return val;
			}

		};
		
		libPath.setPreferenceStore(store);
		libPath.load();


		return parent;
	}


	@Override
	protected void performApply() {
		doStore();
		super.performApply();
	}

	private void doStore() {
		srcPath.store();
		libPath.store();
		buildPath.store();
		try {
			store.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean performOk() {
		doStore();
		return super.performOk();
	}

}
