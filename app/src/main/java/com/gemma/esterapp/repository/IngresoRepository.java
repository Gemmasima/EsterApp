package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.IngresoDAO;
import com.gemma.esterapp.model.Ingreso;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* REPOSITORIO DE INGRESO  -  Capa intermedia entre la UI y la base de datos.
 * La UI nunca habla directamente con IngresoDAO, siempre pasa por aquí.
 * Flujo: UI → Repository → DAO → Room → SQLite */

public class IngresoRepository {

    // DAO para acceder a la tabla ingresos
    private final IngresoDAO ingresoDAO;

    /* ExecutorService gestiona un hilo secundario para las operaciones de escritura.
     * Android no permite modificar la BD en el hilo principal porque bloquea la pantalla,
     * por eso insert, update y delete se ejecutan en un hilo separado. */
    private final ExecutorService executorService;

    // Constructor — obtiene la instancia unica de la BD (Singleton) y prepara el hilo secundario
    public IngresoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        ingresoDAO = db.ingresoDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    // OPERACIONES DE ESCRITURA (hilo secundario)

    // Inserta un ingreso nuevo en la base de datos
    public void insert(Ingreso ingreso) {
        executorService.execute(() -> ingresoDAO.insert(ingreso));
    }

    // Actualiza un ingreso existente en la base de datos
    public void update(Ingreso ingreso) {
        executorService.execute(() -> ingresoDAO.update(ingreso));
    }

    // Elimina un ingreso de la base de datos
    public void delete(Ingreso ingreso) {
        executorService.execute(() -> ingresoDAO.delete(ingreso));
    }


    // OPERACIONES DE LECTURA (devuelven LiveData)
// Room ejecuta automaticamente las lecturas en un hilo secundario, no hace falta ExecutorService
    public LiveData<List<Ingreso>> getAllIngresos() {
        return ingresoDAO.getAllIngresos();
    }

    // Devuelve todos los ingresos de un usuario concreto
    public LiveData<List<Ingreso>> getIngresosByUsuario(int idUsuario) {
        return ingresoDAO.getIngresosByUsuario(idUsuario);
    }

    // Devuelve todos los ingresos de una fecha concreta
    public LiveData<List<Ingreso>> getIngresosByFecha(String fecha) {
        return ingresoDAO.getIngresosByFecha(fecha);
    }

    // Devuelve los ingresos filtrados por tipo: "Efectivo" o "Tarjeta"
    public LiveData<List<Ingreso>> getIngresosByTipo(String tipoingreso) {
        return ingresoDAO.getIngresosByTipo(tipoingreso);
    }

    // Devuelve la suma total de ingresos de un mes — usado en InformesActivity
    public LiveData<Double> getTotalIngresosByMes(String mes) {
        return ingresoDAO.getTotalIngresosByMes(mes);
    }

    // Devuelve la suma total de ingresos de un día concreto
    public LiveData<Double> getTotalIngresosByDia(String fecha) {
        return ingresoDAO.getTotalIngresosByDia(fecha);
    }

    // Devuelve la suma total de ingresos entre dos fechas
    public LiveData<Double> getTotalIngresosByRangoFechas(String desde, String hasta) {
        return ingresoDAO.getTotalIngresosByRangoFechas(desde, hasta);
    }

    // Devuelve todos los ingresos entre dos fechas — usado para generar el CSV
    public LiveData<List<Ingreso>> getIngresosByRangoFechas(String desde, String hasta) {
        return ingresoDAO.getIngresosByRangoFechas(desde, hasta);
    }

    // Devuelve todos los ingresos de un mes — usado en InformesActivity para la lista
    public LiveData<List<Ingreso>> getIngresosByMes(String mes) {
        return ingresoDAO.getIngresosByMes(mes);
    }
}