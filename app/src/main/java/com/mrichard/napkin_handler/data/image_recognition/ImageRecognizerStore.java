package com.mrichard.napkin_handler.data.image_recognition;

import android.content.Context;

public class ImageRecognizerStore {

    protected static IImageRecognizer imageRecognizer = null;

    /**
     * Getting an instance of an IImageRecognizer.
     *
     * @param context This should be an applicationContext otherwise it could cause memory leaks.
     * @param
     * @return
     */
    public static IImageRecognizer GetInstance(Context context) {
        if (imageRecognizer == null) {
            imageRecognizer = new ImageRecognizer(context);
        }

        return imageRecognizer;
    }

}
