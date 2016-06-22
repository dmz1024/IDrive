package net.endlessstudio.xhtmlparser;

import org.xml.sax.SAXException;

public class UnknownTagException extends SAXException {

	private static final long serialVersionUID = 6127418385174204376L;

	public UnknownTagException(String tag) {
		super("unknown tag: " + tag);
	}
}
