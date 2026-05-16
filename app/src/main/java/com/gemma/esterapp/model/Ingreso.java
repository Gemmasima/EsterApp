package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/* INGRESO
 * Clase que representa la tabla ingresos en la DB.
 * Cada ingreso registrado en la app es una fila de esa tabla. A diferencia de Gasto,
 * solo tiene una FK con la tabla usuarios ya que los ingresos no tienen categorias ni subcategorias. */
@Entity(tableName = "ingresos",
        foreignKeys = {
                // Un ingreso pertenece a un usuario — no se puede borrar un usuario con ingresos
                @ForeignKey(
                        entity = Usuario.class,
                        parentColumns = "id_usuario",   // columna PK en la tabla usuarios
                        childColumns = "id_usuario",    // columna FK en la tabla ingresos
                        onDelete = ForeignKey.RESTRICT)
        })
public class Ingreso {

    // Primary Key, Room lo asigna automatic. con cada nuevo registro
    @PrimaryKey(autoGenerate = true)
    private int id_ingreso;

    // Columnas de la tabla ingresos - cada atributo es una columna
    private double importe;         // importe del ingreso en euros
    private String fecha;           // formato "YYYY-MM-DD" — SQLite no tiene tipo DATE nativo
    private String tipoingreso;     // "Efectivo" o "Tarjeta" — sin espacio, nombre exacto del campo
    private String notas;           // notas opcionales
    private int id_usuario;         // FK → tabla usuarios (quién registró el ingreso)

    /* GETTERS Y SETTERS
     * Room necesita los getters para leer los datos de la BD y los setters
     * para escribirlos */
    public int getId_ingreso() { return id_ingreso; }
    public void setId_ingreso(int id_ingreso) { this.id_ingreso = id_ingreso; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getTipoingreso() { return tipoingreso; }
    public void setTipoingreso(String tipoingreso) { this.tipoingreso = tipoingreso; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }
}