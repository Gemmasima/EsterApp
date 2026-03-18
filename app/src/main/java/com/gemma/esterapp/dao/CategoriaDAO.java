package com.gemma.esterapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gemma.esterapp.model.Categoria;
import java.util.List;

@Dao
public interface CategoriaDAO {

    // Inserta una categoria nueva en la tabla categorias
    @Insert
    void insert(Categoria categoria);

    // Actualiza una categoria existente en la tabla categorias
    @Update
    void update(Categoria categoria);

    // Borra una categoria de la tabla categorias
    // Recuerda: usamos RESTRICT, no se puede borrar si tiene subcategorias asociadas
    @Delete
    void delete(Categoria categoria);

    // Devuelve todas las categorias de la tabla
    @Query("SELECT * FROM categorias")
    LiveData<List<Categoria>> getAllCategorias();

    // Busca una categoria por su id
    @Query("SELECT * FROM categorias WHERE id_categoria = :id")
    LiveData<Categoria> getCategoriaById(int id);
}
