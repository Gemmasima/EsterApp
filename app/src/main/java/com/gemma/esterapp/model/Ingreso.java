package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "ingresos" en SQLite
@Entity(tableName = "ingresos",
        foreignKeys = {
                // Un ingreso pertenece a un usuario
                @ForeignKey(entity = Usuario.class,
                        parentColumns = "id_usuario",
                        childColumns = "id_usuario",
                        onDelete = ForeignKey.RESTRICT) //no se puede borrar un usuario con ingresos asociados
        })
public class Ingreso {

    // Clave primaria, se genera automáticamente
    @PrimaryKey(autoGenerate = true) //instrucción para Room
    private int id_ingreso; // id_gasto INTEGER PRIMARY KEY AUTOINCREMENT,

    // Columnas de la tabla ingresos
    private double importe;
    private String fecha; // SQLite no tiene date nativo
    private String tipoingreso;
    private String notas;
    private int id_usuario;

    // GETTERS Y SETTERS
    public int getId_ingreso()
    { return id_ingreso; }
    public void setId_ingreso(int id_ingreso)
    { this.id_ingreso = id_ingreso; }

    public String getFecha()
    { return fecha; }
    public void setFecha(String fecha)
    { this.fecha = fecha; }

    public double getImporte()
    { return importe; }
    public void setImporte(double importe)
    { this.importe = importe; }

    public String getTipoingreso()
    { return tipoingreso; }
    public void setTipoingreso(String tipo)
    { this.tipoingreso = tipo; }

    public String getNotas()
    { return notas; }
    public void setNotas(String notas)
    { this.notas = notas; }

    public int getId_usuario()
    { return id_usuario; }
    public void setId_usuario(int id_usuario)
    { this.id_usuario = id_usuario; }
}
