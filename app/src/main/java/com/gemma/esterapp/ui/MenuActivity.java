package com.gemma.esterapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Usuario;
import com.gemma.esterapp.repository.UsuarioRepository;

/**
 * MENUACTIVITY
 * Pantalla principal después del login. Hub de navegación de la app.
 * Muestra el nombre del usuario que ha iniciado sesión.
 * Controla la visibilidad del botón Gestión de Usuarios según el rol:
 * - Administrador → ve todos los botones incluido Gestión de Usuarios
 * - Gerente y Trabajador → no ven el botón Gestión de Usuarios
 */
public class MenuActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_menu.xml
    private TextView tvBienvenida;          // muestra "Bienvenido, [nombre]"
    private Button btnGastos;               // navega a RegistrarGastoActivity
    private Button btnIngresos;             // navega a RegistrarIngresoActivity
    private Button btnInformes;             // navega a InformesActivity
    private Button btnGestionUsuarios;      // solo visible para Administrador
    private Button btnCerrarSesion;         // vuelve al login y cierra la sesión

    // Repositorio para obtener los datos del usuario que ha iniciado sesión
    private UsuarioRepository usuarioRepository;

    // Guardamos el rol aquí para usarlo en los listeners de los botones
    // (los listeners se ejecutan después del observe(), fuera de su scope)
    private String rolUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Indicamos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_menu);

        // Conectamos cada variable Java con su elemento del XML usando su id
        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnGastos = findViewById(R.id.btnGastos);
        btnIngresos = findViewById(R.id.btnIngresos);
        btnInformes = findViewById(R.id.btnInformes);
        btnGestionUsuarios = findViewById(R.id.btnGestionUsuarios);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Creamos el repositorio para consultar la BD
        usuarioRepository = new UsuarioRepository(getApplication());

        // Recuperamos el id del usuario que nos pasó LoginActivity con putExtra
        int idUsuario = getIntent().getIntExtra("id_usuario", -1);

        // Buscamos el usuario en la BD por su id para obtener nombre y rol
        usuarioRepository.getUsuarioById(idUsuario).observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                if (usuario != null) {

                    // Mostramos el nombre real del usuario en el saludo
                    tvBienvenida.setText("Bienvenido, " + usuario.getNombre());

                    // Guardamos el rol en la variable de clase para usarlo en los botones
                    rolUsuario = usuario.getRol();

                    // Solo el Administrador puede ver y acceder a Gestión de Usuarios
                    if (usuario.getRol().equals("Administrador")) {
                        btnGestionUsuarios.setVisibility(View.VISIBLE); // lo mostramos
                    } else {
                        btnGestionUsuarios.setVisibility(View.GONE);    // lo ocultamos completamente
                    }
                }
            }
        });

        // Botón Gastos → abre RegistrarGastoActivity pasando el id del usuario
        btnGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegistrarGastoActivity.class);
                intent.putExtra("id_usuario", idUsuario); // necesario para saber quién registra
                startActivity(intent);
            }
        });

        // Botón Ingresos → abre RegistrarIngresoActivity pasando el id del usuario
        btnIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegistrarIngresoActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
            }
        });

        // Botón Informes → abre InformesActivity pasando id y rol del usuario
        // El rol es necesario para controlar quién puede eliminar movimientos
        btnInformes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, InformesActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("rol_usuario", rolUsuario); // necesario para control de permisos
                startActivity(intent);
            }
        });

        // Botón Gestión de Usuarios → abre GestionUsuariosActivity (solo Administrador lo ve)
        btnGestionUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GestionUsuariosActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
            }
        });

        // Botón Cerrar Sesión → vuelve al login cerrando todas las pantallas abiertas
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                // FLAG_ACTIVITY_CLEAR_TOP cierra todas las Activities de la pila excepto LoginActivity
                // FLAG_ACTIVITY_NEW_TASK garantiza que LoginActivity se crea como nueva tarea
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // cerramos también MenuActivity
            }
        });
    }
}