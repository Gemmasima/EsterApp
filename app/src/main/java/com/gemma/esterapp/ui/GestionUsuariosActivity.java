package com.gemma.esterapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
 * Pantalla solo accesible para el Administrador (Giuseppe).
 * Permite crear, editar y eliminar usuarios.
 * Muestra la lista de todos los usuarios registrados.
 */
public class GestionUsuariosActivity extends AppCompatActivity {

    // Elementos visuales del XML activity_gestion_usuarios.xml
    private EditText etNombre;
    private EditText etUsuario;
    private EditText etContrasena;
    private Spinner spinnerRol;
    private Button btnGuardarUsuario;
    private Button btnEliminarUsuario;
    private Button btnVolver;
    private TextView tvMensaje;
    private ListView listViewUsuarios;

    // Repositorio para acceder a la base de datos
    private UsuarioRepository usuarioRepository;

    // Lista de usuarios cargados de la base de datos
    private List<Usuario> listaUsuarios = new ArrayList<>();

    // Usuario seleccionado en la lista para editar o eliminar
    private Usuario usuarioSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Le decimos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_gestion_usuarios);

        // Conectamos cada variable Java con su elemento del XML por su id
        etNombre = findViewById(R.id.etNombre);
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario);
        btnVolver = findViewById(R.id.btnVolver);
        tvMensaje = findViewById(R.id.tvMensaje);
        listViewUsuarios = findViewById(R.id.listViewUsuarios);

        // Creamos el repositorio para poder consultar la base de datos
        usuarioRepository = new UsuarioRepository(getApplication());

        // Cargamos los roles disponibles en el spinner
        List<String> roles = Arrays.asList("Administrador", "Gerente", "Trabajador");
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roles);
        adapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterRoles);

        // Cargamos la lista de usuarios de la base de datos
        cargarUsuarios();

        // Cuando el usuario pulsa un elemento de la lista
        // cargamos sus datos en los campos para poder editarlo
        listViewUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuarioSeleccionado = listaUsuarios.get(position);

                // Rellenamos los campos con los datos del usuario seleccionado
                etNombre.setText(usuarioSeleccionado.getNombre());
                etUsuario.setText(usuarioSeleccionado.getUsuario());
                etContrasena.setText(usuarioSeleccionado.getContrasena());

                // Seleccionamos el rol correspondiente en el spinner
                List<String> roles = Arrays.asList("Administrador", "Gerente", "Trabajador");
                int posicionRol = roles.indexOf(usuarioSeleccionado.getRol());
                if (posicionRol >= 0) {
                    spinnerRol.setSelection(posicionRol);
                }

                tvMensaje.setText("Usuario cargado para editar");
                tvMensaje.setTextColor(0xFF2980B9); // Azul
                tvMensaje.setVisibility(View.VISIBLE);
            }
        });

        // Botón Guardar → crea un usuario nuevo o actualiza el seleccionado
        btnGuardarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarUsuario();
            }
        });

        // Botón Eliminar → elimina el usuario seleccionado de la lista
        btnEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUsuario();
            }
        });

        // Botón Volver → cierra esta pantalla y vuelve al menú
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * CARGAUSUARIOS
     * Obtiene todos los usuarios de la base de datos
     * y los muestra en el ListView.
     */
    private void cargarUsuarios() {
        usuarioRepository.getAllUsuarios().observe(this, usuarios -> {
            listaUsuarios = usuarios;

            // Creamos una lista con nombre y rol para mostrar en el ListView
            List<String> nombresUsuarios = new ArrayList<>();
            for (Usuario usuario : usuarios) {
                nombresUsuarios.add(usuario.getNombre() + " (" + usuario.getRol() + ")");
            }

            // ArrayAdapter conecta la lista de nombres con el ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    nombresUsuarios);
            listViewUsuarios.setAdapter(adapter);
        });
    }

    /**
     * GUARDARUSUARIO
     * Si hay un usuario seleccionado lo actualiza.
     * Si no hay ninguno seleccionado crea uno nuevo.
     */
    private void guardarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();

        // Comprobamos que los campos obligatorios no estén vacíos
        if (nombre.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
            tvMensaje.setText("Por favor rellena todos los campos");
            tvMensaje.setTextColor(0xFFE74C3C); // Rojo
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        if (usuarioSeleccionado != null) {
            // Actualizamos el usuario seleccionado con los nuevos datos
            usuarioSeleccionado.setNombre(nombre);
            usuarioSeleccionado.setUsuario(usuario);
            usuarioSeleccionado.setContrasena(contrasena);
            usuarioSeleccionado.setRol(rol);
            usuarioRepository.update(usuarioSeleccionado);

            tvMensaje.setText("Usuario actualizado correctamente");
        } else {
            // Creamos un usuario nuevo con los datos introducidos
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setUsuario(usuario);
            nuevoUsuario.setContrasena(contrasena);
            nuevoUsuario.setRol(rol);
            usuarioRepository.insert(nuevoUsuario);

            tvMensaje.setText("Usuario creado correctamente");
        }

        tvMensaje.setTextColor(0xFF27AE60); // Verde
        tvMensaje.setVisibility(View.VISIBLE);

        // Limpiamos los campos y quitamos la selección
        limpiarCampos();
    }

    /**
     * ELIMINARUSUARIO
     * Elimina el usuario seleccionado de la base de datos.
     */
    private void eliminarUsuario() {
        if (usuarioSeleccionado == null) {
            tvMensaje.setText("Selecciona un usuario de la lista para eliminarlo");
            tvMensaje.setTextColor(0xFFE74C3C); // Rojo
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        usuarioRepository.delete(usuarioSeleccionado);

        tvMensaje.setText("Usuario eliminado correctamente");
        tvMensaje.setTextColor(0xFF27AE60); // Verde
        tvMensaje.setVisibility(View.VISIBLE);

        // Limpiamos los campos y quitamos la selección
        limpiarCampos();
    }

    /**
     * LIMPIARCAMPOS
     * Limpia los campos del formulario y quita la selección del usuario.
     */
    private void limpiarCampos() {
        etNombre.setText("");
        etUsuario.setText("");
        etContrasena.setText("");
        spinnerRol.setSelection(0);
        usuarioSeleccionado = null;
    }
}