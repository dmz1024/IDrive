package net.endlessstudio.xhtmlparser.nod;

public class BrNod extends ElementNod {

	public BrNod(String name) {
		super(name, null);
	}

	@Override
	public Nod execute(Nod value) {
		return new TextNod("\r\n");
	}
}
