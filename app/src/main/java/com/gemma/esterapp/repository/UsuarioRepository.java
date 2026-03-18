package com.gemma.esterapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.gemma.esterapp.database.AppDatabase;
import com.gemma.esterapp.dao.UsuarioDAO;
import com.gemma.esterapp.model.Usuario;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Arquitectura completa:
 * UI → Repository → DAO → Room → SQLite

 * REPOSITORIO DE USUARIO: El Repositorio es la capa que está entre la UI y la base de datos.
 * La Activity no habla directamente con el DAO.
 * La Activity habla con el Repositorio, y el Repositorio habla con el DAO.

 * ¿Por qué esta capa existe? Mantiene la UI limpia (sin lógica de base de datos)
 * - Centraliza el acceso a los datos en un solo sitio
 * - Facilita las pruebas del proyecto */
public class UsuarioRepository {


    private final UsuarioDAO usuarioDAO; // El DAO que usaremos para acceder a la base de datos

    /** ExecutorService: es un gestor de hilos en segundo plano.

     * ¿Por qué necesitamos hilos?
     * Android no permite hacer operaciones de base de datos en el hilo principal (el hilo de la UI).
     * Si lo hiciéramos, la app se bloquearía y daría error.
     * Por eso usamos un hilo separado para todas las operaciones de escritura/lectura.

     * Executors.newSingleThreadExecutor() crea exactamente 1 hilo secundario.
     * Las operaciones se ejecutan una detrás de otra, en orden. */

    private final ExecutorService executorService;

    /** CONSTRUCTOR - Recibe Application (no Context) porque Application vive durante toda la vida de la app.
     * Evita fugas de memoria que ocurrirían si guardáramos una Activity como Context.
     * @param application La aplicación Android*/
    public UsuarioRepository(Application application) {
        // Obtenemos la instancia única de la base de datos (Singleton)
        AppDatabase db = AppDatabase.getInstance(application);

        // Obtenemos el DAO de usuario desde la base de datos
        usuarioDAO = db.usuarioDAO();

        // Creamos el ejecutor con 1 hilo secundario
        executorService = Executors.newSingleThreadExecutor();
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE ESCRITURA (van en hilo secundario)
    // ─────────────────────────────────────────────

    /** Inserta un nuevo usuario en la base de datos.
     * executorService.execute(...) significa:
     * "Ejecuta esto en el hilo secundario, no en el hilo de la UI"
     * @param usuario El objeto Usuario a insertar */

    public void insert(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.insert(usuario));
    }

    /**Actualiza un usuario existente en la base de datos.
     * @param usuario El objeto Usuario con los datos nuevos */

    public void update(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.update(usuario));
    }

    /**Elimina un usuario de la base de datos.
     * @param usuario El objeto Usuario a eliminar */

    public void delete(Usuario usuario) {
        executorService.execute(() -> usuarioDAO.delete(usuario));
    }

    // ─────────────────────────────────────────────
    // OPERACIONES DE LECTURA (devuelven LiveData)
    // ─────────────────────────────────────────────

    /**Devuelve todos los usuarios de la base de datos.
     * ¿Qué es LiveData?  es un contenedor de datos que avisa automáticamente a la UI
     * cuando los datos cambian. La UI no tiene que preguntar cada vez si hay cambios,
     * LiveData lo notifica solo.

     * Room gestiona el hilo de LiveData automáticamente,
     * por eso aquí NO necesitamos executorService.
     * @return Lista observable de todos los usuarios */

    public LiveData<List<Usuario>> getAllUsuarios() {
        return usuarioDAO.getAllUsuarios();
    }

    /** Busca un usuario por su nombre de usuario y contraseña.
     * Se usa para validar el login.

     * IMPORTANTE: Este metodo devuelve LiveData<Usuario>.
     * Si las credenciales son correctas → devuelve el objeto Usuario.
     * Si las credenciales son incorrectas → devuelve null.

     * @param usuario    El nombre de usuario introducido en el login
     * @param contrasena La contraseña introducida en el login
     * @return El usuario encontrado, o null si no existe */

    public LiveData<Usuario> login(String usuario, String contrasena) {
        return usuarioDAO.login(usuario, contrasena);
    }

    /** Busca un usuario por su ID.
     * @param idUsuario El ID del usuario a buscar
     * @return El usuario encontrado, o null si no existe */

    public LiveData<Usuario> getUsuarioById(int idUsuario) {
        return usuarioDAO.getUsuarioById(idUsuario);
    }
}