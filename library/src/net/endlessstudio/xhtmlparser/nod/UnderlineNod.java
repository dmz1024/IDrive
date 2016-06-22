package net.endlessstudio.xhtmlparser.nod;

import net.endlessstudio.xhtmlparser.IllegalFormatException;

import org.xml.sax.SAXException;

import android.text.Spannable;
import android.text.style.UnderlineSpan;

public class UnderlineNod extends ElementNod {

	public UnderlineNod(String name) {
		super(name, null);
	}

	/**
	 * 仅对TextNod作处理
	 */
	@Override
	public Nod execute(Nod nod) throws SAXException {
		if(nod instanceof TextNod){
			TextNod txNod = (TextNod) nod;
			txNod.getValue().setSpan(new UnderlineSpan(), 0, txNod.getValue().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return txNod;
		}
		
		throw new IllegalFormatException();
	}

}
