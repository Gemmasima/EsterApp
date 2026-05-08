package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "subcategorias" en SQLite
// Depende de Categoria — no se puede borrar una categoría si tiene subcategorías asociadas
@Entity(tableName = "subcategorias",
        foreignKeys = {
                @ForeignKey(
                        entity = Categoria.class,
                        parentColumns = "id_categoria",  // columna PK en tabla categorias
                        childColumns = "id_categoria",   // columna FK en tabla subcategorias
                        onDelete = ForeignKey.RESTRICT)
        })
public class Subcategoria {

    // Clave primaria, se genera automáticamente con cada nueva subcategoría
    @PrimaryKey(autoGenerate = true)
    private int id_subcategoria;

    // Nombre de la subcategoría (ej: "Electrodomésticos", "Luz", "Gasolina")
    private String nombre;

    // FK → tabla categorias: indica a qué categoría pertenece esta subcategoría
    private int id_categoria;

    // GETTERS Y SETTERS
    public int getId_subcategoria() { return id_subcategoria; }
    public void setId_subcategoria(int id_subcategoria) { this.id_subcategoria = id_subcategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id_categoria) { this.id_categoria = id_categoria; }
}