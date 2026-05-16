package com.gemma.esterapp.dao;

import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;
import com.gemma.esterapp.model.Usuario;

// @Dao le dice a Room que esta interfaz es un DAO
// Room genera automáticamente el código SQL de cada método al compilar
@Dao
public interface UsuarioDAO {

    // Inserta un usuario nuevo en la tabla usuarios
    @Insert
    void insert(Usuario usuario);

    // Actualiza un usuario existente en la tabla usuarios
    @Update
    void update(Usuario usuario);

    // Borra un usuario de la tabla usuarios
    // RESTRICT: fallará si el usuario tiene gastos o ingresos asociados
    @Delete
    void delete(Usuario usuario);

    /* Devuelve todos los usuarios de la tabla sin ningun filtro
       LiveData: la pantalla se actualiza automáticamente si la lista cambia */
    @Query("SELECT * FROM usuarios")
    LiveData<List<Usuario>> getAllUsuarios();

    /* Busca en la tabla usuarios la fila que coincida con el usuario y la contraseña introducida
    * Devuelve el objeto Usuario conpleto si las credenciales son correctas, null si no existe*/
    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND contrasena = :contrasena")
    LiveData<Usuario> login(String usuario, String contrasena);

    /* Busca y devuelve el usuario que tenga ese id concreto, se usa para cargar los
    * datos del usuario en otras pantallas, el id es el valo que se pasa como parametro al llamar al metodo */
    @Query("SELECT * FROM usuarios WHERE id_usuario = :id")
    LiveData<Usuario> getUsuarioById(int id);
}