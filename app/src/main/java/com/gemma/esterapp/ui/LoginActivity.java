package com.gemma.esterapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Usuario;
import com.gemma.esterapp.repository.UsuarioRepository;

/**
 * LOGINACTIVITY
 * Es la primera pantalla que ve el usuario al abrir la app.
 * Valida el usuario y contraseña contra la base de datos.
 * Si son correctos navega al menú principal.
 * Si son incorrectos muestra un mensaje de error.
 */
public class LoginActivity extends AppCompatActivity {

    // Elementos visuales del XML activity_login.xml
    private EditText etUsuario;
    private EditText etContrasena;
    private Button btnEntrar;
    private TextView tvError;

    // Repositorio para acceder a la base de datos
    private UsuarioRepository usuarioRepository;

    /**
     * onCreate se ejecuta cuando Android crea la Activity.
     * Es el equivalente al constructor en una Activity.
     * Aquí conectamos el XML con el Java y preparamos los elementos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Le decimos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_login);

        // Conectamos cada variable Java con su elemento del XML por su id
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvError = findViewById(R.id.tvError);

        // Creamos el repositorio para poder consultar la base de datos
        usuarioRepository = new UsuarioRepository(getApplication());

        // Programamos qué pasa cuando el usuario pulsa el botón Entrar
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });
    }

    /**
     * VALIDARLOGIN
     * Coge el usuario y contraseña escritos en los campos,
     * consulta la base de datos y decide qué hacer.
     */
    private void validarLogin() {

        // Cogemos el texto escrito en los campos y eliminamos espacios extra
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();


        // Comprobamos que los campos no estén vacíos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            tvError.setText("Por favor rellena todos los campos");
            tvError.setVisibility(View.VISIBLE);
            return; // Salimos del método, no consultamos la BD
        }

        // Consultamos la base de datos con el usuario y contraseña introducidos
        // observe() significa: "avísame cuando llegue el resultado"
        usuarioRepository.login(usuario, contrasena).observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuarioEncontrado) {
                if (usuarioEncontrado != null) {
                    // Las credenciales son correctas → navegamos al menú
                    // Intent es el mecanismo de Android para navegar entre pantallas
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);

                    // Pasamos el id del usuario al menú para saber quién ha iniciado sesión
                    intent.putExtra("id_usuario", usuarioEncontrado.getId_usuario());

                    startActivity(intent);

                    // Cerramos LoginActivity para que no vuelva atrás con el botón back
                    finish();
                } else {
                    // Las credenciales son incorrectas → mostramos el error
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}