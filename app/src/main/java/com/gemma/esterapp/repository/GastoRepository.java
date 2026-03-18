package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.GastoDAO;
import com.gemma.esterapp.model.Gasto;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * REPOSITORIO DE GASTO
 * Capa intermedia entre la UI y la base de datos.
 * La UI nunca habla directamente con GastoDAO, siempre pasa por aquí.
 * Arquitectura: UI → Repository → DAO → Room → SQLite
 */
public class GastoRepository {

    // DAO para acceder a la tabla gastos
    private final GastoDAO gastoDAO;

    // Hilo secundario para operaciones de escritura (insert, update, delete)
    private final ExecutorService executorService;

    /**
     * CONSTRUCTOR
     * Obtiene la instancia única de la base de datos (Singleton)
     * y prepara el hilo secundario.
     */
    public GastoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        gastoDAO = db.gastoDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE ESCRITURA (hilo secundario)
    // ─────────────────────────────────────────────

    // Inserta un gasto nuevo en la base de datos
    public void insert(Gasto gasto) {
        executorService.execute(() -> gastoDAO.insert(gasto));
    }

    // Actualiza un gasto existente en la base de datos
    public void update(Gasto gasto) {
        executorService.execute(() -> gastoDAO.update(gasto));
    }

    // Elimina un gasto de la base de datos
    public void delete(Gasto gasto) {
        executorService.execute(() -> gastoDAO.delete(gasto));
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE LECTURA (devuelven LiveData)
    // ─────────────────────────────────────────────

    // Devuelve todos los gastos. La pantalla se actualiza sola si cambia algo
    public LiveData<List<Gasto>> getAllGastos() {
        return gastoDAO.getAllGastos();
    }

    // Devuelve todos los gastos de un usuario concreto
    public LiveData<List<Gasto>> getGastosByUsuario(int idUsuario) {
        return gastoDAO.getGastosByUsuario(idUsuario);
    }

    // Devuelve todos los gastos de una categoría concreta
    public LiveData<List<Gasto>> getGastosByCategoria(int idCategoria) {
        return gastoDAO.getGastosByCategoria(idCategoria);
    }

    // Devuelve todos los gastos de una fecha concreta
    public LiveData<List<Gasto>> getGastosByFecha(String fecha) {
        return gastoDAO.getGastosByFecha(fecha);
    }

    // Devuelve la suma total de gastos de un mes concreto (para informes)
    public LiveData<Double> getTotalGastosByMes(String mes) {
        return gastoDAO.getTotalGastosByMes(mes);
    }
    // Devuelve la suma total de gastos de un día concreto (para informe diario)
    public LiveData<Double> getTotalGastosByDia(String fecha) {
        return gastoDAO.getTotalGastosByDia(fecha);
    }

    // Devuelve la suma total de gastos entre dos fechas (para informe semanal)
    public LiveData<Double> getTotalGastosByRangoFechas(String desde, String hasta) {
        return gastoDAO.getTotalGastosByRangoFechas(desde, hasta);
    }

}
