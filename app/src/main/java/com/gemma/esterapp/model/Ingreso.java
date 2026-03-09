package com.gemma.esterapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// @Entity indica que esta clase es la tabla "ingresos" en SQLite
@Entity(tableName = "ingresos",
        foreignKeys = {
                // Un ingreso pertenece a un usuario, si se borra el usuario sus ingresos se borran también
                @ForeignKey(entity = Usuario.class,
                        parentColumns = "id_usuario",
                        childColumns = "id_usuario",
                        onDelete = ForeignKey.CASCADE)
        })
public class Ingreso {

    // Clave primaria, se genera automáticamente
    @PrimaryKey(autoGenerate = true)
    private int id_ingreso;

    // Columnas de la tabla ingresos
    private String fecha;
    private double importe;
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
