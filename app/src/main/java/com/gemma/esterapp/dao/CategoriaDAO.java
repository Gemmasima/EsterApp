package com.gemma.esterapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.gemma.esterapp.model.Categoria;
import java.util.List;

// @Dao le dice a Room que esta interfaz es un DAO
// Room genera automáticamente el código SQL de cada método al compilar
@Dao
public interface CategoriaDAO {

    // Inserta una categoría nueva en la tabla categorias
    @Insert
    void insert(Categoria categoria);

    // Actualiza una categoría existente en la tabla categorias
    @Update
    void update(Categoria categoria);

    // Borra una categoría de la tabla categorias
    // RESTRICT: fallará si la categoría tiene subcategorías o gastos asociados
    @Delete
    void delete(Categoria categoria);

    // Devuelve todas las categorías de la tabla
    // Usado en InformesActivity para cargar los mapas de nombres
    @Query("SELECT * FROM categorias")
    LiveData<List<Categoria>> getAllCategorias();

    // Busca una categoría por su id
    @Query("SELECT * FROM categorias WHERE id_categoria = :id")
    LiveData<Categoria> getCategoriaById(int id);
}
