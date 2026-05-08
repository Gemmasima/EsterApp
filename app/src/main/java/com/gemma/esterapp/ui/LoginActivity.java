package com.gemma.esterapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Usuario;
import com.gemma.esterapp.repository.UsuarioRepository;

/**
 * LOGINACTIVITY
 * Primera pantalla que ve el usuario al abrir la app.
 * Valida usuario y contraseña contra la base de datos.
 * Si son correctos navega al menú principal pasando el id del usuario.
 * Si son incorrectos muestra un mensaje de error.
 */
public class LoginActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_login.xml
    private EditText etUsuario;           // campo de texto para el nombre de usuario
    private EditText etContrasena;        // campo de texto para la contraseña
    private Button btnEntrar;             // botón para iniciar sesión
    private TextView tvError;             // mensaje de error (oculto por defecto)
    private ImageButton botonOjo;         // botón para mostrar/ocultar la contraseña

    // Controla si la contraseña se muestra en texto claro o con puntos
    // false = oculta por defecto al abrir la pantalla
    private boolean contrasenaVisible = false;

    // Repositorio para consultar la base de datos sin pasar por el DAO directamente
    private UsuarioRepository usuarioRepository;

    /**
     * onCreate() se ejecuta cuando Android crea la Activity.
     * Es el punto de entrada de la pantalla — equivalente al constructor.
     * Aquí conectamos el XML con el Java y configuramos los listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // llamada obligatoria al padre

        // Indicamos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_login);

        // Conectamos cada variable Java con su elemento del XML usando su id
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvError = findViewById(R.id.tvError);
        botonOjo = findViewById(R.id.botonOjo);

        // Creamos el repositorio — necesitamos getApplication() no this
        // para evitar fugas de memoria
        usuarioRepository = new UsuarioRepository(getApplication());

        // Al pulsar Entrar llamamos al método que valida las credenciales
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });

        // Al pulsar el ojo alternamos entre mostrar y ocultar la contraseña
        botonOjo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invertimos el estado actual de visibilidad
                contrasenaVisible = !contrasenaVisible;

                if (contrasenaVisible) {
                    // Mostramos la contraseña en texto claro
                    etContrasena.setInputType(
                            InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Volvemos a ocultar la contraseña con puntos
                    etContrasena.setInputType(
                            InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // Movemos el cursor al final para que no salte al principio
                // al cambiar el inputType Android resetea la posición del cursor
                etContrasena.setSelection(etContrasena.getText().length());
            }
        });
    }

    /**
     * VALIDARLOGIN
     * Lee los campos de usuario y contraseña, consulta la BD
     * y navega al menú si las credenciales son correctas.
     */
    private void validarLogin() {

        // Leemos los campos y eliminamos espacios extra al principio y al final
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        // Validación: los dos campos deben estar rellenos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            tvError.setText("Por favor rellena todos los campos");
            tvError.setVisibility(View.VISIBLE);
            return; // salimos sin consultar la BD
        }

        // Consultamos la BD con las credenciales introducidas
        // observe() registra un observador: se ejecuta cuando Room devuelve el resultado
        usuarioRepository.login(usuario, contrasena).observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuarioEncontrado) {

                if (usuarioEncontrado != null) {
                    // Credenciales correctas — navegamos al menú principal
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);

                    // Pasamos el id del usuario para que MenuActivity sepa quién ha entrado
                    intent.putExtra("id_usuario", usuarioEncontrado.getId_usuario());

                    startActivity(intent); // abrimos MenuActivity

                    // Cerramos LoginActivity para que el botón Atrás no vuelva al login
                    finish();

                } else {
                    // Credenciales incorrectas — mostramos el mensaje de error
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}