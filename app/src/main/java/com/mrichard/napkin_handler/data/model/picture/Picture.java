package com.mrichard.napkin_handler.data.model.picture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mrichard.napkin_handler.data.db.GsonHandler;
import com.mrichard.napkin_handler.data.image_recognition.ImageRecognizer;

import java.io.File;

@Entity(tableName = Picture.PICTURE_TABLE)
public class Picture implements Comparable<Picture>
{

    public static final String PICTURE_TABLE = "picture";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_ATTRIBUTES = "attributes";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_COUNT = "count";

    @PrimaryKey(autoGenerate = true)
    @Nullable
    protected Long id;

    @ColumnInfo(name = COLUMN_PATH)
    @NonNull
    protected String path;

    @ColumnInfo(name = COLUMN_ATTRIBUTES)
    @NonNull
    protected String attributesJson;

    @Ignore
    protected Integer[] attributes = null;

    @ColumnInfo(name = COLUMN_TIMESTAMP)
    @NonNull
    protected long timestamp;

    @Ignore
    protected double similarity = 0;

    @ColumnInfo(name = COLUMN_COUNT)
    @NonNull
    protected int count;

    // We need this constructor for Room.
    protected Picture()  {
    }

    public Picture(
        String path
        , Integer[] attributes
    ) {
        this(path, attributes, null);
    }

    public Picture(
        String path
        , Integer[] attributes
        , Long timestamp
        )
    {
        this.path = path;
        this.attributes = attributes;
        this.attributesJson = GsonHandler.GetInstance().GetGson().toJson(this.attributes);

        if (timestamp == null)
        {
            this.timestamp = System.currentTimeMillis();
        } else
        {
            this.timestamp = timestamp.longValue() * 1000;
        }
    }

    public static Picture FromFile(File imageFile, ImageRecognizer imageRecognizer) {
        // Decoding image bitmap.
        String filePath = imageFile.getAbsolutePath();
        Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

        // Doing the actual computation of the image processing.
        Integer[] attributes = imageRecognizer.recognize(imageBitmap);

        // Saving the picture and its classification.
        // It needs to run on a separate thread unless we get this nice error below. :)
        // java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
        // It is synchronized as database operations are not threadsafe.
        return new Picture(imageFile.getAbsolutePath(), attributes);
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public Integer[] getAttributes()
    {
        if (attributes == null) {
            attributes = GsonHandler.GetInstance().GetGson().fromJson(attributesJson, Integer[].class);
        }

        return attributes;
    }

    public String getAttributesJson() {
        return attributesJson;
    }

    public void setSimilarityForPicture(Picture other) {
        Integer[] myAttributes = getAttributes();
        Integer[] otherAttributes = other.getAttributes();
        similarity = 0;

        // Assuming the two picture has the same amount of features stored.
        for (int i = 0; i < myAttributes.length; i++) {
            int diff = Math.abs(myAttributes[i] - otherAttributes[i]);
            similarity += diff;
        }
        similarity /= myAttributes.length;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public int compareTo(Picture other) {
        if (getSimilarity() > other.getSimilarity()) {
            return 1;
        }
        if (getSimilarity() < other.getSimilarity()) {
            return -1;
        }

        return 0;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
