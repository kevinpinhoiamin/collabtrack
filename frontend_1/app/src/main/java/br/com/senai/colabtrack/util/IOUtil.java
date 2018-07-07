package br.com.senai.colabtrack.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by kevin on 10/2/17.
 */

public class IOUtil {

    public IOUtil() {
    }

    public static String toString(InputStream in, String charset) throws IOException {
        byte[] bytes = toBytes(in);
        String texto = new String(bytes, charset);
        return texto;
    }

    public static byte[] toBytes(InputStream in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Object len;
        try {
            byte[] e = new byte[1024];

            int len1;
            while((len1 = in.read(e)) > 0) {
                bos.write(e, 0, len1);
            }

            byte[] e1 = bos.toByteArray();
            byte[] var5 = e1;
            return var5;
        } catch (Exception var15) {
            Log.e("IOUtils", var15.getMessage(), var15);
            len = null;
        } finally {
            try {
                bos.close();
                in.close();
            } catch (IOException var14) {
                Log.e("IOUtils", var14.getMessage(), var14);
            }

        }

        return (byte[])len;
    }

    public static void writeString(OutputStream out, String string) {
        writeBytes(out, string.getBytes());
    }

    public static void writeBytes(OutputStream out, byte[] bytes) {
        try {
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException var3) {
            Log.e("IOUtils", var3.getMessage(), var3);
        }

    }

    public static void writeString(File file, String string) {
        writeBytes(file, string.getBytes());
    }

    public static void writeBytes(File file, byte[] bytes) {
        try {
            FileOutputStream e = new FileOutputStream(file);
            e.write(bytes);
            e.flush();
            e.close();
        } catch (IOException var3) {
            Log.e("IOUtils", var3.getMessage(), var3);
        }

    }

    public static String readString(File file) {
        try {
            if(file != null && file.exists()) {
                FileInputStream e = new FileInputStream(file);
                String s = toString(e, "UTF-8");
                return s;
            } else {
                return null;
            }
        } catch (FileNotFoundException var3) {
            return null;
        } catch (IOException var4) {
            Log.e("IOUtils", var4.getMessage(), var4);
            return null;
        }
    }

    public static void writeBitmap(File file, Bitmap bitmap) {
        try {
            if(!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream e = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, e);
            e.close();
        } catch (IOException var3) {
            Log.e("IOUtils", var3.getMessage(), var3);
        }

    }

    public static void saveBitmapToFile(String url, Bitmap bitmap, IOUtil.Callback callback) {
        try {
            if(url == null || bitmap == null && callback != null) {
                return;
            }

            String e = url.substring(url.lastIndexOf("/"));
            File file = SDCardUtil.getPublicFile(e, Environment.DIRECTORY_PICTURES);
            if(file.exists()) {
                callback.onFileSaved(file, true);
            } else {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                Log.d("IOUtils", "Save File: " + file);
                writeBitmap(file, bitmap);
                callback.onFileSaved(file, false);
            }
        } catch (IOException var6) {
            Log.e("IOUtils", var6.getMessage(), var6);
        }

    }

    public static boolean downloadToFile(String url, File file) {
        try {
            InputStream e = (new URL(url)).openStream();
            byte[] bytes = toBytes(e);
            writeBytes(file, bytes);
            Log.d("IOUtils", "downloadToFile: " + file);
            return true;
        } catch (IOException var4) {
            Log.e("IOUtils", var4.getMessage(), var4);
            return false;
        }
    }

    public static void checkMainThread() {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("Qualquer operação de I/O não pode ser executado na UI Thread.");
        }
    }

    public interface Callback {
        void onFileSaved(File var1, boolean var2);
    }

}
