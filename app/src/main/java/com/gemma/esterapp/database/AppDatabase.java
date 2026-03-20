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

// @Database le dice a Room que esta es la clase principal de la base de datos
// entities: lista de todas las tablas que tiene la base de datos
// version: version de la base de datos, empieza en 1
@Database(entities = {Usuario.class, Gasto.class, Ingreso.class, Categoria.class, Subcategoria.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Nombre del archivo de la base de datos en la tablet
    private static final String DB_NAME = "esterapp_db";

    // Instancia unica de la base de datos (patron Singleton)
    // Singleton = una sola instancia para toda la app, compartida por todos.
    // Solo puede existir una conexion a la base de datos a la vez.
    // Sin modificador private para que DatosIniciales (mismo paquete database)
    // pueda acceder a ella para insertar los datos iniciales
    static AppDatabase miBaseDeDatos;

    // Metodos abstractos que devuelven cada DAO
    // Room los implementa automaticamente
    public abstract UsuarioDAO usuarioDAO();
    public abstract GastoDAO gastoDAO();
    public abstract IngresoDAO ingresoDAO();
    public abstract CategoriaDAO categoriaDAO();
    public abstract SubcategoriaDAO subcategoriaDAO();

    // Metodo para obtener la instancia unica de la base de datos
    // synchronized significa que solo un hilo puede acceder a la vez
    public static synchronized AppDatabase getInstance(Context context) {
        if (miBaseDeDatos == null) {
            // Si no existe la instancia, la creamos
            miBaseDeDatos = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME)
                    // Si cambia la version, recrea la BD (solo en desarrollo)
                    .fallbackToDestructiveMigration()
                    // Conecta DatosIniciales para insertar datos la primera vez
                    .addCallback(new DatosIniciales())
                    .build();

            // IMPORTANTE: Room no crea fisicamente la BD hasta que alguien
            // hace la primera consulta. Esta linea fuerza la creacion inmediata
            // del archivo esterapp_db y ejecuta DatosIniciales.onCreate()
            // antes de que el usuario intente hacer login
            miBaseDeDatos.getOpenHelper().getWritableDatabase();
        }
        // Devuelve la instancia existente
        return miBaseDeDatos;
    }
}