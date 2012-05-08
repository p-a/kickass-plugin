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
package org.lyllo.kickassplugin;

import java.util.Random;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Converts the class TextAttributes into a PreferenceStore compatible form.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public final class TextAttributeConverter {

  /**
   * Must not be instanciated.
   */
  private TextAttributeConverter() {
    // Must not be instanciated.
  }

  /**
   * Extract from String data the color value.
   * 
   * @param value The color as a String.
   * 
   * @return Color from String data
   */
  public static Color preferenceDataToColorAttribute(String value) {
    if (value == null || "".equals(value)) {
    	Random r = new Random(System.currentTimeMillis());
      return new Color(Display.getCurrent(), 128+r.nextInt()%128,  128+r.nextInt()%128,  128+r.nextInt()%128);
    }

    String[] data = value.split(",");
    int r, g, b;

    try {
      r = new Integer(data[0]).intValue();
      g = new Integer(data[1]).intValue();
      b = new Integer(data[2]).intValue();
    } catch (Exception e) {
      r = 0;
      g = 0;
      b = 0;
    }

    return new Color(Display.getCurrent(), r, g, b);
  }

  /**
   * Extract from String data the bold status value.
   * 
   * @param value The bold status value as a String.
   * 
   * @return Bold status from String data
   */
  public static boolean preferenceDataToBoldAttribute(String value) {
    if (value == null) {
      return false;
    }

    String[] data = value.split(",");

    if (data.length < 4) {
      return false;
    }

    return (data[3].startsWith("1")) ? true : false;
  }

  /**
   * Extract from String data the italic status value.
   * 
   * @param value Italic status as a String.
   * 
   * @return Italic status from String data
   */
  public static boolean preferenceDataToItalicAttribute(String value) {
    if (value == null) {
      return false;
    }

    String[] data = value.split(",");

    if (data.length < 5) {
      return false;
    }

    return (data[4].startsWith("1")) ? true : false;
  }

  /**
   * Extract from String data the text attribute value.
   * 
   * @param value Text attribute as a String.
   * 
   * @return Text attribute from String data
   */
  public static TextAttribute preferenceDataToTextAttribute(String value) {
    int style;
    Color color;

    style = (preferenceDataToBoldAttribute(value)) ? SWT.BOLD : SWT.NORMAL;
    if (preferenceDataToItalicAttribute(value)) {
      style |= SWT.ITALIC;
    }
    color = preferenceDataToColorAttribute(value);

    return new TextAttribute(color, null, style);
  }

  /**
   * Create String data from color, bold status and italic status values.
   * 
   * @param color The color to be converted.
   * @param bold The bold status.
   * @param italic The italic status.
   * 
   * @return String data from color, bold status and italic status values
   */
  public static String textAttributesToPreferenceData(Color color, boolean bold, boolean italic) {
    String sRed = Integer.toString(color.getRed());
    String sGreen = Integer.toString(color.getGreen());
    String sBlue = Integer.toString(color.getBlue());

    String sBold = (bold) ? "1" : "0";
    String sItalic = (italic) ? "1" : "0";

    return sRed + "," + sGreen + "," + sBlue + "," + sBold + "," + sItalic;
  }
}
