package com.chat.pk.Gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.chat.pk.Util.Constants;

import java.io.IOException;


/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class GetRegId {


    public static String regid;
    GoogleCloudMessaging gcm;
    Context context;
    public GetRegId(Context context)
    {
        this.context=context;
        getRegId();

    }
    public String getRegId()
    {
       if (!TextUtils.isEmpty(regid))
       return regid;
       else getRegistrationId();
       return "";

    }

    public void getRegistrationId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Constants.PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
               // etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }
}


