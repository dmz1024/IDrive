package net.endlessstudio.xhtmlparser.nod;

import org.xml.sax.Attributes;

abstract public class ElementNod extends Nod{
	protected Attributes mAttributes;

	public ElementNod(String name, Attributes attributes) {
		super(name);
		this.mAttributes = attributes;
	}
}
