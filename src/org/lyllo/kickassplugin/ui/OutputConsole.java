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

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.lyllo.kickassplugin.Messages;


/**
 * The console to show the user the output from the compiler an linker.
 * 
 * @author Daniel Mitte
 * @since 13.02.2006
 */
public class OutputConsole {

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.console.MessageConsole
   */
  private MessageConsole console;

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.console.MessageConsoleStream
   */
  private MessageConsoleStream stream;

  /**
   * Create a new console-window.
   * 
   */
  public OutputConsole() {
    console = new MessageConsole(Messages.CONSOLE_TITLE, null);

    ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
    bringConsoleToFront();

    stream = console.newMessageStream();
    stream.print("");
  }

  /**
   * Return the console object.
   * 
   * @return console object.
   */
  public MessageConsole getConsole() {
    return stream.getConsole();
  }

  /**
   * Add a line without lf/cr to console.
   * 
   * @param message The message where print.
   */
  public void print(String message) {
    stream.print(message);
  }

  /**
   * Add a empty line to console.
   * 
   */
  public void println() {
    stream.println();
  }

  /**
   * Add a line to console.
   * 
   * @param message The message where print.
   */
  public void println(String message) {
    stream.println(message);
  }

  /**
   * Show the console if not opened.
   */
  public void bringConsoleToFront() {
    ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
  }
}
