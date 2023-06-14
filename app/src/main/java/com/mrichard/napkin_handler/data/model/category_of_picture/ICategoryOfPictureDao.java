package com.mrichard.napkin_handler.data.model.category_of_picture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ICategoryOfPictureDao
{

		@Query("SELECT * FROM " + CategoryOfPicture.CATEGORY_OF_PICTURE_TABLE + " WHERE picture_id = :pictureId")
		LiveData<List<CategoryOfPicture>> getCategoriesOfPicture(long pictureId);

		@Insert
		void insert(CategoryOfPicture picture);

		@Delete
		void delete(CategoryOfPicture picture);

}
