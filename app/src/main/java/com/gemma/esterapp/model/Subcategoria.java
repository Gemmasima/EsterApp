package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "subcategorias" en SQLite
@Entity(tableName = "subcategorias",
        foreignKeys = { // Subcategoria depende de Categoria
                @ForeignKey(entity = Categoria.class,
                        parentColumns = "id_categoria",
                        childColumns = "id_categoria",
                        onDelete = ForeignKey.RESTRICT) //no se puede borrar una categoria con subcategoria asociada
        })
public class Subcategoria {

    // Clave primaria, se genera automáticamente
    @PrimaryKey(autoGenerate = true)//instrucción para Room
    private int id_subcategoria;    // id_gasto INTEGER PRIMARY KEY AUTOINCREMENT,

    // Nombre de la subcategoria (ej: "Electrodomésticos", "Luz", "Gas"...)
    private String nombre;

    // FK: referencia a la categoria a la que pertenece esta subcategoria
    private int id_categoria;  // Subcategoria sí apunta a Categoria a través de id_categoria


    // GETTERS Y SETTERS
    public int getId_subcategoria()
    { return id_subcategoria; }
    public void setId_subcategoria(int id_subcategoria)
    { this.id_subcategoria = id_subcategoria; }

    public String getNombre()
    { return nombre; }
    public void setNombre(String nombre)
    { this.nombre = nombre; }

    public int getId_categoria()
    { return id_categoria; }
    public void setId_categoria(int id_categoria)
    { this.id_categoria = id_categoria; }
}