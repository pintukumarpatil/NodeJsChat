package com.chat.pk.DAO;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import com.chat.pk.AppSession;
import com.chat.pk.ChatApplication;
import com.chat.pk.ChatService;
import com.chat.pk.Message;
import com.chat.pk.R;
import com.chat.pk.Util.Constants;
import com.chat.pk.Util.Utilities;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
@SuppressLint({ "Wakelock", "DefaultLocale" })
public class UtilityDAO {
	String TAG = getClass().getSimpleName();
	public static SQLiteDatabase database;
	Context context;
	AppSession appSession;
	private Socket mSocket;
	public UtilityDAO(Context context) {
		this.context = context;
		try {
			if (database == null) {
				database = context.openOrCreateDatabase(Constants.DB_NAME,
						SQLiteDatabase.CREATE_IF_NECESSARY, null);
				database.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.TABLE_GROUP_CHAT + "("
						+ Constants.CN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ Constants.CN_GROUP_ID + " varchar,"
						+ Constants.CN_GROUP_NAME + " varchar,"
						+ Constants.CN_GROUP_IMAGE + " varchar,"
						+ Constants.CN_FROM_ID + " varchar,"
						+ Constants.CN_FROM_NAME + " varchar,"
						+ Constants.CN_FROM_IMAGE + " varchar,"
						+ Constants.CN_USER_ID + " varchar,"
						+ Constants.CN_USER_NAME + " varchar,"
						+ Constants.CN_USER_IMAGE + " varchar,"
						+ Constants.CN_MESSAGE + " varchar,"
						+ Constants.CN_FILE_NAME + " varchar,"
						+ Constants.CN_FILE_PATH + " varchar,"
						+ Constants.CN_THUMB_FILE_NAME + " varchar,"
						+ Constants.CN_USER_TYPE + " varchar,"
						+ Constants.CN_MESSAGE_TYPE + " varchar,"
						+ Constants.CN_TIME + " varchar,"
						+ Constants.CN_STATUS + " varchar" + ")");


				database.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.TABLE_SINGLE_CHAT + "("
						+ Constants.CN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ Constants.CN_USER_ID + " varchar,"
						+ Constants.CN_USER_NAME + " varchar,"
						+ Constants.CN_USER_IMAGE + " varchar,"
						+ Constants.CN_FROM_ID + " varchar,"
						+ Constants.CN_FROM_NAME + " varchar,"
						+ Constants.CN_FROM_IMAGE + " varchar,"
						+ Constants.CN_MESSAGE + " varchar,"
						+ Constants.CN_FILE_NAME + " varchar,"
						+ Constants.CN_FILE_PATH + " varchar,"
						+ Constants.CN_THUMB_FILE_NAME + " varchar,"
						+ Constants.CN_USER_TYPE + " varchar,"
						+ Constants.CN_MESSAGE_TYPE + " varchar,"
						+ Constants.CN_TIME + " varchar,"
						+ Constants.CN_STATUS + " varchar" + ")");

			}
			ChatApplication app = (ChatApplication)context.getApplicationContext();
			mSocket=app.getSocket();
			appSession = new AppSession(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// index: 1 for user_id
	// index: 2 for user_name
	// index: 3 for from_id
	// index: 4 for from_name
	// index: 5 for message
	// index: 6 for file
	// index: 7 for user_type
	// index: 8 for message_type
	// index: 9 for date_time
	// index: 10 for status

	// user_id: unique id of login user
	// user_name: user name of login user
	// from_id: unique id of chat user
	// from_name: user name of chat user
	// message: hold all type of messages(text messages,picture,audio,video)
	// message_type: type of messages example: message_type=1->message hold text
	// messages
	// message_type=2->message hold picture
	// message_type=3->message hold audio
	// message_type=4->message hold video

	// user_type: 1 for sending messages and 2 for receiving messages of me
	// date_time:hold current time of messages
	// status: hold read status of messages:example:
	// status=0-> that means messages is not read yet
	// status=1->that means messages is successfully integrated
	// status=2->that means messages is not send
	// status=3 that means message converted into file successfully.

	public long addEntryGroup(Message message) {
		ContentValues values = new ContentValues();
		values.put(Constants.CN_USER_ID, appSession.getUserId());
		values.put(Constants.CN_USER_NAME, appSession.getUserName());
		values.put(Constants.CN_USER_IMAGE,"");
		values.put(Constants.CN_GROUP_ID, message.getToId());
		values.put(Constants.CN_GROUP_NAME, message.getToName());
		values.put(Constants.CN_GROUP_IMAGE, message.getToImage());
		values.put(Constants.CN_FROM_ID, message.getFromId());
		values.put(Constants.CN_FROM_NAME, message.getFromName());
		values.put(Constants.CN_FROM_IMAGE, message.getFromImage());
		values.put(Constants.CN_MESSAGE, message.getMessage());
		values.put(Constants.CN_FILE_NAME, message.getFileName());
		values.put(Constants.CN_FILE_PATH, message.getFilePath());
		values.put(Constants.CN_USER_TYPE, message.getUserType());
		values.put(Constants.CN_MESSAGE_TYPE, message.getMessageType());
		values.put(Constants.CN_TIME, message.getTime());
		values.put(Constants.CN_STATUS, message.getStatus());

		try {
			return database.insert(Constants.TABLE_GROUP_CHAT, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public long addEntrySingle(Message message) {
		ContentValues values = new ContentValues();
		values.put(Constants.CN_USER_ID, message.getUserId());
		values.put(Constants.CN_USER_NAME, message.getUserName());
		values.put(Constants.CN_USER_IMAGE, message.getToImage());
		values.put(Constants.CN_FROM_ID, message.getFriendId());
		values.put(Constants.CN_FROM_NAME, message.getFriendName());
		values.put(Constants.CN_FROM_IMAGE, message.getFromImage());
		values.put(Constants.CN_MESSAGE, message.getMessage());
		values.put(Constants.CN_FILE_NAME, message.getFileName());
		values.put(Constants.CN_FILE_PATH, message.getFilePath());
		values.put(Constants.CN_USER_TYPE, message.getUserType());
		values.put(Constants.CN_MESSAGE_TYPE, message.getMessageType());
		values.put(Constants.CN_TIME, message.getTime());
		values.put(Constants.CN_STATUS, message.getStatus());

		try {
			return database.insert(Constants.TABLE_SINGLE_CHAT, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	public int getCountAllGroup(String userId) {
		int count = 0;
		try {
			Cursor cursor = database
					.rawQuery("SELECT * FROM "+Constants.TABLE_GROUP_CHAT+" where "
									+Constants.CN_USER_ID+"=" + userId
									+" and "
									+Constants.CN_USER_TYPE+"=" + Constants.USER_TYPE_OTHER
									+" and "
									+Constants.CN_STATUS+"=" + Constants.STATUS_UNREAD
									+" COLLATE NOCASE",
							null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int getCountAllSingle(String userId) {
		int count = 0;
		try {
			Cursor cursor = database
					.rawQuery("SELECT * FROM "+Constants.TABLE_SINGLE_CHAT+" where "
									+Constants.CN_USER_ID+"=" + userId
									+" and "
									+Constants.CN_USER_TYPE+"=" + Constants.USER_TYPE_OTHER
									+" and "
									+Constants.CN_STATUS+"=" + Constants.STATUS_UNREAD
									+" COLLATE NOCASE",
							null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int getCountSingle(String userId, String fromId) {
		int count = 0;
		try {
			Cursor cursor = database
					.rawQuery("SELECT * FROM "+Constants.TABLE_SINGLE_CHAT+" where "
									+Constants.CN_USER_ID+"=" + userId
									+" and "
									+ Constants.CN_FROM_ID + "=" + fromId
									+" and "
									+Constants.CN_USER_TYPE+"=" + Constants.USER_TYPE_OTHER
									+" and "
									+Constants.CN_STATUS+"=" + Constants.STATUS_UNREAD
									+" COLLATE NOCASE",
							null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int getCountGroup(String userId, String groupId) {
		int count = 0;
		try {
			Cursor cursor = database
					.rawQuery("SELECT * FROM "+Constants.TABLE_GROUP_CHAT+" where "
									+Constants.CN_USER_ID+"=" + userId
									+" and "
									+ Constants.CN_GROUP_ID + "=" + groupId
									+" and "
									+Constants.CN_USER_TYPE+"=" + Constants.USER_TYPE_OTHER
									+" and "
									+Constants.CN_STATUS+"=" + Constants.STATUS_UNREAD
									+" COLLATE NOCASE",
							null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}


	public Cursor getLastGroupMessages(String userId, String groupId) {
		Cursor cursor = null;
		try {
			cursor = database.rawQuery(
					"SELECT * FROM " + Constants.TABLE_GROUP_CHAT + " where "
							+ Constants.CN_USER_ID + "=" + userId
							+ " and "
							+ Constants.CN_GROUP_ID + "=" + groupId
							+ " ORDER BY " + Constants.CN_ID + " DESC LIMIT 1", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	public Cursor getLastSingleMessages(String userId, String fromId) {
		Cursor cursor = null;
		try {
			cursor = database.rawQuery(
					"SELECT * FROM " + Constants.TABLE_SINGLE_CHAT + " where "
							+ Constants.CN_USER_ID + "=" + userId
							+ " and "
							+ Constants.CN_FROM_ID + "=" + fromId
							+ " ORDER BY " + Constants.CN_ID + " DESC LIMIT 1", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	public Cursor getGroupChat(String userId, String groupId) {
		Cursor cursor = null;
		try {
			cursor = database.rawQuery(
					"SELECT * FROM " + Constants.TABLE_GROUP_CHAT + " where "
							+ Constants.CN_USER_ID + "=" + userId
							+ " and "
							+ Constants.CN_GROUP_ID + "=" + groupId
							+ " COLLATE NOCASE", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	public Cursor getGroupChatByTime(String userId, String groupId,String time) {
		Cursor cursor = null;
		try {
			/*SELECT *
					FROM `iDessert_order`
			WHERE `orderEnteon` < "2016-03-08 03:53:54 "
			AND `orderkitchenId` =0
			ORDER BY orderEnteon DESC
			LIMIT 0 , 10*/

			//select * from (select * from tblmessage order by sortfield ASC limit 10) order by sortfield DESC;

			cursor = database.rawQuery(
					"SELECT * FROM " + Constants.TABLE_GROUP_CHAT + " where "
							+ Constants.CN_TIME + "<" + time
							+ " and "
							+ Constants.CN_USER_ID + "=" + userId
							+ " and "
							+ Constants.CN_GROUP_ID + "=" + groupId
							+ " ORDER BY "+ Constants.CN_TIME+ " DESC LIMIT 0 , 15", null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	public Cursor getPrivateChatByTime(String userId, String friendId,String time) {
		Cursor cursor = null;
		try {
			/*SELECT *
					FROM `iDessert_order`
			WHERE `orderEnteon` < "2016-03-08 03:53:54 "
			AND `orderkitchenId` =0
			ORDER BY orderEnteon DESC
			LIMIT 0 , 10*/

			//select * from (select * from tblmessage order by sortfield ASC limit 10) order by sortfield DESC;

			cursor = database.rawQuery(
					"SELECT * FROM " + Constants.TABLE_SINGLE_CHAT + " where "
							+ Constants.CN_TIME + "<" + time
							+ " and "
							+ Constants.CN_USER_ID + "=" + userId
							+ " and "
							+ Constants.CN_FROM_ID + "=" + friendId
							+ " ORDER BY "+ Constants.CN_TIME+ " DESC LIMIT 0 , 15", null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	public int delete(String id) {
		int count = 0;
		try {
			count = database.delete(Constants.TABLE_GROUP_CHAT,
					Constants.CN_ID + "= ?", new String[]{id});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int updateSingleReadAllTxt(String userId, String fromId,String status) {
		int count = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, status);
			count = database.update(Constants.TABLE_SINGLE_CHAT, values, Constants.CN_USER_ID + "=? and " + Constants.CN_FROM_ID + " =? and " + Constants.CN_STATUS + " =? and " + Constants.CN_MESSAGE_TYPE + " =? and " + Constants.CN_USER_TYPE + " =?",
					new String[]{userId, fromId, Constants.STATUS_UNREAD, Constants.MESSAGE_TYPE_TEXT, Constants.USER_TYPE_OTHER});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int updateGroupReadAllTxt(String userId, String groupId,String status) {
		int count = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, status);
			count = database.update(Constants.TABLE_GROUP_CHAT, values, Constants.CN_USER_ID + "=? and " + Constants.CN_GROUP_ID + " =? and " + Constants.CN_STATUS + " =? and " + Constants.CN_MESSAGE_TYPE + " =? and " + Constants.CN_USER_TYPE + " =?",
					new String[]{userId, groupId, Constants.STATUS_UNREAD, Constants.MESSAGE_TYPE_TEXT, Constants.USER_TYPE_OTHER});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int updateSingleReadAllImage(String userId, String fromId,String status) {
		int count = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, status);
			count = database.update(Constants.TABLE_SINGLE_CHAT, values,
							Constants.CN_USER_ID +"=? and "+
							Constants.CN_FROM_ID+" =? and "+
							Constants.CN_MESSAGE_TYPE+" =? and " +
							Constants.CN_STATUS+" =? OR " +
							Constants.CN_STATUS+" =? and " +
							Constants.CN_USER_TYPE + " =?",
					new String[]{
							userId,
							fromId,
							Constants.MESSAGE_TYPE_IMAGE ,
							Constants.STATUS_DOWNLOADING ,
							Constants.STATUS_UNREAD ,
							Constants.USER_TYPE_OTHER});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int updateGroupReadAllImage(String userId, String groupId,String status) {
		int count = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, status);
			count = database.update(Constants.TABLE_GROUP_CHAT, values,
					Constants.CN_USER_ID +"=? and "+
							Constants.CN_GROUP_ID+" =? and "+
							Constants.CN_MESSAGE_TYPE+" =? and " +
							Constants.CN_STATUS+" =? OR " +
							Constants.CN_STATUS+" =? and " +
							Constants.CN_USER_TYPE + " =?",
					new String[]{
							userId,
							groupId,
							Constants.MESSAGE_TYPE_IMAGE ,
							Constants.STATUS_DOWNLOADING ,
							Constants.STATUS_UNREAD ,
							Constants.USER_TYPE_OTHER});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int updateSingleErrorAllImage(String userId, String fromId) {
		int count = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, Constants.STATUS_WAIT_FOR_UPLOADING);
			count = database.update(Constants.TABLE_SINGLE_CHAT, values,
					Constants.CN_USER_ID +"=? and "+
							Constants.CN_FROM_ID+" =? and "+
							Constants.CN_MESSAGE_TYPE+" =? and " +
							Constants.CN_STATUS+" =? OR "+
							Constants.CN_STATUS+" =? and " +
							Constants.CN_USER_TYPE + " =?",
					new String[]{
							userId,
							fromId,
							Constants.MESSAGE_TYPE_IMAGE ,
							Constants.STATUS_UNREAD,
							Constants.STATUS_START_UPLOADING ,
							Constants.USER_TYPE_ME});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int updateGroupErrorAllImage(String userId, String groupId) {
		int count = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, Constants.STATUS_WAIT_FOR_UPLOADING);
			count = database.update(Constants.TABLE_GROUP_CHAT, values,
					Constants.CN_USER_ID +"=? and "+
							Constants.CN_GROUP_ID+" =? and "+
							Constants.CN_MESSAGE_TYPE+" =? and " +
							Constants.CN_STATUS+" =? OR "+
							Constants.CN_STATUS+" =? and " +
							Constants.CN_USER_TYPE + " =?",
					new String[]{
							userId,
							groupId,
							Constants.MESSAGE_TYPE_IMAGE ,
							Constants.STATUS_UNREAD,
							Constants.STATUS_START_UPLOADING ,
							Constants.USER_TYPE_ME});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	public int updateStatus(String rowId,String status) {

		int count = 0;
		try {// update status of a particular file
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, status);
			count = database.update(Constants.TABLE_GROUP_CHAT, values, Constants.CN_ID + "=?", new String[]{rowId});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("updateStatus", "supdateStatus rowId "+rowId+"status "+status+" success count " + count);
		return count;
	}
	public int updateStatusPrivate(String rowId,String status) {

		int count = 0;
		try {// update status of a particular file
			ContentValues values = new ContentValues();
			values.put(Constants.CN_STATUS, status);
			count = database.update(Constants.TABLE_SINGLE_CHAT, values, Constants.CN_ID + "=?", new String[]{rowId});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("updateStatus", "supdateStatus rowId "+rowId+"status "+status+" success count " + count);
		return count;
	}
	public int updateFileName(String rowId,String fileName) {

		int count = 0;
		try {// update status of a particular file
			ContentValues values = new ContentValues();
			values.put(Constants.CN_FILE_NAME, fileName);
			count = database.update(Constants.TABLE_GROUP_CHAT, values, Constants.CN_ID + "=?", new String[]{rowId});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("fileName", "fileName rowId "+rowId+"fileName "+fileName+" success count " + count);
		return count;
	}
	public int updateFileNameSingle(String rowId,String fileName) {

		int count = 0;
		try {// update status of a particular file
			ContentValues values = new ContentValues();
			values.put(Constants.CN_FILE_NAME, fileName);
			count = database.update(Constants.TABLE_GROUP_CHAT, values, Constants.CN_ID + "=?", new String[]{rowId});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("fileName", "fileName rowId "+rowId+"fileName "+fileName+" success count " + count);
		return count;
	}
	public int updateFilePath(String rowId,String filePath) {

		int count = 0;
		try {// update status of a particular file
			ContentValues values = new ContentValues();
			values.put(Constants.CN_FILE_PATH, filePath);
			count = database.update(Constants.TABLE_GROUP_CHAT, values, Constants.CN_ID + "=?", new String[]{rowId});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("filePath", "filePath rowId "+rowId+"filePath "+filePath+" success count " + count);
		return count;
	}
	public int getUnreadUserCountGroup(String userId,String group_id) {
		int count = 0;
		try {
			Cursor cursor = database.rawQuery("SELECT DISTINCT "+Constants.CN_GROUP_ID+" FROM "+Constants.TABLE_GROUP_CHAT+" where "
							+ Constants.CN_USER_ID + "=" + userId
							+ " and "
							+ Constants.CN_GROUP_ID + "=" + group_id
							+ " and "
							+ Constants.CN_STATUS+"=" + Constants.STATUS_UNREAD
							+" COLLATE NOCASE", null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("getUnreaduserCount", "getUnreaduserCount:  " + count);

		return count;
	}
	public int getUnreadUserCountSingle(String userId,String friendId) {
		int count = 0;
		try {
			Cursor cursor = database.rawQuery("SELECT DISTINCT "+Constants.CN_FROM_ID+" FROM "+Constants.TABLE_SINGLE_CHAT+" where "
					+ Constants.CN_USER_ID + "=" + userId
					+ " and "
					+ Constants.CN_FROM_ID + "=" + friendId
					+ " and "
					+ Constants.CN_STATUS+"=" + Constants.STATUS_UNREAD
					+" COLLATE NOCASE", null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("getUnreaduserCount", "getUnreaduserCount:  " + count);

		return count;
	}
	@SuppressLint("DefaultLocale")
	public void sendRemainsText(String userId) {
		Cursor cursor = null;
		try {
			if (mSocket!=null && mSocket.connected()) {
				appSession = new AppSession(context);
				cursor = database
						.rawQuery(
								"SELECT * FROM "+Constants.TABLE_GROUP_CHAT+" where "
										+ Constants.CN_USER_ID + "=" + userId
										+ " and "
										+ Constants.CN_STATUS+"=" + Constants.STATUS_UNSEND
										+ " and "
										+ Constants.CN_MESSAGE_TYPE+"=" + Constants.MESSAGE_TYPE_TEXT
										+ " COLLATE NOCASE",
								null);

				Log.i("cursor.getCount()", "cursor.getCount() remains "+ cursor.getCount());
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						try {
							sendMessage(cursor);
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
						Log.i("sent successfully ", "sent successfully to "+ cursor.getString(cursor.getColumnIndex(Constants.CN_GROUP_ID)));
						cursor.moveToNext();
					}
					updateStatus(cursor.getString(cursor.getColumnIndex(Constants.CN_USER_ID)),Constants.STATUS_SENT);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(Cursor cursor) {

		// toId,toName,fromId,fromName,message,messageType,formate
			try {

				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.PN_TO_ID, cursor.getString(cursor.getColumnIndex(Constants.CN_GROUP_ID)));
				jsonObject.put(Constants.PN_TO_NAME,cursor.getString(cursor.getColumnIndex(Constants.CN_GROUP_NAME)));
				jsonObject.put(Constants.PN_FROM_ID, appSession.getUserId());
				jsonObject.put(Constants.PN_FROM_NAME,appSession.getUserName().trim());
				jsonObject.put(Constants.PN_MESSAGE, cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));
				jsonObject.put(Constants.PN_CURRENT_TIME, Utilities.getInstance(context).getUTCTime());
				Log.i(TAG, Constants.CHAT_GROUP_MESSAGE+" emit " + jsonObject.toString());
				mSocket.emit(Constants.CHAT_GROUP_MESSAGE,jsonObject.toString(), new Ack() {
							@Override
							public void call(final Object... arg0) {
								// TODO Auto-generated method stub
								if (arg0!=null && arg0.length>0){
									Log.i(TAG,Constants.CHAT_GROUP_MESSAGE+" callback "+ arg0[0].toString());
									String status=new NodeJSDAO(context).parseSuccess(arg0[0].toString());
									if (status!=null){
										if (status.equals("1")) {

										}else if (status.equals("2")) {

										}
									}else {
										Log.i(TAG,"status is null");
									}
								}else {
									Log.i(TAG,"callback is null");
								}
							}
						});
			} catch (OutOfMemoryError e) {
				// TODO: handle exception
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
	public void startChatService(boolean onCurrentTime) {
		try {
			Calendar cal = Calendar.getInstance();
			Intent intent = new Intent(context, ChatService.class);
			intent.setData((Uri.parse("SERVICE_CHAT_PK")));
			PendingIntent pintent = PendingIntent.getService(context, 0,
					intent, 0);
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
				alarm.setRepeating(AlarmManager.RTC_WAKEUP,
						cal.getTimeInMillis(),
						15 * 1000, pintent);
			} else {
				if (onCurrentTime)
					alarm.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()
							+ (0 * 1000), pintent);
				else alarm.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()
						+ (30 * 1000), pintent);
			}
			// Log.i("startChatService", "startChatService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopChatService() {
		try {
			Intent intent = new Intent(context, ChatService.class);
			intent.setData((Uri.parse("SERVICE_CHAT_PK")));
			PendingIntent pintent = PendingIntent.getService(context, 0,
					intent, 0);
			// PendingIntent pintent = PendingIntent.getBroadcast(this, 0,
			// intent, 0);
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarm.cancel(pintent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MediaPlayer mMediaPlayer;
	AudioManager am;

	public void setSound() {
		try {

			int maxVol;
			am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			maxVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0);
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				mMediaPlayer.setVolume(maxVol, maxVol);
				mMediaPlayer = MediaPlayer.create(context, R.raw.phonering11);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int n = 0;

	public void playSound() {
		try {

			setSound();
			if (mMediaPlayer != null) {
				if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
					mMediaPlayer.start();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void vibrate() {
		try {
			// Get instance of Vibrator from current Context
			am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE
					|| am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
				Vibrator v = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);

				v.vibrate(800);

			}
			// Log.i("vibrate..................", "vibrate...............");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCurrentTime() {
		try {
			Date currentDate = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			return dateFormat.format(currentDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "2016-03-04 00:00:00";
		}
	}

	public static String getUpdateTime(String updateTime, Context mContext) {
		Date currentDate = new Date();
		long diff, second, minute, hour, day, year;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			// dateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
			Date updateDate = dateFormat.parse(updateTime);
			diff = currentDate.getTime() - updateDate.getTime();
			second = diff / 1000;
			minute = second / 60;
			hour = minute / 60;
			day = hour / 24;
			year = day / 365;
			if (second <= 59) {
				if (second <= 1)
					return mContext.getResources().getString(
							R.string.A_moment_ago);
				return second
						+ " "
						+ mContext.getResources().getString(
								R.string.seconds_ago);
			} else if (minute <= 59) {
				if (minute == 1)
					return minute
							+ " "
							+ mContext.getResources().getString(
									R.string.Minute_ago);
				else
					return minute
							+ " "
							+ mContext.getResources().getString(
									R.string.minutes_ago);
			} else if (hour <= 23) {
				if (hour == 1)
					return hour
							+ " "
							+ mContext.getResources().getString(
									R.string.hour_ago);
				else
					return hour
							+ " "
							+ mContext.getResources().getString(
									R.string.hours_ago);
			} else if (day <= 364) {
				if (day == 1)
					return day
							+ " "
							+ mContext.getResources().getString(
									R.string.Day_ago);
				else
					return day
							+ " "
							+ mContext.getResources().getString(
									R.string.days_ago);
			} else {
				if (year == 1)
					return year
							+ " "
							+ mContext.getResources().getString(
									R.string.Year_ago);
				else
					return year
							+ " "
							+ mContext.getResources().getString(
									R.string.Years_ago);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// return "a moment ago";
		return " ";
	}
	public  MediaPlayer mMediaPlayerSend;
	public  AudioManager amSendMsg;
	public  void setSendMsgSound(Context context) {
		try {

			int maxVol;
			amSendMsg = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			maxVol = amSendMsg.getStreamVolume(AudioManager.STREAM_MUSIC);
			amSendMsg.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0);
				mMediaPlayerSend = new MediaPlayer();
				mMediaPlayerSend.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				mMediaPlayerSend.setVolume(maxVol, maxVol);
				mMediaPlayerSend = MediaPlayer.create(context, R.raw.send_message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void playSendMsgSoundSound(Context context) {
		try {
			if (mMediaPlayerSend == null) {
				setSendMsgSound(context);
			}
			if (mMediaPlayerSend != null) {
				if (amSendMsg.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
					mMediaPlayerSend.start();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MediaPlayer mMediaPlayerReceive;
	public static AudioManager amReceive;
	public static void setReceiveMsgSound(Context context) {
		try {

			int maxVol;
			amReceive = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			maxVol = amReceive.getStreamVolume(AudioManager.STREAM_MUSIC);
			amReceive.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0);
			if (mMediaPlayerReceive == null) {
				mMediaPlayerReceive = new MediaPlayer();
				mMediaPlayerReceive
						.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				mMediaPlayerReceive.setVolume(maxVol, maxVol);
				mMediaPlayerReceive = MediaPlayer.create(context, R.raw.incoming);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void playReceiveMsgSound(Context context) {
		try {
			if (mMediaPlayerReceive==null) {
				setReceiveMsgSound(context);
			}
			
			if (mMediaPlayerReceive != null) {
				if (amReceive.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
					mMediaPlayerReceive.start();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
