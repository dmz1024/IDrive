package net.endlessstudio.xhtmlparser.nod;

import net.endlessstudio.xhtmlparser.IllegalFormatException;
import net.endlessstudio.xhtmlparser.UnknownTagException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

public class FontNod extends ElementNod {
	private String mSize;
	private String mColor;
	
	public FontNod(String name, Attributes attributes) throws UnknownTagException {
		super(name, attributes);
		for(int i = 0; i < attributes.getLength(); i ++){
			String attrName = attributes.getLocalName(i);
			String attrValue = attributes.getValue(i);
			
			if("size".equals(attrName)){
				mSize = attrValue;
			}
			else if("color".equals(attrName)){
				mColor = attrValue;
			}
			else{
				throw new UnknownTagException(attrName + " of font");
			}
		}
	}

	@Override
	public Nod execute(Nod nod) throws SAXException {
		if(nod instanceof TextNod){
			TextNod txNod = (TextNod) nod;
			if(mSize != null){
				setSize(txNod.getValue(), mSize);
			}
			if(mColor != null){
				setColor(txNod.getValue(), mColor);
			}
			
			return txNod;
		}
		
		throw new IllegalFormatException();
	}

	private void setColor(SpannableString value, String color) throws IllegalFormatException {
		if(!color.startsWith("#")){
			throw new IllegalFormatException("color format error " + color);
		}
		
		color = color.length() == 7 ? "ff" + color.substring(1) : color.substring(1);
		int a = Integer.parseInt(color.substring(0, 2), 16);
		int r = Integer.parseInt(color.substring(2, 4), 16);
		int g = Integer.parseInt(color.substring(4, 6), 16);
		int b = Integer.parseInt(color.substring(6, 8), 16);
		int iColor = Color.argb(a, r, g, b);
		
		value.setSpan(new ForegroundColorSpan(iColor), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	
	private void setSize(SpannableString value, String size) throws IllegalFormatException {
		float fSize = Float.valueOf(size);
		value.setSpan(new RelativeSizeSpan(fSize), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
}
