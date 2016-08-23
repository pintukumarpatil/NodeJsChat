package com.chat.pk.Util;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class Utilities {
    String TAG = getClass().getSimpleName();
    private ConnectivityManager cm;
    static Context context;
    /**
     * Validation ragular expression
     */
    Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("^([a-zA-Z0-9._-]+)@{1}(([a-zA-Z0-9_-]{1,67})|([a-zA-Z0-9-]+\\.[a-zA-Z0-9-]{1,67}))\\.(([a-zA-Z0-9]{2,6})(\\.[a-zA-Z0-9]{2,6})?)$");
    Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^[0-9]{8,14}$");
    Pattern ZIP_PATTERN = Pattern.compile("^[0-9a-zA-Z-]{3,8}$");

    DecimalFormat priceFormat = new DecimalFormat("#.00");
    SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat(
            "dd MMM yyyy", Locale.getDefault());
    //time format for 12 hours
    SimpleDateFormat HH_MM_AM_PM = new SimpleDateFormat("hh:mm a",
            Locale.getDefault());

    private static Utilities singleton = null;

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    public Utilities() {

    }

    /* Static 'instance' method */
    public static Utilities getInstance(Context mContext) {
        context = mContext;
        if (singleton == null)
            singleton = new Utilities();
        return singleton;
    }
    /* Other methods protected by singleton-ness */
//    protected static void demoMethod( ) {
//        System.out.println("demoMethod for singleton");
//    }

//	public Utilities(Context context) {
//		this.context = context;
//	}

    /**
     * Method for checking network availability
     */
    public boolean isNetworkAvailable() {
        try {
            cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            // if no network is available networkInfo will be null
            // otherwise check if we are connected
            if (networkInfo != null && networkInfo.isConnected())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean validarCURP(String curp)
    { String regex =
            "[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}" +
                    "(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])" +
                    "[HM]{1}" +
                    "(AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)" +
                    "[B-DF-HJ-NP-TV-Z]{3}" +
                    "[0-9A-Z]{1}[0-9]{1}$";
        Pattern patron = Pattern.compile(regex);
        if(!patron.matcher(curp).matches())
        { return false;
        }else
        { return true;
        }
    }
    /**
     * Method for getting device unique number i.e IMEI
     */
    public String getIMEIorDeviceId() {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            //imei = "" + System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }
    //14 December, 2015 at 04:13 PM
    public static String getDateLocal(SimpleDateFormat sdf,String time) {
        try {
            long timestamp = Long.parseLong(time) * 1000;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp);
            return sdf.format(cal.getTime());
        }catch (Exception e){
            return "N/A";
        }
    }
    public static String getDateLocal(SimpleDateFormat sdf,Date date) {
        try {
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        }catch (Exception e){
            return "N/A";
        }
    }
    //14 December, 2015 at 04:13 PM
    public static Calendar getCal(String time) {
        try {
            long timestamp = Long.parseLong(time) * 1000;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp);
            return cal;
        }catch (Exception e){
            return null;
        }
    }
    public String getDateLocalToUTC(SimpleDateFormat sdf,Date date) {
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);
        }catch (Exception e){
            return "N/A";
        }
    }

    //14 December, 2015 at 04:13 PM
    public static String getDate(String time) {
        try {
            //for convert time according to timezone it's depend on sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat df1 = new SimpleDateFormat(
                    "dd MMM", Locale.getDefault());
            SimpleDateFormat df2 = new SimpleDateFormat(
                    "yyyy", Locale.getDefault());
            SimpleDateFormat df3 = new SimpleDateFormat(
                    "hh:mm a", Locale.getDefault());
            long timestamp = Long.parseLong(time) * 1000;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp);
            return df1.format(cal.getTime())+", "+df2.format(cal.getTime())+" at "+df3.format(cal.getTime());
        }catch (Exception e){
            return "N/A";
        }
    }

    public String getUTCTimeFormat() {
        try {
            SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            YYYY_MM_DD_HH_MM_SS.setTimeZone(TimeZone.getTimeZone("UTC"));
            return YYYY_MM_DD_HH_MM_SS.format(Calendar.getInstance(Locale.getDefault()).getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "N/A";
        }
    }

    public String getUTCTime() {
        try {
            SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            YYYY_MM_DD_HH_MM_SS.setTimeZone(TimeZone.getTimeZone("UTC"));
            YYYY_MM_DD_HH_MM_SS.setCalendar(Calendar.getInstance(Locale.getDefault()));
            return YYYY_MM_DD_HH_MM_SS.getCalendar().getTimeInMillis()+"";
        }catch (Exception e){
            e.printStackTrace();
            return "00000";
        }
    }

    public String getUpdateTime(String updateTime) {
        try {
            if (TextUtils.isEmpty(updateTime))return "N/A";
            if (updateTime.equalsIgnoreCase("N/A"))return "N/A";
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(updateTime));
            return HH_MM_AM_PM.format(cal.getTime());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.i("updateTime ", "updateTime " + updateTime);
            return "N/A";
        }

    }

    public String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public String getFilename(String filepath) {
        if (filepath == null)
            return null;

        final String[] filepathParts = filepath.split("/");

        return filepathParts[filepathParts.length - 1];
    }
    public String getTimeLogByFormat(String updateTime) {
        try {
            if (TextUtils.isEmpty(updateTime))return "N/A";
            if (updateTime.equalsIgnoreCase("N/A"))return "N/A";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date updateDate = dateFormat.parse(updateTime);

            if (DateUtils.isToday(updateDate.getTime())){
                return "Today";
            }else if (isYesterday(updateDate.getTime())){
                return "Yesterday";
            }else {
                return DD_MMM_YYYY.format(updateDate);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return "N/A";
        }

    }

    public String getTimeLog(String updateTime) {
        try {
            if (TextUtils.isEmpty(updateTime))return "N/A";
            if (updateTime.equalsIgnoreCase("N/A"))return "N/A";
            long timestamp = Long.parseLong(updateTime);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp);
            if (DateUtils.isToday(cal.getTime().getTime())){
                return "Today";
            }else if (isYesterday(cal.getTime().getTime())){
                return "Yesterday";
            }else {
                return DD_MMM_YYYY.format(cal.getTime());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return "N/A";
        }

    }
    public static boolean isYesterday(long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        Calendar calendar=Calendar.getInstance(TimeZone.getDefault());
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        time.set(calendar.getTimeInMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    /**
     * Method for getting application version code
     */
    public String getAppVersion() {
        String appVersion = "";
        try {
            appVersion = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    public void exit() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            ((Activity) context).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for Checking application running on emulator or real device
     */
    public boolean isAndroidEmulator() {
        String product = Build.PRODUCT;
        boolean isEmulator = false;
        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk")
                    || product.contains("sdk_");
        }
        return isEmulator;
    }

    /**
     * method for email validation
     */
    public boolean checkEmail(String email) {
        try {
            return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
        } catch (NullPointerException exception) {
            return false;
        }
    }

    /**
     * method for mobile number validation
     */
    public boolean checkMobile(String mobile) {
        try {
//            mobile = mobile.replaceAll("[^0-9]", "");
            if (MOBILE_NUMBER_PATTERN.matcher(mobile).matches())
                return true;
            else
                return false;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * method for zip validation
     */
    public boolean checkZip(String zip) {
        try {
            if (ZIP_PATTERN.matcher(zip).matches())
                return true;
            else
                return false;
        } catch (Exception exception) {
            return false;
        }
    }

    MediaPlayer mMediaPlayer;
    AudioManager am;

    public void setSound(int sound) {
        try {
            // Log.i(getClass().getName(), "setSound................");
            int maxVol;
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            maxVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0);
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setVolume(maxVol, maxVol);
                mMediaPlayer = MediaPlayer.create(context, sound);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(int sound) {
        try {
            setSound(sound);
            if (mMediaPlayer != null) {
                if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    mMediaPlayer.start();
                }
            }
            // Log.i(getClass().getName(), "playSound.................");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mp = MediaPlayer.create(context, notification);
            mp.start();
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
                v.vibrate(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentDateTime() {
        try {
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.format(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "2015-01-01 00:00:00";
        }
    }

    /**
     * This method used to create new file if not exist .
     */
    public File getNewFile(String directoryName, String imageName) {
        String root = Environment.getExternalStorageDirectory()
                + directoryName;
        File file;
        if (isSDCARDMounted()) {
            new File(root).mkdirs();
            file = new File(root, imageName);
        } else {
            file = new File(context.getFilesDir(), imageName);
        }
        return file;
    }

    public boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaColumns.DATA};
        // Cursor cursor = ((Activity) context).managedQuery(uri, projection,
        // null, null, null);
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void deletePicture(String directoryName) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            new File(root + directoryName).mkdirs();
            File f = new File(root + directoryName);
            File[] files = f.listFiles();
            if (files == null)
                return;
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile())
                    files[i].delete();
            }
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void customToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static String upperCaseFirst(String value) {
        if (value == null || value.equals(""))
            return value;
        // Convert String to char array.
        char[] array = value.toCharArray();
        // Modify first element in array.
        array[0] = Character.toUpperCase(array[0]);
        // Return string.
        return new String(array);
    }
    private String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);

    }

    public String getReceiveFileDirectory() {
        File cacheDir;
        try {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED))
                cacheDir = new File(
                        android.os.Environment.getExternalStorageDirectory(),
                        "PKChat/Media/Receive");
            else
                cacheDir = context.getCacheDir();
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            return cacheDir.getAbsolutePath();
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e("Teg", "Exception FileCache(Context context)");

        }
        return null;
    }

    public String getReceiveThumbFilePath() {
        File cacheDir;
        try {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                cacheDir = new File( android.os.Environment.getExternalStorageDirectory(),"PKChat/Media/Receive/Thumb/");
            else
                cacheDir = context.getCacheDir();
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            return cacheDir.getAbsolutePath()+"pk"+Calendar.getInstance().getTimeInMillis()+".png";
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e("Teg", "Exception FileCache(Context context)");

        }
        return null;
    }
    public String convertImage(String fileName) {
        FileOutputStream fos=null;
        try {

            if (TextUtils.isEmpty(fileName) && !new File(fileName).exists()){
                customToast("File not found");
                return null;
            }
            Bitmap original = BitmapFactory.decodeFile(fileName);
            if (original==null){
                customToast("Error accessing file");
            }
           /* Bitmap resized = ThumbnailUtils.extractThumbnail(original, Constants.WIDTH, Constants.HEIGHT,android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            if (resized==null) {
                customToast("Error accessing file");
            }*/
            String filePath=getReceiveThumbFilePath();
            fos = new FileOutputStream(filePath);
            original.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return filePath;
        } catch (FileNotFoundException e) {
            customToast("File not found");
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
            customToast("Error accessing file");
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                customToast("Error accessing file");
            }
        }
        return null;
    }

}
