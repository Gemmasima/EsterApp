package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.CategoriaDAO;
import com.gemma.esterapp.model.Categoria;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * REPOSITORIO DE CATEGORIA
 * Capa intermedia entre la UI y la base de datos.
 * La UI nunca habla directamente con CategoriaDAO, siempre pasa por aquí.
 * Arquitectura: UI → Repository → DAO → Room → SQLite
 */
public class CategoriaRepository {

    // DAO para acceder a la tabla categorias
    private final CategoriaDAO categoriaDAO;

    // Hilo secundario para operaciones de escritura (insert, update, delete)
    private final ExecutorService executorService;

    // CONSTRUCTOR — obtiene la instancia única de la BD y prepara el hilo secundario
    public CategoriaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoriaDAO = db.categoriaDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE ESCRITURA (hilo secundario)
    // ─────────────────────────────────────────────

    // Inserta una categoría nueva en la base de datos
    public void insert(Categoria categoria) {
        executorService.execute(() -> categoriaDAO.insert(categoria));
    }

    // Actualiza una categoría existente en la base de datos
    public void update(Categoria categoria) {
        executorService.execute(() -> categoriaDAO.update(categoria));
    }

    // Elimina una categoría de la base de datos
    // OJO: RESTRICT — fallará si la categoría tiene subcategorías o gastos asociados
    public void delete(Categoria categoria) {
        executorService.execute(() -> categoriaDAO.delete(categoria));
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE LECTURA (devuelven LiveData)
    // ─────────────────────────────────────────────

    // Devuelve todas las categorías — usado en InformesActivity para el mapa de nombres
    public LiveData<List<Categoria>> getAllCategorias() {
        return categoriaDAO.getAllCategorias();
    }

    // Devuelve una categoría por su id
    public LiveData<Categoria> getCategoriaById(int id) {
        return categoriaDAO.getCategoriaById(id);
    }
}