package com.gemma.esterapp.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import com.gemma.esterapp.dao.CategoriaDAO;
import com.gemma.esterapp.dao.GastoDAO;
import com.gemma.esterapp.dao.IngresoDAO;
import com.gemma.esterapp.dao.SubcategoriaDAO;
import com.gemma.esterapp.dao.UsuarioDAO;
import com.gemma.esterapp.model.Categoria;
import com.gemma.esterapp.model.Gasto;
import com.gemma.esterapp.model.Ingreso;
import com.gemma.esterapp.model.Subcategoria;
import com.gemma.esterapp.model.Usuario;

// @Database le dice a Room que esta clase es la BASE DE DATOS principal de la app
// entities: declara las 5 tablas que existen en la base de datos
//Version 3: actualizada para añadir la categoria Otros sin borrar datos existentes
@Database(entities = {Usuario.class, Gasto.class, Ingreso.class, Categoria.class, Subcategoria.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase { // abstract porque Room genera la implementacion automaticamente

    // Nombre del archivo fisico que Room crea, despues de getInstance, en el interno de la tablet.
    private static final String DB_NAME = "esterapp_db"; //constante

    // Variable que guarda la unica instancia de la base de datos
    // static: pertenece a la clase, no a un objeto concreto
    // Sin private para que DatosIniciales (mismo paquete) pueda acceder a ella
    static AppDatabase miBaseDeDatos;

    // Cada metodo abstracto devuelve un DAO
    // Room genera automaticamente el codigo de cada uno al compilar
    public abstract UsuarioDAO usuarioDAO();       // operaciones sobre la tabla usuarios
    public abstract GastoDAO gastoDAO();           // operaciones sobre la tabla gastos
    public abstract IngresoDAO ingresoDAO();       // operaciones sobre la tabla ingresos
    public abstract CategoriaDAO categoriaDAO();   // operaciones sobre la tabla categorias
    public abstract SubcategoriaDAO subcategoriaDAO(); // operaciones sobre la tabla subcategorias

    /* MIGRATION DE V.2 A V.3 de la BD
    * Añadida la categoria Otros con id=6 en la tabla de categorias sin borrar los datos existentes
    * a diferencia del anterior metodo fallbackToDestructiveMigration(), está migración mantiene
    * todos los datos (gasto, ingresos, usuarios*/

    static final Migration MIGRATION_2a3 =new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //inserta la categoria Otros que faltaba en los datos iniciales.
            database.execSQL("INSERT INTO categorias (nombre) VALUES ('Otros')");
        }
    };

    /* El metodo getInstance() garantiza que solo se cree una conexión a la base de datos. (abrir el archivo.db)
    para consultas o modificaciones. Cuando una pantalla necesita acceder a datos llama a este metodo, comprueba
    si ya hay una conex. abierta de SQLite; si no existe la crea, si ya existe la devuelve. De esta forma todas
    las pantallas comparten siempre la misma conex. el llamado PATRON SINGLETON. */

    /* synchronized garantiza que aunque haya varios hilos (pantallas) intentando acceder a este metodo a la vez,
    solo uno pueda ejecutarse, evitando que se creen dos conexiones simultáneas por error. */
    public static synchronized AppDatabase getInstance(Context context) {

        // Solo creamos la instancia si todavia no existe
        if (miBaseDeDatos == null) {

            // Room.databaseBuilder construye la base de datos con su configuracion
            miBaseDeDatos = Room.databaseBuilder(
                            context.getApplicationContext(), // contexto de la app, no de una pantalla
                            AppDatabase.class,              // clase que define la estructura de la BD
                            DB_NAME)                        // nombre del archivo fisico en la tablet

                    // Si la version cambia, Room borra y recrea la BD automaticamente
                    .fallbackToDestructiveMigration()

                    /* Registra DatosIniciales
                       DatosIniciales.onCreate() se ejecutara cuando la BD se cree por primera vez */
                    .addCallback(new DatosIniciales())

                    // build() termina de configurar Room pero todavia NO crea el archivo fisico
                    .build();

            /* ---SOLUCIÓN AL PROBLEMA DE INICIALIZACIÓN---
            Por defecto Room no crea fisicamente el archivo.db hasta que se hace la primera consulta, esto significa que
            DatosIniciales.onCreate() no se ejecuta y la BD está completamente vacia.
            El problema era que incialmente al poner el usuario+password correcto daba error, pq la tabla usuarios estaba vacía
            y por lo tanto era no se realizaba la 1a consulta, es un bucle sin fin.
            Con getWritableDatabase() se fuerza a Room a crear el archivo .db y seguidamente se ejecuta DatosIniciales
            insertando todos los datos iniciales antes de que aparezca la pantalla login. */
            miBaseDeDatos.getOpenHelper().getWritableDatabase();
        }

        // Si la instancia ya existe, la devolvemos directamente sin crear nada nuevo
        return miBaseDeDatos;
    }
}