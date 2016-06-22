package com.trs.wcm;

import com.trs.constants.Constants;
import com.trs.util.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;

/**
 * Created by wbq on 14-7-9.
 * 参数：
 * "key":""
 * "params1":""
 * "params2":""
 *
 */
public abstract class LoadSoapDataTask extends AsyncTask<String,Integer,ArrayList<String>>{
    public static String EXTRA_KEY = "key";
    public static String EXTRA_PARAMS = "params";

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> arrayList = new ArrayList<String>();
        SoapObject request = new SoapObject(Constants.serviceNameSpace, params[0]);
        if(params.length > 1){
            for(int i = 1;i <= params.length;i++){
                request.addProperty(params[i],params[++i]);
            }
        }
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = request;
        (new MarshalBase64()).register(envelope);
        AndroidHttpTransport transport = new AndroidHttpTransport(Constants.serviceURL);
        transport.debug = true;
        try {
            transport.call(Constants.serviceNameSpace + Constants.ChannleBySitName, envelope);
            if(envelope.getResponse()!=null){
                arrayList.add(envelope.bodyIn.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> list) {

    }
}
