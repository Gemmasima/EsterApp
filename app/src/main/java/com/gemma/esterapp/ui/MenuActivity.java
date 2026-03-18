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
 * Pantalla principal después del login.
 * Muestra los botones de navegación según el rol del usuario.
 * Administrador ve todos los botones incluido Gestión de Usuarios.
 * Gerente y Trabajador no ven el botón de Gestión de Usuarios.
 */
public class MenuActivity extends AppCompatActivity {

    // Elementos visuales del XML activity_menu.xml
    private TextView tvBienvenida;
    private Button btnGastos;
    private Button btnIngresos;
    private Button btnInformes;
    private Button btnGestionUsuarios;
    private Button btnCerrarSesion;

    // Repositorio para obtener los datos del usuario que ha iniciado sesión
    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Le decimos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_menu);

        // Conectamos cada variable Java con su elemento del XML por su id
        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnGastos = findViewById(R.id.btnGastos);
        btnIngresos = findViewById(R.id.btnIngresos);
        btnInformes = findViewById(R.id.btnInformes);
        btnGestionUsuarios = findViewById(R.id.btnGestionUsuarios);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Creamos el repositorio para poder consultar la base de datos
        usuarioRepository = new UsuarioRepository(getApplication());

        // Cogemos el id del usuario que nos pasó LoginActivity
        int idUsuario = getIntent().getIntExtra("id_usuario", -1);

        // Buscamos el usuario en la base de datos por su id
        usuarioRepository.getUsuarioById(idUsuario).observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                if (usuario != null) {
                    // Mostramos el nombre del usuario en el título de bienvenida
                    tvBienvenida.setText("Bienvenido, " + usuario.getNombre());

                    // Si el rol es Administrador mostramos el botón de Gestión de Usuarios
                    // Si es Gerente o Trabajador lo ocultamos
                    if (usuario.getRol().equals("Administrador")) {
                        btnGestionUsuarios.setVisibility(View.VISIBLE);
                    } else {
                        btnGestionUsuarios.setVisibility(View.GONE);
                    }
                }
            }
        });

        // Botón Gastos → navega a RegistrarGastoActivity
        btnGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegistrarGastoActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
            }
        });

        // Botón Ingresos → navega a RegistrarIngresoActivity
        btnIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegistrarIngresoActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
            }
        });

        // Botón Informes → navega a InformesActivity
        btnInformes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, InformesActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
            }
        });

        // Botón Gestión de Usuarios → navega a GestionUsuariosActivity
        btnGestionUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GestionUsuariosActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
            }
        });

        // Botón Cerrar Sesión → vuelve al login y cierra todas las pantallas anteriores
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                // FLAG_ACTIVITY_CLEAR_TOP cierra todas las Activities abiertas excepto LoginActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}