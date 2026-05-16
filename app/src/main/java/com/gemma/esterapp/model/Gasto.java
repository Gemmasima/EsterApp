package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


/* GASTO - Clase que representa la tabla gastos en la DB.
*   Cada gasto registrado en la app es una fila de esa tabla, los FK definene las relaciones
*   con otras tablas y garantizan que no queden gastos huerfanos, es decir, gastos que no apunten
*   a ningun usuario, categoria o subcategoria. */

// @Entity le dice a Room que tiene que crear una tabla llamada gastos
// RESTRICT significa que no se puede borrar el padre si tiene hijos asociados
@Entity(tableName = "gastos",
        foreignKeys = {
                @ForeignKey(
                        entity = Usuario.class,
                        parentColumns = "id_usuario",   // columna PK en la tabla usuarios
                        childColumns = "id_usuario",    // columna FK en la tabla gastos
                        onDelete = ForeignKey.RESTRICT), // impide borrar un usuario si tiene gastos asociados

                @ForeignKey(
                        entity = Categoria.class,
                        parentColumns = "id_categoria",
                        childColumns = "id_categoria",
                        onDelete = ForeignKey.RESTRICT),

                @ForeignKey(
                        entity = Subcategoria.class,
                        parentColumns = "id_subcategoria",
                        childColumns = "id_subcategoria",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Gasto {

    // Primary Key, Room lo asigna automatic. con cada nuevo registro
    @PrimaryKey(autoGenerate = true)
    private int id_gasto;

    // Columnas de la tabla gastos - cada atributo es una columna
    private double importe;         // importe del gasto en euros
    private String fecha;           // formato "YYYY-MM-DD" — SQLite no tiene tipo DATE nativo
    private String notas;           // descripción opcional del gasto
    private int id_categoria;       // FK → tabla categorias
    private int id_subcategoria;    // FK → tabla subcategorias (0 si no aplica)
    private int id_usuario;         // FK → tabla usuarios (quién registró el gasto)

    /* GETTERS Y SETTERS
    * Room necesita los getters para leer los datos de la BD y los setters
    * para escribirlos */
    public int getId_gasto() { return id_gasto; }
    public void setId_gasto(int id_gasto) { this.id_gasto = id_gasto; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id_categoria) { this.id_categoria = id_categoria; }

    public int getId_subcategoria() { return id_subcategoria; }
    public void setId_subcategoria(int id_subcategoria) { this.id_subcategoria = id_subcategoria; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }
}