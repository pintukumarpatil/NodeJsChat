package com.chat.pk.Gcm;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.chat.pk.AppSession;
import com.chat.pk.ChatApplication;
import com.chat.pk.ChatService;
import com.chat.pk.DAO.UtilityDAO;
import com.chat.pk.R;
import com.chat.pk.Util.Constants;

import java.util.List;

import io.socket.client.Socket;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class GcmMessageHandler extends IntentService{

    private Handler handler;
    AppSession appSession;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(getClass().getName(), "onHandleIntent.................................................");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        reactToNotification(getApplicationContext(), intent);
        // in your BroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void reactToNotification(Context context, Intent intent) {
        try {
            Log.i(getClass().getName(), "reactToNotification.................................................");
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, getClass().getName());
            wakeLock.acquire();
            boolean isAppOpen = false;
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(am
                    .getRunningAppProcesses().size());
            for (ActivityManager.RunningTaskInfo runningTaskInfo : tasks) {
                if (runningTaskInfo.topActivity.getPackageName().equals(context.getPackageName()))
                    isAppOpen = true;
                break;
            }
            String title = "", message = "", type = "";
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.i(getClass().getName(), "bundle : " + bundle.toString());
                title = bundle.getString("title");
                message = bundle.getString("msg");
                type = bundle.getString("type");
                Log.i(getClass().getName(), "type : " + type);
                Log.i(getClass().getName(), "title : " + title);
                Log.i(getClass().getName(), "message : " + message);
            } else {
                bundle = new Bundle();
            }
            appSession = new AppSession(context);
            if ( type == null)
                return;
           if (!TextUtils.isEmpty(type) && type.equals(Constants.NT_CHAT_SERVICE)) {
               ChatService.isLogin=false;
               ChatApplication app = (ChatApplication)getApplication();
               Socket  mSocket=app.getSocket();
               if (mSocket != null) {
                   mSocket.connect();
               }
               new UtilityDAO(context).startChatService(true);
           }/* else if (appSession.isNotification()) {
                if (!isAppOpen) {
                    intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bundle.putString(PN_NOTIFICATION_TYPE, type);
                    intent.putExtras(bundle);
                    sendToNotificationArea(context, intent, title, message);
                } else {
                    intent = new Intent(context, NotificationDialog.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendToNotificationArea(Context context, Intent intent, String title,
                                String message) {
        Log.i(getClass().getName(), "Notification Bar.....................................");
        appSession = new AppSession(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                        //.setTicker("Hearty365")
                .setContentTitle(title)
                .setContentText(message)
                        // .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent);
        //.setContentInfo("Info");
        Notification notification = builder.build();

        // notification.number = number;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        if (appSession.isSound())
            notification.defaults |= Notification.DEFAULT_SOUND;
        if (appSession.isVibration())
            notification.vibrate = new long[]{300L, 300L, 600L, 800L};
        notificationManager.notify(101, notification);
    }
}
