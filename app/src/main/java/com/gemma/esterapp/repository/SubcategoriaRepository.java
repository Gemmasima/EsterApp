package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.SubcategoriaDAO;
import com.gemma.esterapp.model.Subcategoria;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* REPOSITORIO DE SUBCATEGORIA - Capa intermedia entre la UI y la base de datos.
 * La UI nunca habla directamente con SubcategoriaDAO, siempre pasa por aquí.
 * Flujo: UI → Repository → DAO → Room → SQLite */

public class SubcategoriaRepository {

    // DAO para acceder a la tabla subcategorias
    private final SubcategoriaDAO subcategoriaDAO;

    // Hilo secundario para operaciones de escritura (insert, update, delete)
    private final ExecutorService executorService;

    // CONSTRUCTOR — obtiene la instancia única de la BD y prepara el hilo secundario
    public SubcategoriaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        subcategoriaDAO = db.subcategoriaDAO();
        executorService = Executors.newSingleThreadExecutor();
    }


    // OPERACIONES DE ESCRITURA (hilo secundario)

    // Inserta una subcategoría nueva en la base de datos
    public void insert(Subcategoria subcategoria) {
        executorService.execute(() -> subcategoriaDAO.insert(subcategoria));
    }

    // Actualiza una subcategoría existente en la base de datos
    public void update(Subcategoria subcategoria) {
        executorService.execute(() -> subcategoriaDAO.update(subcategoria));
    }

    // Elimina una subcategoría de la base de datos
    // OJO: RESTRICT — fallará si la subcategoría tiene gastos asociados
    public void delete(Subcategoria subcategoria) {
        executorService.execute(() -> subcategoriaDAO.delete(subcategoria));
    }

    // OPERACIONES DE LECTURA (devuelven LiveData)
    // Room ejecuta automaticamente las lecturas en un hilo secundario, no hace falta ExecutorService

    // Devuelve todas las subcategorías
    public LiveData<List<Subcategoria>> getAllSubcategorias() {
        return subcategoriaDAO.getAllSubcategorias();
    }

    // Devuelve todas las subcategorias que pertenecen a una categoria concreta
    // Por ejemplo, si el usuario selecciona Vehiculos devuelve Gasolina, Seguros y Taller
    // Se usa en RegistrarGastoActivity para mostrar los botones de subcategoria dinamicamente
    public LiveData<List<Subcategoria>> getSubcategoriasByCategoria(int idCategoria) {
        return subcategoriaDAO.getSubcategoriasByCategoria(idCategoria);
    }

    // Busca y devuelve una subcategoría por su id
    public LiveData<Subcategoria> getSubcategoriaById(int id) {
        return subcategoriaDAO.getSubcategoriaById(id);
    }
}