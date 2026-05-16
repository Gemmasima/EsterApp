package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/* SUBCATEGORIA - Clase que representa la tabla subcategorias en la DB.
 * Cada subcategoria es una fila de esa tabla y pertenece siempre a una categoria.
 * Solo las categorias Articulos, Servicios y Vehiculos tienen subcategorias.
 * Impuestos, Salarios y Otros no tienen subcategorias. */

// @Entity le dice a Room que tiene que crear una tabla llamada subcategorias
// RESTRICT significa que no se puede borrar el padre si tiene hijos asociados
@Entity(tableName = "subcategorias",
        foreignKeys = {
                @ForeignKey(
                        entity = Categoria.class,
                        parentColumns = "id_categoria",  // columna PK en tabla categorias
                        childColumns = "id_categoria",   // columna FK en tabla subcategorias
                        onDelete = ForeignKey.RESTRICT)
        })
public class Subcategoria {

    // Primary Key, Room lo asigna automatic. con cada nueva subcategoria
    @PrimaryKey(autoGenerate = true)
    private int id_subcategoria;

    // Columnas de la tabla subcategorias - cada atributo es una columna
    private String nombre; // nombre de la subcategoria (ej: "Electrodomesticos", "Luz", "Gasolina")
    private int id_categoria; // FK → tabla categorias: indica a que categoria pertenece esta subcategoria

    /* GETTERS Y SETTERS
     * Room necesita los getters para leer los datos de la BD y los setters
     * para escribirlos */
    public int getId_subcategoria() { return id_subcategoria; }
    public void setId_subcategoria(int id_subcategoria) { this.id_subcategoria = id_subcategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id_categoria) { this.id_categoria = id_categoria; }
}