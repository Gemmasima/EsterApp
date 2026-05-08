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

    // Devuelve todos los usuarios de la tabla
    // LiveData: la pantalla se actualiza automáticamente si la lista cambia
    @Query("SELECT * FROM usuarios")
    LiveData<List<Usuario>> getAllUsuarios();

    // Valida el login buscando usuario + contraseña en la BD
    // :usuario y :contrasena son parámetros que Room conecta con los de Java
    // Devuelve el Usuario si las credenciales son correctas, null si no existe
    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND contrasena = :contrasena")
    LiveData<Usuario> login(String usuario, String contrasena);

    // Busca un usuario por su id — usado para cargar datos de perfil
    @Query("SELECT * FROM usuarios WHERE id_usuario = :id")
    LiveData<Usuario> getUsuarioById(int id);
}