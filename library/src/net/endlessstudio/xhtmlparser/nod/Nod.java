package net.endlessstudio.xhtmlparser.nod;

import org.xml.sax.SAXException;

abstract public class Nod {
	protected String mName;
	
	public Nod(String name){
		this.mName = name;
	}
	
	public String getName(){
		return mName;
	}
	
	abstract public Nod execute(Nod nod) throws SAXException;
}
