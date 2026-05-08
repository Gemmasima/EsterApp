package com.gemma.esterapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Gasto;
import com.gemma.esterapp.model.Ingreso;
import com.gemma.esterapp.repository.GastoRepository;
import com.gemma.esterapp.repository.IngresoRepository;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * GENERARINFORMEACTIVITY
 * Pantalla para generar un CSV con los datos del periodo seleccionado.
 * Accesible solo para Administrador y Gerente.
 * Permite seleccionar un rango de fechas con MaterialDatePicker,
 * filtrar por tipo (Gastos / Ingresos / Todo) y por categoría o tipo de ingreso.
 */
public class GenerarInformeActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_generar_informe.xml
    private Button btnSeleccionarFechas;        // abre el calendario de rango
    private TextView tvRangoFechas;             // muestra el rango seleccionado
    private Spinner spinnerTipo;                // Gastos / Ingresos / Todo
    private LinearLayout layoutCategorias;      // checklist de categorías (solo para Gastos)
    private LinearLayout layoutTipoIngreso;     // checklist de tipos (solo para Ingresos)
    private CheckBox cbTodoCategorias;          // selecciona todas las categorías
    private CheckBox cbProveedoresMateriales;
    private CheckBox cbProveedoresServicios;
    private CheckBox cbImpuestos;
    private CheckBox cbSalarios;
    private CheckBox cbVehiculos;
    private CheckBox cbTodoIngresos;            // selecciona Efectivo y Tarjeta
    private CheckBox cbEfectivo;
    private CheckBox cbTarjeta;
    private Button btnGenerarCSV;               // genera y comparte el CSV
    private ImageButton iconoHome;
    private ImageButton iconoInformes;

    // Repositorios para consultar gastos e ingresos
    private GastoRepository gastoRepository;
    private IngresoRepository ingresoRepository;

    // Id y rol del usuario que ha abierto esta pantalla
    private int idUsuario;
    private String rolUsuario;

    // Fechas seleccionadas en el picker — formato YYYY-MM-DD para consultar la BD
    private String fechaDesde = null;
    private String fechaHasta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_informe);

        // Recuperamos id y rol del usuario desde InformesActivity
        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        rolUsuario = getIntent().getStringExtra("rol_usuario");

        // Conectamos variables Java con elementos del XML
        btnSeleccionarFechas = findViewById(R.id.btnSeleccionarFechas);
        tvRangoFechas = findViewById(R.id.tvRangoFechas);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        layoutCategorias = findViewById(R.id.layoutCategorias);
        layoutTipoIngreso = findViewById(R.id.layoutTipoIngreso);
        cbTodoCategorias = findViewById(R.id.cbTodoCategorias);
        cbProveedoresMateriales = findViewById(R.id.cbProveedoresMateriales);
        cbProveedoresServicios = findViewById(R.id.cbProveedoresServicios);
        cbImpuestos = findViewById(R.id.cbImpuestos);
        cbSalarios = findViewById(R.id.cbSalarios);
        cbVehiculos = findViewById(R.id.cbVehiculos);
        cbTodoIngresos = findViewById(R.id.cbTodoIngresos);
        cbEfectivo = findViewById(R.id.cbEfectivo);
        cbTarjeta = findViewById(R.id.cbTarjeta);
        btnGenerarCSV = findViewById(R.id.btnGenerarCSV);
        iconoHome = findViewById(R.id.iconoHome);
        iconoInformes = findViewById(R.id.iconoInformes);

        gastoRepository = new GastoRepository(getApplication());
        ingresoRepository = new IngresoRepository(getApplication());

        // Cargamos el spinner con las 3 opciones de tipo de informe
        List<String> tipos = Arrays.asList("Gastos", "Ingresos", "Todo");
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        // Cuando cambia el spinner mostramos u ocultamos el checklist correspondiente
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Gastos → mostramos checklist de categorías
                    layoutCategorias.setVisibility(View.VISIBLE);
                    layoutTipoIngreso.setVisibility(View.GONE);
                } else if (position == 1) {
                    // Ingresos → mostramos checklist de tipo (Efectivo/Tarjeta)
                    layoutCategorias.setVisibility(View.GONE);
                    layoutTipoIngreso.setVisibility(View.VISIBLE);
                } else {
                    // Todo → no hay checklist, incluye todo sin filtro
                    layoutCategorias.setVisibility(View.GONE);
                    layoutTipoIngreso.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Botón fechas → abre el MaterialDatePicker de rango
        btnSeleccionarFechas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { abrirDatePicker(); }
        });

        // Botón Generar → valida y genera el CSV según el tipo seleccionado
        btnGenerarCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { generarCSV(); }
        });

        // Icono casita → vuelve al menú principal
        iconoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GenerarInformeActivity.this, MenuActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
                finish();
            }
        });

        // Icono informes → vuelve a InformesActivity
        iconoInformes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GenerarInformeActivity.this, InformesActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("rol_usuario", rolUsuario);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * ABRIRDATEPICKER
     * Abre el MaterialDatePicker de rango de fechas.
     * El usuario selecciona fecha inicio y fin como en una reserva de hotel.
     * Convierte los milisegundos a String YYYY-MM-DD para la BD
     * y a DD/MM/YYYY para mostrarlo en pantalla.
     */
    private void abrirDatePicker() {
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Selecciona el periodo")
                        .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            // Convertimos milisegundos a String YYYY-MM-DD para guardar en fechaDesde/fechaHasta
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDesde = sdf.format(new Date(selection.first));   // fecha inicio
            fechaHasta = sdf.format(new Date(selection.second));  // fecha fin

            // Convertimos a DD/MM/YYYY para mostrar al usuario
            SimpleDateFormat sdfMostrar = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String textoRango = "Del " + sdfMostrar.format(new Date(selection.first))
                    + " al " + sdfMostrar.format(new Date(selection.second));
            tvRangoFechas.setText(textoRango);
            tvRangoFechas.setVisibility(View.VISIBLE);
        });

        // show() necesita el FragmentManager para mostrar el diálogo dentro de la Activity
        picker.show(getSupportFragmentManager(), "date_picker");
    }

    /**
     * GENERARCSV
     * Valida que se haya seleccionado un rango y llama al método
     * correspondiente según el tipo elegido en el spinner.
     */
    private void generarCSV() {
        if (fechaDesde == null || fechaHasta == null) {
            Toast.makeText(this, "Por favor selecciona un periodo", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = spinnerTipo.getSelectedItem().toString();

        if (tipo.equals("Gastos")) {
            generarCSVGastos();
        } else if (tipo.equals("Ingresos")) {
            generarCSVIngresos();
        } else {
            generarCSVTodo(); // Todo: gastos + ingresos sin filtro
        }
    }

    /**
     * GENERARCSVGASTOS
     * Consulta los gastos del periodo. Si está marcado "Todo categorías" incluye todos.
     * Si no, incluye solo las categorías marcadas en el checklist.
     */
    private void generarCSVGastos() {
        gastoRepository.getGastosByRangoFechas(fechaDesde, fechaHasta).observe(this, gastos -> {

            // Si "Todo" no está marcado construimos la lista de categorías seleccionadas
            List<String> categoriasFiltro = new ArrayList<>();
            if (cbTodoCategorias.isChecked()) {
                categoriasFiltro = null; // null = sin filtro, incluimos todo
            } else {
                if (cbProveedoresMateriales.isChecked())
                    categoriasFiltro.add("Proveedores de materiales");
                if (cbProveedoresServicios.isChecked())
                    categoriasFiltro.add("Proveedores de servicios");
                if (cbImpuestos.isChecked()) categoriasFiltro.add("Impuestos");
                if (cbSalarios.isChecked()) categoriasFiltro.add("Salarios");
                if (cbVehiculos.isChecked()) categoriasFiltro.add("Vehículos");
            }

            // Construimos el CSV con cabecera y una línea por gasto
            StringBuilder csv = new StringBuilder();
            csv.append("Fecha,Notas,Categoria,Importe\n"); // cabecera

            double total = 0.0;
            for (Gasto gasto : gastos) {
                csv.append(gasto.getFecha()).append(",");
                csv.append(gasto.getNotas()).append(",");
                csv.append(gasto.getId_categoria()).append(","); // id de categoría
                csv.append(String.format("%.2f", gasto.getImporte())).append("\n");
                total += gasto.getImporte();
            }
            csv.append("Total,,,").append(String.format("%.2f", total)).append("\n");

            compartirCSV(csv.toString(), "gastos");
        });
    }

    /**
     * GENERARCSVINGRESOOS
     * Consulta los ingresos del periodo.
     * Si "Todo ingresos" no está marcado filtra por Efectivo o Tarjeta.
     */
    private void generarCSVIngresos() {
        ingresoRepository.getIngresosByRangoFechas(fechaDesde, fechaHasta).observe(this, ingresos -> {
            StringBuilder csv = new StringBuilder();
            csv.append("Fecha,Tipo,Notas,Importe\n"); // cabecera

            double total = 0.0;
            for (Ingreso ingreso : ingresos) {
                // Filtramos por tipo si "Todo" no está marcado
                if (!cbTodoIngresos.isChecked()) {
                    if (ingreso.getTipoingreso().equals("Efectivo") && !cbEfectivo.isChecked())
                        continue; // saltamos este ingreso
                    if (ingreso.getTipoingreso().equals("Tarjeta") && !cbTarjeta.isChecked())
                        continue;
                }
                csv.append(ingreso.getFecha()).append(",");
                csv.append(ingreso.getTipoingreso()).append(",");
                csv.append(ingreso.getNotas()).append(",");
                csv.append(String.format("%.2f", ingreso.getImporte())).append("\n");
                total += ingreso.getImporte();
            }
            csv.append("Total,,,").append(String.format("%.2f", total)).append("\n");

            compartirCSV(csv.toString(), "ingresos");
        });
    }

    /**
     * GENERARCSVTODO
     * Consulta gastos e ingresos del periodo sin ningún filtro.
     * Muestra totales separados y balance final.
     */
    private void generarCSVTodo() {
        ingresoRepository.getIngresosByRangoFechas(fechaDesde, fechaHasta).observe(this, ingresos -> {
            gastoRepository.getGastosByRangoFechas(fechaDesde, fechaHasta).observe(this, gastos -> {
                StringBuilder csv = new StringBuilder();
                csv.append("Fecha,Tipo,Notas,Importe\n");

                double totalIngresos = 0.0;
                for (Ingreso ingreso : ingresos) {
                    csv.append(ingreso.getFecha()).append(",");
                    csv.append("Ingreso").append(",");
                    csv.append(ingreso.getNotas()).append(",");
                    csv.append("+").append(String.format("%.2f", ingreso.getImporte())).append("\n");
                    totalIngresos += ingreso.getImporte();
                }

                double totalGastos = 0.0;
                for (Gasto gasto : gastos) {
                    csv.append(gasto.getFecha()).append(",");
                    csv.append("Gasto").append(",");
                    csv.append(gasto.getNotas()).append(",");
                    csv.append("-").append(String.format("%.2f", gasto.getImporte())).append("\n");
                    totalGastos += gasto.getImporte();
                }

                // Resumen final con totales y balance
                csv.append("Total ingresos,,,+")
                        .append(String.format("%.2f", totalIngresos)).append("\n");
                csv.append("Total gastos,,,-")
                        .append(String.format("%.2f", totalGastos)).append("\n");
                csv.append("Balance,,,")
                        .append(String.format("%.2f", totalIngresos - totalGastos)).append("\n");

                compartirCSV(csv.toString(), "todo");
            });
        });
    }

    /**
     * COMPARTIRCSV
     * Guarda el CSV en el almacenamiento del dispositivo y abre
     * el diálogo de compartir de Android (WhatsApp, email, Drive...).
     * Usa FileProvider para crear un URI seguro compatible con Android 7+.
     */
    private void compartirCSV(String contenido, String tipo) {
        try {
            // Nombre del archivo con tipo y rango de fechas para identificarlo
            String nombreArchivo = "informe_" + tipo + "_"
                    + fechaDesde + "_" + fechaHasta + ".csv";

            // Guardamos en la carpeta Documents de la app en almacenamiento externo
            File archivo = new File(
                    getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), nombreArchivo);

            // Escribimos el contenido del CSV en el archivo
            FileWriter writer = new FileWriter(archivo);
            writer.write(contenido);
            writer.close();

            // FileProvider crea un URI seguro — necesario desde Android 7 (API 24)
            // Sin FileProvider Android bloquea el acceso al archivo por seguridad
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", archivo);

            // Abrimos el diálogo de compartir de Android
            Intent intentCompartir = new Intent(Intent.ACTION_SEND);
            intentCompartir.setType("text/csv");                            // tipo MIME del archivo
            intentCompartir.putExtra(Intent.EXTRA_STREAM, uri);             // adjuntamos el archivo
            intentCompartir.putExtra(Intent.EXTRA_SUBJECT, "Informe Gazzola Shop");
            intentCompartir.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // permiso temporal de lectura
            startActivity(Intent.createChooser(intentCompartir, "Compartir informe"));

        } catch (IOException e) {
            // Error al escribir el archivo — mostramos aviso
            Toast.makeText(this, "Error al generar el archivo", Toast.LENGTH_SHORT).show();
        }
    }
}