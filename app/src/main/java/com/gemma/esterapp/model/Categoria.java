package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/* CATEGORIA
 * Clase que representa la tabla categorias en la DB.
 * Cada categoria es una fila de esa tabla. No tiene FK porque es una tabla independiente,
 * son las otras tablas las que apuntan a ella. */

// @Entity le dice a Room que tiene que crear una tabla llamada categorias
@Entity(tableName = "categorias")
public class Categoria {

    // Primary Key, Room lo asigna automatic. con cada nueva categoria
    @PrimaryKey(autoGenerate = true)
    private int id_categoria;

    // Columnas de la tabla categorias - cada atributo es una columna
    private String nombre; // nombre de la categoria (prov. de materiales, ...)

    /* GETTERS Y SETTERS
     * Room necesita los getters para leer los datos de la BD y los setters
     * para escribirlos */
    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id_categoria) { this.id_categoria = id_categoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}