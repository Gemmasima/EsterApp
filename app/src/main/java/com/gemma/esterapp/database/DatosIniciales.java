package com.gemma.esterapp.database;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gemma.esterapp.model.Categoria;
import com.gemma.esterapp.model.Subcategoria;
import com.gemma.esterapp.model.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DATOSINICIALES
 * Esta clase se ejecuta automaticamente la primera vez que se crea la base de datos.
 * Inserta los usuarios, categorias y subcategorias necesarios para que la app funcione.
 * Sin estos datos no se puede hacer login ni registrar gastos.
 * Se conecta a AppDatabase a traves del metodo addCallback() en el databaseBuilder. */

public class DatosIniciales extends RoomDatabase.Callback {

    /**
     * onCreate se ejecuta automaticamente cuando Room crea la BD por primera vez.
     * Solo se ejecuta UNA vez: cuando el archivo esterapp_db no existe todavia.
     * Las siguientes veces que se abre la app este metodo NO se ejecuta.
     */
    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);

        // Obtenemos la instancia de la BD para poder usar los DAOs
        AppDatabase database = AppDatabase.miBaseDeDatos;

        // Usamos un hilo secundario porque no podemos hacer operaciones
        // de BD en el hilo principal
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            // ─────────────────────────────────────────────
            // INSERTAR USUARIOS
            // ─────────────────────────────────────────────

            // Administrador: Giuseppe — acceso total a toda la app
            Usuario giuseppe = new Usuario();
            giuseppe.setNombre("Giuseppe");
            giuseppe.setUsuario("giuseppe");
            giuseppe.setContrasena("admin123");
            giuseppe.setRol("Administrador");
            database.usuarioDAO().insert(giuseppe);

            // Gerente: Ester — duena del negocio
            Usuario ester = new Usuario();
            ester.setNombre("Ester");
            ester.setUsuario("ester");
            ester.setContrasena("ester123");
            ester.setRol("Gerente");
            database.usuarioDAO().insert(ester);

            // Trabajador: Francesca — empleada
            Usuario francesca = new Usuario();
            francesca.setNombre("Francesca");
            francesca.setUsuario("francesca");
            francesca.setContrasena("francesca123");
            francesca.setRol("Trabajador");
            database.usuarioDAO().insert(francesca);

            // ─────────────────────────────────────────────
            // INSERTAR CATEGORIAS
            // ─────────────────────────────────────────────

            Categoria materiales = new Categoria();
            materiales.setNombre("Proveedores de materiales");
            database.categoriaDAO().insert(materiales);

            Categoria servicios = new Categoria();
            servicios.setNombre("Proveedores de servicios");
            database.categoriaDAO().insert(servicios);

            Categoria impuestos = new Categoria();
            impuestos.setNombre("Impuestos");
            database.categoriaDAO().insert(impuestos);

            Categoria salarios = new Categoria();
            salarios.setNombre("Salarios");
            database.categoriaDAO().insert(salarios);

            Categoria vehiculos = new Categoria();
            vehiculos.setNombre("Vehiculos");
            database.categoriaDAO().insert(vehiculos);

            // ─────────────────────────────────────────────
            // INSERTAR SUBCATEGORIAS
            // ─────────────────────────────────────────────

            // Para las subcategorias necesitamos el id de la categoria
            // Room asigna los ids en orden de insercion: materiales=1, servicios=2...

            // Subcategorias de Proveedores de materiales (id_categoria = 1)
            String[] subcatMateriales = {"Electrodomesticos", "Hogar", "Cocina", "Juguetes", "BallonArt"};
            for (String nombre : subcatMateriales) {
                Subcategoria s = new Subcategoria();
                s.setNombre(nombre);
                s.setId_categoria(1);
                database.subcategoriaDAO().insert(s);
            }

            // Subcategorias de Proveedores de servicios (id_categoria = 2)
            String[] subcatServicios = {"Agua", "Luz", "Gas", "Conectividad", "Basuras"};
            for (String nombre : subcatServicios) {
                Subcategoria s = new Subcategoria();
                s.setNombre(nombre);
                s.setId_categoria(2);
                database.subcategoriaDAO().insert(s);
            }

            // Subcategorias de Vehiculos (id_categoria = 5)
            // Impuestos y Salarios no tienen subcategorias
            String[] subcatVehiculos = {"Gasolina", "Seguros", "Taller"};
            for (String nombre : subcatVehiculos) {
                Subcategoria s = new Subcategoria();
                s.setNombre(nombre);
                s.setId_categoria(5);
                database.subcategoriaDAO().insert(s);
            }
        });
    }
}
