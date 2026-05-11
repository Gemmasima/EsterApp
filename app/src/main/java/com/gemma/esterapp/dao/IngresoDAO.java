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
// Room genera automáticamente el código SQL de cada metodo al compilar
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

    /* Suma todos los ingresos del mes. substring(1,7) significa que extrae
    * los 7 primeros de la fecha, asi filtra por mes sin importar el dia,
    * se usa en informesActivity para mostrar el total de ingresos en verde */
    @Query("SELECT SUM(importe) FROM ingresos WHERE substr(fecha, 1, 7) = :mes")
    LiveData<Double> getTotalIngresosByMes(String mes);

    // Devuelve la suma total de ingresos de un día concreto
    @Query("SELECT SUM(importe) FROM ingresos WHERE fecha = :fecha")
    LiveData<Double> getTotalIngresosByDia(String fecha);

    /* Suma todos los ingresos entre dos fechas BETWEEN, se usa
     * GenerarInformesActivity para calcular el total del periodo selccionado */
    @Query("SELECT SUM(importe) FROM ingresos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<Double> getTotalIngresosByRangoFechas(String desde, String hasta);

    /* Devuelve la lista completa de ingresos entre dos fechas, a diferencia
    * del metodo anterior, este no suma los importes, devuelve cada ingreso
    * como una fila en el archivo CSV*/
    @Query("SELECT * FROM ingresos WHERE fecha BETWEEN :desde AND :hasta")
    LiveData<List<Ingreso>> getIngresosByRangoFechas(String desde, String hasta);

    /* Devuelve la lista de ingresos del mes ordenada DESC, utiliza el substr
    * para filtrar por mes sin importar el dia y se usa en InformesActivity para
    * mostrar la lista de movimientos de mes con fondo color verde */
    @Query("SELECT * FROM ingresos WHERE substr(fecha, 1, 7) = :mes ORDER BY fecha DESC")
    LiveData<List<Ingreso>> getIngresosByMes(String mes);
}