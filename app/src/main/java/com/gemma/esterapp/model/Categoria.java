package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "categorias" en SQLite
// Esta tabla no tiene FK porque es independiente. Categoria no necesita saber de gasto
@Entity(tableName = "categorias")
public class Categoria {

    // Clave primaria, se genera automáticamente
    @PrimaryKey(autoGenerate = true)//instrucción para Room
    private int id_categoria; // id_gasto INTEGER PRIMARY KEY AUTOINCREMENT,


    // Nombre de la categoría (ej: "Proveedores de materiales", "Impuestos", "Salarios"...)
    private String nombre;

    // GETTERS Y SETTERS
    public int getId_categoria()
        { return id_categoria; }
    public void setId_categoria(int id_categoria)
    { this.id_categoria = id_categoria; }

    public String getNombre()
    { return nombre; }
    public void setNombre(String nombre)
    { this.nombre = nombre; }
}
