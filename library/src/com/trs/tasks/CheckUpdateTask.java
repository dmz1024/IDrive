package com.trs.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.google.gson.Gson;
import com.trs.app.TRSApplication;
import com.trs.constants.Constants;
import com.trs.util.AsyncTask;
import net.endlessstudio.util.Util;

import java.io.IOException;

public class CheckUpdateTask extends AsyncTask {
	public class CheckUpdateResult{
		public int responsecode;
		public String responsemessage;
		public String iversion;
		public String aversion;
		public String iURL;
		public String aURL;
	}

	private Context context;
	public CheckUpdateTask(Context context) {
		this.context = context;
	}

	@Override
	protected Object doInBackground(Object[] params) {
		try {
			String json = Util.getString(context, Constants.CHECK_UPDATE_URL, "utf-8");

			return new Gson().fromJson(json, CheckUpdateResult.class);
		} catch (IOException e) {
			return e;
		}
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);

		if(o != null && o instanceof CheckUpdateResult){
			final CheckUpdateResult result = (CheckUpdateResult) o;
			if(result.responsecode == 0){

				if(TRSApplication.app().needUpdate(result)){

					new AlertDialog.Builder(context)
							.setMessage("有可用更新, 是否下载? ")
							.setPositiveButton("下载", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(Intent.ACTION_VIEW);
									intent.setData(Uri.parse(result.aURL));
									context.startActivity(intent);
								}
							})
							.setNegativeButton("取消", null).show();

					onHasUpdate(result);
				}
				else{
					onIsLatest(result);
				}
			}
		}
	}

	public void onHasUpdate(CheckUpdateResult result){

	}

	public void onIsLatest(CheckUpdateResult result){

	}
}
