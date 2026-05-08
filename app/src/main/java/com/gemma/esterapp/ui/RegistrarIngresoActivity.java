package com.gemma.esterapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Ingreso;
import com.gemma.esterapp.repository.IngresoRepository;

import java.util.Calendar;

/**
 * REGISTRARINGRESOACTIVITY
 * Pantalla para registrar un nuevo ingreso o editarlo desde InformesActivity.
 * El tipo de ingreso se selecciona con dos botones: Efectivo o Tarjeta.
 * El botón seleccionado se pone azul, el otro queda gris.
 * Más simple que RegistrarGastoActivity porque los ingresos no tienen categorías.
 */
public class RegistrarIngresoActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_registrar_ingreso.xml
    private EditText etFecha;               // campo fecha — abre DatePickerDialog al pulsar
    private EditText etImporte;             // campo importe en euros
    private EditText etNotas;               // notas opcionales
    private Button btnEfectivo;             // selecciona tipo Efectivo
    private Button btnTarjeta;              // selecciona tipo Tarjeta
    private Button btnGuardarIngreso;       // guarda o actualiza el ingreso
    private TextView tvMensaje;             // mensaje de error o confirmación
    private ImageButton iconoHome;          // navega al menú principal
    private ImageButton iconoInformes;      // navega a InformesActivity
    private Calendar calendario;            // gestiona la fecha seleccionada

    // Tipo de ingreso seleccionado: "Efectivo", "Tarjeta" o "" si no se ha elegido
    private String tipoIngresoSeleccionado = "";

    // Repositorio para acceder a la tabla ingresos
    private IngresoRepository ingresoRepository;

    // Datos del usuario y modo de la pantalla
    private int idUsuario;                      // id del usuario que registra
    private boolean modoEdicion = false;        // true = estamos editando un ingreso existente
    private int idIngresoEditar = -1;           // id del ingreso a editar (-1 si es nuevo)
    private Ingreso ingresoEditar = null;        // objeto ingreso cargado para editar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_ingreso);

        // Conectamos variables Java con elementos del XML
        etFecha = findViewById(R.id.etFecha);
        etImporte = findViewById(R.id.etImporte);
        etNotas = findViewById(R.id.etNotas);
        btnEfectivo = findViewById(R.id.btnEfectivo);
        btnTarjeta = findViewById(R.id.btnTarjeta);
        btnGuardarIngreso = findViewById(R.id.btnGuardarIngreso);
        tvMensaje = findViewById(R.id.tvMensaje);
        iconoHome = findViewById(R.id.iconoHome);
        iconoInformes = findViewById(R.id.iconoInformes);

        // El campo fecha no es editable a mano — al pulsar abre el DatePickerDialog
        calendario = Calendar.getInstance(); // fecha de hoy por defecto
        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        actualizarFecha(); // muestra la fecha de hoy en el campo

        ingresoRepository = new IngresoRepository(getApplication());

        // Recuperamos el id del usuario y el modo (nuevo o edición)
        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);
        idIngresoEditar = getIntent().getIntExtra("id_ingreso", -1);

        // Botón Efectivo — lo ponemos azul y Tarjeta en gris
        btnEfectivo.setOnClickListener(v -> {
            tipoIngresoSeleccionado = "Efectivo";
            btnEfectivo.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF0288D1)); // azul
            btnTarjeta.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // gris
        });

        // Botón Tarjeta — lo ponemos azul y Efectivo en gris
        btnTarjeta.setOnClickListener(v -> {
            tipoIngresoSeleccionado = "Tarjeta";
            btnTarjeta.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF0288D1)); // azul
            btnEfectivo.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // gris
        });

        // Si venimos de InformesActivity en modo edición cargamos los datos del ingreso
        if (modoEdicion) {
            btnGuardarIngreso.setText("Actualizar Ingreso");
            cargarIngresoEditar();
        }

        btnGuardarIngreso.setOnClickListener(v -> guardarIngreso());

        iconoHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarIngresoActivity.this, MenuActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
            finish();
        });

        iconoInformes.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarIngresoActivity.this, InformesActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
            finish();
        });
    }

    // Abre el DatePickerDialog con la fecha actual como valor inicial
    private void mostrarDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendario.set(Calendar.YEAR, year);
                    calendario.set(Calendar.MONTH, month);
                    calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    actualizarFecha(); // actualizamos el campo con la nueva fecha
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    // Muestra la fecha en el campo en formato DD/MM/YYYY (legible para el usuario)
    private void actualizarFecha() {
        String fechaMostrar = String.format("%02d/%02d/%04d",
                calendario.get(Calendar.DAY_OF_MONTH),
                calendario.get(Calendar.MONTH) + 1,
                calendario.get(Calendar.YEAR));
        etFecha.setText(fechaMostrar);
    }

    // Convierte la fecha de DD/MM/YYYY (pantalla) a YYYY-MM-DD (base de datos)
    private String convertirFechaParaBD(String fechaMostrar) {
        String[] partes = fechaMostrar.split("/");
        if (partes.length == 3) {
            return partes[2] + "-" + partes[1] + "-" + partes[0];
        }
        return fechaMostrar;
    }

    /**
     * CARGARINGRESOEDIT AR
     * Carga los datos del ingreso a editar en el formulario.
     * Selecciona el botón correcto (Efectivo o Tarjeta) según el tipo guardado.
     */
    private void cargarIngresoEditar() {
        ingresoRepository.getAllIngresos().observe(this, ingresos -> {
            for (Ingreso ingreso : ingresos) {
                if (ingreso.getId_ingreso() == idIngresoEditar) {
                    ingresoEditar = ingreso;

                    // Rellenamos el formulario con los datos del ingreso
                    etImporte.setText(String.valueOf(ingreso.getImporte()));
                    String[] partes = ingreso.getFecha().split("-");
                    etFecha.setText(partes[2] + "/" + partes[1] + "/" + partes[0]);
                    etNotas.setText(ingreso.getNotas());

                    // Seleccionamos el botón correcto según el tipo guardado
                    tipoIngresoSeleccionado = ingreso.getTipoingreso();
                    if ("Tarjeta".equals(tipoIngresoSeleccionado)) {
                        btnTarjeta.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(0xFF0288D1)); // azul
                        btnEfectivo.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // gris
                    } else {
                        // Por defecto Efectivo
                        btnEfectivo.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(0xFF0288D1));
                        btnTarjeta.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(0xFFCCCCCC));
                    }
                    break; // encontrado, salimos del bucle
                }
            }
        });
    }

    /**
     * GUARDARINGRESO
     * Valida que el importe no esté vacío y que se haya seleccionado un tipo.
     * En modo edición actualiza el ingreso existente y cierra la pantalla.
     * En modo nuevo inserta el ingreso y resetea el formulario.
     */
    private void guardarIngreso() {
        String fecha = etFecha.getText().toString().trim();
        String importeTexto = etImporte.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();

        // Validación 1: el importe es obligatorio
        if (importeTexto.isEmpty()) {
            tvMensaje.setText("Por favor introduce un importe");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }
        // Convertimos el importe aquí para que esté disponible en todo el método
        // Si lo declaráramos más abajo causaría "Cannot resolve symbol" en el bloque modoEdicion
        double importe = Double.parseDouble(importeTexto);

        // Validación 2: debe haberse seleccionado Efectivo o Tarjeta
        if (tipoIngresoSeleccionado.isEmpty()) {
            tvMensaje.setText("Por favor selecciona Efectivo o Tarjeta");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        if (modoEdicion && ingresoEditar != null) {
            // Actualizamos el ingreso existente y cerramos la pantalla
            ingresoEditar.setFecha(convertirFechaParaBD(fecha));
            ingresoEditar.setImporte(importe);
            ingresoEditar.setTipoingreso(tipoIngresoSeleccionado);
            ingresoEditar.setNotas(notas);
            ingresoRepository.update(ingresoEditar);
            finish(); // volvemos a InformesActivity
            return;
        }

        // Creamos un nuevo ingreso y lo insertamos en la BD
        Ingreso ingreso = new Ingreso();
        ingreso.setFecha(convertirFechaParaBD(fecha));
        ingreso.setImporte(importe);
        ingreso.setTipoingreso(tipoIngresoSeleccionado);
        ingreso.setNotas(notas);
        ingreso.setId_usuario(idUsuario); // quién registra el ingreso

        ingresoRepository.insert(ingreso);

        // Confirmación y reseteo del formulario para registrar otro ingreso
        tvMensaje.setText("Ingreso guardado correctamente");
        tvMensaje.setTextColor(0xFF27AE60);
        tvMensaje.setVisibility(View.VISIBLE);

        etImporte.setText("");
        etNotas.setText("");
        tipoIngresoSeleccionado = ""; // resetamos la selección
        btnEfectivo.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // gris
        btnTarjeta.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // gris
    }
}