package com.gemma.esterapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gemma.esterapp.model.Ingreso;
import java.util.List;

@Dao
public interface IngresoDAO {


    @Insert // Inserta un ingreso nuevo en la tabla ingresos
    void insert(Ingreso ingreso);


    @Update // Actualiza un ingreso existente en la tabla ingresos
    void update(Ingreso ingreso);


    @Delete    // Borra un ingreso de la tabla ingresos
    void delete(Ingreso ingreso);

    // Devuelve todos los ingresos de la tabla
    @Query("SELECT * FROM ingresos")
    LiveData<List<Ingreso>> getAllIngresos();

    // Devuelve todos los ingresos de un usuario concreto
    @Query("SELECT * FROM ingresos WHERE id_usuario = :idUsuario")
    LiveData<List<Ingreso>> getIngresosByUsuario(int idUsuario);

    // Devuelve todos los ingresos de una fecha concreta
    @Query("SELECT * FROM ingresos WHERE fecha = :fecha")
    LiveData<List<Ingreso>> getIngresosByFecha(String fecha);

    // Devuelve los ingresos por tipo (efectivo o tarjeta)
    @Query("SELECT * FROM ingresos WHERE tipoingreso = :tipoingreso")
    LiveData<List<Ingreso>> getIngresosByTipo(String tipoingreso);

    // Devuelve la suma total de ingresos de un mes concreto para los informes
    @Query("SELECT SUM(importe) FROM ingresos WHERE substr(fecha, 1, 7) = :mes")
    LiveData<Double> getTotalIngresosByMes(String mes);

    // Devuelve la suma total de ingresos de un día concreto para el informe diario
    @Query("SELECT SUM(importe) FROM ingresos WHERE fecha = :fecha")
    LiveData<Double> getTotalIngresosByDia(String fecha);

    // Devuelve la suma total de ingresos entre dos fechas para el informe semanal
    // BETWEEN incluye las dos fechas extremas
    @Query("SELECT SUM(importe) FROM ingresos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<Double> getTotalIngresosByRangoFechas(String desde, String hasta);
}