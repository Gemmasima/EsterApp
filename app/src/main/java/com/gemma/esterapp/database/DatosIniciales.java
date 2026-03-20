package com.gemma.esterapp.database;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatosIniciales extends RoomDatabase.Callback {

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);

        // Room ya llama a onCreate() en un hilo secundario
        // No necesitamos ExecutorService aqui

        // USUARIOS
        db.execSQL("INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES ('Giuseppe', 'giuseppe', 'admin123', 'Administrador')");
        db.execSQL("INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES ('Ester', 'ester', 'ester123', 'Gerente')");
        db.execSQL("INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES ('Francesca', 'francesca', 'francesca123', 'Trabajador')");

        // CATEGORIAS
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Proveedores de materiales')");
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Proveedores de servicios')");
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Impuestos')");
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Salarios')");
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Vehiculos')");

        // SUBCATEGORIAS materiales (id=1)
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Electrodomesticos', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Hogar', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Cocina', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Juguetes', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('BallonArt', 1)");

        // SUBCATEGORIAS servicios (id=2)
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Agua', 2)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Luz', 2)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Gas', 2)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Conectividad', 2)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Basuras', 2)");

        // SUBCATEGORIAS vehiculos (id=5)
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Gasolina', 5)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Seguros', 5)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Taller', 5)");


    }
}