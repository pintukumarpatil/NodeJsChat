package com.chat.pk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class AppSession {
	private SharedPreferences sharedPref;
	private Editor editor;
	private static String SHARED = "pk_chat_preferences";
	@SuppressLint("CommitPrefEdits")
	public AppSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	// ////////////////////////////////////////System preferances
	public String getUserId() {
		return sharedPref.getString("UserId", null);
	}

	public void setUserId(String value) {
		editor.putString("UserId", value);
		editor.commit();
		
	}
	public String getUserName() {
		return sharedPref.getString("UserName", null);
	}

	public void setUserName(String value) {
		editor.putString("UserName", value);
		editor.commit();

	}
	public boolean isLogin() {
		return sharedPref.getBoolean("LoginFlage", false);
	}

	public void setLogin(boolean value) {
		editor.putBoolean("LoginFlage", value);
		editor.commit();
		
	}
	public boolean isSound() {
		return sharedPref.getBoolean("isSound", false);
	}
	public void setSound(boolean value) {
		editor.putBoolean("isSound", value);
		editor.commit();

	}
	public boolean isVibration() {
		return sharedPref.getBoolean("isVibration", false);
	}
	public void setVibration(boolean value) {
		editor.putBoolean("isVibration", value);
		editor.commit();

	}
}