package com.chat.pk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.pk.DAO.NodeJSDAO;
import com.chat.pk.DAO.UtilityDAO;
import com.chat.pk.Gcm.GetRegId;
import com.chat.pk.Util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.Socket;


/**
 * A login screen that offers login via username.
 */



/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView,userid_input;

    private Handler mLoginHandler = new Handler();
    AppSession appSession=null;
    String TAG = getClass().getSimpleName();
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appSession=new AppSession(getBaseContext());
        new UtilityDAO(getApplicationContext()).startChatService(true);
        new GetRegId(this).getRegId();

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });
        userid_input = (EditText) findViewById(R.id.userid_input);
        userid_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public void login() {

        if (TextUtils.isEmpty( new GetRegId(this).getRegId())){
            return;
        }
        final ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        final String userId= userid_input.getText().toString().trim();
        final String userName = mUsernameView.getText().toString().trim();
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(getBaseContext(), "Please enter user id", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getBaseContext(), "Please enter user name",Toast.LENGTH_SHORT).show();
            return;
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.PN_USER_ID, userId);
                jsonObject.put(Constants.PN_USER_NAME, userName);
                jsonObject.put(Constants.PN_NOTIFICATION_ID, new GetRegId(this).getRegId());

            } catch (JSONException e) { // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mSocket != null) {


                if (!mSocket.connected()){
                    mSocket.connect();
                    new UtilityDAO(getBaseContext()).startChatService(false);
                    Log.i(TAG, "Socket not connected ");
                    Toast.makeText(getBaseContext(), "Socket not connected, Please use valid socket url and port.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "response emit for " + Constants.CHAT_LOGIN+" Is "+jsonObject.toString());

                mSocket.emit(Constants.CHAT_LOGIN,jsonObject.toString(), new Ack() {
                            @Override
                            public void call(final Object... args) {
                                // TODO Auto-generated method stub
                                if (args != null && args.length > 0) {
                                    Log.i(TAG, "response" + args[0]);
                                    mLoginHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            String response=new NodeJSDAO(getBaseContext()).parseSuccess(args[0].toString());
                                            if (TextUtils.isEmpty(response)){
                                                Toast.makeText(getBaseContext(), "response "+response,Toast.LENGTH_SHORT).show();
                                            }else if (response.equals("1")){
                                                appSession.setUserId(userId);
                                                appSession.setUserName(userName);
                                                appSession.setLogin(true);
                                                ChatService.isLogin=true;
                                                new UtilityDAO(getApplicationContext()).startChatService(true);
                                                closeKeyboard(LoginActivity.this, userid_input);
                                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(getBaseContext(), "Invalid login details.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.i(TAG, "login response null h");
                                }
                            }
                        });
            }
        }
    }
    public static void closeKeyboard(final Context context, final View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}



