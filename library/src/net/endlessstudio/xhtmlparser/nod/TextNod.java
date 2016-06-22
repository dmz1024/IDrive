package net.endlessstudio.xhtmlparser.nod;

import net.endlessstudio.xhtmlparser.IllegalFormatException;

import org.xml.sax.SAXException;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;

public class TextNod extends Nod {
	private SpannableString mValue;
	
	public TextNod(CharSequence value) {
		super("text");
		this.mValue = new SpannableString(value);
	}
	
	@Override
	public Nod execute(Nod nod) throws SAXException {
		if(nod instanceof TextNod){
			SpannableStringBuilder builder = new SpannableStringBuilder();
			builder.append(mValue).append(((TextNod)nod).getValue());
			return new TextNod(builder); 
		}
		
		throw new IllegalFormatException();
	}
	
	public SpannableString getValue(){
		return mValue;
	}
}
