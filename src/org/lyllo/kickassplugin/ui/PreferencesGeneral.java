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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.model.WorkbenchViewerSorter;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.Messages;
import org.lyllo.kickassplugin.TextAttributeConverter;


/**
 * Preferences Page for Syntax Highlight.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class PreferencesGeneral extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * The keys of the overlay store.
	 */
	private final String[][] fSyntaxColorListModel = new String[][] {
			{ Messages.TEXTCOLOR_STRING_NAME,
				Constants.PREFERENCES_TEXTCOLOR_STRING },
				{ Messages.TEXTCOLOR_COMMENT_NAME,
					Constants.PREFERENCES_TEXTCOLOR_COMMENT },
					{ Messages.TEXTCOLOR_INSTRUCTION_NAME,
						Constants.PREFERENCES_TEXTCOLOR_INSTRUCTION },
						{ Messages.TEXTCOLOR_SEGMENT_NAME,
							Constants.PREFERENCES_TEXTCOLOR_SEGMENT },
							{ Messages.TEXTCOLOR_FUNCTION_NAME,
								Constants.PREFERENCES_TEXTCOLOR_FUNCTION }
							,
							{ Messages.TEXTCOLOR_CONSTANT_NAME,
								Constants.PREFERENCES_TEXTCOLOR_CONSTANT }
							,
							{ Messages.TEXTCOLOR_CLASS_NAME,
								Constants.PREFERENCES_TEXTCOLOR_CLASS }

	};

	private ColorSelector fSyntaxForegroundColorEditor;

	/**
	 * Check box for bold preference.
	 */
	private Button fBoldCheckBox;

	/**
	 * Check box for italic preference.
	 */
	private Button fItalicCheckBox;

	/**
	 * List for text attribute items.
	 */
	private final List<HighlightingColorListItem> fHighlightingColorList = new ArrayList<HighlightingColorListItem>();

	/**
	 * Highlighting color list viewer
	 */
	private TableViewer fHighlightingColorListViewer;

	/**
	 * Creates a new preference page.
	 */
	public PreferencesGeneral() {
		noDefaultAndApplyButton();

		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * Update controls after item select.
	 */
	private void handleSyntaxColorListSelection() {
		HighlightingColorListItem item = getHighlightingColorListItem();

		if (item == null)
			return;
		
		Color color = null;
		boolean bold = false;
		boolean italic = false;

		if (!item.hasChanged()) {
			String data = Activator.getDefault().getPreferenceStore().getString(item.getItemKey());
			color = TextAttributeConverter.preferenceDataToColorAttribute(data);
			bold = TextAttributeConverter.preferenceDataToBoldAttribute(data);
			italic = TextAttributeConverter.preferenceDataToItalicAttribute(data);
		} else {
			color = item.getItemColor();
			bold = item.getItemBold();
			italic = item.getItemItalic();
		}

		fSyntaxForegroundColorEditor.setColorValue(color.getRGB());
		fBoldCheckBox.setSelection(bold);
		fItalicCheckBox.setSelection(italic);

		fSyntaxForegroundColorEditor.getButton().setEnabled(true);
		fBoldCheckBox.setEnabled(true);
		fItalicCheckBox.setEnabled(true);
	}

	/**
	 * Create all visual controls.
	 * 
	 * @param parent The parent object.
	 * 
	 * @return A Control object.
	 */
	private Control createSyntaxPage(Composite parent) {
		Label title = new Label(parent, SWT.LEFT);
		title.setText(Messages.SYNTAXHIGHLIGHT_TITLE);
		title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(parent, SWT.LEFT);
		label.setText(Messages.FOREGROUNDCOLOR_TEXT + ":");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite editorComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		editorComposite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		editorComposite.setLayoutData(gd);

		fHighlightingColorListViewer = new TableViewer(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER
				| SWT.FULL_SELECTION);
		fHighlightingColorListViewer.setLabelProvider(new ColorListLabelProvider());
		fHighlightingColorListViewer.setContentProvider(new ColorListContentProvider());
		fHighlightingColorListViewer.setSorter(new WorkbenchViewerSorter());
		gd = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
		gd.heightHint = convertHeightInCharsToPixels(5);
		fHighlightingColorListViewer.getControl().setLayoutData(gd);

		Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.horizontalSpan = 2;

		label = new Label(stylesComposite, SWT.LEFT);
		label.setText(Messages.COLOR_TEXT + ":");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = 20;
		label.setLayoutData(gd);

		fSyntaxForegroundColorEditor = new ColorSelector(stylesComposite);
		Button foregroundColorButton = fSyntaxForegroundColorEditor.getButton();
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		foregroundColorButton.setLayoutData(gd);

		fBoldCheckBox = new Button(stylesComposite, SWT.CHECK);
		fBoldCheckBox.setText(Messages.BOLD_TEXT);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = 20;
		gd.horizontalSpan = 2;
		fBoldCheckBox.setLayoutData(gd);

		fItalicCheckBox = new Button(stylesComposite, SWT.CHECK);
		fItalicCheckBox.setText(Messages.ITALIC_TEXT);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = 20;
		gd.horizontalSpan = 2;
		fItalicCheckBox.setLayoutData(gd);

		fHighlightingColorListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSyntaxColorListSelection();
			}
		});

		foregroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item = getHighlightingColorListItem();
				item.setItemColor(fSyntaxForegroundColorEditor.getColorValue());
				fHighlightingColorListViewer.setInput(fHighlightingColorList);
			}
		});

		fBoldCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item = getHighlightingColorListItem();
				item.setItemBold(fBoldCheckBox.getSelection());
			}
		});

		fItalicCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item = getHighlightingColorListItem();
				item.setItemItalic(fItalicCheckBox.getSelection());
			}
		});

		parent.layout();

		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));

		createSyntaxPage(contents);

		initialize();

		Dialog.applyDialogFont(contents);
		return contents;
	}

	/**
	 * Setup the text attribute list.
	 */
	private void initialize() {
		String data = "";
		Color color = null;
		boolean bold = false;
		boolean italic = false;

		for (int i = 0, n = fSyntaxColorListModel.length; i < n; i++) {
			data = Activator.getDefault().getPreferenceStore().getString(fSyntaxColorListModel[i][1]);
			color = TextAttributeConverter.preferenceDataToColorAttribute(data);
			bold = TextAttributeConverter.preferenceDataToBoldAttribute(data);
			italic = TextAttributeConverter.preferenceDataToItalicAttribute(data);
			fHighlightingColorList.add(new HighlightingColorListItem(fSyntaxColorListModel[i][0],
					fSyntaxColorListModel[i][1], color, bold, italic));
		}

		fHighlightingColorListViewer.setInput(fHighlightingColorList);
		fHighlightingColorListViewer.setSelection(new StructuredSelection(fHighlightingColorListViewer.getElementAt(0)));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean performOk() {
		saveChanges();

		return super.performOk();
	}

	/**
	 * Save all changes to PreferencesStore.
	 */
	private void saveChanges() {
		String data = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean changes = false;

		for (HighlightingColorListItem hcli : fHighlightingColorList) {
			if (hcli.hasChanged()) {
				data = TextAttributeConverter.textAttributesToPreferenceData(hcli.getItemColor(), hcli.getItemBold(),
						hcli.getItemItalic());
				store.setValue(hcli.getItemKey(), data);
				changes = true;
			}
		}

		if (changes) {
			Activator.getDefault().savePluginPreferences();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Returns the current highlighting color list item.
	 * 
	 * @return the current highlighting color list item
	 */
	private HighlightingColorListItem getHighlightingColorListItem() {
		IStructuredSelection selection = (IStructuredSelection) fHighlightingColorListViewer.getSelection();
		return (HighlightingColorListItem) selection.getFirstElement();
	}

	/**
	 * Item in the highlighting color list.
	 */
	private static class HighlightingColorListItem {

		/** Display name */
		private String fDisplayName;

		/** Color preference key */
		private String fItemKey;

		/** Item color */
		private Color fItemColor;

		/** Item bold */
		private boolean fItemBold;

		/** Item italic */
		private boolean fItemItalic;

		/** Item value changes */
		private boolean change = false;

		/**
		 * Initialize the item with the given values.
		 * 
		 * @param displayName the display name
		 * @param itemKey the color preference key
		 */
		public HighlightingColorListItem(String displayName, String itemKey) {
			fDisplayName = displayName;
			fItemKey = itemKey;
			fItemColor = new Color(Display.getCurrent(), 0, 0, 0);
			fItemBold = false;
			fItemItalic = false;
		}

		/**
		 * Initialize the item with the given values.
		 * 
		 * @param displayName the display name
		 * @param itemKey the color preference key
		 * @param itemColor the item color
		 * @param itemBold the item bold
		 * @param itemItalic the item italic
		 */
		public HighlightingColorListItem(String displayName, String itemKey, Color itemColor, boolean itemBold,
				boolean itemItalic) {
			fDisplayName = displayName;
			fItemKey = itemKey;
			fItemColor = itemColor;
			fItemBold = itemBold;
			fItemItalic = itemItalic;
		}

		/**
		 * @return the color preference key
		 */
		public String getItemKey() {
			return fItemKey;
		}

		/**
		 * @return the display name
		 */
		public String getDisplayName() {
			return fDisplayName;
		}

		/**
		 * @return the color
		 */
		public Color getItemColor() {
			return fItemColor;
		}

		/**
		 * @return the bold status
		 */
		public boolean getItemBold() {
			return fItemBold;
		}

		/**
		 * @return the italic status
		 */
		public boolean getItemItalic() {
			return fItemItalic;
		}

		/**
		 * Set the Item color
		 * 
		 * @param color The Color to be set.
		 */
		public void setItemColor(Color color) {
			fItemColor = color;
			change = true;
		}

		/**
		 * Set the Item color
		 * 
		 * @param rgb The RGB value to be set.
		 */
		public void setItemColor(RGB rgb) {
			fItemColor = new Color(Display.getCurrent(), rgb);
			change = true;
		}

		/**
		 * Set the Item bold status
		 * 
		 * @param bold The bold status to be set.
		 */
		public void setItemBold(boolean bold) {
			fItemBold = bold;
			change = true;
		}

		/**
		 * Set the Item italic status
		 * 
		 * @param italic The italic status to be set.
		 */
		public void setItemItalic(boolean italic) {
			fItemItalic = italic;
			change = true;
		}

		/**
		 * @return change value
		 */
		public boolean hasChanged() {
			return change;
		}
	}

	/**
	 * Color list label provider.
	 */
	private class ColorListLabelProvider extends LabelProvider implements IColorProvider {

		/**
		 * {@inheritDoc}
		 */
		public String getText(Object element) {
			return ((HighlightingColorListItem) element).getDisplayName();
		}

		/**
		 * {@inheritDoc}
		 */
		public Color getForeground(Object element) {
			return ((HighlightingColorListItem) element).getItemColor();
		}

		/**
		 * {@inheritDoc}
		 */
		public Color getBackground(Object element) {
			return null;
		}
	}

	/**
	 * Color list content provider.
	 */
	private class ColorListContentProvider implements IStructuredContentProvider {

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
