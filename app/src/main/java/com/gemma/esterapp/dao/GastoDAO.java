package com.gemma.esterapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gemma.esterapp.model.Gasto;
import java.util.List;

@Dao //le dice a Room que esta interfaz es un DAO y así Room generará automáticamente el código SQL de cada metodo
public interface GastoDAO {


    @Insert // Inserta un gasto nuevo en la tabla gastos
    void insert(Gasto gasto);


    @Update // Actualiza un gasto existente en la tabla gastos
    void update(Gasto gasto);


    @Delete // Borra un gasto de la tabla gastos
    void delete(Gasto gasto);


    @Query("SELECT * FROM gastos")  // Devuelve todos los gastos de la tabla
    List<Gasto> getAllGastos();

    // Devuelve todos los gastos de un usuario concreto
    @Query("SELECT * FROM gastos WHERE id_usuario = :idUsuario")
    List<Gasto> getGastosByUsuario(int idUsuario);

    // Devuelve todos los gastos de una categoría concreta
    @Query("SELECT * FROM gastos WHERE id_categoria = :idCategoria")
    List<Gasto> getGastosByCategoria(int idCategoria);

    // Devuelve todos los gastos de una fecha concreta
    @Query("SELECT * FROM gastos WHERE fecha = :fecha")
    List<Gasto> getGastosByFecha(String fecha);

    // Devuelve la suma total de gastos de un mes concreto para los informes
    // substr extrae el año y el mes de la fecha (ej: "2026-03")
    @Query("SELECT SUM(importe) FROM gastos WHERE substr(fecha, 1, 7) = :mes")
    double getTotalGastosByMes(String mes);
}