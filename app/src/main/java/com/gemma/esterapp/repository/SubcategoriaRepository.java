package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.SubcategoriaDAO;
import com.gemma.esterapp.model.Subcategoria;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * REPOSITORIO DE SUBCATEGORIA
 * Capa intermedia entre la UI y la base de datos.
 * La UI nunca habla directamente con SubcategoriaDAO, siempre pasa por aquí.
 * Arquitectura: UI → Repository → DAO → Room → SQLite
 */
public class SubcategoriaRepository {

    // DAO para acceder a la tabla subcategorias
    private final SubcategoriaDAO subcategoriaDAO;

    // Hilo secundario para operaciones de escritura (insert, update, delete)
    private final ExecutorService executorService;

    /**
     * CONSTRUCTOR
     * Obtiene la instancia única de la base de datos (Singleton)
     * y prepara el hilo secundario.
     */
    public SubcategoriaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        subcategoriaDAO = db.subcategoriaDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE ESCRITURA (hilo secundario)
    // ─────────────────────────────────────────────

    // Inserta una subcategoria nueva en la base de datos
    public void insert(Subcategoria subcategoria) {
        executorService.execute(() -> subcategoriaDAO.insert(subcategoria));
    }

    // Actualiza una subcategoria existente en la base de datos
    public void update(Subcategoria subcategoria) {
        executorService.execute(() -> subcategoriaDAO.update(subcategoria));
    }

    // Elimina una subcategoria de la base de datos
    public void delete(Subcategoria subcategoria) {
        executorService.execute(() -> subcategoriaDAO.delete(subcategoria));
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE LECTURA (devuelven LiveData)
    // ─────────────────────────────────────────────

    // Devuelve todas las subcategorias. La pantalla se actualiza sola si cambia algo
    public LiveData<List<Subcategoria>> getAllSubcategorias() {
        return subcategoriaDAO.getAllSubcategorias();
    }

    // Devuelve las subcategorias de una categoria concreta
    // Se usa cuando el usuario selecciona una categoria en el formulario de gastos
    public LiveData<List<Subcategoria>> getSubcategoriasByCategoria(int idCategoria) {
        return subcategoriaDAO.getSubcategoriasByCategoria(idCategoria);
    }

    // Devuelve una subcategoria por su id
    public LiveData<Subcategoria> getSubcategoriaById(int id) {
        return subcategoriaDAO.getSubcategoriaById(id);
    }
}