package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.UsuarioDAO;
import com.gemma.esterapp.model.Usuario;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* REPOSITORIO DE USUARIO - Capa intermedia entre la UI y la base de datos.
 * La Activity nunca habla directamente con UsuarioDAO, siempre pasa por aquí.
 * Flujo: UI → Repository → DAO → Room → SQLite */

public class UsuarioRepository {

    // DAO para acceder a la tabla usuarios
    private final UsuarioDAO usuarioDAO;

    /* ExecutorService gestiona un hilo secundario para las operaciones de escritura.
     * Android no permite modificar la BD en el hilo principal porque bloquea la pantalla,
     * por eso insert, update y delete se ejecutan en un hilo separado. */
    private final ExecutorService executorService;

    // CONSTRUCTOR — obtiene la instancia unica de la BD (Singleton) y prepara el hilo secundario
    // Recibe Application en lugar de Context para evitar fugas de memoria
    public UsuarioRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application); // instancia única Singleton
        usuarioDAO = db.usuarioDAO();                          // obtenemos el DAO de usuarios
        executorService = Executors.newSingleThreadExecutor(); // un unico hilo secundario para evitar conflictos
    }


    // OPERACIONES DE ESCRITURA (hilo secundario)

    // Inserta un usuario nuevo
    public void insert(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.insert(usuario));
    }

    // Actualiza un usuario existente
    public void update(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.update(usuario));
    }

    // Elimina un usuario
    // OJO: RESTRICT — fallara si el usuario tiene gastos o ingresos asociados
    public void delete(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.delete(usuario));
    }

    /* DELETECALLBACK - Interfaz que avisa a la Activity del resultado del delete.
     * onSuccess() se llama si el delete fue bien.
     * onError() se llama si la BD lanzo una excepcion RESTRICT porque el usuario
     * tiene gastos o ingresos asociados y no se puede eliminar. */
    public interface DeleteCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    /* Delete con callback — a diferencia de delete() normal, este captura la excepcion
     * RESTRICT y la comunica a la UI en lugar de silenciarla, para que la pantalla
     * pueda mostrar un mensaje de error al usuario */
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


    // OPERACIONES DE LECTURA (devuelven LiveData)
    // Room ejecuta automaticamente las lecturas en un hilo secundario, no hace falta ExecutorService

    // Devuelve todos los usuarios — la UI se actualiza sola si cambia algo
    public LiveData<List<Usuario>> getAllUsuarios() {
        return usuarioDAO.getAllUsuarios();
    }

    // Valida el login — devuelve el Usuario si las credenciales son correctas, null si no
    public LiveData<Usuario> login(String usuario, String contrasena) {
        return usuarioDAO.login(usuario, contrasena);
    }

    // Busca y devuelve un usuario que tenga ese id correcto
    public LiveData<Usuario> getUsuarioById(int idUsuario) {
        return usuarioDAO.getUsuarioById(idUsuario);
    }
}