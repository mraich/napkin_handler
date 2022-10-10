package com.mrichard.napkin_handler.data.model.picture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
    protected String attributes;

    @ColumnInfo(name = COLUMN_TIMESTAMP)
    @NonNull
    protected long timestamp;

    // We need this constructor for Room.
    protected Picture()  {
    }

    public Picture(
        String path
        , String attributes
    ) {
        this(path, attributes, null);
    }

    public Picture(
        String path
        , String attributes
        , Long timestamp
        )
    {
        this.path = path;
        this.attributes = attributes;

        if (timestamp == null)
        {
            this.timestamp = System.currentTimeMillis();
        } else
        {
            this.timestamp = timestamp.longValue() * 1000;
        }
    }

    public String getPath() {
        return path;
    }

    public String getAttributes() {
        return attributes;
    }

}
