package com.chat.pk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.chat.pk.DAO.NodeJSDAO;
import com.chat.pk.DAO.UtilityDAO;
import com.chat.pk.Util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class ChatService extends Service {



	public static  String TAG = "ChatService";
	public static AppSession appSession = null;
	UtilityDAO utilityDAO = null;
	public static Socket mSocket;
	public static boolean isLogin=false;
	public static Context context;
	Handler mHandler = new Handler(Looper.getMainLooper());
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			context=getBaseContext();
			Log.i("Service call ", "Service call............................................");

			appSession = new AppSession(getBaseContext());
			utilityDAO = new UtilityDAO(getBaseContext());
			ChatApplication app = (ChatApplication)getApplication();
			mSocket=app.getSocket();
			if (appSession != null && appSession.isLogin()) {
				if (mSocket != null) {
					if (!mSocket.connected()) {
						Log.i("Service call !connect", "Service call connectSocket............................................");
						if (isNetworkAvailable()) {
							connectSocket();
						}else {
							mSocket.disconnect();
						}
					}else if (!isLogin){
						Log.i("Service call !isLogin", "Service call else "+mSocket+" is connected "+mSocket.connected());
						login();
					}
				}
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
					new UtilityDAO(getBaseContext()).startChatService(false);
				}
			} else {
				//new UtilityDAO(getBaseContext()).stopChatService();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}



	public void connectSocket() {
		if (!mSocket.hasListeners(Socket.EVENT_CONNECT_ERROR))
			mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
		if (!mSocket.hasListeners(Socket.EVENT_CONNECT_TIMEOUT))
			mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
		if (!mSocket.hasListeners(Socket.EVENT_CONNECT))
			mSocket.on(Socket.EVENT_CONNECT, onConnect);
		if (!mSocket.hasListeners(Socket.EVENT_RECONNECT))
			mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
		if (!mSocket.hasListeners(Socket.EVENT_DISCONNECT))
			mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
		if (!mSocket.hasListeners(Constants.CHAT_GROUP_MESSAGE))
			mSocket.on(Constants.CHAT_GROUP_MESSAGE, onGroupMessage);
		if (!mSocket.hasListeners(Constants.CHAT_TYPING))
			mSocket.on(Constants.CHAT_TYPING, onTyping);
		if (!mSocket.hasListeners(Constants.CHAT_STOP_TYPING))
			mSocket.on(Constants.CHAT_STOP_TYPING, onStopTyping);

		if (!mSocket.hasListeners(Constants.CHAT_PRIVATE_MESSAGE))
			mSocket.on(Constants.CHAT_PRIVATE_MESSAGE, onPrivateMessage);
		if (!mSocket.hasListeners(Constants.CHAT_PRIVATE_TYPING))
			mSocket.on(Constants.CHAT_PRIVATE_TYPING, onPrivateTyping);
		if (!mSocket.hasListeners(Constants.CHAT_STOP_PRIVATE_TYPING))
			mSocket.on(Constants.CHAT_STOP_PRIVATE_TYPING, onStopPrivateTyping);


		mSocket.connect();
	}


	public static void login() {

		if (appSession==null ||context==null)
			return;

		if (TextUtils.isEmpty(appSession.getUserId())) {
			Toast.makeText(context, "Please enter user id",Toast.LENGTH_SHORT).show();
		}else if (TextUtils.isEmpty(appSession.getUserName())) {
			Toast.makeText(context, "Please enter user name",Toast.LENGTH_SHORT).show();
		} else {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Constants.PN_USER_ID, appSession.getUserId());
				jsonObject.put(Constants.PN_USER_NAME, appSession.getUserName());
			} catch (JSONException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mSocket != null) {
				Log.i(TAG, jsonObject.toString()+"response emit for " + Constants.CHAT_LOGIN);
				mSocket.emit(Constants.CHAT_LOGIN,
						jsonObject.toString(), new Ack() {
							@Override
							public void call(Object... args) {
								// TODO Auto-generated method stub
								if (args != null && args.length > 0) {
									Log.i(TAG, "response" + args[0]);
									isLogin=true;
								} else {
									Log.i(TAG, "login response null h");
								}
							}
						});
			}
		}
	}


	private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
				}
			});

		}
	};
	private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), R.string.error_connect_timeout, Toast.LENGTH_LONG).show();
				}
			});
		}
	};
	private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), R.string.socket_connected, Toast.LENGTH_LONG).show();
					login();
					utilityDAO.sendRemainsText(appSession.getUserId());
				}
			});
		}
	};
	private Emitter.Listener onReconnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), R.string.socket_reconnected, Toast.LENGTH_LONG).show();
					login();
				}
			});
		}
	};
	private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), R.string.socket_disconnected, Toast.LENGTH_LONG).show();
				}
			});
		}
	};
	private Emitter.Listener onGroupMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					//ChatFragment.removeTyping(username);

					Log.i(TAG, "response " + Constants.CHAT_GROUP_MESSAGE + args[0].toString());
					Message message=new NodeJSDAO(getBaseContext()).parseGroupMessages(args[0].toString());
					if (message == null) {
						Log.i(TAG, "responce null h ");
						return;
					}
					message.setUserType(Constants.USER_TYPE_OTHER);
					if (!GroupChatFragment.isChatMinimize && !TextUtils.isEmpty(GroupChatFragment.groupId) && message.getToId().equalsIgnoreCase(GroupChatFragment.groupId)) {
						Log.i(TAG, "processPacket(-) : MyChat is Running-Opern-no need notifications");
							if (message.getFromId().equalsIgnoreCase(appSession.getUserId())) {
								Log.i(TAG, "processPacket(-) : SENDER AND RECEIVER BOTH ARE SAME");
								message.setUserType(Constants.USER_TYPE_ME);
							}
							switch (message.getMessageType()){
							case Constants.MESSAGE_TYPE_TEXT:
								message.setStatus(Constants.STATUS_READ);
								message.setRowId(utilityDAO.addEntryGroup(message) + "");
								if (message.getUserType().equals(Constants.USER_TYPE_OTHER))
									GroupChatFragment.addMessageLeft(message.getFromName(), message);
								else
									GroupChatFragment.addMessageRight(message.getFromName(), message);
								stopGroupTyping(message);
								break;
							case Constants.MESSAGE_TYPE_IMAGE:
								message.setStatus(Constants.STATUS_WAIT_FOR_DOWNLOAD);
								message.setRowId(utilityDAO.addEntryGroup(message) + "");
								if (message.getUserType().equals(Constants.USER_TYPE_OTHER))
								GroupChatFragment.addImageLeft(message.getFromName(), message);
								else GroupChatFragment.addImageRight(message.getFromName(), message);
								break;
							case Constants.MESSAGE_TYPE_JOIN:
								message.setStatus(Constants.STATUS_READ);
								message.setRowId(utilityDAO.addEntryGroup(message) + "");
								GroupChatFragment.addLog(message.getMessage());
								break;
						}
					} else {
						if (message.getFromId().equalsIgnoreCase(appSession.getUserId())) {
							Log.i(TAG, "processPacket(-) : SENDER AND RECEIVER BOTH ARE SAME");
							message.setUserType(Constants.USER_TYPE_ME);
						}
						Log.i(TAG,"processPacket(-) : MyChat is Running- but minimized");
						message.setStatus(Constants.STATUS_UNREAD);
						message.setRowId(utilityDAO.addEntryGroup(message)+"");
						UtilityDAO.playReceiveMsgSound(getApplicationContext());
						setGroupNotifications(message.getToId(), message.getToName(),Constants.CONST_GROUP);
					}

					///For GROUP SCREEN
					if (GroupsFragment.isGroupActive) {
						Log.i(TAG, "processPacket(-) : GroupActive");
						GroupsFragment.refreshList(message);
					}else {
						Log.i(TAG,"processPacket(-) : group not active in chat");
					}

				}
			});
		}
	};
	private Emitter.Listener onPrivateMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					//ChatFragment.removeTyping(username);

					Log.i(TAG, "response " + Constants.CHAT_PRIVATE_MESSAGE + args[0].toString());
					Message message=new NodeJSDAO(getBaseContext()).parsePrivateMessages(args[0].toString());
					if (message == null) {
						Log.i(TAG, "responce null h ");
						return;
					}
					message.setUserType(Constants.USER_TYPE_OTHER);
					if (!SingleChatFragment.isPrivateChatMinimize && !TextUtils.isEmpty(SingleChatFragment.friendId) && message.getFriendId().equalsIgnoreCase(SingleChatFragment.friendId)) {
						Log.i(TAG, "processPacket(-) : MyChat is Running-Opern-no need notifications");
						if (message.getFriendId().equalsIgnoreCase(appSession.getUserId())) {
							Log.i(TAG, "processPacket(-) : SENDER AND RECEIVER BOTH ARE SAME");
							message.setUserType(Constants.USER_TYPE_ME);
						}
						switch (message.getMessageType()){
							case Constants.MESSAGE_TYPE_TEXT:
								message.setStatus(Constants.STATUS_READ);
								message.setRowId(utilityDAO.addEntrySingle(message) + "");
								if (message.getUserType().equals(Constants.USER_TYPE_OTHER))
									SingleChatFragment.addMessageLeft(message.getFriendName(), message);
								else
									SingleChatFragment.addMessageRight(message.getFriendName(), message);
								stopPrivateTyping(message);
								break;
							case Constants.MESSAGE_TYPE_IMAGE:
								message.setStatus(Constants.STATUS_WAIT_FOR_DOWNLOAD);
								message.setRowId(utilityDAO.addEntrySingle(message) + "");
								if (message.getUserType().equals(Constants.USER_TYPE_OTHER))
									SingleChatFragment.addImageLeft(message.getFriendName(), message);
								else SingleChatFragment.addImageRight(message.getFriendName(), message);
								break;
							case Constants.MESSAGE_TYPE_JOIN:
								message.setStatus(Constants.STATUS_READ);
								message.setRowId(utilityDAO.addEntrySingle(message) + "");
								SingleChatFragment.addLog(message.getMessage());
								break;
						}
					} else {
						if (message.getFriendName().equalsIgnoreCase(appSession.getUserId())) {
							Log.i(TAG, "processPacket(-) : SENDER AND RECEIVER BOTH ARE SAME");
							message.setUserType(Constants.USER_TYPE_ME);
						}
						Log.i(TAG,"processPacket(-) : MyChat is Running- but minimized");
						message.setStatus(Constants.STATUS_UNREAD);
						message.setRowId(utilityDAO.addEntrySingle(message)+"");
						UtilityDAO.playReceiveMsgSound(getApplicationContext());
						setGroupNotifications(message.getFriendId(), message.getFriendName(),Constants.CONST_PRIVATE);
					}

					///For GROUP SCREEN
					if (FriendsFragment.isFriendActive) {
						Log.i(TAG, "processPacket(-) : FriendActive");
						FriendsFragment.refreshList(message);
					}else {
						Log.i(TAG,"processPacket(-) : friend not active in chat");
					}

				}
			});
		}
	};

	private Emitter.Listener onTyping = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					Log.i(TAG, "response " + Constants.CHAT_TYPING + args[0].toString());
					Message message=new NodeJSDAO(getBaseContext()).parseGroupMessages(args[0].toString());
					if (message == null) {
						Log.i(TAG, "responce null h ");
						return;
					}
					if (!GroupChatFragment.isChatMinimize && !TextUtils.isEmpty(GroupChatFragment.groupId) && message.getGroupId().equalsIgnoreCase(GroupChatFragment.groupId)) {
						GroupChatFragment.addTyping(message.getMessage());
					}
					///For GROUP SCREEN
					if (GroupsFragment.isGroupActive) {
						Log.i(TAG, "processPacket(-) : GroupActive");
						GroupsFragment.addTyping(message);
					}else {
						Log.i(TAG, "processPacket(-) : group not active in chat");
					}
				}
			});
		}
	};
	private Emitter.Listener onStopTyping = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, "response " + Constants.CHAT_STOP_TYPING + args[0].toString());
					Message message = new NodeJSDAO(getBaseContext()).parseGroupMessages(args[0].toString());
					if (message == null) {
						Log.i(TAG, "responce null h ");
						return;
					}
					if (!GroupChatFragment.isChatMinimize && !TextUtils.isEmpty(GroupChatFragment.groupId) && message.getGroupId().equalsIgnoreCase(GroupChatFragment.groupId)) {
						GroupChatFragment.removeTyping(message.getMessage());
					}
					///For GROUP SCREEN
					if (GroupsFragment.isGroupActive) {
						Log.i(TAG, "processPacket(-) : GroupActive");
						GroupsFragment.removeTyping(message);
					}else {
						Log.i(TAG, "processPacket(-) : group not active in chat");
					}
				}
			});
		}
	};
	private Emitter.Listener onPrivateTyping = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					Log.i(TAG, "response " + Constants.CHAT_PRIVATE_TYPING + args[0].toString());
					Message message=new NodeJSDAO(getBaseContext()).parsePrivateMessages(args[0].toString());
					if (message == null) {
						Log.i(TAG, "responce null h ");
						return;
					}
					if (!SingleChatFragment.isPrivateChatMinimize && !TextUtils.isEmpty(SingleChatFragment.friendId) && message.getFriendId().equalsIgnoreCase(SingleChatFragment.friendId)) {
						SingleChatFragment.addTyping(message.getMessage());
					}
					///For GROUP SCREEN
					if (FriendsFragment.isFriendActive) {
						Log.i(TAG, "processPacket(-) : FriendActive");
						FriendsFragment.addTyping(message);
					}else {
						Log.i(TAG, "processPacket(-) : friend not active in chat");
					}
				}
			});
		}
	};
	private Emitter.Listener onStopPrivateTyping = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, "response " + Constants.CHAT_STOP_PRIVATE_TYPING + args[0].toString());
					Message message = new NodeJSDAO(getBaseContext()).parsePrivateMessages(args[0].toString());
					if (message == null) {
						Log.i(TAG, "responce null h ");
						return;
					}
					if (!SingleChatFragment.isPrivateChatMinimize && !TextUtils.isEmpty(SingleChatFragment.friendId) && message.getFriendId().equalsIgnoreCase(SingleChatFragment.friendId)) {
						SingleChatFragment.removeTyping(message.getMessage());
					}
					///For GROUP SCREEN
					if (FriendsFragment.isFriendActive) {
						Log.i(TAG, "processPacket(-) : FriendActive");
						FriendsFragment.removeTyping(message);
					}else {
						Log.i(TAG, "processPacket(-) : friend not active in chat");
					}
				}
			});
		}
	};


	public static void stopGroupTyping(Message message){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Constants.PN_USER_ID, appSession.getUserId());
			jsonObject.put(Constants.PN_USER_NAME, appSession.getUserName());
			jsonObject.put(Constants.PN_GROUP_ID, message.getToId());
			jsonObject.put(Constants.PN_GROUP_NAME,message.getToName());
		} catch (JSONException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		// perform the TYPING attempt.
		Log.i(TAG, "response emit for " + Constants.CHAT_STOP_TYPING + " " + jsonObject.toString());
		mSocket.emit(Constants.CHAT_STOP_TYPING, jsonObject.toString());
	}

	public static void stopPrivateTyping(Message message){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Constants.PN_FROM_ID, appSession.getUserId());
			jsonObject.put(Constants.PN_FROM_NAME, appSession.getUserName());
			jsonObject.put(Constants.PN_TO_ID, message.getFriendId());
			jsonObject.put(Constants.PN_TO_NAME,message.getFriendName());
		} catch (JSONException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		// perform the TYPING attempt.
		Log.i(TAG, "response emit for " + Constants.CHAT_STOP_PRIVATE_TYPING + " " + jsonObject.toString());
		mSocket.emit(Constants.CHAT_STOP_PRIVATE_TYPING, jsonObject.toString());
	}


	/* method for checking Network availability */
	public boolean isNetworkAvailable() {

		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error r) {
			r.printStackTrace();
		}
		return false;
	}

	

	
	
	int notifyID = 0;
	Intent notificationIntent;
	NotificationCompat.Builder mBuilder;
	NotificationManager mNotificationManager;

private void setGroupNotifications(String groupId,String groupName,String chatMode) {
	String msg = "";
		if (chatMode.equals(Constants.CONST_GROUP)){
			if (new UtilityDAO(getBaseContext()).getUnreadUserCountGroup(appSession.getUserId(), groupId) == 1) {
				notificationIntent = new Intent(getBaseContext(), MainActivity.class);
			} else {
				notificationIntent = new Intent(getBaseContext(), MainActivity.class);
			}
			int count = new UtilityDAO(getBaseContext()).getCountAllGroup(appSession.getUserId());
			if (count == 1) {
				msg = getBaseContext().getResources().getString(
						R.string.You_have_a_new_message_from)
						+ " " + groupName;
			} else {
				msg = count + " "
						+ getBaseContext().getResources().getString(R.string.new_messages);

			}
		}else if (chatMode.equals(Constants.CONST_PRIVATE)){
			if (new UtilityDAO(getBaseContext()).getUnreadUserCountSingle(appSession.getUserId(), groupId) == 1) {
				notificationIntent = new Intent(getBaseContext(), MainActivity.class);
			} else {
				notificationIntent = new Intent(getBaseContext(), MainActivity.class);
			}
			int count = new UtilityDAO(getBaseContext()).getCountAllSingle(appSession.getUserId());
			if (count == 1) {
				msg = getBaseContext().getResources().getString(
						R.string.You_have_a_new_message_from)
						+ " " + groupName;
			} else {
				msg = count + " "
						+ getBaseContext().getResources().getString(R.string.new_messages);

			}
		}

		if (appSession.getUserId().equalsIgnoreCase("2")) {
			notificationIntent.putExtra("chatUserId", "1");
			notificationIntent.putExtra("chatUserName", "sky");
			notificationIntent.putExtra("chatUserImage", "");
		}else {
			notificationIntent.putExtra("chatUserId", "1");
			notificationIntent.putExtra("chatUserName", "pk");
			notificationIntent.putExtra("chatUserImage", "");
		}
		

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		Bitmap largeIcon = BitmapFactory.decodeResource(getBaseContext().getResources(),
				R.drawable.ic_launcher);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		


		// onReceive();
		mBuilder = new NotificationCompat.Builder(getBaseContext())
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(getBaseContext().getResources().getString(R.string.app_name))
				.setLargeIcon(largeIcon).setContentText(msg)
				.setAutoCancel(true).setNumber(0).setSound(alarmSound).setVibrate(new long[] { 100, 250, 100, 250, 100, 250 });
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
		if (new UtilityDAO(getBaseContext()).getUnreadUserCountGroup(appSession.getUserId(), groupId) == 1) {
			stackBuilder.addParentStack(MainActivity.class);
		} else {
			stackBuilder.addParentStack(MainActivity.class);
		}

		stackBuilder.addNextIntent(notificationIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		mNotificationManager = (NotificationManager) getBaseContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifyID, mBuilder.build());
	}
}
