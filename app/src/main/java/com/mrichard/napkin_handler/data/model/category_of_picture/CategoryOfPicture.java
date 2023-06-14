package com.mrichard.napkin_handler.data.model.category_of_picture;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.mrichard.napkin_handler.data.model.category.Category;
import com.mrichard.napkin_handler.data.model.picture.Picture;

@Entity(
        tableName = CategoryOfPicture.CATEGORY_OF_PICTURE_TABLE,
        primaryKeys = {
            CategoryOfPicture.COLUMN_CATEGORY_ID, CategoryOfPicture.COLUMN_PICTURE_ID
        },
        foreignKeys = {
            @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "category_id", onDelete = ForeignKey.CASCADE ),
            @ForeignKey(entity = Picture.class, parentColumns = "id", childColumns = "picture_id", onDelete = ForeignKey.CASCADE )
        }
)
public class CategoryOfPicture
{

    public static final String CATEGORY_OF_PICTURE_TABLE = "category_of_picture";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_PICTURE_ID = "picture_id";

    @ColumnInfo(name = COLUMN_CATEGORY_ID)
    @NonNull
    protected Integer categoryId;

    @ColumnInfo(name = COLUMN_PICTURE_ID)
    @NonNull
    protected Integer pictureId;

    // We need this constructor for Room.
    protected CategoryOfPicture() {
    }

    public CategoryOfPicture(
        int categoryId,
        int pictureId
    ) {
        this.categoryId = categoryId;
        this.pictureId = pictureId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getPictureId() { return pictureId; }

}
