package com.mrichard.napkin_handler.data.model.picture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IPictureDao
{

		@Query("SELECT * FROM " + Picture.PICTURE_TABLE + " WHERE id = :id LIMIT 1")
		Picture getPicture(long id);

		@Query("SELECT * FROM " + Picture.PICTURE_TABLE)
		LiveData<List<Picture>> getAll();

		@Query("SELECT * FROM " + Picture.PICTURE_TABLE + " WHERE is_new = 1")
		LiveData<List<Picture>> newPictures();

		@Insert
		void insert(Picture picture);

		@Update
		void update(Picture picture);

		@Delete
		void delete(Picture picture);

}
