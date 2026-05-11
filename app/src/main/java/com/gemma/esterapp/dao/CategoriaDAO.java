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
// Room genera automáticamente el código SQL de cada metodo al compilar
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

    // Devuelve toda la tabla
    // LiveData hace que la pantalla se actualice automatic. si cambia algun dato
    @Query("SELECT * FROM categorias")
    LiveData<List<Categoria>> getAllCategorias();

    // Busca y devuelve LiveData la categoría que tenga ese id concreto.
    @Query("SELECT * FROM categorias WHERE id_categoria = :id")
    LiveData<Categoria> getCategoriaById(int id);
}
