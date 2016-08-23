package com.chat.pk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.pk.Adapter.SingleMessageAdapter;
import com.chat.pk.DAO.NodeJSDAO;
import com.chat.pk.DAO.UtilityDAO;
import com.chat.pk.DTO.ChatDTO;
import com.chat.pk.Upload.UploadService;
import com.chat.pk.Util.Constants;
import com.chat.pk.Util.OnItemClickListener;
import com.chat.pk.Util.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;


/**
 * A chat fragment containing messages view and input form.
 */


/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class SingleChatFragment extends BaseFragment {
    private static final String USER_AGENT = "UploadServiceDemo/" + BuildConfig.VERSION_NAME;
    public static TextView tv_header_subtitle,tv_header_title;
    ImageView iv_menu;
    int u = 0;
    String TAG = getClass().getSimpleName();
    public  static UtilityDAO utilityDAO = null;
    AppSession appSession;
    private boolean mTyping = false;
    public static RecyclerView mMessagesView;
    private EditText et_message_input;
    public static List<Message> mMessages = new ArrayList<Message>();
    public static SingleMessageAdapter mAdapter;
    private Handler mTypingHandler = new Handler();
    private Handler mScrollingHandler = new Handler();
    private Socket mSocket;
    Bundle bundle;
    public static boolean isPrivateChatMinimize=false;
    public static String friendId="";
    String friendName="";
    public static  Context context;
    public static int page=1;
    public static int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    // _____________________________________________________PATIL
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_PICTURE_FROMGallery = 2;
    private static final int PICK_VIDEOS = 3;
    private static final int PICK_AUDIOS = 4;

    LinearLayoutManager mLinearLayoutManager;
    ImageView iv_toast_top,iv_toast_bottom;
    private String[] items;
    private AlertDialog.Builder builder;
    private ArrayAdapter<String> adapterSelector;
    View view;
    public SingleChatFragment() {
        super();
    }
//https://romannurik.github.io/AndroidAssetStudio/nine-patches.html
    //https://www.learn2crack.com/2014/08/android-upload-image-node-js-server.html/2
    //https://github.com/gotev/android-upload-service
    //http://www.androidhive.info/2012/04/android-downloading-file-by-showing-progress-bar/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isPrivateChatMinimize=false;
        context=activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        utilityDAO=new UtilityDAO(getActivity());
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        appSession=new AppSession(getActivity());
        bundle = getArguments();
        items = new String[] { "Camera", "Gallery"};
        adapterSelector = new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_item, items);
        builder = new AlertDialog.Builder(getActivity());
        if (bundle != null) {
            friendId = bundle.getString(Constants.PN_TO_ID);
            friendName= bundle.getString(Constants.PN_TO_NAME);
            Log.i(getClass().getName(), "friendId : " +friendId);
            Log.i(getClass().getName(), "friendName : " + friendName);
            u = utilityDAO.updateSingleReadAllTxt(appSession.getUserId(), friendId, Constants.STATUS_READ);
            u = utilityDAO.updateSingleReadAllImage(appSession.getUserId(), friendId, Constants.STATUS_WAIT_FOR_DOWNLOAD);
            Log.i(getClass().getName(), "Update success : " + u);
            u = utilityDAO.updateSingleErrorAllImage(appSession.getUserId(), friendId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.chat_fragment, container, false);
        return view;
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isPrivateChatMinimize=false;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        friendId = "";
        isPrivateChatMinimize = true;
        UploadService.stopAllUploads();
        if (toast!=null);
        toast.cancel();
        stopAllDownloads();
        if (mAdapter!=null && mAdapter.uploadReceiver!=null)
            mAdapter.uploadReceiver.unregister(context);

    }
    /**
     * Stop all the active uploads.
     */
    public  void stopAllDownloads() {
        if (mAdapter.downloadProgressHolders.isEmpty()) {
            return;
        }
        // using iterator instead for each loop, because it's faster on Android
        Iterator<String> iterator = mAdapter.downloadProgressHolders.keySet().iterator();
        while (iterator.hasNext()) {
            SingleMessageAdapter.TaskForDownloadImage taskToCancel = mAdapter.downloadProgressHolders.get(iterator.next());
            taskToCancel.cancel(true);
        }
        mAdapter.downloadProgressHolders.clear();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setView();

        userInfo(Utilities.getInstance(getActivity()).getUTCTime());
        setDataFromLocal(Utilities.getInstance(getActivity()).getUTCTime());
        createToast();
    }
       private void initView(View view){

        iv_toast_top=(ImageView)view.findViewById(R.id.iv_toast_top);
        iv_toast_bottom=(ImageView)view.findViewById(R.id.iv_toast_bottom);
        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mMessagesView.setLayoutManager(mLinearLayoutManager);
        tv_header_subtitle=(TextView)view.findViewById(R.id.tv_header_subtitle);
        tv_header_title=(TextView)view.findViewById(R.id.tv_header_title);
        iv_menu=(ImageView)view.findViewById(R.id.iv_menu);
        mMessagesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (firstVisibleItem == 0 && toast != null) {
                    toast.cancel();
                } else
                    showToast(firstVisibleItem);

                if (dy > 0) {
                    if (iv_toast_bottom.getVisibility()==View.GONE){
                        iv_toast_bottom.setVisibility(View.VISIBLE);
                    }
                    if (iv_toast_top.getVisibility()==View.VISIBLE){
                        iv_toast_top.setVisibility(View.GONE);
                    }
                    Log.i("onScrolled ","onScrolled 1");
                } else {
                    Log.i("onScrolled ", "onScrolled 2");

                    if (iv_toast_bottom.getVisibility()==View.VISIBLE){
                        iv_toast_bottom.setVisibility(View.GONE);
                    }
                    if (iv_toast_top.getVisibility()==View.GONE){
                        iv_toast_top.setVisibility(View.VISIBLE);
                    }
                }
                mScrollingHandler.removeCallbacks(onScrollTimeout);
                mScrollingHandler.postDelayed(onScrollTimeout, Constants.TYPING_SCROLL_LENGTH);
            }
        });

        et_message_input = (EditText) view.findViewById(R.id.et_message_input);
        et_message_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSendText();
                    return true;
                }
                return false;
            }
        });
        et_message_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(friendId)) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    if (mSocket == null) return;
                    if (!mSocket.connected()) return;

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.PN_FROM_ID, appSession.getUserId());
                        jsonObject.put(Constants.PN_FROM_NAME, appSession.getUserName());
                        jsonObject.put(Constants.PN_TO_ID, friendId);
                        jsonObject.put(Constants.PN_TO_NAME, friendName);
                    } catch (JSONException e) { // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }


                    // perform the TYPING attempt.
                    Log.i(TAG, "response emit for " + Constants.CHAT_PRIVATE_TYPING + " " + jsonObject.toString());
                    mSocket.emit(Constants.CHAT_PRIVATE_TYPING, jsonObject.toString());
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, Constants.TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSendText();
            }
        });
        LinearLayout ll_back=(LinearLayout)view.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFileChooser(builder);
            }
        });
           iv_toast_top.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mMessagesView.scrollToPosition(0);
               }
           });
           iv_toast_bottom.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
               }
           });
    }
    private Runnable onScrollTimeout = new Runnable() {
        @Override
        public void run() {
           iv_toast_bottom.setVisibility(View.GONE);
            iv_toast_top.setVisibility(View.GONE);
        }
    };
    private void createFileChooser(AlertDialog.Builder builder) {
        // http://www.londatiga.net/it/how-to-create-android-image-picker/
        builder.setTitle(getString(R.string.pick_image_from));
        builder.setAdapter(adapterSelector,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            pickFromCamera();
                            dialog.cancel();
                        } else if (item == 1) {
                            pickFromGallery();
                            dialog.cancel();
                        }
                    }
                });
        builder.show();
    }
    public void pickFromGallery() {
        Intent intent1 = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent1.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent1, "Complete action using"),PICK_PICTURE_FROMGallery);
    }

    private Uri mImageCaptureUri;
    public void pickFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),"pk_client" + String.valueOf(System.currentTimeMillis())
                        + ".jpg");
        mImageCaptureUri = Uri.fromFile(file);

        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "onActivityResult ");

        if (resultCode != Activity.RESULT_OK)
            return;

        String path = "";

        if (requestCode == PICK_PICTURE_FROMGallery) {
            path =  Utilities.getInstance(context).getPathFromURI(data.getData());; // from Gallery
            Log.v("PICK_PICTURE", "PICK_PICTURE " + path);
            if (path != null) {
                sendUploadImage("pk", path);
            }else {
                showToast("File not found");
            }
        }
        if (requestCode == PICK_FROM_CAMERA) {
            path = mImageCaptureUri.getPath();// from Camera
            Log.v("CAPTURE_PICTURE", "CAPTURE_PICTURE " + path);
            if (path != null) {
                sendUploadImage("pk", path);
            }else {
                showToast("File not found");
            }
        }
    }

    private void setView(){
        page=1;
        isChatLoading=true;
        isSoundActive=false;
        tv_header_title.setText(friendName);
        mMessages = new ArrayList<Message>();
        mAdapter = new SingleMessageAdapter(getActivity(), mMessages,onItemClickCallback,onImageClickCallback);
        mMessagesView.setAdapter(mAdapter);

    }
    public static boolean isChatLoading=false;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {


           if ( mAdapter.getItem(position).getType()==Message.TYPE_LOAD_EARLIER){
               if (!isChatLoading && mAdapter.getItemCount()>3){
                   for (int i=0;i<mAdapter.getItemCount()-1;i++){
                       if (!TextUtils.isEmpty(mAdapter.getItem(i).getTime())){
                           page=page+1;
                           isChatLoading=true;
                           isSoundActive=false;
                           setDataFromLocal(mAdapter.getItem(i).getTime());//load more from local
                           break;
                       }
                   }
               }
           }
        }
    };
    private OnItemClickListener.OnItemClickCallback onImageClickCallback = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            if (!TextUtils.isEmpty(mMessages.get(position).getFilePath()) && (mMessages.get(position).getStatus().equals(Constants.STATUS_UPLOADED)||mMessages.get(position).getStatus().equals(Constants.STATUS_SENT)  || mMessages.get(position).getStatus().equals(Constants.STATUS_DOWNLOADED))){
                FullViewFragment fullViewFragment = new FullViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CN_FILE_PATH, mMessages.get(position).getFilePath());
                bundle.putString(Constants.CN_FILE_NAME, Constants.CHAT_IMAGES_URL +mMessages.get(position).getFileName());
                fullViewFragment.setArguments(bundle);
                addFragment(fullViewFragment, "FullViewFragment");
            }

        }
    };

       @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDataFromLocal(String time){
        Cursor cursor = utilityDAO.getPrivateChatByTime(appSession.getUserId(), friendId, time);
        Log.i(" size", "" + cursor.getCount());

        Log.i("CURRENT_TIME ","CURRENT_TIME " +time);

        if (mMessages.size()>0 && mMessages.get(0).getType()==Message.TYPE_LOAD_EARLIER){
            mMessages.remove(0);
            mAdapter.notifyItemRemoved(0);
        }

        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            List<Message> mMessagesTemp=new ArrayList<>();
            mMessagesTemp.addAll(mMessages);
            mMessages.clear();
            addLoadEarlier(getResources().getString(R.string.load_earlier_messages));

            while (!cursor.isBeforeFirst()) {
                Message message=new Message();
                message.setRowId(cursor.getString(0));
                message.setMessage(cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));
                message.setTime(cursor.getString(cursor.getColumnIndex(Constants.CN_TIME)));
                message.setStatus(cursor.getString(cursor.getColumnIndex(Constants.CN_STATUS)));
                message.setFilePath(cursor.getString(cursor.getColumnIndex(Constants.CN_FILE_PATH)));
                message.setFileName(cursor.getString(cursor.getColumnIndex(Constants.CN_FILE_NAME)));
                message.setMessageType(cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE_TYPE)));
                message.setUserType(cursor.getString(cursor.getColumnIndex(Constants.CN_USER_TYPE)));
                message.setFriendId(cursor.getString(cursor.getColumnIndex(Constants.CN_FROM_ID)));
                message.setFriendName(cursor.getString(cursor.getColumnIndex(Constants.CN_FROM_NAME)));
                message.setUserId(cursor.getString(cursor.getColumnIndex(Constants.CN_USER_ID)));
                message.setUserName(cursor.getString(cursor.getColumnIndex(Constants.CN_USER_NAME)));


                Log.i("CN_TIME ", "CN_TIME AND TYPE" + cursor.getString(cursor.getColumnIndex(Constants.CN_TIME)) + " TYPE" + cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE_TYPE)));
                Log.i("CN_MESSAGE ", "CN_MESSAGE AND CN_MESSAGE" + cursor.getString(cursor.getColumnIndex(Constants.CN_TIME))+" CN_MESSAGE" +cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));

                if (cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE_TYPE)).equals(Constants.MESSAGE_TYPE_TEXT)){
                    if (cursor.getString(cursor.getColumnIndex(Constants.CN_USER_TYPE)).equals(Constants.USER_TYPE_ME)){
                        addMessageRight(cursor.getString(cursor.getColumnIndex(Constants.CN_USER_NAME)),message);
                    }else{
                        addMessageLeft(cursor.getString(cursor.getColumnIndex(Constants.CN_FROM_NAME)), message);
                    }
                }else if (cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE_TYPE)).equals(Constants.MESSAGE_TYPE_IMAGE)){
                    if (cursor.getString(cursor.getColumnIndex(Constants.CN_USER_TYPE)).equals(Constants.USER_TYPE_ME)){
                        addImageRight(cursor.getString(cursor.getColumnIndex(Constants.CN_USER_NAME)), message);
                    }else{
                        addImageLeft(cursor.getString(cursor.getColumnIndex(Constants.CN_FROM_NAME)), message);
                    }
                }else  if (cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE_TYPE)).equals(Constants.MESSAGE_TYPE_JOIN)){
                    if (cursor.getString(cursor.getColumnIndex(Constants.CN_USER_TYPE)).equals(Constants.USER_TYPE_OTHER)){
                        addLog(cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));
                    }
                }
                cursor.moveToPrevious();
            }

            int newListLastPos =mMessages.size()-1;
            int oldListFirstPos =mMessages.size();
            mMessages.addAll(mMessagesTemp);

            //remove log of old time
            if (mMessagesTemp.size()>0 && mMessages.size()>0){
                for (int i=oldListFirstPos;i<mMessages.size();i++)
                     if (Utilities.getInstance(context).getTimeLog(mMessages.get(newListLastPos).getTime()).equals(Utilities.getInstance(context).getTimeLog(mMessages.get(i).getTime())) && mMessages.get(i).getType()==Message.TYPE_TIME_LOG){
                        mMessages.remove(i);
                        mAdapter.notifyItemRemoved(i);
                        break;
                    }
            }
        }
        mTypingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page == 1)
                    isChatLoading = false;
                scrollToBottom();
                isChatLoading = false;
                isSoundActive = true;
            }
        }, 200);
    }
    private void setDataFromLocal(){
        Cursor cursor = utilityDAO.getGroupChat(appSession.getUserId(),friendId);
        Log.i(" size", "" + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE_TYPE)).equals(Constants.MESSAGE_TYPE_TEXT)){
                    Message message=new Message();
                    message.setMessage(cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));
                    message.setTime(cursor.getString(cursor.getColumnIndex(Constants.CN_TIME)));
                    if (cursor.getString(cursor.getColumnIndex(Constants.CN_USER_TYPE)).equals(Constants.USER_TYPE_ME)){
                        addMessageRight(cursor.getString(cursor.getColumnIndex(Constants.CN_USER_NAME)),message);
                    }else{
                        addMessageLeft(cursor.getString(cursor.getColumnIndex(Constants.CN_FROM_NAME)),message);
                    }
                }else  if (cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE_TYPE)).equals(Constants.MESSAGE_TYPE_JOIN)){
                    if (cursor.getString(cursor.getColumnIndex(Constants.CN_USER_TYPE)).equals(Constants.USER_TYPE_OTHER)){
                        addLog(cursor.getString(cursor.getColumnIndex(Constants.CN_MESSAGE)));
                    }
                }
                cursor.moveToNext();
            }
        }
        mTypingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToBottom();
                isSoundActive = true;
            }
        }, 200);
    }

    private void setDataFromServer(List<Message> chatHistory){
       if (chatHistory == null) {
           return;
       }
        Log.i(" size", "" + chatHistory.size());
        if (chatHistory.size() > 0) {
            if (mMessages.size()>0 && mMessages.get(0).getType()==Message.TYPE_LOAD_EARLIER){
                mMessages.remove(0);
                mAdapter.notifyItemRemoved(0);
            }
            List<Message> mMessagesTemp=new ArrayList<>();
            mMessagesTemp.addAll(mMessages);
            mMessages.clear();
            addLoadEarlier(getResources().getString(R.string.load_earlier_messages));
            for (int i=0;i<chatHistory.size();i++){
                if (chatHistory.get(i).getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)){
                    Message message=new Message();
                    message.setMessage(chatHistory.get(i).getMessage());
                    message.setTime(chatHistory.get(i).getTime());
                    if (chatHistory.get(i).getUserType().equals(Constants.USER_TYPE_ME)){
                        addMessageRight(chatHistory.get(i).getUserName(),message);
                    }else{
                        addMessageLeft(chatHistory.get(i).getFromName(), message);
                    }
                }else  if (chatHistory.get(i).getMessageType().equals(Constants.MESSAGE_TYPE_JOIN)){
                    if (chatHistory.get(i).getUserType().equals(Constants.USER_TYPE_OTHER)){
                        addLog(chatHistory.get(i).getMessage());
                    }
                }
            }


        }
        mTypingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page == 1)
                    isChatLoading = false;
                scrollToBottom();
                isChatLoading = false;
                isSoundActive = true;
            }
        }, 200);
    }


    Toast toast;
    private TextView tv_toast;
    private void createToast(){
        //Creating the LayoutInflater instance
        LayoutInflater li = getActivity().getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        View layout = li.inflate(R.layout.item_log,(ViewGroup) getActivity().findViewById(R.id.custom_toast_layout));
        tv_toast=(TextView)layout.findViewById(R.id.tv_msg);
        //Creating the Toast object
        toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_SHORT);
        // set position
        int margin = getResources().getDimensionPixelSize(R.dimen.toast_height);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL, 0, margin);
        toast.setView(layout);//setting the view of custom toast layout

    }

    private void showToast(int pos){
        if ( mAdapter.getItem(pos).getType()==Message.TYPE_MESSAGE_LEFT||mAdapter.getItem(pos).getType()==Message.TYPE_MESSAGE_RIGHT){
            tv_toast.setText(Utilities.getInstance(context).getTimeLog(mMessages.get(pos).getTime()));
            toast.show();
        }
    }

    public static void checkTimeLog(Message message){
        if (mMessages.size()==0){
            addTimeLog(message.getTime());
        }else  if (mMessages.size()>0){
            if (!Utilities.getInstance(context).getTimeLog(mMessages.get(mMessages.size() - 1).getTime()).equals(Utilities.getInstance(context).getTimeLog(message.getTime()))){
                addTimeLog(message.getTime());
            }
        }
    }
    public static void addTimeLog(String time) {

        Message message1=new Message();
        message1.setMessage(Utilities.getInstance(context).getTimeLog(time));
        message1.setTime(time);
        message1.setType(Message.TYPE_TIME_LOG);

        mMessages.add(message1);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }
    public static void addLog(String message) {

        Message message1 = new Message();
        message1.setMessage(message);
        message1.setType(Message.TYPE_LOG);

        mMessages.add(message1);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }
    public static void addLoadEarlier(String message) {

        Message message1 = new Message();
        message1.setMessage(message);
        message1.setType(Message.TYPE_LOAD_EARLIER);
        mMessages.add(message1);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
    }
    private static boolean isSoundActive=false;
    public static void addMessageLeft(String username, Message message) {
        checkTimeLog(message);
        message.setType(Message.TYPE_MESSAGE_LEFT);
        message.setUserName(username);
        mMessages.add(message);
        mAdapter.notifyItemInserted(mMessages.size() - 1);

        if ((lastVisibleItem == totalItemCount - 1) && mMessages.size()>3){
            scrollToBottom();
        }else {
            if (isSoundActive)
            utilityDAO.playReceiveMsgSound(context);
        }
    }

    public static void addMessageRight(String username,Message message) {
        checkTimeLog(message);
        message.setType(Message.TYPE_MESSAGE_RIGHT);
        message.setUserName(username);
        mMessages.add(message);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    public static int addImageRight(String username, Message message) {
        checkTimeLog(message);
        message.setType(Message.TYPE_IMAGE_RIGHT);
        message.setUserName(username);
        mMessages.add(message);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
        return mMessages.size() - 1;
    }
    public static int addImageLeft(String username,Message message) {
        checkTimeLog(message);
        message.setType(Message.TYPE_IMAGE_LEFT);
        message.setUserName(username);
        mMessages.add(message);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        if ((lastVisibleItem == totalItemCount - 1) && mMessages.size()>3){
            scrollToBottom();
        }else {
            if (isSoundActive)
                utilityDAO.playReceiveMsgSound(context);
        }
        return mMessages.size() - 1;
    }



    public static void addTyping(String message) {
        tv_header_subtitle.setText(message);
    }

    public static void removeTyping(String message) {
        tv_header_subtitle.setText(message);
    }

    private void userInfo(String time){


        if (TextUtils.isEmpty(friendId)) return;
        if (TextUtils.isEmpty(appSession.getUserId())) return;
        if (mSocket==null)return;
        if (!mSocket.connected())return;


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PN_FROM_ID, appSession.getUserId());
            jsonObject.put(Constants.PN_TO_ID,friendId);
            jsonObject.put(Constants.PN_CURRENT_TIME,time);
            jsonObject.put(Constants.PN_CHAT_TYPE,Constants.CONST_PRIVATE);
        } catch (JSONException e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        // perform the sending message attempt.
        Log.i(TAG,"response emit for "+Constants.CHAT_FRIEND_INFO+" "+jsonObject.toString());
        mSocket.emit(Constants.CHAT_FRIEND_INFO, jsonObject.toString(), userInfoAcknowledgement);
    }

    private void attemptSendText() {
        if (TextUtils.isEmpty(friendId)) return;
        if (mSocket==null)return;
        if (!mSocket.connected())return;

        mTyping = false;

        String message = et_message_input.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            et_message_input.requestFocus();
            return;
        }

        et_message_input.setText("");


        Message message1=new Message();
        message1.setStatus(Constants.STATUS_SENT);
        message1.setMessageType(Constants.MESSAGE_TYPE_TEXT);
        message1.setUserType(Constants.USER_TYPE_ME);
        message1.setMessage(message);
        message1.setFriendId(friendId);
        message1.setFriendName(friendName);
        message1.setUserId(appSession.getUserId());
        message1.setUserName(appSession.getUserName());
        message1.setTime(Utilities.getInstance(getActivity()).getUTCTime());
        message1.setRowId(utilityDAO.addEntrySingle(message1) + "");
        addMessageRight(appSession.getUserName(), message1);

        utilityDAO.playSendMsgSoundSound(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PN_FROM_ID, appSession.getUserId());
            jsonObject.put(Constants.PN_FROM_NAME, appSession.getUserName());
            jsonObject.put(Constants.PN_TO_ID, friendId);
            jsonObject.put(Constants.PN_TO_NAME,friendName);
            jsonObject.put(Constants.PN_MESSAGE,message);
            jsonObject.put(Constants.PN_MESSAGE_TYPE,Constants.MESSAGE_TYPE_TEXT);
            jsonObject.put(Constants.PN_CURRENT_TIME, Utilities.getInstance(getActivity()).getUTCTime());
        } catch (JSONException e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        // perform the sending message attempt.
        Log.i(TAG, "response emit for " + Constants.CHAT_PRIVATE_MESSAGE + " " + jsonObject.toString());
        mSocket.emit(Constants.CHAT_PRIVATE_MESSAGE, jsonObject.toString(), sendTextAcknowledgement);
    }

    private void sendUploadImage(String message,String path) {
        if (TextUtils.isEmpty(friendId)) return;
        if (TextUtils.isEmpty(path)) return;
        if (TextUtils.isEmpty(message)) return;
        if (mSocket==null)return;
        if (!mSocket.connected())return;

        Message message1=new Message();
        message1.setStatus(Constants.STATUS_WAIT_FOR_UPLOADING);
        message1.setMessageType(Constants.MESSAGE_TYPE_IMAGE);
        message1.setUserType(Constants.USER_TYPE_ME);
        message1.setFileName("");
        message1.setFilePath(path);
        message1.setMessage(message);
        message1.setFriendId(friendId);
        message1.setFriendName(friendName);
        message1.setUserId(appSession.getUserId());
        message1.setUserName(appSession.getUserName());
        message1.setTime(Utilities.getInstance(getActivity()).getUTCTime());
        message1.setRowId(utilityDAO.addEntrySingle(message1) + "");
        int position =addImageRight(appSession.getUserName(), message1);
        utilityDAO.playSendMsgSoundSound(context);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
    private void leave() {
        friendId = "";
        mSocket.disconnect();
        mSocket.connect();
        getActivity().finish();
    }

    public static void scrollToBottom() {
        if (isChatLoading)
            return;
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            Message message=new Message();
            message.setFriendId(friendId);
            message.setFriendName(friendName);
            ChatService.stopPrivateTyping(message);
        }
    };

    private Ack sendTextAcknowledgement=new Ack() {
         @Override
         public void call(final Object... args) {
             // TODO Auto-generated method stub
             if ( getActivity()==null)
                 return;

             getActivity().runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Log.i(TAG, "response is " + Constants.CHAT_PRIVATE_MESSAGE + " " + args[0].toString());
                     String response = new NodeJSDAO(getActivity()).parseSuccess(args[0].toString());
                     if (TextUtils.isEmpty(response)) {
                         Toast.makeText(getActivity(), "Response is null", Toast.LENGTH_SHORT).show();
                     }
                     // removeTyping(username);
                     // addMessage(username, message);
                 }
             });
         }
     };

    private Ack userInfoAcknowledgement=new Ack() {
        @Override
        public void call(final Object... args) {
            // TODO Auto-generated method stub

            if ( getActivity()==null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "response is " + Constants.CHAT_FRIEND_INFO + " " + args[0].toString());
                    ChatDTO chatDTO = new NodeJSDAO(getActivity()).parseUserInfo(args[0].toString());
                    if (chatDTO == null) {
                        Toast.makeText(getActivity(), "Response is null", Toast.LENGTH_SHORT).show();
                    } else {
                        tv_header_subtitle.setText(chatDTO.getMessage());
                        if (chatDTO.getSuccess().equals(Constants.SUCCESS_1)) {
                           /* if (chatDTO.getChatHistory() != null && chatDTO.getChatHistory().size() > 0)
                                setDataFromServer(chatDTO.getChatHistory());
                            else {
                                if (mMessages.size() > 0 && mMessages.get(0).getType() == Message.TYPE_LOAD_EARLIER) {
                                    mMessages.remove(0);
                                    mAdapter.notifyItemRemoved(0);
                                }
                            }*/
                        }
                    }
                }
            });
        }
    };
}

