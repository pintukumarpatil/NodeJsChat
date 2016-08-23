package com.chat.pk.Util;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class Constants {
    public static final String CHAT_SERVER_URL = "http://121.121.12.121:4000";///As a demo socket url
    public static final String CHAT_IMAGES_THUMB_URL = "http://121.121.12.121/node-chat/data_file/thumbnails/";
    public static final String CHAT_IMAGES_URL = "http://121.121.12.121/node-chat/data_file/";

   // public static final String CHAT_SERVER_URL = "http://chat.socket.io";


    public static final String PROJECT_NUMBER = "111111111111";//demo project number



    public static final String NT_CHAT_SERVICE = "NOTIFY_TO_OFFLINE_USER";

    public static final int TYPING_TIMER_LENGTH = 1000;
    public static final int TYPING_SCROLL_LENGTH = 1000;
    public static final int BUFFER_SIZE = 1024;
    public static final String CHAT_METHOD_UPLOAD = "/upload";

    public static final String CHAT_PRIVATE_MESSAGE = "privateMessage";
    public static final String CHAT_GROUP_MESSAGE = "groupMessage";
    public static final String CHAT_TYPING = "startTyping";
    public static final String CHAT_STOP_TYPING = "stopTyping";
    public static final String CHAT_PRIVATE_TYPING = "startPrivateTyping";
    public static final String CHAT_STOP_PRIVATE_TYPING = "stopPrivateTyping";



    public static final String CHAT_LOGIN = "login";
    public static final String CHAT_GROUP_LIST = "getGroupList";
    public static final String CHAT_GROUP_INFO = "getGroupInfo";
    public static final String CHAT_FRIEND_LIST = "getFriendList";
    public static final String CHAT_FRIEND_INFO = "getFriendsInfo";
    /*PARAMS FOR DB*/
    public static final String DB_NAME = "IdealChat";
    public static final String TABLE_GROUP_CHAT = "group_chat";
    public static final String TABLE_SINGLE_CHAT = "single_chat";
    public static final String CN_ID = "id";
    public static final String CN_GROUP_ID = "group_id";
    public static final String CN_GROUP_NAME = "group_name";
    public static final String CN_GROUP_IMAGE = "group_image";
    public static final String CN_USER_ID = "user_id";
    public static final String CN_USER_NAME = "user_name";
    public static final String CN_USER_IMAGE = "user_image";
    public static final String CN_FROM_ID = "from_id";
    public static final String CN_FROM_NAME = "from_name";
    public static final String CN_FROM_IMAGE = "from_image";

    public static final String CN_MESSAGE = "message";
    public static final String CN_FILE_NAME = "file_name";
    public static final String CN_FILE_PATH = "file_path";
    public static final String CN_THUMB_FILE_NAME = "thumb_file_name";
    public static final String CN_USER_TYPE = "user_type";
    public static final String CN_MESSAGE_TYPE = "message_type";
    public static final String CN_TIME = "date_time";
    public static final String CN_STATUS = "status";

    /*USER TYPE*/
    public static final String USER_TYPE_ME = "1";
    public static final String USER_TYPE_OTHER = "2";


    /*CHAT STATUS*/
    public static final String STATUS_UNREAD = "0";
    public static final String STATUS_READ = "1";
    public static final String STATUS_SENT = "1";
    public static final String STATUS_UNSEND = "2";
    public static final String STATUS_START_UPLOADING = "3";
    public static final String STATUS_SENDING_FAILED = "4";
    public static final String STATUS_UPLOADED = "5";
    public static final String STATUS_UPLOADING_CANCELED = "6";
    public static final String STATUS_WAIT_FOR_DOWNLOAD = "7";
    public static final String STATUS_DOWNLOADED = "8";
    public static final String STATUS_DOWNLOAD_FAILED = "9";
    public static final String STATUS_DOWNLOADING = "10";
    public static final String STATUS_WAIT_FOR_UPLOADING = "11";
    public static final String STATUS_DOWNLOADING_CANCELED = "12";
    /*MESSAGE TYPE*/
    public static final String MESSAGE_TYPE_TEXT = "1";
    public static final String MESSAGE_TYPE_IMAGE = "2";
    public static final String MESSAGE_TYPE_JOIN = "3";
    public static final String MESSAGE_TYPE_LEFT = "4";

    /*SUCCESS*/
    public static final String SUCCESS_1 = "1";
    public static final String SUCCESS_0 = "0";
    public static final int TYPING_COUNT = -2;

    /*SIZE*/
    public static final int WIDTH = 120;
    public static final int HEIGHT = 120;
    public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;
    /*PARAMS*/

    public static final String PN_CHAT_TYPE = "chat_type";
    public static final String PN_NOTIFICATION_ID = "notification_id";
    public static final String PN_USER_ID = "user_id";
    public static final String PN_USER_NAME = "user_name";
    public static final String PN_GROUP_ID = "group_id";
    public static final String PN_GROUP_NAME = "group_name";
    public static final String PN_TO_ID = "to_id";
    public static final String PN_TO_NAME = "to_name";
    public static final String PN_FROM_ID = "from_id";
    public static final String PN_FROM_NAME = "from_name";
    public static final String PN_MESSAGE = "message";
    public static final String PN_MESSAGE_TYPE = "msg_type";
    public static final String PN_CURRENT_TIME = "current_time";
    public static final String PN_FILE = "file";
    public static final String PN_FILE_NAME = "file_name";


    /*CONST*/
    public static final String CONST_PRIVATE = "private";
    public static final String CONST_GROUP = "group";


}
