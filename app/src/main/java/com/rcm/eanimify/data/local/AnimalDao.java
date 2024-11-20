package com.rcm.eanimify.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AnimalDao {
    @Insert
    void insert(Animal animal);

    @Query("SELECT * FROM animals")
    List<Animal> getAllAnimals();

    @Query("SELECT * FROM animals WHERE common_name = :animalName")
    Animal getAnimalByCommonName(String animalName);

    // Add more queries as needed
}