package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.UsuarioDAO;
import com.gemma.esterapp.model.Usuario;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * REPOSITORIO DE USUARIO
 * Capa intermedia entre la UI y la base de datos.
 * La Activity nunca habla directamente con UsuarioDAO, siempre pasa por aquí.
 * Arquitectura: UI → Repository → DAO → Room → SQLite
 *
 * Ventajas de esta capa:
 * - Mantiene la UI limpia, sin lógica de base de datos
 * - Centraliza el acceso a los datos en un solo sitio
 * - Facilita las pruebas del proyecto
 */
public class UsuarioRepository {

    // DAO para acceder a la tabla usuarios
    private final UsuarioDAO usuarioDAO;

    // ExecutorService: gestor de hilos en segundo plano
    // Android no permite operaciones de BD en el hilo principal (UI thread)
    // newSingleThreadExecutor() crea 1 hilo secundario — las operaciones van en orden
    private final ExecutorService executorService;

    // CONSTRUCTOR — recibe Application (no Context) porque vive durante toda la app
    // Evita fugas de memoria que ocurrirían guardando una Activity como Context
    public UsuarioRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application); // instancia única Singleton
        usuarioDAO = db.usuarioDAO();                          // obtenemos el DAO de usuarios
        executorService = Executors.newSingleThreadExecutor(); // creamos el hilo secundario
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE ESCRITURA (hilo secundario)
    // ─────────────────────────────────────────────

    // Inserta un usuario nuevo — se ejecuta en hilo secundario
    public void insert(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.insert(usuario));
    }

    // Actualiza un usuario existente — se ejecuta en hilo secundario
    public void update(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.update(usuario));
    }

    // Elimina un usuario — se ejecuta en hilo secundario
    // Puede fallar si el usuario tiene gastos o ingresos asociados (RESTRICT)
    public void delete(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.delete(usuario));
    }

    // Interfaz para avisar a la Activity si el delete ha ido bien o mal
    // onSuccess() → el delete funcionó correctamente
    // onError()   → la BD lanzó excepción RESTRICT (usuario con movimientos asociados)
    public interface DeleteCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    // Delete con callback — avisa a la Activity del resultado
    // A diferencia de delete() normal, este captura la excepción RESTRICT
    // y la comunica a la UI en lugar de silenciarla
    public void deleteConCallback(Usuario usuario, DeleteCallback callback) {
        executorService.execute(() -> {
            try {
                usuarioDAO.delete(usuario);
                callback.onSuccess(); // el delete fue bien
            } catch (Exception e) {
                // RESTRICT: el usuario tiene gastos o ingresos asociados
                callback.onError("No se puede eliminar este usuario porque tiene gastos o ingresos asociados");
            }
        });
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE LECTURA (devuelven LiveData)
    // ─────────────────────────────────────────────

    // LiveData: la UI se actualiza automáticamente cuando cambian los datos
    // Room gestiona el hilo de LiveData solo — NO necesitamos executorService aquí

    // Devuelve todos los usuarios de la tabla
    public LiveData<List<Usuario>> getAllUsuarios() {
        return usuarioDAO.getAllUsuarios();
    }

    // Valida el login — devuelve el Usuario si las credenciales son correctas, null si no
    public LiveData<Usuario> login(String usuario, String contrasena) {
        return usuarioDAO.login(usuario, contrasena);
    }

    // Busca un usuario por su id
    public LiveData<Usuario> getUsuarioById(int idUsuario) {
        return usuarioDAO.getUsuarioById(idUsuario);
    }
}