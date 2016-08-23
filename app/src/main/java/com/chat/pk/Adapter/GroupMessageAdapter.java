package com.chat.pk.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.pk.BuildConfig;
import com.chat.pk.ChatApplication;
import com.chat.pk.DAO.NodeJSDAO;
import com.chat.pk.DAO.UtilityDAO;
import com.chat.pk.MainActivity;
import com.chat.pk.Message;
import com.chat.pk.R;
import com.chat.pk.Upload.MultipartUploadRequest;
import com.chat.pk.Upload.UploadNotificationConfig;
import com.chat.pk.Upload.UploadService;
import com.chat.pk.Upload.UploadServiceBroadcastReceiver;
import com.chat.pk.Util.Constants;
import com.chat.pk.Util.OnItemClickListener;
import com.chat.pk.Util.RateTextCircularProgressBar;
import com.chat.pk.Util.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {
    private static final String USER_AGENT = "UploadServiceDemo/" + BuildConfig.VERSION_NAME;
    private List<Message> mMessages;
    private int[] mUsernameColors;
    Context context;
    private Socket mSocket;
    String TAG = getClass().getSimpleName();
    public  static UtilityDAO utilityDAO = null;
    OnItemClickListener.OnItemClickCallback onItemClickCallback;
    OnItemClickListener.OnItemClickCallback onImageClickCallback;
    public GroupMessageAdapter(Context context, List<Message> messages, OnItemClickListener.OnItemClickCallback onItemClickCallback,
                               OnItemClickListener.OnItemClickCallback onImageClickCallback) {
        mMessages = messages;
        this.context=context;
        ChatApplication app = (ChatApplication)context.getApplicationContext();
        mSocket = app.getSocket();
        utilityDAO=new UtilityDAO(context);
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
        this.onItemClickCallback=onItemClickCallback;
        this.onImageClickCallback=onImageClickCallback;
        uploadReceiver.register(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
        case Message.TYPE_MESSAGE_LEFT:
            layout = R.layout.list_item_chat_text_left;
            break;
        case Message.TYPE_MESSAGE_RIGHT:
            layout = R.layout.list_item_chat_text_right;
            break;
        case Message.TYPE_IMAGE_LEFT:
            layout = R.layout.list_item_chat_image_left;
            break;
        case Message.TYPE_IMAGE_RIGHT:
            layout = R.layout.list_item_chat_image_right;
            break;
        case Message.TYPE_LOG:
            layout = R.layout.item_log;
            break;
        case Message.TYPE_TIME_LOG:
             layout = R.layout.item_log;
             break;
        case Message.TYPE_ACTION:
            layout = R.layout.item_action;
            break;
        case Message.TYPE_LOAD_EARLIER:
             layout = R.layout.item_load_earier;
             break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUserName());
        viewHolder.setTime(Utilities.getInstance(context).getUpdateTime(message.getTime()));
        viewHolder.setMessageImage(position);
        viewHolder.setStatus(message.getStatus());
        viewHolder.setItemClick(position);
        viewHolder.setCentreClick(viewHolder,position);
        viewHolder.setItemDelete(position);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public Message getItem(int position) {
        return mMessages.get(position);
    }
    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_user_name,tv_msg,tv_time;
        private LinearLayout ll_item;
        private RateTextCircularProgressBar rate_progress_bar;
        private ImageView msg_img,msg_img_download;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_msg);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            ll_item=(LinearLayout)itemView.findViewById(R.id.ll_item);
            rate_progress_bar=(RateTextCircularProgressBar)itemView.findViewById(R.id.rate_progress_bar);
            msg_img=(ImageView)itemView.findViewById(R.id.msg_img);
            msg_img_download=(ImageView)itemView.findViewById(R.id.msg_img_download);
        }
        public void setUsername(String username) {
            if (null == tv_user_name) return;
            tv_user_name.setText(username);
            tv_user_name.setTextColor(getUsernameColor(username));
        }
        public void setMessage(String message) {
            if (null == tv_msg) return;
            tv_msg.setText(message);
        }
        //http://www.101apps.co.za/index.php/articles/android-recyclerview-and-picasso-tutorial.html
        public void setMessageImage(int position) {
            if (null == msg_img) return;
            try {
                msg_img.setOnClickListener(new OnItemClickListener(position, onImageClickCallback));
                Context context=msg_img.getContext();
                Log.i("FilePath", "FilePath " + Constants.CHAT_IMAGES_THUMB_URL + mMessages.get(position).getFileName());

                if (!TextUtils.isEmpty(mMessages.get(position).getFilePath())){
                     Log.i("getFilePath", "getFilePath " + mMessages.get(position).getFilePath());
                     Picasso.with(context)
                             .load(new File(mMessages.get(position).getFilePath()))
                             .resize(200,200).centerCrop()
                             .into(msg_img);
                }else  if (!TextUtils.isEmpty(mMessages.get(position).getFileName())){
                    Picasso.with(context)
                            .load(Constants.CHAT_IMAGES_THUMB_URL + mMessages.get(position).getFileName())
                            .resize(200,200).centerCrop()
                            .into(msg_img);
                }else {
                    msg_img.setImageResource(0);
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        public void setStatus(String status) {

            if (null == msg_img) return;
            if (null == rate_progress_bar) return;
            if (null == msg_img_download) return;
            if (TextUtils.isEmpty(status)) return;
            Log.i("setImageStatus", "setImageStatus " + status);
            if (status.equals(Constants.STATUS_WAIT_FOR_UPLOADING)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.image_upload_normal);
            }else if(status.equals(Constants.STATUS_START_UPLOADING)){
                rate_progress_bar.setVisibility(View.VISIBLE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.broadcast_cross);
            }else if (status.equals(Constants.STATUS_SENDING_FAILED)||status.equals(Constants.STATUS_UPLOADING_CANCELED)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.image_upload_normal);
            }else if (status.equals(Constants.STATUS_UPLOADED)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.image_upload_normal);
            }else if (status.equals(Constants.STATUS_DOWNLOADING)){
                rate_progress_bar.setVisibility(View.VISIBLE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.broadcast_cross);
            }else if (status.equals(Constants.STATUS_DOWNLOADED)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.GONE);
            }else if (status.equals(Constants.STATUS_DOWNLOAD_FAILED)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.image_download_normal);
            }else if (status.equals(Constants.STATUS_WAIT_FOR_DOWNLOAD)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.image_download_normal);
            }else if (status.equals(Constants.STATUS_DOWNLOADING_CANCELED)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.VISIBLE);
                msg_img_download.setImageResource(R.drawable.image_download_normal);
            }else if (status.equals(Constants.STATUS_SENT)){
                rate_progress_bar.setVisibility(View.GONE);
                msg_img_download.setVisibility(View.GONE);
            }
        }
        public void setTime(String time) {
            if (null == tv_time) return;
            tv_time.setText(time);
        }
        public void setProgress(int progress) {

            Log.i("progress", "progress " + progress);

            if (null == rate_progress_bar) return;
            //rate_progress_bar.getCircularProgressBar().setCircleWidth(20);
            rate_progress_bar.setProgress(progress);
        }
        void onCancelUploadClick(int position) {

            utilityDAO.updateStatus(mMessages.get(position).getRowId(), Constants.STATUS_UPLOADING_CANCELED);
            mMessages.get(position).setStatus(Constants.STATUS_UPLOADING_CANCELED);
            setStatus(Constants.STATUS_UPLOADING_CANCELED);

            if (mMessages.get(position).getUploadId() == null)
                return;
            UploadService.stopUpload(mMessages.get(position).getUploadId());

        }
        void onCancelDownloadingClick(int position) {
            if (downloadProgressHolders.get(position)!=null && downloadProgressHolders.get(position).getStatus()!=AsyncTask.Status.FINISHED ){
                downloadProgressHolders.get(position).cancel(true);
                utilityDAO.updateStatus(mMessages.get(position).getRowId(), Constants.STATUS_DOWNLOADING_CANCELED);
                mMessages.get(position).setStatus(Constants.STATUS_DOWNLOADING_CANCELED);
                downloadProgressHolders.containsKey(position);
                downloadProgressHolders.remove(position);
                setStatus(Constants.STATUS_DOWNLOADING_CANCELED);
            }
        }
        public void setItemClick(final int position) {
            if (null == ll_item) return;
            ll_item.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
        }
        public void setItemDelete(final int position) {
            if (null != ll_item)
                ll_item.setOnLongClickListener(new View.OnLongClickListener() {
                                                   @Override
                                                   public boolean onLongClick(View v) {
                                                       itemDelete(position);
                                                       return true;
                                                   }
                                               }
                );

            if (null != msg_img_download)
            msg_img_download.setOnLongClickListener(new View.OnLongClickListener() {
                                               @Override
                                               public boolean onLongClick(View v) {
                                                   itemDelete(position);
                                                   return true;
                                               }
                                           }
            );
            if (null != msg_img)
                msg_img.setOnLongClickListener(new View.OnLongClickListener() {
                                                            @Override
                                                            public boolean onLongClick(View v) {
                                                                itemDelete(position);
                                                                return true;
                                                            }
                                                        }
                );
        }
        public void itemDelete(final int position) {

            if (mMessages.get(position).getType() == Message.TYPE_LOAD_EARLIER) {
                return;
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.are_you_sure_you_want_to_delete_this_message));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    utilityDAO.delete(mMessages.get(position).getRowId());
                    mMessages.remove(position);
                    notifyItemRemoved(position);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            builder.show();
            }


    public void setCentreClick(final ViewHolder viewHolder,final int position) {
        if (null == msg_img_download) return;
            msg_img_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String status=mMessages.get(position).getStatus();
                    if (status.equals(Constants.STATUS_START_UPLOADING)){
                        onCancelUploadClick(position);
                    }else if (status.equals(Constants.STATUS_SENDING_FAILED)||status.equals(Constants.STATUS_UPLOADING_CANCELED)) {
                        attemptUploadImage(viewHolder,position);
                    }else if (status.equals(Constants.STATUS_WAIT_FOR_UPLOADING)) {
                        attemptUploadImage(viewHolder,position);
                    }else if (status.equals(Constants.STATUS_WAIT_FOR_DOWNLOAD)) {
                        attemptDownloadImage(viewHolder,position);
                    }else if (status.equals(Constants.STATUS_DOWNLOAD_FAILED)||status.equals(Constants.STATUS_DOWNLOADING_CANCELED)) {
                        attemptDownloadImage(viewHolder,position);
                    }else if (status.equals(Constants.STATUS_DOWNLOADING)) {
                        onCancelDownloadingClick(position);
                    }else if (status.equals(Constants.STATUS_UPLOADED)) {
                       attemptSendImage(viewHolder,position);
                    }else {
                        showToast(status);
                    }
                }
            });
        }
        private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }
    }
    private void attemptDownloadImage(ViewHolder viewHolder,int position) {
        if (TextUtils.isEmpty(mMessages.get(position).getFileName())){
            showToast(context.getResources().getString(R.string.picture_not_available));
            return;
        }
        if (downloadProgressHolders.get(position)==null){
            TaskForDownloadImage taskForDownloadImage=new TaskForDownloadImage(viewHolder,mMessages.get(position).getRowId());
            downloadProgressHolders.put(mMessages.get(position).getRowId(),taskForDownloadImage);
            taskForDownloadImage.execute();
        }
    }
    public  int containsRowPosition(String rowId) {
        for (Message object : mMessages) {
            if (rowId!=null && object!=null && object.getRowId()!=null && object.getRowId().equals(rowId)) {
                return  mMessages.indexOf(object);
            }
        }
        return -1;
    }
    public  int containsUploadIdPosition(String UploadId) {
        for (Message object : mMessages) {
            if (object!=null && object.getUploadId()!=null)
            if (object.getUploadId().equals(UploadId)) {
                return  mMessages.indexOf(object);
            }
        }
        return -1;
    }

    private void attemptUploadImage(ViewHolder viewHolder,int position) {
        if (TextUtils.isEmpty(mMessages.get(position).getToId())){ showToast("getToId"); return;}
        if (TextUtils.isEmpty(mMessages.get(position).getFilePath())) { showToast("getFilePath"); return;}
        if (mSocket==null)return;
        if (!mSocket.connected())return;

        utilityDAO.playSendMsgSoundSound(context);
        mMessages.get(position).setStatus( Constants.STATUS_START_UPLOADING);
        viewHolder.setStatus(Constants.STATUS_START_UPLOADING);
        utilityDAO.updateStatus(mMessages.get(position).getRowId(), Constants.STATUS_START_UPLOADING);

        try {
            final String filename =Utilities.getInstance(context).getFilename(mMessages.get(position).getFilePath());

            MultipartUploadRequest req = new MultipartUploadRequest(context, Constants.CHAT_SERVER_URL+Constants.CHAT_METHOD_UPLOAD)
                    .addFileToUpload(mMessages.get(position).getFilePath(), Constants.PN_FILE)
                    .setNotificationConfig(getNotificationConfig(true,filename))
                    .setCustomUserAgent(USER_AGENT)
                    .setAutoDeleteFilesAfterSuccessfulUpload(false)
                    .setUsesFixedLengthStreamingMode(true)
                    .setMaxRetries(2);

            req.setUtf8Charset();

            String uploadID = req.startUpload();
            mMessages.get(position).setUploadId(uploadID);
            addUploadToList(viewHolder,position);

            // these are the different exceptions that may be thrown
        } catch (FileNotFoundException exc) {
            showToast(exc.getMessage());
        } catch (IllegalArgumentException exc) {
            showToast("Missing some arguments. " + exc.getMessage());
        } catch (MalformedURLException exc) {
            showToast(exc.getMessage());
        }
    }
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    private UploadNotificationConfig getNotificationConfig(boolean display,String filename) {
        if (!display) return null;

        return new UploadNotificationConfig()
                .setIcon(R.drawable.ic_upload)
                .setTitle(filename)
                .setInProgressMessage(context.getString(R.string.uploading))
                .setCompletedMessage(context.getString(R.string.upload_success))
                .setErrorMessage(context.getString(R.string.upload_error))
                .setAutoClearOnSuccess(true)
                .setClickIntent(new Intent(context, MainActivity.class))
                .setClearOnAction(true)
                .setRingToneEnabled(true);
    }
    private void addUploadToList(ViewHolder viewHolder,int position) {

        uploadProgressHolders.put(mMessages.get(position).getUploadId(), viewHolder);
    }

    private Map<String, ViewHolder> uploadProgressHolders = new HashMap<>();


    public final UploadServiceBroadcastReceiver uploadReceiver =new UploadServiceBroadcastReceiver() {

        @Override
        public void onProgress(String uploadId, int progress) {
            Log.i(TAG, "The progress of the upload with ID " + uploadId + " is: " + progress);

            if (uploadProgressHolders.get(uploadId) == null || uploadProgressHolders.get(uploadId).getAdapterPosition()==RecyclerView.NO_POSITION)
                return;
             uploadProgressHolders.get(uploadId).setProgress(progress);
        }

        @Override
        public void onError(String uploadId, Exception exception) {
            Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
                    + exception.getLocalizedMessage(), exception);

            if (containsUploadIdPosition(uploadId)!=-1){
                utilityDAO.updateStatus(mMessages.get(containsUploadIdPosition(uploadId)).getRowId(), Constants.STATUS_SENDING_FAILED);
                mMessages.get(containsUploadIdPosition(uploadId)).setStatus(Constants.STATUS_SENDING_FAILED);
            }
            if (uploadProgressHolders.get(uploadId) != null && uploadProgressHolders.get(uploadId).getAdapterPosition()!=RecyclerView.NO_POSITION)
            {
                uploadProgressHolders.get(uploadId).setStatus(Constants.STATUS_SENDING_FAILED);
                uploadProgressHolders.remove(uploadId);
            }

        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, byte[] serverResponseBody) {
            Log.i(TAG, "Upload with ID " + uploadId + " is completed: " + serverResponseCode + ", " + new String(serverResponseBody));

            if (uploadProgressHolders.get(uploadId) == null)
                return;

            String[] response= new NodeJSDAO(context).parseUploadResponse(new String(serverResponseBody)) ;

            if (response!=null && !TextUtils.isEmpty(response[0]) && response[0].equals("1")){

                if (containsUploadIdPosition(uploadId)!=-1){
                    utilityDAO.updateStatus(mMessages.get(containsUploadIdPosition(uploadId)).getRowId(), Constants.STATUS_UPLOADED);
                    mMessages.get(containsUploadIdPosition(uploadId)).setStatus(Constants.STATUS_UPLOADED);
                    utilityDAO.updateFileName(mMessages.get(containsUploadIdPosition(uploadId)).getRowId(), response[1]);
                    mMessages.get(containsUploadIdPosition(uploadId)).setFileName(response[1]);

                    attemptSendImage(uploadProgressHolders.get(uploadId), containsUploadIdPosition(uploadId));
                    if (uploadProgressHolders.get(uploadId) != null && uploadProgressHolders.get(uploadId).getAdapterPosition()!=RecyclerView.NO_POSITION) {
                        uploadProgressHolders.get(uploadId).setStatus(Constants.STATUS_UPLOADED);
                        uploadProgressHolders.remove(uploadId);
                    }
                }
            }else {
                if (containsUploadIdPosition(uploadId)!=-1) {
                    utilityDAO.updateStatus(mMessages.get(containsUploadIdPosition(uploadId)).getRowId(), Constants.STATUS_SENDING_FAILED);
                    mMessages.get(containsUploadIdPosition(uploadId)).setStatus(Constants.STATUS_SENDING_FAILED);
                }
                if (uploadProgressHolders.get(uploadId) != null && uploadProgressHolders.get(uploadId).getAdapterPosition()!=RecyclerView.NO_POSITION)
                {
                    uploadProgressHolders.get(uploadId).setStatus(Constants.STATUS_SENDING_FAILED);
                    uploadProgressHolders.remove(uploadId);
                }
            }
        }
        @Override
        public void onCancelled(String uploadId) {
            Log.i(TAG, "Upload with ID " + uploadId + " is cancelled");


            if (containsUploadIdPosition(uploadId)!=-1){
                utilityDAO.updateStatus(mMessages.get(containsUploadIdPosition(uploadId)).getRowId(), Constants.STATUS_UPLOADING_CANCELED);
                mMessages.get(containsUploadIdPosition(uploadId)).setStatus(Constants.STATUS_UPLOADING_CANCELED);
            }
            if (uploadProgressHolders.get(uploadId) != null && uploadProgressHolders.get(uploadId).getAdapterPosition()!=RecyclerView.NO_POSITION)
            {
                uploadProgressHolders.get(uploadId).setStatus(Constants.STATUS_UPLOADING_CANCELED);
                uploadProgressHolders.remove(uploadId);
            }
        }
    };
    private void attemptSendImage(final ViewHolder viewHolder,final int position) {
       // if (TextUtils.isEmpty(mMessages.get(position).getToId())) return;
       // if (TextUtils.isEmpty(mMessages.get(position).getFileName())) return;
        if (mSocket==null)return;
        if (!mSocket.connected())return;

        utilityDAO.playSendMsgSoundSound(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PN_FROM_ID, mMessages.get(position).getFromId());
            jsonObject.put(Constants.PN_FROM_NAME, mMessages.get(position).getFromName());
            jsonObject.put(Constants.PN_TO_ID, mMessages.get(position).getToId());
            jsonObject.put(Constants.PN_TO_NAME,mMessages.get(position).getToName());
            jsonObject.put(Constants.PN_MESSAGE,mMessages.get(position).getMessage());
            jsonObject.put(Constants.PN_FILE_NAME,mMessages.get(position).getFileName());
            jsonObject.put(Constants.PN_MESSAGE_TYPE,Constants.MESSAGE_TYPE_IMAGE);
            jsonObject.put(Constants.PN_CURRENT_TIME, Utilities.getInstance(context).getUTCTime());
        } catch (JSONException e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        Ack sendImageAcknowledgement=new Ack() {
            @Override
            public void call(final Object... args) {
                // TODO Auto-generated method stub
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "response is " + Constants.CHAT_GROUP_MESSAGE + " " + args[0].toString());
                        String response = new NodeJSDAO(context).parseSuccess(args[0].toString());
                        if (TextUtils.isEmpty(response)) {
                            Toast.makeText(context, "Response is null", Toast.LENGTH_SHORT).show();
                        }else {
                            utilityDAO.updateStatus(mMessages.get(position).getRowId(), Constants.STATUS_SENT);
                            mMessages.get(position).setStatus(Constants.STATUS_SENT);
                            if (viewHolder.getAdapterPosition()!=RecyclerView.NO_POSITION)
                            {
                                viewHolder.setStatus(Constants.STATUS_SENT);
                            }
                        }
                    }
                });
            }
        };
        // perform the sending message attempt.
        Log.i(TAG,"response emit for "+Constants.CHAT_GROUP_MESSAGE+" "+jsonObject.toString());
        mSocket.emit(Constants.CHAT_GROUP_MESSAGE, jsonObject.toString(), sendImageAcknowledgement);
    }
    public Map<String, TaskForDownloadImage> downloadProgressHolders = new HashMap<>();
    public  class TaskForDownloadImage extends AsyncTask<Void, String, Boolean> {

        ViewHolder viewHolder;
        String rowId="";
        TaskForDownloadImage(ViewHolder viewHolder,String rowId){
            this.viewHolder=viewHolder;
            this.rowId=rowId;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (containsRowPosition(rowId)==-1){
                showToast("blank -1");
                return;
            }
            utilityDAO.updateStatus(rowId, Constants.STATUS_DOWNLOADING);
            mMessages.get(containsRowPosition(rowId)).setStatus(Constants.STATUS_DOWNLOADING);
            if (viewHolder.getAdapterPosition()!=RecyclerView.NO_POSITION){
                viewHolder.setStatus(Constants.STATUS_DOWNLOADING);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String Fullurl = Constants.CHAT_IMAGES_URL + mMessages.get(viewHolder.getAdapterPosition()).getFileName();

            int count;
            try {
                try {
                    URL url = new URL(Fullurl);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(Utilities.getInstance(context).getReceiveFileDirectory() + "/" + mMessages.get(viewHolder.getAdapterPosition()).getFileName());

                    byte data[] = new byte[Constants.BUFFER_SIZE];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress(""
                                + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    return true;
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Log.e("Error: ", e.getMessage());
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage());
                return false;
            }


        }

        @Override
        protected void onCancelled() {
            Log.i("onCancelled ", "onCancelled ");
            if (containsRowPosition(rowId)!=-1){
                utilityDAO.updateStatus(mMessages.get(containsRowPosition(rowId)).getRowId(), Constants.STATUS_DOWNLOAD_FAILED);
                mMessages.get(containsRowPosition(rowId)).setStatus(Constants.STATUS_DOWNLOAD_FAILED);
            }
            if (viewHolder.getAdapterPosition()!=RecyclerView.NO_POSITION){
                viewHolder.setStatus(Constants.STATUS_DOWNLOAD_FAILED);
            }
        }

        protected void onProgressUpdate(String... progress) {

            if (containsRowPosition(rowId)!=-1) {
                mMessages.get(containsRowPosition(rowId)).setProgress(Integer.parseInt(progress[0]));
            }
            if (viewHolder.getAdapterPosition()!=RecyclerView.NO_POSITION){
                viewHolder.setProgress(Integer.parseInt(progress[0]));
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (downloadProgressHolders.containsKey(containsRowPosition(rowId)))
            downloadProgressHolders.remove(containsRowPosition(rowId));
            if (result) {

                if (containsRowPosition(rowId)!=-1) {
                    utilityDAO.updateStatus(mMessages.get(containsRowPosition(rowId)).getRowId(), Constants.STATUS_DOWNLOADED);
                    mMessages.get(containsRowPosition(rowId)).setStatus(Constants.STATUS_DOWNLOADED);
                    utilityDAO.updateFilePath(mMessages.get(containsRowPosition(rowId)).getRowId(), Utilities.getInstance(context).getReceiveFileDirectory() + "/" + mMessages.get(containsRowPosition(rowId)).getFileName());
                    mMessages.get(containsRowPosition(rowId)).setFilePath(Utilities.getInstance(context).getReceiveFileDirectory() + "/" + mMessages.get(containsRowPosition(rowId)).getFileName());
                }
                if (viewHolder.getAdapterPosition()!=RecyclerView.NO_POSITION){
                    viewHolder.setStatus(Constants.STATUS_DOWNLOADED);
                    viewHolder.setMessageImage(containsRowPosition(rowId));
                }
            } else {
                if (viewHolder.getAdapterPosition()!=RecyclerView.NO_POSITION){
                    viewHolder.setStatus(Constants.STATUS_DOWNLOAD_FAILED);
                }
                if (containsRowPosition(rowId)!=-1) {
                    utilityDAO.updateStatus(mMessages.get(containsRowPosition(rowId)).getRowId(), Constants.STATUS_DOWNLOAD_FAILED);
                    mMessages.get(containsRowPosition(rowId)).setStatus(Constants.STATUS_DOWNLOAD_FAILED);
                }
            }
        }
    }
}
