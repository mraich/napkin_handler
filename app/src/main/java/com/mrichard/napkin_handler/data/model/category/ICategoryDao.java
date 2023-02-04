package com.mrichard.napkin_handler.data.model.category;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ICategoryDao
{

		@Query("SELECT * FROM " + Category.CATEGORY_TABLE + " WHERE id = :id LIMIT 1")
		Category getCategory(long id);

		@Query("SELECT * FROM " + Category.CATEGORY_TABLE)
		LiveData<List<Category>> getAll();

		@Insert
		void insert(Category picture);

		@Update
		void update(Category picture);

		@Delete
		void delete(Category picture);

}
