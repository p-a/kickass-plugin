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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.TextAttributeConverter;


/**
 * Default RuleBaseScanner.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class PropertyChangeRuleBaseScanner extends RuleBasedScanner implements IPropertyChangeListener {

  /** Default Token for the text attributes * */
  private Token defToken;

  /** Editor need for refresh * */
  private ASMEditor editor;

  /** Key for preference store * */
  private String preferencesKey;

  /**
   * Constructor of PropertyChangeRuleBaseScanner
   * 
   * @param editor The given Editor.
   * @param preferencesKey The preference key to be listen on.
   */
  public PropertyChangeRuleBaseScanner(final ASMEditor editor, final String preferencesKey) {
    this.editor = editor;
    this.preferencesKey = preferencesKey;

    IPreferenceStore store = Activator.getDefault().getPreferenceStore();

    defToken = new Token(TextAttributeConverter.preferenceDataToTextAttribute(store.getString(preferencesKey)));

    super.setDefaultReturnToken(defToken);

    Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
  }

  /**
   * Remove rule scanner from property listener.
   */
  public void dispose() {
    Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
  }

  /**
   * {@inheritDoc}
   */
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getProperty().equals(preferencesKey)) {
      defToken.setData(TextAttributeConverter.preferenceDataToTextAttribute((String) event.getNewValue()));
    }

    editor.refreshSourceViewer();
  }
}
