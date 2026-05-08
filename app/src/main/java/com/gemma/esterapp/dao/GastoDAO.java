package com.gemma.esterapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.gemma.esterapp.model.Gasto;
import java.util.List;

// @Dao le dice a Room que esta interfaz es un DAO
// Room genera automáticamente el código SQL de cada método al compilar
@Dao
public interface GastoDAO {

    // Inserta un gasto nuevo en la tabla gastos
    @Insert
    void insert(Gasto gasto);

    // Actualiza un gasto existente en la tabla gastos
    @Update
    void update(Gasto gasto);

    // Borra un gasto de la tabla gastos
    @Delete
    void delete(Gasto gasto);

    // Devuelve todos los gastos de la tabla
    @Query("SELECT * FROM gastos")
    LiveData<List<Gasto>> getAllGastos();

    // Devuelve todos los gastos registrados por un usuario concreto
    @Query("SELECT * FROM gastos WHERE id_usuario = :idUsuario")
    LiveData<List<Gasto>> getGastosByUsuario(int idUsuario);

    // Devuelve todos los gastos de una categoría concreta
    @Query("SELECT * FROM gastos WHERE id_categoria = :idCategoria")
    LiveData<List<Gasto>> getGastosByCategoria(int idCategoria);

    // Devuelve todos los gastos de una fecha exacta (formato "YYYY-MM-DD")
    @Query("SELECT * FROM gastos WHERE fecha = :fecha")
    LiveData<List<Gasto>> getGastosByFecha(String fecha);

    // Devuelve la suma total de gastos de un mes concreto
    // substr(fecha, 1, 7) extrae los primeros 7 caracteres: "2026-03"
    @Query("SELECT SUM(importe) FROM gastos WHERE substr(fecha, 1, 7) = :mes")
    LiveData<Double> getTotalGastosByMes(String mes);

    // Devuelve la suma total de gastos de un día concreto
    @Query("SELECT SUM(importe) FROM gastos WHERE fecha = :fecha")
    LiveData<Double> getTotalGastosByDia(String fecha);

    // Devuelve la suma total de gastos entre dos fechas
    // BETWEEN incluye las dos fechas extremas
    @Query("SELECT SUM(importe) FROM gastos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<Double> getTotalGastosByRangoFechas(String desde, String hasta);

    // Devuelve todos los gastos entre dos fechas — usado para generar el CSV
    @Query("SELECT * FROM gastos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<List<Gasto>> getGastosByRangoFechas(String desde, String hasta);

    // Devuelve todos los gastos de un mes concreto ordenados por fecha descendente
    // Usado en InformesActivity para mostrar la lista de movimientos
    @Query("SELECT * FROM gastos WHERE substr(fecha, 1, 7) = :mes ORDER BY fecha DESC")
    LiveData<List<Gasto>> getGastosByMes(String mes);
}