package com.mrichard.napkin_handler.data.image_recognition;

import android.graphics.Bitmap;

public interface IImageRecognizer {

    Integer[] recognize(Bitmap image);

}
