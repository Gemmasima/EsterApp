package com.gemma.esterapp.dao;

import java.util.List;
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

    @Query("SELECT * FROM usuarios") // todos los usuarios de la tabla usuarios
    List<Usuario> getAllUsuarios(); //lista

    /* Busca un usuario por su nombre de usuario y contraseña
     * Se usa para validar el login :usuario y :contrasena son los parámetros que se pasan al metodo*/
    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND contrasena = :contraseña")
    Usuario login(String usuario, String contraseña);

    // Busca un usuario por su id y devuelve un unico usuario, no una lista!!
    @Query("SELECT * FROM usuarios WHERE id_usuario = :id")
    Usuario getUsuarioById(int id);
}