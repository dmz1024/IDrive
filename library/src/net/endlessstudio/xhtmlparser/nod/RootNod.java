package net.endlessstudio.xhtmlparser.nod;

import org.xml.sax.SAXException;

public class RootNod extends ElementNod {

	public RootNod(String name) {
		super(name, null);
	}

	@Override
	public Nod execute(Nod nod) throws SAXException {
		return nod;
	}

}
