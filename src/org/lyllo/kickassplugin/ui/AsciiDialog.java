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

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Messages;


/**
 * Dialog to show the user a table with all ascii-codes.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class AsciiDialog extends Dialog {

  private static final int MAX_CHARS = 256;

  // Table for ASCII Chars
  private Table table = null;

  // Names for each of the columns
  private static final String[] COLUMN_NAMES = { "Char", "Dec", "Hex", "Oct", "Bin", "Name" };

  // The names of the first 32 characters
  private static final String[] CHAR_NAMES = { "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "TAB",
                                              "LF", "VT", "FF", "CR", "SO", "SI", "DLE", "DC1", "DC2", "DC3", "DC4",
                                              "NAK", "SYN", "ETB", "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US",
                                              "Space" };

  // The font to use for displaying characters
  private Font font;

  /**
   * The constructor.
   * 
   * @param parent Parent of the Dialog.
   */
  public AsciiDialog(Shell parent) {
    this(parent, SWT.DIALOG_TRIM | SWT.MIN);
  }

  /**
   * The constructor.
   * 
   * @param parent Parent of the Dialog.
   * @param style Style of the Dialog.
   */
  public AsciiDialog(Shell parent, int style) {
    super(parent, style);
    setText("ASCII Table");
  }

  /**
   * Opens the Dialog.
   */
  public void open() {
    Shell shell = new Shell(getParent(), getStyle());
    shell.setText(getText());

    createContents(shell);

    shell.pack();
    Rectangle rect = shell.getBounds();
    shell.setBounds(rect.x, rect.y, rect.width, 400);
    shell.open();

    Display display = getParent().getDisplay();

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }

    if (font != null) {
      font.dispose();
    }
  }

  /**
   * Create all contents.
   * 
   * @param shell The parent shell.
   */
  private void createContents(final Shell shell) {
    GridLayout gl = new GridLayout();
    gl.numColumns = 2;
    shell.setLayout(gl);

    font = new Font(Display.getCurrent(), "Monospaced", 12, SWT.BOLD);

    // Create a table with visible headers and lines
    table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 2;
    table.setLayoutData(gd);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    table.setRedraw(false);
    table.setFont(font);

    // Create the columns
    TableColumn[] columns = createColumns();
    String charmap = Activator.getFilePathFromPlugin("char850.gif");
    Image img = new Image(Display.getCurrent(), charmap);
    ImageData id = img.getImageData();
    int c;

    for (int i = 0; i < MAX_CHARS; i++) {
      // Create the row in the table by creating
      // a TableItem and setting text for each
      // column
      c = 0;

      TableItem item = new TableItem(table, SWT.NONE);

      if ((i < 33) || (i > 254)) {
        img = new Image(Display.getCurrent(), getAsciiPicture(id, 255));
      } else {
        img = new Image(Display.getCurrent(), getAsciiPicture(id, i));
      }

      item.setImage(c++, img);
      item.setText(c++, Integer.toString(i));
      item.setText(c++, fillZero(Integer.toHexString(i).toUpperCase(), 2));
      item.setText(c++, Integer.toOctalString(i));
      item.setText(c++, fillZero(Integer.toBinaryString(i), 8));
      item.setText(c++, i < CHAR_NAMES.length ? CHAR_NAMES[i] : "");
    }

    // Now that we've set the text into the columns,
    // we call pack() on each one to size it to the
    // contents.
    for (int i = 0, n = columns.length; i < n; i++) {
      columns[i].pack();
    }

    // Set redraw back to true so that the table
    // will paint appropriately
    table.setRedraw(true);

    Button okBtn = new Button(shell, SWT.BORDER | SWT.PUSH);
    okBtn.setText(Messages.OK_LABEL);
    okBtn.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        shell.dispose();
      }
    });

    Button copyBtn = new Button(shell, SWT.BORDER | SWT.PUSH);
    copyBtn.setText(Messages.COPY_LABEL);
    copyBtn.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        String text = "";
        TableItem[] selection = table.getSelection();
        int c = 0;

        for (int i = 0; i < selection.length; i++) {
          if (text.length() > 0) {
            text += "\n";
          }

          c = Integer.parseInt(selection[i].getText(1));

          if ((c > 32) && (c < 128)) {
            text += Character.toString((char) c) + ", ";
          }

          text += selection[i].getText(1) + ", " + selection[i].getText(2) + ", " + selection[i].getText(3) + ", "
                  + selection[i].getText(4);

          if (selection[i].getText(5).length() > 0) {
            text += ", " + selection[i].getText(5);
          }
        }

        new Clipboard(Display.getCurrent()).setContents(new Object[] { text },
                                                        new Transfer[] { TextTransfer.getInstance() });
      }
    });
  }

  /**
   * Creates the columns for the table
   * 
   * @return TableColumn[]
   */
  private TableColumn[] createColumns() {
    TableColumn[] columns = new TableColumn[COLUMN_NAMES.length];

    for (int i = 0, n = columns.length; i < n; i++) {
      // Create the TableColumn with right alignment
      columns[i] = new TableColumn(table, SWT.RIGHT);

      // This text will appear in the column header
      columns[i].setText(COLUMN_NAMES[i]);
    }
    return columns;
  }

  /**
   * Returns the ascii picture.
   * 
   * @param idin given data.
   * @param pos given pos.
   * 
   * @return The ascii picture.
   */
  private ImageData getAsciiPicture(ImageData idin, int pos) {
    int tpos, x, y, left, top;

    tpos = pos - 32;
    if (tpos < 1) {
      tpos = 1;
    } else if (tpos > 223) {
      tpos = 223;
    }

    y = (int) Math.ceil((double) tpos / 20.0d);
    x = tpos - ((y - 1) * 20);

    left = 2 + ((x - 1) * 19);
    top = 2 + ((y - 1) * 25);

    return getSubImage(idin, left, top, 16, 22);
  }

  /**
   * Gets an Image from the specified parameters.
   * 
   * @param idin The Image.
   * @param left Left position of Sub-Image.
   * @param top Top position of Sub-Image.
   * @param width Width of Sub-Image.
   * @param height Hight of Sub-Image.
   * 
   * @return Teh Image.
   */
  private ImageData getSubImage(ImageData idin, int left, int top, int width, int height) {
    int bytesPerPixel = idin.bytesPerLine / idin.width;
    int destBytesPerLine = width * bytesPerPixel;
    byte[] newData = new byte[width * height * bytesPerPixel];
    int srcIndex, destIndex;

    for (int srcX = left; srcX < (left + width); srcX++) {
      for (int srcY = top; srcY < (top + height); srcY++) {
        destIndex = ((srcY - top) * destBytesPerLine) + ((srcX - left) * bytesPerPixel);
        srcIndex = (srcY * idin.bytesPerLine) + (srcX * bytesPerPixel);

        System.arraycopy(idin.data, srcIndex, newData, destIndex, bytesPerPixel);
      }
    }

    return new ImageData(width, height, idin.depth, idin.palette, destBytesPerLine, newData);
  }

  /**
   * Fills zeros into textnumbers.
   * 
   * @param textnumber The String.
   * @param len The absolute length.
   * 
   * @return The new String, filled with zeros.
   */
  private String fillZero(String textnumber, int len) {
    int tnlen = textnumber.length();
    String result = textnumber;

    if (tnlen < len) {
      for (int i = tnlen + 1; i <= len; i++) {
        result = "0" + result;
      }
    }

    return result;
  }
}
