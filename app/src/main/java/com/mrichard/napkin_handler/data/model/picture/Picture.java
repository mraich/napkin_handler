package com.mrichard.napkin_handler.data.model.picture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mrichard.napkin_handler.data.db.GsonHandler;

@Entity(tableName = Picture.PICTURE_TABLE)
public class Picture
{

    public static final String PICTURE_TABLE = "picture";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_ATTRIBUTES = "attributes";
    public static final String COLUMN_TIMESTAMP = "timestamp";

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
            int diff = myAttributes[i] - otherAttributes[i];
            similarity += (diff * diff);
        }
        similarity = Math.sqrt(similarity);
    }

    public double getSimilarity() {
        return similarity;
    }

}
