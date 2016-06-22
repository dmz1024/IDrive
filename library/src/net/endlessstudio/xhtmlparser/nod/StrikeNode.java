package net.endlessstudio.xhtmlparser.nod;

import net.endlessstudio.xhtmlparser.IllegalFormatException;

import org.xml.sax.SAXException;

import android.text.Spannable;
import android.text.style.StrikethroughSpan;


public class StrikeNode extends ElementNod {
	public StrikeNode(String name) {
		super(name, null);
	}

	@Override
	public Nod execute(Nod nod) throws SAXException {
		if(nod instanceof TextNod){
			TextNod txNod = (TextNod) nod;
			txNod.getValue().setSpan(new StrikethroughSpan(), 0, txNod.getValue().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return txNod;
		}
		
		throw new IllegalFormatException();
	}

}
