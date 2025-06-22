package com.dange.roomly;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class FileUtils {
    public static File getFile(Context context, Uri uri) {
        // Check for older Android versions using file path directly
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return new File(uri.getPath());
        }

        // For newer versions, handle URIs more robustly
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        // Handle case where we couldn't get file path from MediaStore
        if (filePath != null) {
            return new File(filePath);
        } else {
            // If path retrieval failed, copy the content to a new file
            return copyUriToFile(context, uri);
        }
    }

    private static File copyUriToFile(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File tempFile = new File(context.getCacheDir(), "image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return tempFile;  // Return the copied file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;  // If everything fails
    }
}
