package br.com.senai.colabtrack.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.senai.colabtrack.ColabTrackApplication;

/**
 * Created by ricardo on 26/08/15.
 */
public class CameraUtil {

    private static final String TAG = "camera";
    private File file;

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Se girou a tela recupera o estado
            file = (File) savedInstanceState.getSerializable("file");
        }
    }

    private File createFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public Intent open(Context context, String fileName) throws IOException  {
        // Cria o arquivo no sdcard para tirar a foto
        file = createFile(context);
        // Intent da Camera
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Cria a Uri do arquivo.
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return i;
    }

    public File getFile() {
        return file;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (file != null) {
            outState.putSerializable("file", file);
        }
    }

    public Bitmap getBitmap(int w, int h) {

        if (file != null && file.exists()) {
            Log.d(ColabTrackApplication.DEBUG_TAG, file.getAbsolutePath());

            // Resize
            Uri uri = Uri.fromFile(file);
            Bitmap bitmap = ImageResizeUtil.getResizedImage(uri, w, h, false);
            Log.d(ColabTrackApplication.DEBUG_TAG, "w/h: " + bitmap.getWidth() + "/" + bitmap.getHeight());

            return bitmap;
        }
        return null;
    }

    // Salva o bitmap alterado no arquivo
    public void save(Bitmap bitmap) {
        if (file != null) {
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();

                Log.d("foto", "file compress ok: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
