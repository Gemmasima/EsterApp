package com.gemma.esterapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Ingreso;
import com.gemma.esterapp.repository.IngresoRepository;

import java.util.Arrays;
import java.util.List;

/**
 * REGISTRARINGRESOACTIVITY
 * Pantalla para registrar un nuevo ingreso.
 * El usuario rellena fecha, importe, tipo de ingreso y notas opcionales.
 * Al pulsar Guardar se inserta el ingreso en la base de datos.
 */
public class RegistrarIngresoActivity extends AppCompatActivity {

    // Elementos visuales del XML activity_registrar_ingreso.xml
    private EditText etFecha;
    private EditText etImporte;
    private Spinner spinnerTipoIngreso;
    private EditText etNotas;
    private Button btnGuardarIngreso;
    private Button btnVolver;
    private TextView tvMensaje;

    // Repositorio para acceder a la base de datos
    private IngresoRepository ingresoRepository;

    // Id del usuario que ha iniciado sesión
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Le decimos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_registrar_ingreso);

        // Conectamos cada variable Java con su elemento del XML por su id
        etFecha = findViewById(R.id.etFecha);
        etImporte = findViewById(R.id.etImporte);
        spinnerTipoIngreso = findViewById(R.id.spinnerTipoIngreso);
        etNotas = findViewById(R.id.etNotas);
        btnGuardarIngreso = findViewById(R.id.btnGuardarIngreso);
        btnVolver = findViewById(R.id.btnVolver);
        tvMensaje = findViewById(R.id.tvMensaje);

        // Creamos el repositorio para poder insertar ingresos en la base de datos
        ingresoRepository = new IngresoRepository(getApplication());

        // Cogemos el id del usuario que nos pasó MenuActivity
        idUsuario = getIntent().getIntExtra("id_usuario", -1);

        // Cargamos los tipos de ingreso en el spinner
        // Arrays.asList crea una lista a partir de valores fijos
        List<String> tiposIngreso = Arrays.asList("Efectivo", "Tarjeta");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tiposIngreso);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoIngreso.setAdapter(adapter);

        // Botón Guardar → guarda el ingreso en la base de datos
        btnGuardarIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarIngreso();
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
     * GUARDARINGRESO
     * Valida los campos y guarda el ingreso en la base de datos.
     */
    private void guardarIngreso() {

        // Cogemos el texto de los campos
        String fecha = etFecha.getText().toString().trim();
        String importeTexto = etImporte.getText().toString().trim();
        String tipoIngreso = spinnerTipoIngreso.getSelectedItem().toString();
        String notas = etNotas.getText().toString().trim();

        // Comprobamos que los campos obligatorios no estén vacíos
        if (fecha.isEmpty() || importeTexto.isEmpty()) {
            tvMensaje.setText("Por favor rellena la fecha y el importe");
            tvMensaje.setTextColor(0xFFE74C3C); // Rojo
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // Convertimos el importe de String a double
        double importe = Double.parseDouble(importeTexto);

        // Creamos el objeto Ingreso con todos los datos
        // OJO: el campo se llama tipoingreso no tipo (decisión de diseño)
        Ingreso ingreso = new Ingreso();
        ingreso.setFecha(fecha);
        ingreso.setImporte(importe);
        ingreso.setTipoingreso(tipoIngreso);
        ingreso.setNotas(notas);
        ingreso.setId_usuario(idUsuario);

        // Insertamos el ingreso en la base de datos a través del repositorio
        ingresoRepository.insert(ingreso);

        // Mostramos mensaje de confirmación
        tvMensaje.setText("Ingreso guardado correctamente");
        tvMensaje.setTextColor(0xFF27AE60); // Verde
        tvMensaje.setVisibility(View.VISIBLE);

        // Limpiamos los campos para poder registrar otro ingreso
        etFecha.setText("");
        etImporte.setText("");
        etNotas.setText("");
    }
}