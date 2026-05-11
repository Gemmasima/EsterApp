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
// Room genera automáticamente el código SQL de cada metodo al compilar
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

    // Devuelve todos los gastos de la tabla sin ningun filtro
    @Query("SELECT * FROM gastos")
    LiveData<List<Gasto>> getAllGastos();

    /* Devuelve solo los gastos registrados por un usuario concreto, se usa
    para que Gerente y Trabajador solo vean sus propios movimientos */
    @Query("SELECT * FROM gastos WHERE id_usuario = :idUsuario")
    LiveData<List<Gasto>> getGastosByUsuario(int idUsuario);

    // Devuelve todos los gastos que pertenecen a una categoria concreta (id)
    @Query("SELECT * FROM gastos WHERE id_categoria = :idCategoria")
    LiveData<List<Gasto>> getGastosByCategoria(int idCategoria);

    // Devuelve todos los gastos de una fecha exacta (formato "YYYY-MM-DD")
    @Query("SELECT * FROM gastos WHERE fecha = :fecha")
    LiveData<List<Gasto>> getGastosByFecha(String fecha);

    /* Calcula y devuelve la suma total de los importes de un mes concreto
    se usa en InformesActivity para mostrar el total de gastos del mes en rojo */
    @Query("SELECT SUM(importe) FROM gastos WHERE substr(fecha, 1, 7) = :mes")
    LiveData<Double> getTotalGastosByMes(String mes);

    // Calcula y devuelve la suma total de los importes de un día concreto
    @Query("SELECT SUM(importe) FROM gastos WHERE fecha = :fecha")
    LiveData<Double> getTotalGastosByDia(String fecha);

    /* Calcula y devuelve la suma total de los importes entre dos fechas
    BETWEN incluye las dos fechas (desde y hasta) */
    @Query("SELECT SUM(importe) FROM gastos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<Double> getTotalGastosByRangoFechas(String desde, String hasta);

    /* Devuelve todos los gastos entre dos fechas, se usa en GeneraraInformesActivity
    para construir el CSV con el rango de fechas seleccionado */
    @Query("SELECT * FROM gastos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<List<Gasto>> getGastosByRangoFechas(String desde, String hasta);

    /* Devuelve todos los gastos de un mes concreto ordenados de más reciente al
    mas antiguo. Se usa en InformesActivity para mostrar la lista de movimientos del  mes */
    @Query("SELECT * FROM gastos WHERE substr(fecha, 1, 7) = :mes ORDER BY fecha DESC")
    LiveData<List<Gasto>> getGastosByMes(String mes);
}