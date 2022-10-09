package com.mrichard.napkin_handler.data.image;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageUtils {

    protected String IMAGES_DIR = "images";
    protected String IMAGES_EXTENSION = "jpg";

    /**
     * Creates an image file and names it with UUID.randomUUID().
     *
     * @return a new file.
     */
    public File createImageFile(Context context) throws IOException {
        String imagePath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + IMAGES_DIR;

        File storageDir = new File(imagePath);
        storageDir.mkdirs();

        File image = File.createTempFile(
                UUID.randomUUID().toString(), // prefix, imageFileName
                "." + IMAGES_EXTENSION, // suffix, extension
                storageDir // directory, absolute
        );

        return image;
    }

    public String UriToFilePath(Context context, Uri uri) {
        String uriString = uri.toString();

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index > 0) {
                        return cursor.getString(index);
                    }
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            return new File(uriString).getAbsolutePath();
        }
        return "";
    }

}
