package com.chat.pk.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chat.pk.DAO.UtilityDAO;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            {
                new UtilityDAO(context).startChatService(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
