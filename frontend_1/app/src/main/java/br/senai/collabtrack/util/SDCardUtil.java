package br.senai.collabtrack.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by kevin on 10/2/17.
 */

public class SDCardUtil {

    private static final String TAG = SDCardUtil.class.getName();

    public SDCardUtil() {
    }

    public static File getPublicFile(String fileName) {
        File sdCardDir = Environment.getExternalStorageDirectory();
        return createFile(sdCardDir, fileName);
    }

    public static File getPublicFile(String fileName, String type) {
        File sdCardDir = Environment.getExternalStoragePublicDirectory(type);
        return createFile(sdCardDir, fileName);
    }

    public static File getPrivateFile(Context context, String fileName) {
        File sdCardDir = context.getExternalFilesDir((String)null);
        return createFile(sdCardDir, fileName);
    }

    public static File getPrivateFile(Context context, String fileName, String type) {
        File sdCardDir = context.getExternalFilesDir(type);
        return createFile(sdCardDir, fileName);
    }

    private static File createFile(File sdCardDir, String fileName) {
        if(!sdCardDir.exists()) {
            sdCardDir.mkdir();
        }

        File file = new File(sdCardDir, fileName);
        return file;
    }

    public static File getSdCardDir(Context context, String preferedDir) {
        File dir = null;
        if(Environment.getExternalStorageState().equals("mounted")) {
            dir = new File(Environment.getExternalStorageDirectory(), preferedDir);
        } else {
            dir = context.getCacheDir();
        }

        if(!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static File getSdCardFile(Context context, String folderName, String fileName) {
        File sdcard = getSdCardDir(context, folderName);
        File f = new File(sdcard, fileName);
        Log.d(TAG, "<< getSdCardFile > " + f);
        return f;
    }

}
