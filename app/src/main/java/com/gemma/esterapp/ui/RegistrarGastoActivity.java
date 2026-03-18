package com.gemma.esterapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Categoria;
import com.gemma.esterapp.model.Gasto;
import com.gemma.esterapp.model.Subcategoria;
import com.gemma.esterapp.repository.CategoriaRepository;
import com.gemma.esterapp.repository.GastoRepository;
import com.gemma.esterapp.repository.SubcategoriaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * REGISTRARGASTOACTIVITY
 * Pantalla para registrar un nuevo gasto.
 * El usuario rellena importe, fecha, descripción, categoría y subcategoría.
 * Al pulsar Guardar se inserta el gasto en la base de datos.
 */
public class RegistrarGastoActivity extends AppCompatActivity {

    // Elementos visuales del XML activity_registrar_gasto.xml
    private EditText etImporte;
    private EditText etFecha;
    private EditText etDescripcion;
    private Spinner spinnerCategoria;
    private Spinner spinnerSubcategoria;
    private Button btnGuardarGasto;
    private Button btnVolver;
    private TextView tvMensaje;

    // Repositorios para acceder a la base de datos
    private GastoRepository gastoRepository;
    private CategoriaRepository categoriaRepository;
    private SubcategoriaRepository subcategoriaRepository;

    // Listas para guardar las categorías y subcategorías cargadas de la BD
    private List<Categoria> listaCategorias = new ArrayList<>();
    private List<Subcategoria> listaSubcategorias = new ArrayList<>();

    // Id del usuario que ha iniciado sesión
    private int idUsuario;

    // Id de la categoría seleccionada en el spinner
    private int idCategoriaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Le decimos a Android qué XML usar como diseño de esta pantalla
        setContentView(R.layout.activity_registrar_gasto);

        // Conectamos cada variable Java con su elemento del XML por su id
        etImporte = findViewById(R.id.etImporte);
        etFecha = findViewById(R.id.etFecha);
        etDescripcion = findViewById(R.id.etDescripcion);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerSubcategoria = findViewById(R.id.spinnerSubcategoria);
        btnGuardarGasto = findViewById(R.id.btnGuardarGasto);
        btnVolver = findViewById(R.id.btnVolver);
        tvMensaje = findViewById(R.id.tvMensaje);

        // Creamos los repositorios para poder consultar la base de datos
        gastoRepository = new GastoRepository(getApplication());
        categoriaRepository = new CategoriaRepository(getApplication());
        subcategoriaRepository = new SubcategoriaRepository(getApplication());

        // Cogemos el id del usuario que nos pasó MenuActivity
        idUsuario = getIntent().getIntExtra("id_usuario", -1);

        // Cargamos las categorías en el spinner
        cargarCategorias();

        // Programamos qué pasa cuando el usuario selecciona una categoría
        // Se cargan las subcategorías de esa categoría automáticamente
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Guardamos la categoría seleccionada y cargamos sus subcategorías
                idCategoriaSeleccionada = listaCategorias.get(position).getId_categoria();
                cargarSubcategorias(idCategoriaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacemos nada si no se selecciona nada
            }
        });

        // Botón Guardar → guarda el gasto en la base de datos
        btnGuardarGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarGasto();
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
     * CARGARCATEGORIAS
     * Obtiene todas las categorías de la base de datos
     * y las muestra en el spinner de categorías.
     */
    private void cargarCategorias() {
        categoriaRepository.getAllCategorias().observe(this, categorias -> {
            // Guardamos la lista de categorías para usarla después
            listaCategorias = categorias;

            // Creamos una lista con solo los nombres para mostrar en el spinner
            List<String> nombresCategorias = new ArrayList<>();
            for (Categoria categoria : categorias) {
                nombresCategorias.add(categoria.getNombre());
            }

            // ArrayAdapter conecta la lista de nombres con el spinner
            // android.R.layout.simple_spinner_item es el diseño por defecto de Android
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    RegistrarGastoActivity.this,
                    android.R.layout.simple_spinner_item,
                    nombresCategorias);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapter);
        });
    }

    /**
     * CARGARSUBCATEGORIAS
     * Obtiene las subcategorías de la categoría seleccionada
     * y las muestra en el spinner de subcategorías.
     *
     * @param idCategoria El id de la categoría seleccionada
     */
    private void cargarSubcategorias(int idCategoria) {
        subcategoriaRepository.getSubcategoriasByCategoria(idCategoria).observe(this, subcategorias -> {
            // Guardamos la lista de subcategorías para usarla después
            listaSubcategorias = subcategorias;

            // Creamos una lista con solo los nombres para mostrar en el spinner
            List<String> nombresSubcategorias = new ArrayList<>();
            for (Subcategoria subcategoria : subcategorias) {
                nombresSubcategorias.add(subcategoria.getNombre());
            }

            // Conectamos la lista de nombres con el spinner de subcategorías
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    RegistrarGastoActivity.this,
                    android.R.layout.simple_spinner_item,
                    nombresSubcategorias);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSubcategoria.setAdapter(adapter);
        });
    }

    /**
     * GUARDARGASTO
     * Valida los campos y guarda el gasto en la base de datos.
     */
    private void guardarGasto() {

        // Cogemos el texto de los campos
        String importeTexto = etImporte.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        // Comprobamos que los campos obligatorios no estén vacíos
        if (importeTexto.isEmpty() || fecha.isEmpty()) {
            tvMensaje.setText("Por favor rellena el importe y la fecha");
            tvMensaje.setTextColor(0xFFE74C3C); // Rojo
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // Comprobamos que haya categorías y subcategorías cargadas
        if (listaCategorias.isEmpty() || listaSubcategorias.isEmpty()) {
            tvMensaje.setText("No hay categorías disponibles");
            tvMensaje.setTextColor(0xFFE74C3C); // Rojo
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // Convertimos el importe de String a double
        double importe = Double.parseDouble(importeTexto);

        // Cogemos el id de la subcategoría seleccionada en el spinner
        int idSubcategoriaSeleccionada = listaSubcategorias
                .get(spinnerSubcategoria.getSelectedItemPosition())
                .getId_subcategoria();

        // Creamos el objeto Gasto con todos los datos
        Gasto gasto = new Gasto();
        gasto.setImporte(importe);
        gasto.setFecha(fecha);
        gasto.setDescripcion(descripcion);
        gasto.setId_categoria(idCategoriaSeleccionada);
        gasto.setId_subcategoria(idSubcategoriaSeleccionada);
        gasto.setId_usuario(idUsuario);

        // Insertamos el gasto en la base de datos a través del repositorio
        gastoRepository.insert(gasto);

        // Mostramos mensaje de confirmación
        tvMensaje.setText("Gasto guardado correctamente");
        tvMensaje.setTextColor(0xFF27AE60); // Verde
        tvMensaje.setVisibility(View.VISIBLE);

        // Limpiamos los campos para poder registrar otro gasto
        etImporte.setText("");
        etFecha.setText("");
        etDescripcion.setText("");
    }
}