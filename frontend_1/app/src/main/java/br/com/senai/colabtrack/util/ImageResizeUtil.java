package br.com.senai.colabtrack.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import br.com.senai.colabtrack.ColabTrackApplication;

/**
 * Created by kevin on 10/2/17.
 */

public class ImageResizeUtil {

    private static final String TAG = ImageResizeUtil.class.getName();

    public ImageResizeUtil() {
    }

    public static Bitmap getResizedImageResource(Context context, int resImgId, int width, int height) {
        try {
            BitmapFactory.Options e = new BitmapFactory.Options();
            e.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), resImgId, e);
            int w = e.outWidth;
            int h = e.outHeight;
            if(width == 0 || height == 0) {
                width = w / 2;
                height = h / 2;
            }

            Log.d(TAG, "Resize img, w:" + w + " / h:" + h + ", to w:" + width + " / h:" + height);
            int scaleFactor = Math.min(w / width, h / height);
            e.inSampleSize = scaleFactor;
            Log.d(TAG, "inSampleSize:" + e.inSampleSize);
            e.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resImgId, e);
            Log.d(TAG, "Resize OK, w:" + bitmap.getWidth() + " / h:" + bitmap.getHeight());
            return bitmap;
        } catch (RuntimeException var9) {
            Log.e(TAG, var9.getMessage(), var9);
            return null;
        }
    }

    public static Bitmap getResizedImage(Uri uriFile, int width, int height) {
        return getResizedImage(uriFile, width, height, false);
    }

    public static Bitmap getResizedImage(Uri uriFile, int width, int height, boolean fixMatrix) {
        try {
            BitmapFactory.Options e = new BitmapFactory.Options();
            e.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(uriFile.getPath(), e);
            int w = e.outWidth;
            int h = e.outHeight;
            if(width == 0 || height == 0) {
                width = w / 2;
                height = h / 2;
            }

            Log.d(TAG, "Resize img, w:" + w + " / h:" + h + ", to w:" + width + " / h:" + height);
            int scaleFactor = Math.min(w / width, h / height);
            e.inSampleSize = scaleFactor;
            Log.d(TAG, "inSampleSize:" + e.inSampleSize);
            e.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(uriFile.getPath(), e);
            Log.d(TAG, "Resize OK, w:" + bitmap.getWidth() + " / h:" + bitmap.getHeight());
            if(fixMatrix) {
                Bitmap newBitmap = fixMatrix(uriFile, bitmap);
                bitmap.recycle();
                return newBitmap;
            }

            return bitmap;
        } catch (RuntimeException var10) {
            Log.e(TAG, var10.getMessage(), var10);
        } catch (IOException var11) {
            Log.e(TAG, var11.getMessage(), var11);
        }

        return null;
    }

    private static Bitmap fixMatrix(Uri uriFile, Bitmap bitmap) throws IOException {
        Matrix matrix = new Matrix();
        ExifInterface exif = new ExifInterface(uriFile.getPath());
        int orientation = exif.getAttributeInt("Orientation", 1);
        boolean fix = false;
        switch(orientation) {
            case 3:
                matrix.postRotate(180.0F);
                fix = true;
                break;
            case 6:
                matrix.postRotate(90.0F);
                fix = true;
                break;
            case 8:
                matrix.postRotate(270.0F);
                fix = true;
                break;
            default:
                fix = false;
        }

        if(!fix) {
            return bitmap;
        } else {
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            bitmap = null;
            return newBitmap;
        }
    }

}
