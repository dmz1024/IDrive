package com.ccpress.izijia.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.ccpress.izijia.R;
import com.trs.util.StringUtil;

/**
 * Created by WLH on 2015/5/7 16:39.
 */
public class CallUtil {

    public static void showDialog(final String number, final Context mContext){
        if(StringUtil.isEmpty(number)){
            return;
        }
        final Dialog dialog = new Dialog(mContext, R.style.popToCenterDialog);//, R.style.Dialog_Fullscreen
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_call, null);
        TextView mTxtnumber = (TextView) view.findViewById(R.id.phone_num);
        mTxtnumber.setText(number);
        TextView mTxtCancel = (TextView) view.findViewById(R.id.phone_cancel);
        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView mTxtCall = (TextView) view.findViewById(R.id.phone_call);
        mTxtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                mContext.startActivity(intent);
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
}
