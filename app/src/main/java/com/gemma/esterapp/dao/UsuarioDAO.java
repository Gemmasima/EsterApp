package com.gemma.esterapp.dao;

import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao; // Importaciones @Dao para indicar a Room que esta interfaz es un DAO
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query; // para consultas SQL

import com.gemma.esterapp.model.Usuario; // Importacion de la entidad Usuario del paquete model

@Dao //le dice a Room que esta interfaz es un DAO y así Room generará automáticamente el código SQL de cada metodo
public interface UsuarioDAO {
    @Insert  // Inserta un usuario nuevo en la tabla usuarios
    void insert(Usuario usuario);

    @Update // Actualiza un usuario existente en la tabla usuarios
    void update(Usuario usuario);

    @Delete // Borra un usuario de la tabla usuarios
    void delete(Usuario usuario);

    @Query("SELECT * FROM usuarios") // Devuelve todos los usuarios. LiveData avisa a la pantalla si la lista cambia
    LiveData<List<Usuario>> getAllUsuarios();

    /* Busca un usuario por nombre de usuario y contraseña para validar el login
     * Devuelve el Usuario si las credenciales son correctas, o null si no existe
     * :usuario y :contrasena son los parámetros que Room conecta con los de Java (sin tilde para evitar errores)*/
    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND contrasena = :contrasena")
    LiveData<Usuario> login(String usuario, String contrasena);

    // Busca un usuario por su id. Devuelve un único Usuario o null si no existe. LiveData avisa si cambia
    @Query("SELECT * FROM usuarios WHERE id_usuario = :id")
    LiveData<Usuario> getUsuarioById(int id);
}