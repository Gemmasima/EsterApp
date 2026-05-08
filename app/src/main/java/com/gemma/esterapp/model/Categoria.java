package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "categorias" en SQLite
// No tiene FK porque es una tabla independiente — las otras tablas apuntan a ella,
// pero ella no depende de ninguna otra
@Entity(tableName = "categorias")
public class Categoria {

    // Clave primaria, se genera automáticamente con cada nueva categoría
    @PrimaryKey(autoGenerate = true)
    private int id_categoria;

    // Nombre de la categoría (ej: "Proveedores de materiales", "Impuestos", "Salarios")
    private String nombre;

    // GETTERS Y SETTERS
    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id_categoria) { this.id_categoria = id_categoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}