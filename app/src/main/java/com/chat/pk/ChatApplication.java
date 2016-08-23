package com.chat.pk;

import android.app.Application;

import com.chat.pk.Upload.Logger;
import com.chat.pk.Upload.UploadService;
import com.chat.pk.Util.Constants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class ChatApplication extends Application {


    //http://socket.io/blog/native-socket-io-and-android/
    private static Socket mSocket;
    {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getSocket() {
        if (mSocket==null){
            try {
                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                opts.reconnection = true;
                mSocket = IO.socket(Constants.CHAT_SERVER_URL);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return mSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Set your application namespace to avoid conflicts with other apps
        // using this library
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        // Set upload service debug log messages level
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
