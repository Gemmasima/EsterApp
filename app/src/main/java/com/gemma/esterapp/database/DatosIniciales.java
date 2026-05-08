package com.gemma.esterapp.database;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// DatosIniciales extiende RoomDatabase.Callback para interceptar el momento
// en que Room crea la base de datos por primera vez
// Solo se ejecuta UNA vez: cuando el archivo esterapp_db no existe todavia
public class DatosIniciales extends RoomDatabase.Callback {

    // onCreate() es llamado automaticamente por Room cuando crea la BD por primera vez
    // db: conexion directa a SQLite que Room nos pasa para poder ejecutar SQL
    // @NonNull garantiza que db nunca sera null cuando Room llame a este metodo
    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db); // llamamos al metodo padre antes de hacer nada

        // Room llama a onCreate() en un hilo secundario automaticamente
        // por eso NO necesitamos ExecutorService aqui, a diferencia de los repositorios
        // db.execSQL() ejecuta sentencias SQL directamente sobre la base de datos

        // ---- USUARIOS ----
        // Insertamos los 3 usuarios iniciales del negocio con sus roles
        // El orden de los campos debe coincidir exactamente con la tabla usuarios
        db.execSQL("INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES ('Giuseppe', 'giuseppe', 'admin123', 'Administrador')"); // propietario, acceso total
        db.execSQL("INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES ('Ester', 'ester', 'ester123', 'Gerente')");             // gerente, puede registrar y modificar sus movimientos
        db.execSQL("INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES ('Francesca', 'francesca', 'francesca123', 'Trabajador')"); // trabajadora, puede registrar y modificar sus movimientos

        // ---- CATEGORIAS ----
        // Insertamos las 5 categorias de gastos del negocio
        // Room asigna automaticamente el id (autoGenerate), empezando por 1
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Proveedores de materiales')"); // id=1 — productos que vende el negocio
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Proveedores de servicios')");  // id=2 — suministros del local
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Impuestos')");                 // id=3 — sin subcategorias
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Salarios')");                  // id=4 — sin subcategorias
        db.execSQL("INSERT INTO categorias (nombre) VALUES ('Vehiculos')");                 // id=5 — gastos de los vehiculos del negocio

        // ---- SUBCATEGORIAS de Proveedores de materiales (id_categoria = 1) ----
        // Tipos de productos que vende el negocio
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Electrodomesticos', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Hogar', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Cocina', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Juguetes', 1)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('BallonArt', 1)");

        // ---- SUBCATEGORIAS de Proveedores de servicios (id_categoria = 2) ----
        // Suministros y servicios del local
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Agua', 2)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Luz', 2)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Gas', 2)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Conectividad', 2)"); // moviles e internet
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Basuras', 2)");

        // ---- SUBCATEGORIAS de Vehiculos (id_categoria = 5) ----
        // Impuestos (id=3) y Salarios (id=4) no tienen subcategorias
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Gasolina', 5)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Seguros', 5)");
        db.execSQL("INSERT INTO subcategorias (nombre, id_categoria) VALUES ('Taller', 5)");
    }
}