package com.gemma.esterapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.gemma.esterapp.model.Ingreso;
import java.util.List;

// @Dao le dice a Room que esta interfaz es un DAO
// Room genera automáticamente el código SQL de cada método al compilar
@Dao
public interface IngresoDAO {

    // Inserta un ingreso nuevo en la tabla ingresos
    @Insert
    void insert(Ingreso ingreso);

    // Actualiza un ingreso existente en la tabla ingresos
    @Update
    void update(Ingreso ingreso);

    // Borra un ingreso de la tabla ingresos
    @Delete
    void delete(Ingreso ingreso);

    // Devuelve todos los ingresos de la tabla
    @Query("SELECT * FROM ingresos")
    LiveData<List<Ingreso>> getAllIngresos();

    // Devuelve todos los ingresos registrados por un usuario concreto
    @Query("SELECT * FROM ingresos WHERE id_usuario = :idUsuario")
    LiveData<List<Ingreso>> getIngresosByUsuario(int idUsuario);

    // Devuelve todos los ingresos de una fecha exacta (formato "YYYY-MM-DD")
    @Query("SELECT * FROM ingresos WHERE fecha = :fecha")
    LiveData<List<Ingreso>> getIngresosByFecha(String fecha);

    // Devuelve los ingresos filtrados por tipo: "Efectivo" o "Tarjeta"
    @Query("SELECT * FROM ingresos WHERE tipoingreso = :tipoingreso")
    LiveData<List<Ingreso>> getIngresosByTipo(String tipoingreso);

    // Devuelve la suma total de ingresos de un mes concreto
    // substr(fecha, 1, 7) extrae los primeros 7 caracteres: "2026-03"
    @Query("SELECT SUM(importe) FROM ingresos WHERE substr(fecha, 1, 7) = :mes")
    LiveData<Double> getTotalIngresosByMes(String mes);

    // Devuelve la suma total de ingresos de un día concreto
    @Query("SELECT SUM(importe) FROM ingresos WHERE fecha = :fecha")
    LiveData<Double> getTotalIngresosByDia(String fecha);

    // Devuelve la suma total de ingresos entre dos fechas
    // BETWEEN incluye las dos fechas extremas
    @Query("SELECT SUM(importe) FROM ingresos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<Double> getTotalIngresosByRangoFechas(String desde, String hasta);

    // Devuelve todos los ingresos entre dos fechas — usado para generar el CSV
    @Query("SELECT * FROM ingresos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<List<Ingreso>> getIngresosByRangoFechas(String desde, String hasta);

    // Devuelve todos los ingresos de un mes concreto ordenados por fecha descendente
    // Usado en InformesActivity para mostrar la lista de movimientos
    @Query("SELECT * FROM ingresos WHERE substr(fecha, 1, 7) = :mes ORDER BY fecha DESC")
    LiveData<List<Ingreso>> getIngresosByMes(String mes);
}