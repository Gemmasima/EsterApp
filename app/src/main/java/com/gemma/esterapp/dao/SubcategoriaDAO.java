package com.gemma.esterapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gemma.esterapp.model.Subcategoria;
import java.util.List;

@Dao
public interface SubcategoriaDAO {

    // Inserta una subcategoria nueva en la tabla subcategorias
    @Insert
    void insert(Subcategoria subcategoria);

    // Actualiza una subcategoria existente en la tabla subcategorias
    @Update
    void update(Subcategoria subcategoria);

    // Borra una subcategoria de la tabla subcategorias
    @Delete
    void delete(Subcategoria subcategoria);

    // Devuelve todas las subcategorias de la tabla
    @Query("SELECT * FROM subcategorias")
    List<Subcategoria> getAllSubcategorias();

    // Devuelve todas las subcategorias de una categoria concreta
    // Se usa para cargar las subcategorias cuando el usuario selecciona una categoria
    @Query("SELECT * FROM subcategorias WHERE id_categoria = :idCategoria")
    List<Subcategoria> getSubcategoriasByCategoria(int idCategoria);

    // Busca una subcategoria por su id
    @Query("SELECT * FROM subcategorias WHERE id_subcategoria = :id")
    Subcategoria getSubcategoriaById(int id);
}