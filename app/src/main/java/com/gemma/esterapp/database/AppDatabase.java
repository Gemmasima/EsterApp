package com.gemma.esterapp.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
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

// @Database le dice a Room que esta clase es la base de datos principal de la app
// entities: declara todas las tablas que existen en la base de datos
// version = 1: version actual de la BD, se incrementa si se cambia la estructura
@Database(entities = {Usuario.class, Gasto.class, Ingreso.class, Categoria.class, Subcategoria.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase { // abstract porque Room genera la implementacion automaticamente

    // Nombre del archivo fisico que Room crea en el almacenamiento interno de la tablet
    private static final String DB_NAME = "esterapp_db";

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

    // getInstance() es el metodo que toda la app usa para obtener la base de datos
    // synchronized: garantiza que si dos hilos llaman a la vez, solo uno entra
    // Esto evita que se creen dos instancias distintas por accidente
    public static synchronized AppDatabase getInstance(Context context) {

        // Solo creamos la instancia si todavia no existe
        if (miBaseDeDatos == null) {

            // Room.databaseBuilder construye la base de datos con su configuracion
            miBaseDeDatos = Room.databaseBuilder(
                            context.getApplicationContext(), // contexto de la app, no de una pantalla
                            AppDatabase.class,              // clase que define la estructura de la BD
                            DB_NAME)                        // nombre del archivo fisico en la tablet

                    // Si la version cambia, Room borra y recrea la BD automaticamente
                    // Solo se usa en desarrollo, en produccion se usaria una migracion
                    .fallbackToDestructiveMigration()

                    // Registra DatosIniciales como callback
                    // DatosIniciales.onCreate() se ejecutara cuando la BD se cree por primera vez
                    .addCallback(new DatosIniciales())

                    // build() termina de configurar Room pero todavia NO crea el archivo fisico
                    .build();

            // SOLUCION AL PROBLEMA DE INICIALIZACION:
            // Room no crea fisicamente el archivo esterapp_db hasta que alguien
            // hace la primera consulta. Si no forzamos esto, DatosIniciales.onCreate()
            // no se ejecutaria hasta que el usuario pulsara Entrar en el login,
            // y la tabla usuarios estaria vacia en ese momento.
            // getWritableDatabase() fuerza la creacion inmediata del archivo
            // y dispara DatosIniciales.onCreate() con todas las inserciones
            // antes de que la pantalla de login este disponible para el usuario
            miBaseDeDatos.getOpenHelper().getWritableDatabase();
        }

        // Si la instancia ya existe, la devolvemos directamente sin crear nada nuevo
        return miBaseDeDatos;
    }
}