package com.gemma.esterapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Usuario;
import com.gemma.esterapp.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GESTIONUSUARIOSACTIVITY
 * Pantalla solo accesible para el Administrador (giuseppe).
 * Permite crear usuarios nuevos y eliminar usuarios existentes.
 * Los usuarios giuseppe, ester y francesca nunca se pueden eliminar.
 * Los nuevos usuarios se crean siempre con rol Trabajador.
 */
public class GestionUsuariosActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_gestion_usuarios.xml
    private ListView listViewUsuarios;      // lista de todos los usuarios
    private LinearLayout layoutFormulario;  // formulario de creación (oculto por defecto)
    private EditText etUsuario;             // campo nombre de usuario nuevo
    private EditText etContrasena;          // campo contraseña usuario nuevo
    private Button btnCrearUsuario;         // alterna entre "Crear nuevo usuario" y "Guardar usuario"
    private Button btnEliminarUsuario;      // elimina el usuario seleccionado
    private TextView tvMensaje;             // mensajes de error o confirmación
    private ImageButton iconoHome;          // navega al menú principal
    private ImageButton iconoInformes;      // navega a InformesActivity

    // Repositorio para acceder a la tabla usuarios
    private UsuarioRepository usuarioRepository;

    // Lista de usuarios cargados de la BD — se actualiza con LiveData
    private List<Usuario> listaUsuarios = new ArrayList<>();

    // Usuario seleccionado al pulsar una fila de la lista
    private Usuario usuarioSeleccionado = null;

    // Posición seleccionada — usada para resaltar la fila en verde
    private int posicionSeleccionada = -1;

    // Controla si el formulario de creación está visible o no
    private boolean formularioVisible = false;

    // Id del Administrador que ha iniciado sesión
    private int idUsuario;

    // Lista de usuarios que nunca se pueden eliminar — son los 3 usuarios del negocio
    private static final List<String> USUARIOS_PROTEGIDOS =
            Arrays.asList("giuseppe", "ester", "francesca");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        // Recuperamos el id del administrador que viene de MenuActivity
        idUsuario = getIntent().getIntExtra("id_usuario", -1);

        // Conectamos variables Java con elementos del XML
        listViewUsuarios = findViewById(R.id.listViewUsuarios);
        layoutFormulario = findViewById(R.id.layoutFormulario);
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnCrearUsuario = findViewById(R.id.btnCrearUsuario);
        btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario);
        tvMensaje = findViewById(R.id.tvMensaje);
        iconoHome = findViewById(R.id.iconoHome);
        iconoInformes = findViewById(R.id.iconoInformes);

        usuarioRepository = new UsuarioRepository(getApplication());

        // Cargamos la lista de usuarios desde la BD
        cargarUsuarios();

        // Botón Crear / Guardar — tiene dos comportamientos según el estado del formulario:
        // Si el formulario está oculto → lo muestra y cambia el texto a "Guardar usuario"
        // Si el formulario está visible → guarda el nuevo usuario
        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!formularioVisible) {
                    layoutFormulario.setVisibility(View.VISIBLE);
                    btnCrearUsuario.setText("Guardar usuario");
                    formularioVisible = true;
                    tvMensaje.setVisibility(View.GONE);
                } else {
                    guardarUsuario();
                }
            }
        });

        // Botón Eliminar → llama al método que valida y elimina el usuario seleccionado
        btnEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUsuario();
            }
        });

        // Icono casita → vuelve al menú principal
        iconoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestionUsuariosActivity.this, MenuActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
                finish();
            }
        });

        // Icono informes → navega a InformesActivity
        iconoInformes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestionUsuariosActivity.this, InformesActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * CARGAUSUARIOS
     * Observa la BD con LiveData y actualiza la lista cada vez que cambia.
     * El adaptador personalizado muestra nombre + rol y resalta en verde la fila seleccionada.
     */
    private void cargarUsuarios() {
        usuarioRepository.getAllUsuarios().observe(this, usuarios -> {
            listaUsuarios = usuarios;

            // Adaptador personalizado para mostrar cada fila con formato propio
            ArrayAdapter<Usuario> adapter = new ArrayAdapter<Usuario>(this,
                    android.R.layout.simple_list_item_1, usuarios) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    // Creamos una fila horizontal con texto: "usuario  ·  rol"
                    LinearLayout fila = new LinearLayout(GestionUsuariosActivity.this);
                    fila.setOrientation(LinearLayout.HORIZONTAL);
                    fila.setPadding(24, 20, 24, 20);

                    TextView texto = new TextView(GestionUsuariosActivity.this);
                    texto.setText(usuarios.get(position).getUsuario()
                            + "  ·  " + usuarios.get(position).getRol());
                    texto.setTextSize(15);
                    texto.setTextColor(Color.parseColor("#2C3E50"));
                    texto.setPadding(16, 0, 0, 0);
                    fila.addView(texto);

                    // Fondo verde claro si esta fila es la seleccionada, blanco si no
                    if (position == posicionSeleccionada) {
                        fila.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    } else {
                        fila.setBackgroundColor(Color.WHITE);
                    }
                    return fila;
                }
            };

            listViewUsuarios.setAdapter(adapter);

            // Al pulsar una fila: guardamos el usuario seleccionado, resaltamos la fila
            // y ponemos el botón Eliminar en azul para indicar que está listo
            listViewUsuarios.setOnItemClickListener((parent, view, position, id) -> {
                posicionSeleccionada = position;
                usuarioSeleccionado = listaUsuarios.get(position);
                adapter.notifyDataSetChanged(); // redibujamos la lista para aplicar el color
                btnEliminarUsuario.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFF0288D1)); // azul
                tvMensaje.setVisibility(View.GONE);
            });
        });
    }

    /**
     * GUARDARUSUARIO
     * Valida los campos y crea un usuario nuevo con rol Trabajador.
     * El rol siempre es Trabajador — no se puede crear Administrador ni Gerente desde aquí.
     */
    private void guardarUsuario() {
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        // Los dos campos son obligatorios
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            tvMensaje.setText("Por favor rellena todos los campos");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // Creamos el nuevo usuario — el rol siempre es Trabajador
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsuario(usuario);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setRol("Trabajador");       // rol fijo, no se puede elegir
        nuevoUsuario.setNombre(usuario);         // el nombre es igual al usuario por defecto

        usuarioRepository.insert(nuevoUsuario);

        tvMensaje.setText("Usuario creado correctamente");
        tvMensaje.setTextColor(0xFF27AE60);
        tvMensaje.setVisibility(View.VISIBLE);

        limpiarCampos();
    }

    /**
     * ELIMINARUSUARIO
     * Comprueba que hay un usuario seleccionado, que no es un usuario protegido,
     * y llama a deleteConCallback para capturar la excepción RESTRICT si tiene datos asociados.
     */
    private void eliminarUsuario() {
        // Debe haber un usuario seleccionado en la lista
        if (usuarioSeleccionado == null) {
            tvMensaje.setText("Selecciona un usuario de la lista para eliminarlo");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // giuseppe, ester y francesca nunca se pueden eliminar
        if (USUARIOS_PROTEGIDOS.contains(usuarioSeleccionado.getUsuario().toLowerCase())) {
            tvMensaje.setText("Este usuario no se puede eliminar");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // deleteConCallback captura la excepción RESTRICT si el usuario tiene movimientos
        usuarioRepository.deleteConCallback(usuarioSeleccionado,
                new UsuarioRepository.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        // El delete fue correcto — actualizamos la UI en el hilo principal
                        runOnUiThread(() -> {
                            tvMensaje.setText("Usuario eliminado correctamente");
                            tvMensaje.setTextColor(0xFF27AE60);
                            tvMensaje.setVisibility(View.VISIBLE);
                            limpiarCampos();
                        });
                    }

                    @Override
                    public void onError(String mensaje) {
                        // RESTRICT: el usuario tiene gastos o ingresos — mostramos el error
                        runOnUiThread(() -> {
                            tvMensaje.setText(mensaje);
                            tvMensaje.setTextColor(0xFFE74C3C);
                            tvMensaje.setVisibility(View.VISIBLE);
                        });
                    }
                });
    }

    /**
     * LIMPIARCAMPOS
     * Oculta el formulario, limpia los campos de texto,
     * quita la selección de la lista y resetea el botón Eliminar a gris.
     */
    private void limpiarCampos() {
        etUsuario.setText("");
        etContrasena.setText("");
        layoutFormulario.setVisibility(View.GONE);  // ocultamos el formulario
        btnCrearUsuario.setText("Crear nuevo usuario"); // resetamos el texto del botón
        formularioVisible = false;
        usuarioSeleccionado = null;
        posicionSeleccionada = -1;
        btnEliminarUsuario.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // gris = inactivo
    }
}