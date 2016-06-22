package net.endlessstudio.xhtmlparser.nod;

import net.endlessstudio.xhtmlparser.IllegalFormatException;

import org.xml.sax.Attributes;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Html.ImageGetter;
import android.text.style.ImageSpan;


public class ImageNod extends ElementNod {
	ImageGetter mImageGetter;
	
	public ImageNod(String name, Attributes attributes, ImageGetter imageGetter) {
		super(name, attributes);
		this.mImageGetter = imageGetter;
	}

	@Override
	public Nod execute(Nod nod) throws IllegalFormatException {
		if(nod instanceof TextNod){
			TextNod txNod = (TextNod)nod;
			Drawable d = mImageGetter.getDrawable(txNod.getValue().toString());
			
			txNod.getValue().setSpan(new ImageSpan(d), 0, txNod.getValue().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			return txNod;
		}
		
		throw new IllegalFormatException();
	}

}
