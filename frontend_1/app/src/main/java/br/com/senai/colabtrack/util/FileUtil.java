package br.com.senai.colabtrack.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kevin on 07/06/17.
 */

public class FileUtil {

    public static String readFile(int resourceId, Context context) {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

}
