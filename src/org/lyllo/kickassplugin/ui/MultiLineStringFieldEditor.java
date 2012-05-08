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

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A multi line StringFieldEditor.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class MultiLineStringFieldEditor extends StringFieldEditor {

  private Text textFieldML = null;

  private int validateStrategyML = VALIDATE_ON_KEY_STROKE;

  private int textLimitML = UNLIMITED;

  /**
   * Creates a multi-line string field editor. Use the method
   * <code>setTextLimit</code> to limit the text.
   * 
   * @param name the name of the preference this field editor works on
   * @param labelText the label text of the field editor
   * @param width the width of the text input field in characters, or
   *          <code>UNLIMITED</code> for no limit
   * @param strategy either <code>VALIDATE_ON_KEY_STROKE</code> to perform on
   *          the fly checking (the default), or
   *          <code>VALIDATE_ON_FOCUS_LOST</code> to perform validation only
   *          after the text has been typed in
   * @param parent the parent of the field editor's control
   */
  public MultiLineStringFieldEditor(String name, String labelText, int width, int strategy, Composite parent) {
    super(name, labelText, width, strategy, parent);
    setValidateStrategy(strategy);
  }

  /**
   * Creates a multi-line string field editor. Use the method
   * <code>setTextLimit</code> to limit the text.
   * 
   * @param name the name of the preference this field editor works on
   * @param labelText the label text of the field editor
   * @param width the width of the text input field in characters, or
   *          <code>UNLIMITED</code> for no limit
   * @param parent the parent of the field editor's control
   */
  public MultiLineStringFieldEditor(String name, String labelText, int width, Composite parent) {
    this(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
  }

  /**
   * Creates a multi-line string field editor of unlimited width. Use the method
   * <code>setTextLimit</code> to limit the text.
   * 
   * @param name the name of the preference this field editor works on
   * @param labelText the label text of the field editor
   * @param parent the parent of the field editor's control
   */
  public MultiLineStringFieldEditor(String name, String labelText, Composite parent) {
    this(name, labelText, UNLIMITED, parent);
  }

  /**
   * {@inheritDoc}
   */
  public void setValidateStrategy(int value) {
    super.setValidateStrategy(value);
    Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST || value == VALIDATE_ON_KEY_STROKE);
    validateStrategyML = value;
  }

  /**
   * {@inheritDoc}
   */
  public void setTextLimit(int limit) {
    super.setTextLimit(limit);
    textLimitML = limit;
  }

  /**
   * {@inheritDoc}
   */
  public Text getTextControl(Composite parent) {
    textFieldML = super.getTextControl();
    if (textFieldML == null) {
      textFieldML = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.LEFT | SWT.V_SCROLL);
      textFieldML.setFont(parent.getFont());
      switch (validateStrategyML) {
      case VALIDATE_ON_KEY_STROKE:
        textFieldML.addKeyListener(new KeyAdapter() {

          /*
           * (non-Javadoc)
           * 
           * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
           */
          public void keyReleased(KeyEvent e) {
            valueChanged();
          }
        });

        break;
      case VALIDATE_ON_FOCUS_LOST:
        textFieldML.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            clearErrorMessage();
          }
        });
        textFieldML.addFocusListener(new FocusAdapter() {
          public void focusGained(FocusEvent e) {
            refreshValidState();
          }

          public void focusLost(FocusEvent e) {
            valueChanged();
            clearErrorMessage();
          }
        });
        break;
      default:
        Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
      }
      textFieldML.addDisposeListener(new DisposeListener() {
        public void widgetDisposed(DisposeEvent event) {
          textFieldML = null;
        }
      });
      if (textLimitML > 0) {// Only set limits above 0 - see SWT spec
        textFieldML.setTextLimit(textLimitML);
      }
    } else {
      checkParent(textFieldML, parent);
    }
    return textFieldML;
  }

  /**
   * {@inheritDoc}
   */
  protected void doFillIntoGrid(Composite parent, int numColumns) {
    super.doFillIntoGrid(parent, numColumns);

    textFieldML = super.getTextControl();
    GridData gd = (GridData) textFieldML.getLayoutData();
    gd.verticalAlignment = GridData.FILL;
    gd.grabExcessVerticalSpace = true;
    textFieldML.setLayoutData(gd);

    Label label = getLabelControl(parent);
    gd = new GridData();
    gd.verticalAlignment = SWT.TOP;
    label.setLayoutData(gd);
  }
  
  @Override
	protected boolean doCheckState() {
		return super.getStringValue().matches("(\\s*\\S+\\s*=\\s*\\S+\\s*\\n?)*");
	}
}
