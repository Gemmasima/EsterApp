package com.gemma.esterapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.gemma.esterapp.model.Subcategoria;
import java.util.List;

// @Dao le dice a Room que esta interfaz es un DAO
// Room genera automáticamente el código SQL de cada método al compilar
@Dao
public interface SubcategoriaDAO {

    // Inserta una subcategoría nueva en la tabla subcategorias
    @Insert
    void insert(Subcategoria subcategoria);

    // Actualiza una subcategoría existente en la tabla subcategorias
    @Update
    void update(Subcategoria subcategoria);

    // Borra una subcategoría de la tabla subcategorias
    // RESTRICT: fallará si la subcategoría tiene gastos asociados
    @Delete
    void delete(Subcategoria subcategoria);

    /* Devuelve todas las subcategorías de la tabla sin ningun filtro
    * Se usa en informesactivity para poder mostrar el nombre de la subcategoria
    * en la lista de movimientos */
    @Query("SELECT * FROM subcategorias")
    LiveData<List<Subcategoria>> getAllSubcategorias();

    /* Devuelve las subcategorías de una categoría concreta.
    * Se usa en resgistrargastoactivity para mostrar los botones de subcate */
    @Query("SELECT * FROM subcategorias WHERE id_categoria = :idCategoria")
    LiveData<List<Subcategoria>> getSubcategoriasByCategoria(int idCategoria);

    // Busca una subcategoría por su id
    @Query("SELECT * FROM subcategorias WHERE id_subcategoria = :id")
    LiveData<Subcategoria> getSubcategoriaById(int id);
}