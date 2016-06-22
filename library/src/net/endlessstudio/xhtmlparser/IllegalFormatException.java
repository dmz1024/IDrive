package net.endlessstudio.xhtmlparser;

import org.xml.sax.SAXException;


public class IllegalFormatException extends SAXException {
	
	private static final long serialVersionUID = 832359989077597306L;

	public IllegalFormatException() {
		super();
	}
	
	public IllegalFormatException(String msg){
		super(msg);
	}
}
