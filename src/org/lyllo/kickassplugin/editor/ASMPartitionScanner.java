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

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.lyllo.kickassplugin.Constants;


/**
 * A partition scanner for the ASM editor.
 * 
 * @author Andy Reek
 * @since 13.02.2006
 */
public class ASMPartitionScanner extends RuleBasedPartitionScanner {

  /**
   * Partition scanner for the ASMEditor.
   */
  public ASMPartitionScanner() {
    IToken string = new Token(Constants.PARTITION_STRING);
    IToken commentM = new Token(Constants.PARTITION_COMMENT_MULTI);
    IToken commentS = new Token(Constants.PARTITION_COMMENT_SINGLE);

    ArrayList<IRule> rules = new ArrayList<IRule>();
    rules.add(new SingleLineRule("\"", "\"", string));
    rules.add(new MultiLineRule("/*", "*/", commentM));
    rules.add(new EndOfLineRule("//", commentS));
    setPredicateRules(rules.toArray(new IPredicateRule[] {}));
  }

  /**
   * Convert all characters to lower case, need for case insensitive
   * MultiLineRule.
   * 
   * {@inheritDoc}
   */
  public int read() {
    int c = super.read();

    if (c != EOF) {
      c = Character.toLowerCase((char) c);
    }

    return c;
  }
}
