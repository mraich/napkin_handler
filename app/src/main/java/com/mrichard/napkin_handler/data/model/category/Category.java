package com.mrichard.napkin_handler.data.model.category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Categories are tags to that pictures will belong.
 */
@Entity(tableName = Category.CATEGORY_TABLE)
public class Category
{

    public static final String CATEGORY_TABLE = "category";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_BASIC = "basic";

    @PrimaryKey(autoGenerate = true)
    @Nullable
    protected Long id;

    @ColumnInfo(name = COLUMN_NAME)
    @NonNull
    protected String name;

    @ColumnInfo(name = COLUMN_TIMESTAMP)
    @NonNull
    protected long timestamp;

    @ColumnInfo(name = COLUMN_BASIC)
    @NonNull
    protected boolean basic;

    // We need this constructor for Room.
    protected Category()  {
    }

    public Category(
        String name
    ) {
        this(name, null);
    }

    public Category(
        String name
        , Long timestamp
        )
    {
        this.name = name;

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

    public String getName() {
        return name;
    }

    public Boolean isBasic() { return basic; }

}
