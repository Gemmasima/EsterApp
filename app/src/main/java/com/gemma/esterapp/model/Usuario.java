package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/* @Entity le dice a room que esta clase java es una tabla con SQLite
y que se llamará usuarios */
@Entity(tableName = "usuarios")
public class Usuario {

    /* el id_usuario es PK de la tabla y se genera automaticamente (true),
    cada vez que se añada un usuario nuevo. */
    @PrimaryKey(autoGenerate = true)
    private int id_usuario;

    //Atributos, columnas de la tabla
    private String nombre;
    private String usuario;
    private String contrasena;
    private String rol;

    /* Getters y Setters
    metodos para leer y modificar los atributos desde fuera de la clase
    getNombre, devuelve el nombre del usuario
    setNombre, lo modifica */
    public int getId_usuario()
    { return id_usuario; }
    public void setId_usuario(int id_usuario)
    { this.id_usuario = id_usuario; }

    public String getNombre()
    { return nombre; }
    public void setNombre(String nombre)
    { this.nombre = nombre; }

    public String getUsuario()
    { return usuario; }
    public void setUsuario(String usuario)
    { this.usuario = usuario; }

    public String getContrasena()
    { return contrasena; }
    public void setContrasena(String contrasena)
    { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}