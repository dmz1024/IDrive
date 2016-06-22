package net.endlessstudio.xhtmlparser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;


public class ResourceImageGetter implements ImageGetter {
	
	private Context context;
	public ResourceImageGetter(Context context) {
		this.context = context;
	}
	
	@Override
	public Drawable getDrawable(String source) {
		int id = context.getResources().getIdentifier(source, "drawable", "com.molifang");
		if (id != 0) {
			Drawable d = context.getResources().getDrawable(id);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			return d;
		}

		return null;
	}

}
