package com.mrichard.napkin_handler.data.image;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    private String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};

        ContentResolver contentResolver = context.getContentResolver();
        Cursor metaCursor = contentResolver.query(uri, projection, null, null, null);
        String path = "";
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    path = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        return path;
    }

    public String saveFile(Context context, Uri uri) {
        String sourceFilename = getPath(context, uri);
        String destinationFilename = "";

        try {
            destinationFilename = createImageFile(context).getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return destinationFilename;
    }

}
