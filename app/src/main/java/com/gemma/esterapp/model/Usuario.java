package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity le dice a Room que esta clase es la tabla "usuarios" en SQLite
@Entity(tableName = "usuarios")
public class Usuario {

    // Clave primaria, se genera automáticamente con cada nuevo usuario
    @PrimaryKey(autoGenerate = true)
    private int id_usuario;

    // Columnas de la tabla usuarios
    private String nombre;      // nombre real (ej: "Giuseppe")
    private String usuario;     // nombre de login (ej: "giuseppe")
    private String contrasena;  // contraseña de acceso
    private String rol;         // "Administrador", "Gerente" o "Trabajador"

    // GETTERS Y SETTERS
    // Permiten leer y modificar los atributos desde fuera de la clase
    // Room los necesita para construir y leer objetos de la tabla

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