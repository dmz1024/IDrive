package com.trs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.trs.mobile.R;
import com.trs.readhistory.ReadHistoryManager;
import com.trs.types.ListItem;
import com.trs.util.ImageDownloader;
import com.trs.util.StringUtil;

/**
 * Created by john on 13-11-18.
 */
public class IconTitleDateSummaryAdapter extends AbsListAdapter {

	public IconTitleDateSummaryAdapter(Context context){
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView != null){
			view = convertView;
		}
		else{
			view = LayoutInflater.from(getContext()).inflate(getViewID(), parent, false);
		}

		ListItem item = (ListItem) getItem(position);
		boolean hasRead = ReadHistoryManager.getInstance(getContext()).hasRead(item);

		ImageView img = (ImageView) view.findViewById(R.id.img);
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView date = (TextView) view.findViewById(R.id.date);
		TextView summary = (TextView) view.findViewById(R.id.summary);

		boolean hasImg = !StringUtil.isEmpty(item.getImgUrl());
		boolean hasTitle = !StringUtil.isEmpty(item.getTitle());
		boolean hasDate = !StringUtil.isEmpty(item.getDate());
		boolean hasSummary = !StringUtil.isEmpty(item.getSummary());

		if(img != null){
			img.setVisibility(hasImg ? View.VISIBLE : View.GONE);
			if(hasImg){
				new ImageDownloader.Builder()
						.setOptionsType(ImageDownloader.OptionsType.DEFAULT_PIC)
						.build(item.getImgUrl(), img)
						.start();
			}
			else{
				img.setImageDrawable(new BitmapDrawable());
			}
		}

		if(title != null){
			title.setVisibility(hasTitle ? View.VISIBLE : View.GONE);
			if(hasTitle){
				title.setText(item.getTitle());
				title.setSingleLine(hasSummary);
				title.setMaxLines(hasSummary? 1: 2);
				updateTextColor(title, getContext().getResources().getColor(R.color.list_item_title), hasRead);
			}
		}

		if(date != null){
			date.setVisibility(hasDate ? View.VISIBLE : View.GONE);
			if(hasDate){
				date.setText(item.getDate());
				updateTextColor(date, getContext().getResources().getColor(R.color.list_item_date), hasRead);
			}
		}

		if(summary != null){
			summary.setVisibility(hasSummary? View.VISIBLE : View.GONE);
			if(hasSummary){
				summary.setText(item.getSummary());
				updateTextColor(summary, getContext().getResources().getColor(R.color.list_item_summary), hasRead);
			}
		}

		return view;
	}

	private void updateTextColor(TextView view, int defaultColor, boolean hasRead){
		view.setTextColor(hasRead? Color.GRAY: defaultColor);
	}
	
	public int getViewID(){
		return R.layout.list_item_icon_title_date_summary;
	}

}
