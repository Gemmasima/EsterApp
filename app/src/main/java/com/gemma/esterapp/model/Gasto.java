package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "gastos" en SQLite
// FK define las relaciones con otras tablas
@Entity(tableName = "gastos",
        foreignKeys = { // le dice que esta tabla tiene relacion con otras tablas
                // Un gasto pertenece a un usuario
                @ForeignKey(
                        entity = Usuario.class,         // ¿A Qué tabla apunta?
                        parentColumns = "id_usuario",   // ¿Qué columna de ESA tabla?
                        childColumns = "id_usuario",    // ¿Qué columna de ESTA tabla?
                        onDelete = ForeignKey.RESTRICT), // No deja borrar un usuario si tiene gastos asociados

                // Un gasto pertenece a una categoría
                @ForeignKey(entity = Categoria.class,
                        parentColumns = "id_categoria",
                        childColumns = "id_categoria",
                        onDelete = ForeignKey.RESTRICT),// No deja borrar un usuario si tiene gastos asociados

                // Un gasto pertenece a una subcategoría
                @ForeignKey(entity = Subcategoria.class,
                        parentColumns = "id_subcategoria",
                        childColumns = "id_subcategoria",
                        onDelete = ForeignKey.RESTRICT)// No deja borrar un usuario si tiene gastos asociados
        })
public class Gasto {

    // Clave primaria, se genera automáticamente
    @PrimaryKey(autoGenerate = true) //instrucción para Room
    private int id_gasto;            // id_gasto INTEGER PRIMARY KEY AUTOINCREMENT,

    // Columnas de la tabla gastos
    private double importe;
    private String fecha;       // SQLite no tiene date nativo
    private String descripcion;
    private int id_categoria;
    private int id_subcategoria;
    private int id_usuario;

    // GETTERS Y SETTERS
    public int getId_gasto() //llave lee
    { return id_gasto; }
    public void setId_gasto(int id_gasto) //llave asigna
    { this.id_gasto = id_gasto; }

    public double getImporte()
    { return importe; }
    public void setImporte(double importe)
    { this.importe = importe; }

    public String getFecha()
    { return fecha; }
    public void setFecha(String fecha)
    { this.fecha = fecha; }

    public String getDescripcion()
    { return descripcion; }
    public void setDescripcion(String descripcion)
    { this.descripcion = descripcion; }

    public int getId_categoria()
    { return id_categoria; }
    public void setId_categoria(int id_categoria)
    { this.id_categoria = id_categoria; }

    public int getId_subcategoria()
    { return id_subcategoria; }
    public void setId_subcategoria(int id_subcategoria)
    { this.id_subcategoria = id_subcategoria; }

    public int getId_usuario()
    { return id_usuario; }
    public void setId_usuario(int id_usuario)
    { this.id_usuario = id_usuario; }
}
