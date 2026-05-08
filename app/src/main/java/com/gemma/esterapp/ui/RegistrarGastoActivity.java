package com.gemma.esterapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Gasto;
import com.gemma.esterapp.repository.CategoriaRepository;
import com.gemma.esterapp.repository.GastoRepository;
import com.gemma.esterapp.repository.SubcategoriaRepository;

import java.util.Calendar;

/**
 * REGISTRARGASTOACTIVITY
 * Pantalla para registrar un nuevo gasto o editarlo desde InformesActivity.
 * Las categorías se seleccionan con 6 botones en cuadrícula (no Spinners).
 * Al seleccionar una categoría con subcategorías aparece un GridLayout dinámico.
 * Impuestos, Salarios y Otros no tienen subcategorías.
 * Si se selecciona Otros la descripción es obligatoria.
 */
public class RegistrarGastoActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_registrar_gasto.xml
    private EditText etFecha;               // campo de fecha — abre DatePickerDialog al pulsar
    private EditText etImporte;             // campo importe en euros
    private EditText etNotas;         // descripción opcional (obligatoria si Otros)
    private Button btnGuardarGasto;         // guarda o actualiza el gasto
    private TextView tvMensaje;             // mensaje de error o confirmación
    private TextView tvLabelSubcategoria;   // etiqueta "Subcategoría" (oculta si no aplica)
    private GridLayout gridSubcategorias;   // cuadrícula de botones de subcategoría (dinámica)
    private ImageButton iconoHome;          // navega al menú principal
    private ImageButton iconoInformes;      // navega a InformesActivity
    private Calendar calendario;            // gestiona la fecha seleccionada

    // Botones de categoría — uno por cada categoría del negocio
    private Button btnCatArticulos;   // id=1 Proveedores de materiales
    private Button btnCatServicios;   // id=2 Proveedores de servicios
    private Button btnCatImpuestos;   // id=3 sin subcategorías
    private Button btnCatSalarios;    // id=4 sin subcategorías
    private Button btnCatVehiculos;   // id=5
    private Button btnCatOtros;       // id=0 sin subcategorías, descripción obligatoria

    // Repositorios para acceder a la BD
    private GastoRepository gastoRepository;
    private CategoriaRepository categoriaRepository;
    private SubcategoriaRepository subcategoriaRepository;

    // Datos del usuario y modo de la pantalla
    private int idUsuario;                              // id del usuario que registra
    private boolean modoEdicion = false;                // true = estamos editando un gasto existente
    private int idGastoEditar = -1;                     // id del gasto a editar (-1 si es nuevo)
    private Gasto gastoEditar = null;                   // objeto gasto cargado para editar

    // Estado de la selección de categoría y subcategoría
    private int idCategoriaSeleccionada = -1;           // -1 = ninguna seleccionada
    private int idSubcategoriaSeleccionada = -1;        // -1 = ninguna, 0 = no necesita
    private String nombreCategoriaSeleccionada = "";
    private String nombreSubcategoriaSeleccionada = "";
    private boolean categoriaEsOtros = false;           // true = categoría Otros seleccionada
    private boolean subcategoriaEsOtros = false;        // true = subcategoría Otros seleccionada

    // Flag para evitar que cargarGastoEditar() se ejecute más de una vez
    // LiveData puede volver a emitir datos y recargaría el formulario innecesariamente
    private boolean gastoEditorCargado = false;

    // Constantes con los ids de las categorías — deben coincidir con DatosIniciales
    private static final int ID_CAT_ARTICULOS = 1;
    private static final int ID_CAT_SERVICIOS = 2;
    private static final int ID_CAT_IMPUESTOS = 3;
    private static final int ID_CAT_SALARIOS  = 4;
    private static final int ID_CAT_VEHICULOS = 5;
    private static final int ID_CAT_OTROS     = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_gasto);

        // Conectamos variables Java con elementos del XML
        etFecha = findViewById(R.id.etFecha);
        etImporte = findViewById(R.id.etImporte);
        etNotas = findViewById(R.id.etNotas);
        btnGuardarGasto = findViewById(R.id.btnGuardarGasto);
        tvMensaje = findViewById(R.id.tvMensaje);
        tvLabelSubcategoria = findViewById(R.id.tvLabelSubcategoria);
        gridSubcategorias = findViewById(R.id.gridSubcategorias);
        iconoHome = findViewById(R.id.iconoHome);
        iconoInformes = findViewById(R.id.iconoInformes);

        btnCatArticulos = findViewById(R.id.btnCatArticulos);
        btnCatServicios = findViewById(R.id.btnCatServicios);
        btnCatImpuestos = findViewById(R.id.btnCatImpuestos);
        btnCatSalarios  = findViewById(R.id.btnCatSalarios);
        btnCatVehiculos = findViewById(R.id.btnCatVehiculos);
        btnCatOtros     = findViewById(R.id.btnCatOtros);

        gastoRepository = new GastoRepository(getApplication());
        categoriaRepository = new CategoriaRepository(getApplication());
        subcategoriaRepository = new SubcategoriaRepository(getApplication());

        // Recuperamos el id del usuario y el modo (nuevo o edición)
        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);
        idGastoEditar = getIntent().getIntExtra("id_gasto", -1);

        // El campo fecha no es editable a mano — al pulsar abre el DatePickerDialog
        calendario = Calendar.getInstance(); // fecha de hoy por defecto
        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        actualizarFecha(); // muestra la fecha de hoy en el campo

        // Si venimos de InformesActivity en modo edición cargamos los datos del gasto
        if (modoEdicion) {
            btnGuardarGasto.setText("Actualizar Gasto");
            cargarGastoEditar();
        }

        // Cada botón de categoría llama a seleccionarCategoria() con su id y nombre
        btnCatArticulos.setOnClickListener(v ->
                seleccionarCategoria(ID_CAT_ARTICULOS, "Artículos", false));
        btnCatServicios.setOnClickListener(v ->
                seleccionarCategoria(ID_CAT_SERVICIOS, "Servicios", false));
        btnCatImpuestos.setOnClickListener(v ->
                seleccionarCategoria(ID_CAT_IMPUESTOS, "Impuestos", false));
        btnCatSalarios.setOnClickListener(v ->
                seleccionarCategoria(ID_CAT_SALARIOS, "Salarios", false));
        btnCatVehiculos.setOnClickListener(v ->
                seleccionarCategoria(ID_CAT_VEHICULOS, "Vehículos", false));
        btnCatOtros.setOnClickListener(v ->
                seleccionarCategoria(ID_CAT_OTROS, "Otros", true));

        btnGuardarGasto.setOnClickListener(v -> guardarGasto());

        iconoHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarGastoActivity.this, MenuActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
            finish();
        });

        iconoInformes.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarGastoActivity.this, InformesActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
            finish();
        });
    }

    /**
     * SELECCIONARCATEGORIA
     * Pone en azul el botón seleccionado y en gris los demás.
     * Si la categoría tiene subcategorías carga el GridLayout dinámico desde la BD.
     * Impuestos, Salarios y Otros no tienen subcategorías — marcamos idSub=0 directamente.
     */
    private void seleccionarCategoria(int idCategoria, String nombre, boolean esOtros) {
        // Guardamos la selección actual
        idCategoriaSeleccionada = idCategoria;
        nombreCategoriaSeleccionada = nombre;
        categoriaEsOtros = esOtros;

        // Resetamos la subcategoría al cambiar de categoría
        idSubcategoriaSeleccionada = -1;
        nombreSubcategoriaSeleccionada = "";
        subcategoriaEsOtros = false;

        // Ponemos todos los botones de categoría en gris antes de resaltar el seleccionado
        ponerTodosCategoriaGris();
        gridSubcategorias.setVisibility(View.GONE);
        tvLabelSubcategoria.setVisibility(View.GONE);

        // Categoría Otros: azul, no necesita subcategoría
        if (esOtros) {
            btnCatOtros.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF0288D1));
            idSubcategoriaSeleccionada = 0; // 0 = no aplica subcategoría
            return;
        }

        // Ponemos en azul el botón de la categoría seleccionada
        Button[] botones = {btnCatArticulos, btnCatServicios, btnCatImpuestos,
                btnCatSalarios, btnCatVehiculos};
        int[] ids = {ID_CAT_ARTICULOS, ID_CAT_SERVICIOS, ID_CAT_IMPUESTOS,
                ID_CAT_SALARIOS, ID_CAT_VEHICULOS};
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == idCategoria) {
                botones[i].setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFF0288D1));
                break;
            }
        }

        // Impuestos y Salarios no tienen subcategorías — marcamos 0 y salimos
        if (idCategoria == ID_CAT_IMPUESTOS || idCategoria == ID_CAT_SALARIOS) {
            idSubcategoriaSeleccionada = 0;
            return;
        }

        // Para el resto de categorías cargamos las subcategorías desde la BD
        subcategoriaRepository.getSubcategoriasByCategoria(idCategoria).observe(this, subcategorias -> {
            gridSubcategorias.removeAllViews(); // limpiamos botones anteriores
            gridSubcategorias.setColumnCount(3); // 3 columnas

            // Creamos un botón por cada subcategoría devuelta por la BD
            for (com.gemma.esterapp.model.Subcategoria sub : subcategorias) {
                Button btn = new Button(this);
                btn.setText(sub.getNombre());
                btn.setTextSize(11);

                // Configuramos el tamaño del botón en el GridLayout
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // reparte el ancho
                params.width = 0;
                params.height = 160;
                params.setMargins(8, 8, 8, 8);
                btn.setLayoutParams(params);
                btn.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // gris por defecto

                // Al pulsar: guardamos la subcategoría y ponemos este botón en azul
                btn.setOnClickListener(v -> {
                    idSubcategoriaSeleccionada = sub.getId_subcategoria();
                    nombreSubcategoriaSeleccionada = sub.getNombre();
                    subcategoriaEsOtros = false;
                    // Ponemos todos en gris primero
                    for (int i = 0; i < gridSubcategorias.getChildCount(); i++) {
                        View child = gridSubcategorias.getChildAt(i);
                        if (child instanceof Button) {
                            ((Button) child).setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(0xFFCCCCCC));
                        }
                    }
                    btn.setBackgroundTintList( // azul el seleccionado
                            android.content.res.ColorStateList.valueOf(0xFF0288D1));
                });

                gridSubcategorias.addView(btn);
            }

            // Botón "Otros" siempre al final del grid — descripción obligatoria si se elige
            Button btnOtrosSub = new Button(this);
            btnOtrosSub.setText("Otros");
            btnOtrosSub.setTextSize(11);
            GridLayout.LayoutParams paramsOtros = new GridLayout.LayoutParams();
            paramsOtros.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            paramsOtros.width = 0;
            paramsOtros.height = 160;
            paramsOtros.setMargins(8, 8, 8, 8);
            btnOtrosSub.setLayoutParams(paramsOtros);
            btnOtrosSub.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFCCCCCC));

            btnOtrosSub.setOnClickListener(v -> {
                idSubcategoriaSeleccionada = 0;
                nombreSubcategoriaSeleccionada = "Otros";
                subcategoriaEsOtros = true; // activa la validación de descripción obligatoria
                for (int i = 0; i < gridSubcategorias.getChildCount(); i++) {
                    View child = gridSubcategorias.getChildAt(i);
                    if (child instanceof Button) {
                        ((Button) child).setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(0xFFCCCCCC));
                    }
                }
                btnOtrosSub.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFF0288D1));
            });

            gridSubcategorias.addView(btnOtrosSub);
            tvLabelSubcategoria.setVisibility(View.VISIBLE);
            gridSubcategorias.setVisibility(View.VISIBLE); // mostramos el grid
        });
    }

    // Pone todos los botones de categoría en gris — se llama antes de resaltar el seleccionado
    private void ponerTodosCategoriaGris() {
        int gris = 0xFFCCCCCC;
        btnCatArticulos.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gris));
        btnCatServicios.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gris));
        btnCatImpuestos.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gris));
        btnCatSalarios.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gris));
        btnCatVehiculos.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gris));
        btnCatOtros.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gris));
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
     * CARGARGASTOEDITAR
     * Carga los datos del gasto a editar en el formulario.
     * El flag gastoEditorCargado evita que LiveData recargue el formulario
     * si emite de nuevo (lo que podría sobreescribir cambios del usuario).
     */
    private void cargarGastoEditar() {
        if (gastoEditorCargado) return; // ya cargado, no volvemos a cargar
        gastoEditorCargado = true;

        gastoRepository.getAllGastos().observe(this, gastos -> {
            for (Gasto gasto : gastos) {
                if (gasto.getId_gasto() == idGastoEditar) {
                    gastoEditar = gasto;

                    // Rellenamos el formulario con los datos del gasto
                    etImporte.setText(String.valueOf(gasto.getImporte()));
                    String[] partes = gasto.getFecha().split("-");
                    etFecha.setText(partes[2] + "/" + partes[1] + "/" + partes[0]);
                    etNotas.setText(gasto.getNotas());

                    // Seleccionamos la categoría correcta y cargamos sus subcategorías
                    String nombreCat = obtenerNombreCategoria(gasto.getId_categoria());
                    boolean esOtros = (gasto.getId_categoria() == ID_CAT_OTROS);
                    seleccionarCategoria(gasto.getId_categoria(), nombreCat, esOtros);
                    break;
                }
            }
        });
    }

    // Devuelve el nombre de la categoría según su id — usado en cargarGastoEditar()
    private String obtenerNombreCategoria(int idCat) {
        switch (idCat) {
            case ID_CAT_ARTICULOS: return "Artículos";
            case ID_CAT_SERVICIOS: return "Servicios";
            case ID_CAT_IMPUESTOS: return "Impuestos";
            case ID_CAT_SALARIOS:  return "Salarios";
            case ID_CAT_VEHICULOS: return "Vehículos";
            default:               return "Otros";
        }
    }

    /**
     * GUARDARGASTO
     * Valida todos los campos y guarda o actualiza el gasto en la BD.
     * Subcategoría solo es obligatoria para Artículos, Servicios y Vehículos.
     * Descripción solo es obligatoria si la categoría o subcategoría es Otros.
     */
    private void guardarGasto() {
        String importeTexto = etImporte.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();

        // Importe y fecha son siempre obligatorios
        if (importeTexto.isEmpty() || fecha.isEmpty()) {
            tvMensaje.setText("Por favor rellena el importe y la fecha");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // Debe haberse seleccionado una categoría
        if (idCategoriaSeleccionada == -1) {
            tvMensaje.setText("Por favor selecciona una categoría");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // Subcategoría obligatoria solo para Artículos, Servicios y Vehículos
        boolean necesitaSubcategoria = (idCategoriaSeleccionada != ID_CAT_IMPUESTOS
                && idCategoriaSeleccionada != ID_CAT_SALARIOS
                && idCategoriaSeleccionada != ID_CAT_OTROS);

        if (necesitaSubcategoria && idSubcategoriaSeleccionada == -1) {
            tvMensaje.setText("Por favor selecciona una subcategoría");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        // Descripción obligatoria si se seleccionó Otros (categoría o subcategoría)
        if ((categoriaEsOtros || subcategoriaEsOtros) && notas.isEmpty()) {
            tvMensaje.setText("Por favor escribe una descripción para Otros");
            tvMensaje.setTextColor(0xFFE74C3C);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        double importe = Double.parseDouble(importeTexto);
        int idCat = idCategoriaSeleccionada;                                    // id real de la categoría
        int idSub = (idSubcategoriaSeleccionada == -1) ? 0 : idSubcategoriaSeleccionada; // 0 si no aplica

        if (modoEdicion && gastoEditar != null) {
            // Actualizamos el gasto existente y cerramos la pantalla
            gastoEditar.setImporte(importe);
            gastoEditar.setFecha(convertirFechaParaBD(fecha));
            gastoEditar.setNotas(notas);
            gastoEditar.setId_categoria(idCat);
            gastoEditar.setId_subcategoria(idSub);
            gastoRepository.update(gastoEditar);
            finish(); // volvemos a InformesActivity
            return;
        }

        // Creamos un nuevo gasto y lo insertamos en la BD
        Gasto gasto = new Gasto();
        gasto.setImporte(importe);
        gasto.setFecha(convertirFechaParaBD(fecha));
        gasto.setNotas(notas);
        gasto.setId_categoria(idCat);
        gasto.setId_subcategoria(idSub);
        gasto.setId_usuario(idUsuario); // quién registra el gasto

        gastoRepository.insert(gasto);

        // Confirmación y reseteo del formulario para registrar otro gasto
        tvMensaje.setText("Gasto guardado correctamente");
        tvMensaje.setTextColor(0xFF27AE60);
        tvMensaje.setVisibility(View.VISIBLE);

        etImporte.setText("");
        etNotas.setText("");
        actualizarFecha();                              // reseteamos la fecha a hoy
        ponerTodosCategoriaGris();                      // todos los botones a gris
        gridSubcategorias.setVisibility(View.GONE);
        tvLabelSubcategoria.setVisibility(View.GONE);
        idCategoriaSeleccionada = -1;
        idSubcategoriaSeleccionada = -1;
        categoriaEsOtros = false;
        subcategoriaEsOtros = false;
    }
}