package org.lyllo.kickassplugin.editor;

import org.eclipse.jface.text.rules.IToken;

public class ITokenMatch {

	private String string;
	private int column;
	private IToken token;
	
	public ITokenMatch(String string, int column, IToken token){
		this.string = string;
		this.column = column;
		this.token = token;
	}

	public IToken getToken() {
		return token;
	}

	public int getColumn() {
		return column;
	}

	public String getString() {
		return string;
	}
	
}
