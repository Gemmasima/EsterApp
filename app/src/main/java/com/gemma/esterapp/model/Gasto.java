package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "gastos" en SQLite
// foreignKeys define las relaciones con otras tablas
// RESTRICT significa que no se puede borrar el padre si tiene hijos asociados
@Entity(tableName = "gastos",
        foreignKeys = {
                // Un gasto pertenece a un usuario — no se puede borrar un usuario con gastos
                @ForeignKey(
                        entity = Usuario.class,
                        parentColumns = "id_usuario",   // columna PK en la tabla usuarios
                        childColumns = "id_usuario",    // columna FK en la tabla gastos
                        onDelete = ForeignKey.RESTRICT),

                // Un gasto pertenece a una categoría — no se puede borrar una categoría con gastos
                @ForeignKey(
                        entity = Categoria.class,
                        parentColumns = "id_categoria",
                        childColumns = "id_categoria",
                        onDelete = ForeignKey.RESTRICT),

                // Un gasto pertenece a una subcategoría — no se puede borrar una subcategoría con gastos
                @ForeignKey(
                        entity = Subcategoria.class,
                        parentColumns = "id_subcategoria",
                        childColumns = "id_subcategoria",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Gasto {

    // Clave primaria, se genera automáticamente con cada nuevo gasto
    @PrimaryKey(autoGenerate = true)
    private int id_gasto;

    // Columnas de la tabla gastos
    private double importe;         // importe del gasto en euros
    private String fecha;           // formato "YYYY-MM-DD" — SQLite no tiene tipo DATE nativo
    private String notas;     // descripción opcional del gasto
    private int id_categoria;       // FK → tabla categorias
    private int id_subcategoria;    // FK → tabla subcategorias (0 si no aplica)
    private int id_usuario;         // FK → tabla usuarios (quién registró el gasto)

    // GETTERS Y SETTERS
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