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
import com.gemma.esterapp.model.Categoria;
import com.gemma.esterapp.repository.CategoriaRepository;
import com.gemma.esterapp.repository.GastoRepository;
import com.gemma.esterapp.repository.IngresoRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * INFORMESACTIVITY
 * Pantalla para generar informes financieros.
 * Permite consultar totales de ingresos y gastos por día, semana, mes y categoría.
 */
public class InformesActivity extends AppCompatActivity {

    // Elementos visuales del XML activity_informes.xml
    private EditText etDia;
    private EditText etSemanaDesde;
    private EditText etSemanaHasta;
    private EditText etMes;
    private Spinner spinnerCategoriaInforme;
    private Button btnInformeDiario;
    private Button btnInformeSemanal;
    private Button btnInformeMensual;
    private Button btnInformeCategoria;
    private Button btnVolver;
    private TextView tvResultadoDiario;
    private TextView tvResultadoSemanal;
    private TextView tvResultadoMensual;
    private TextView tvResultadoCategoria;

    // Repositorios para acceder a la base de datos
    private GastoRepository gastoRepository;
    private IngresoRepository ingresoRepository;
    private CategoriaRepository categoriaRepository;

    // Lista de categorías para el spinner
    private List<Categoria> listaCategorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Le decimos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_informes);

        // Conectamos cada variable Java con su elemento del XML por su id
        etDia = findViewById(R.id.etDia);
        etSemanaDesde = findViewById(R.id.etSemanaDesde);
        etSemanaHasta = findViewById(R.id.etSemanaHasta);
        etMes = findViewById(R.id.etMes);
        spinnerCategoriaInforme = findViewById(R.id.spinnerCategoriaInforme);
        btnInformeDiario = findViewById(R.id.btnInformeDiario);
        btnInformeSemanal = findViewById(R.id.btnInformeSemanal);
        btnInformeMensual = findViewById(R.id.btnInformeMensual);
        btnInformeCategoria = findViewById(R.id.btnInformeCategoria);
        btnVolver = findViewById(R.id.btnVolver);
        tvResultadoDiario = findViewById(R.id.tvResultadoDiario);
        tvResultadoSemanal = findViewById(R.id.tvResultadoSemanal);
        tvResultadoMensual = findViewById(R.id.tvResultadoMensual);
        tvResultadoCategoria = findViewById(R.id.tvResultadoCategoria);

        // Creamos los repositorios para poder consultar la base de datos
        gastoRepository = new GastoRepository(getApplication());
        ingresoRepository = new IngresoRepository(getApplication());
        categoriaRepository = new CategoriaRepository(getApplication());

        // Cargamos las categorías en el spinner
        cargarCategorias();

        // Botón informe diario
        btnInformeDiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarInformeDiario();
            }
        });

        // Botón informe semanal
        btnInformeSemanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarInformeSemanal();
            }
        });

        // Botón informe mensual
        btnInformeMensual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarInformeMensual();
            }
        });

        // Botón informe por categoría
        btnInformeCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarInformeCategoria();
            }
        });

        // Botón volver → cierra esta pantalla y vuelve al menú
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * CARGARCATEGORIAS
     * Carga las categorías de la base de datos en el spinner del informe por categoría.
     */
    private void cargarCategorias() {
        categoriaRepository.getAllCategorias().observe(this, categorias -> {
            listaCategorias = categorias;
            List<String> nombresCategorias = new ArrayList<>();
            for (Categoria categoria : categorias) {
                nombresCategorias.add(categoria.getNombre());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    nombresCategorias);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoriaInforme.setAdapter(adapter);
        });
    }

    /**
     * GENERARINFORMEDIARIO
     * Consulta el total de ingresos y gastos de un día concreto.
     */
    private void generarInformeDiario() {
        String fecha = etDia.getText().toString().trim();

        if (fecha.isEmpty()) {
            tvResultadoDiario.setText("Por favor introduce una fecha");
            tvResultadoDiario.setVisibility(View.VISIBLE);
            return;
        }

        // Observamos el total de ingresos del día
        ingresoRepository.getTotalIngresosByDia(fecha).observe(this, totalIngresos -> {
            // Observamos el total de gastos del día
            gastoRepository.getTotalGastosByDia(fecha).observe(this, totalGastos -> {
                double ingresos = totalIngresos != null ? totalIngresos : 0.0;
                double gastos = totalGastos != null ? totalGastos : 0.0;
                double balance = ingresos - gastos;

                tvResultadoDiario.setText(
                        "── Informe día " + fecha + " ──\n" +
                                "Total ingresos: " + String.format("%.2f", ingresos) + " €\n" +
                                "Total gastos:   " + String.format("%.2f", gastos) + " €\n" +
                                "Balance:        " + String.format("%.2f", balance) + " €"
                );
                tvResultadoDiario.setVisibility(View.VISIBLE);
            });
        });
    }

    /**
     * GENERARINFORMESEMANAL
     * Consulta el total de ingresos y gastos entre dos fechas.
     */
    private void generarInformeSemanal() {
        String desde = etSemanaDesde.getText().toString().trim();
        String hasta = etSemanaHasta.getText().toString().trim();

        if (desde.isEmpty() || hasta.isEmpty()) {
            tvResultadoSemanal.setText("Por favor introduce las dos fechas");
            tvResultadoSemanal.setVisibility(View.VISIBLE);
            return;
        }

        ingresoRepository.getTotalIngresosByRangoFechas(desde, hasta).observe(this, totalIngresos -> {
            gastoRepository.getTotalGastosByRangoFechas(desde, hasta).observe(this, totalGastos -> {
                double ingresos = totalIngresos != null ? totalIngresos : 0.0;
                double gastos = totalGastos != null ? totalGastos : 0.0;
                double balance = ingresos - gastos;

                tvResultadoSemanal.setText(
                        "── Informe " + desde + " → " + hasta + " ──\n" +
                                "Total ingresos: " + String.format("%.2f", ingresos) + " €\n" +
                                "Total gastos:   " + String.format("%.2f", gastos) + " €\n" +
                                "Balance:        " + String.format("%.2f", balance) + " €"
                );
                tvResultadoSemanal.setVisibility(View.VISIBLE);
            });
        });
    }

    /**
     * GENERARINFORMEMENSUAL
     * Consulta el total de ingresos y gastos de un mes concreto.
     */
    private void generarInformeMensual() {
        String mes = etMes.getText().toString().trim();

        if (mes.isEmpty()) {
            tvResultadoMensual.setText("Por favor introduce un mes");
            tvResultadoMensual.setVisibility(View.VISIBLE);
            return;
        }

        ingresoRepository.getTotalIngresosByMes(mes).observe(this, totalIngresos -> {
            gastoRepository.getTotalGastosByMes(mes).observe(this, totalGastos -> {
                double ingresos = totalIngresos != null ? totalIngresos : 0.0;
                double gastos = totalGastos != null ? totalGastos : 0.0;
                double balance = ingresos - gastos;

                tvResultadoMensual.setText(
                        "── Informe mes " + mes + " ──\n" +
                                "Total ingresos: " + String.format("%.2f", ingresos) + " €\n" +
                                "Total gastos:   " + String.format("%.2f", gastos) + " €\n" +
                                "Balance:        " + String.format("%.2f", balance) + " €"
                );
                tvResultadoMensual.setVisibility(View.VISIBLE);
            });
        });
    }

    /**
     * GENERARINFORMECATEGORIA
     * Consulta el total de gastos de la categoría seleccionada en el spinner.
     */
    private void generarInformeCategoria() {
        if (listaCategorias.isEmpty()) {
            tvResultadoCategoria.setText("No hay categorías disponibles");
            tvResultadoCategoria.setVisibility(View.VISIBLE);
            return;
        }

        // Cogemos la categoría seleccionada en el spinner
        int idCategoria = listaCategorias
                .get(spinnerCategoriaInforme.getSelectedItemPosition())
                .getId_categoria();
        String nombreCategoria = listaCategorias
                .get(spinnerCategoriaInforme.getSelectedItemPosition())
                .getNombre();

        // Consultamos todos los gastos de esa categoría
        gastoRepository.getGastosByCategoria(idCategoria).observe(this, gastos -> {
            double total = 0.0;
            // Sumamos el importe de todos los gastos de esa categoría
            for (com.gemma.esterapp.model.Gasto gasto : gastos) {
                total += gasto.getImporte();
            }

            tvResultadoCategoria.setText(
                    "── Informe categoría: " + nombreCategoria + " ──\n" +
                            "Total gastos: " + String.format("%.2f", total) + " €"
            );
            tvResultadoCategoria.setVisibility(View.VISIBLE);
        });
    }
}