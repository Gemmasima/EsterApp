package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/* USUARIO - Clase que representa la tabla usuarios en la DB.
 * Cada usuario registrado en la app es una fila de esa tabla. No tiene FK porque es una
 * tabla independiente, son las otras tablas las que apuntan a ella, no al reves. */

// @Entity le dice a Room que tiene que crear una tabla llamada usuarios
@Entity(tableName = "usuarios")
public class Usuario {

    // Primary Key, Room lo asigna automatic. con cada nuevo usuario
    @PrimaryKey(autoGenerate = true)
    private int id_usuario;

    // Columnas de la tabla usuarios - cada atributo es una columna
    private String nombre;      // nombre real (ej: "Giuseppe")
    private String usuario;     // nombre de login (ej: "giuseppe")
    private String contrasena;  // contraseña de acceso
    private String rol;         // "Administrador", "Gerente" o "Trabajador"

    /* GETTERS Y SETTERS
     * Room necesita los getters para leer los datos de la BD y los setters
     * para escribirlos */

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}