package com.gemma.esterapp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.gemma.esterapp.R;
import com.gemma.esterapp.model.Gasto;
import com.gemma.esterapp.model.Ingreso;
import com.gemma.esterapp.model.Usuario;
import com.gemma.esterapp.repository.CategoriaRepository;
import com.gemma.esterapp.repository.GastoRepository;
import com.gemma.esterapp.repository.IngresoRepository;
import com.gemma.esterapp.repository.SubcategoriaRepository;
import com.gemma.esterapp.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * INFORMESACTIVITY
 * Pantalla principal de consulta financiera. Funciona como el extracto de un banco.
 * Muestra totales de ingresos, gastos y balance del mes seleccionado.
 * Lista todos los movimientos del mes ordenados por fecha descendente.
 * Gastos en rojo, ingresos en verde, con categoría y nombre del usuario.
 * Administrador puede modificar y eliminar cualquier movimiento.
 * Gerente y Trabajador solo pueden modificar sus propios movimientos.
 */
public class InformesActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_informes.xml
    private Spinner spinnerAnio;            // selector de año (últimos 5 años)
    private Spinner spinnerMes;             // selector de mes (Enero...Diciembre)
    private TextView tvTotalIngresos;       // total ingresos del mes en verde
    private TextView tvTotalGastos;         // total gastos del mes en rojo
    private TextView tvBalance;             // balance = ingresos - gastos
    private Button btnGenerarCSV;           // abre GenerarInformeActivity (Admin y Gerente)
    private ListView listViewMovimientos;   // lista de movimientos del mes
    private ImageButton iconoHome;
    private ImageButton iconoInformes;

    // Repositorios — necesitamos 5 porque la lista mezcla datos de varias tablas
    private GastoRepository gastoRepository;
    private IngresoRepository ingresoRepository;
    private UsuarioRepository usuarioRepository;
    private CategoriaRepository categoriaRepository;
    private SubcategoriaRepository subcategoriaRepository;

    // Id y rol del usuario que ha iniciado sesión
    private int idUsuario;
    private String rolUsuario;

    // Lista de movimientos mostrada en el ListView
    private List<Movimiento> listaMovimientos = new ArrayList<>();

    // Mapas para traducir ids a nombres legibles en la lista
    // Se cargan una vez al abrir la pantalla y se reutilizan para cada movimiento
    private Map<Integer, String> mapaUsuarios = new HashMap<>();      // id → nombre usuario
    private Map<Integer, String> mapaCategorias = new HashMap<>();    // id → nombre categoría
    private Map<Integer, String> mapaSubcategorias = new HashMap<>(); // id → nombre subcategoría

    // Referencias a los LiveData activos — guardadas para poder quitar observadores
    // al cambiar de mes y evitar que se acumulen observadores duplicados
    private LiveData<List<Gasto>> liveGastos = null;
    private LiveData<List<Ingreso>> liveIngresos = null;

    // Flag para no activar los spinners hasta que los tres mapas estén cargados
    // Sin este flag los movimientos se mostrarían sin nombres de categoría/usuario
    private boolean datosAuxiliaresListos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);

        // Recuperamos id y rol del usuario desde MenuActivity
        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        rolUsuario = getIntent().getStringExtra("rol_usuario");

        // Conectamos variables Java con elementos del XML
        spinnerAnio = findViewById(R.id.spinnerAnio);
        spinnerMes = findViewById(R.id.spinnerMes);
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos);
        tvTotalGastos = findViewById(R.id.tvTotalGastos);
        tvBalance = findViewById(R.id.tvBalance);
        btnGenerarCSV = findViewById(R.id.btnGenerarCSV);
        listViewMovimientos = findViewById(R.id.listViewMovimientos);
        iconoHome = findViewById(R.id.iconoHome);
        iconoInformes = findViewById(R.id.iconoInformes);

        gastoRepository = new GastoRepository(getApplication());
        ingresoRepository = new IngresoRepository(getApplication());
        usuarioRepository = new UsuarioRepository(getApplication());
        categoriaRepository = new CategoriaRepository(getApplication());
        subcategoriaRepository = new SubcategoriaRepository(getApplication());

        // El botón Generar CSV solo es visible para Administrador y Gerente
        // Trabajador (Francesca) no puede generar informes
        if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) {
            btnGenerarCSV.setVisibility(View.VISIBLE);
        }

        // Cargamos los tres mapas en paralelo con LiveData
        // Cada observador llama a intentarActivarSpinners() al terminar
        // Los spinners solo se activan cuando los TRES mapas tienen datos
        usuarioRepository.getAllUsuarios().observe(this, usuarios -> {
            mapaUsuarios.clear();
            for (Usuario usuario : usuarios) {
                mapaUsuarios.put(usuario.getId_usuario(), usuario.getUsuario());
            }
            intentarActivarSpinners();
        });

        categoriaRepository.getAllCategorias().observe(this, categorias -> {
            mapaCategorias.clear();
            for (com.gemma.esterapp.model.Categoria cat : categorias) {
                mapaCategorias.put(cat.getId_categoria(), cat.getNombre());
            }
            intentarActivarSpinners();
        });

        subcategoriaRepository.getAllSubcategorias().observe(this, subcategorias -> {
            mapaSubcategorias.clear();
            for (com.gemma.esterapp.model.Subcategoria sub : subcategorias) {
                mapaSubcategorias.put(sub.getId_subcategoria(), sub.getNombre());
            }
            intentarActivarSpinners();
        });

        // Botón Generar CSV → abre GenerarInformeActivity
        btnGenerarCSV.setOnClickListener(v -> {
            Intent intent = new Intent(InformesActivity.this, GenerarInformeActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("rol_usuario", rolUsuario);
            startActivity(intent);
        });

        iconoHome.setOnClickListener(v -> {
            Intent intent = new Intent(InformesActivity.this, MenuActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
            finish();
        });

        iconoInformes.setOnClickListener(v -> {
            Intent intent = new Intent(InformesActivity.this, InformesActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("rol_usuario", rolUsuario);
            startActivity(intent);
            finish();
        });
    }

    /**
     * INTENTARACTIVARSPINNERS
     * Solo activa los spinners cuando los tres mapas tienen datos.
     * Si ya están activos, simplemente refresca los datos.
     * Esto garantiza que nunca se muestra "?" en lugar del nombre de categoría o usuario.
     */
    private void intentarActivarSpinners() {
        // Si algún mapa está vacío todavía no podemos mostrar los movimientos correctamente
        if (mapaUsuarios.isEmpty() || mapaCategorias.isEmpty() || mapaSubcategorias.isEmpty()) {
            return;
        }

        if (datosAuxiliaresListos) {
            // Los spinners ya están configurados — solo refrescamos los datos
            actualizarTotales();
            cargarMovimientos();
            return;
        }

        // Primera vez que los tres mapas están listos — configuramos los spinners
        datosAuxiliaresListos = true;
        cargarSpinnerAnio();
        cargarSpinnerMes(); // este llama a setSelection() que dispara onItemSelected()

        // Cada vez que el usuario cambia el año o el mes recargamos todo
        spinnerAnio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarTotales();
                cargarMovimientos();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarTotales();
                cargarMovimientos();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Al volver de RegistrarGastoActivity o RegistrarIngresoActivity refrescamos la lista
    @Override
    protected void onResume() {
        super.onResume();
        if (datosAuxiliaresListos
                && spinnerAnio.getSelectedItem() != null
                && spinnerMes.getSelectedItem() != null) {
            actualizarTotales();
            cargarMovimientos();
        }
    }

    // Carga el spinner de año con los últimos 5 años (año actual primero)
    private void cargarSpinnerAnio() {
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        String[] anios = new String[5];
        for (int i = 0; i < 5; i++) {
            anios[i] = String.valueOf(anioActual - i); // 2026, 2025, 2024...
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, anios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnio.setAdapter(adapter);
    }

    // Carga el spinner de mes con los 12 meses y selecciona el mes actual
    private void cargarSpinnerMes() {
        List<String> meses = Arrays.asList(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, meses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapter);
        spinnerMes.setSelection(Calendar.getInstance().get(Calendar.MONTH)); // mes actual
    }

    // Devuelve el mes seleccionado en formato YYYY-MM para las consultas SQL
    private String getMesSeleccionado() {
        String anio = spinnerAnio.getSelectedItem().toString();
        int posicionMes = spinnerMes.getSelectedItemPosition() + 1; // +1 porque la lista empieza en 0
        return anio + "-" + String.format("%02d", posicionMes); // ej: "2026-03"
    }

    // Calcula y muestra los totales de ingresos, gastos y balance del mes seleccionado
    private void actualizarTotales() {
        String mes = getMesSeleccionado();
        ingresoRepository.getTotalIngresosByMes(mes).observe(this, totalIngresos -> {
            gastoRepository.getTotalGastosByMes(mes).observe(this, totalGastos -> {
                double ingresos = totalIngresos != null ? totalIngresos : 0.0;
                double gastos = totalGastos != null ? totalGastos : 0.0;
                double balance = ingresos - gastos;

                tvTotalIngresos.setText("+" + String.format("%.2f", ingresos) + " €");
                tvTotalIngresos.setTextColor(0xFF27AE60); // verde

                tvTotalGastos.setText("-" + String.format("%.2f", gastos) + " €");
                tvTotalGastos.setTextColor(0xFFE74C3C); // rojo

                // Balance en verde si positivo, rojo si negativo
                if (balance >= 0) {
                    tvBalance.setText("+" + String.format("%.2f", balance) + " €");
                    tvBalance.setTextColor(0xFF27AE60);
                } else {
                    tvBalance.setText(String.format("%.2f", balance) + " €");
                    tvBalance.setTextColor(0xFFE74C3C);
                }
            });
        });
    }

    /**
     * CARGARMOVIMIENTOS
     * Consulta gastos e ingresos del mes, los mezcla en una lista de Movimiento,
     * los ordena por fecha descendente y los muestra en el ListView.
     * Elimina los observadores anteriores antes de crear nuevos para evitar duplicados.
     */
    private void cargarMovimientos() {
        String mes = getMesSeleccionado();

        // Eliminamos observadores anteriores para no acumular callbacks al cambiar de mes
        if (liveGastos != null) liveGastos.removeObservers(this);
        if (liveIngresos != null) liveIngresos.removeObservers(this);

        liveGastos = gastoRepository.getGastosByMes(mes);
        liveIngresos = ingresoRepository.getIngresosByMes(mes);

        liveGastos.observe(this, gastos -> {
            liveIngresos.observe(this, ingresos -> {
                listaMovimientos = new ArrayList<>();

                // Convertimos cada Gasto en un Movimiento con los nombres resueltos
                for (Gasto gasto : gastos) {
                    String nombre = mapaUsuarios.containsKey(gasto.getId_usuario())
                            ? mapaUsuarios.get(gasto.getId_usuario()) : "?";
                    String nombreCat = mapaCategorias.containsKey(gasto.getId_categoria())
                            ? mapaCategorias.get(gasto.getId_categoria()) : "";
                    String nombreSub = mapaSubcategorias.containsKey(gasto.getId_subcategoria())
                            ? mapaSubcategorias.get(gasto.getId_subcategoria()) : "";

                    listaMovimientos.add(new Movimiento(
                            "gasto",
                            convertirFecha(gasto.getFecha()),
                            gasto.getImporte(),
                            gasto.getNotas(),
                            nombre,
                            gasto.getId_gasto(),
                            gasto.getId_usuario(),
                            nombreCat,
                            nombreSub
                    ));
                }

                // Convertimos cada Ingreso en un Movimiento (sin categoría)
                for (Ingreso ingreso : ingresos) {
                    String nombre = mapaUsuarios.containsKey(ingreso.getId_usuario())
                            ? mapaUsuarios.get(ingreso.getId_usuario()) : "?";

                    listaMovimientos.add(new Movimiento(
                            "ingreso",
                            convertirFecha(ingreso.getFecha()),
                            ingreso.getImporte(),
                            ingreso.getTipoingreso(), // "Efectivo" o "Tarjeta"
                            nombre,
                            ingreso.getId_ingreso(),
                            ingreso.getId_usuario(),
                            "", // ingresos no tienen categoría
                            ""
                    ));
                }

                // Ordenamos por fecha descendente (más reciente primero)
                Collections.sort(listaMovimientos,
                        (a, b) -> b.getFecha().compareTo(a.getFecha()));
                mostrarListaMovimientos();
            });
        });
    }

    // Convierte la fecha de YYYY-MM-DD (BD) a DD/MM/YYYY (pantalla)
    private String convertirFecha(String fechaBD) {
        String[] partes = fechaBD.split("-");
        if (partes.length == 3) {
            return partes[2] + "/" + partes[1] + "/" + partes[0];
        }
        return fechaBD;
    }

    /**
     * MOSTRALISTAMOVIMIENTOS
     * Adaptador personalizado que construye cada fila del ListView en código Java.
     * Cada fila muestra: descripción/categoría + importe (línea 1),
     * categoría › subcategoría en azul (línea 2, solo gastos),
     * usuario · fecha en gris (línea 3).
     * Fondo rojo claro para gastos, verde claro para ingresos.
     */
    private void mostrarListaMovimientos() {
        ArrayAdapter<Movimiento> adapter = new ArrayAdapter<Movimiento>(this,
                android.R.layout.simple_list_item_1, listaMovimientos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Movimiento mov = listaMovimientos.get(position);

                // Contenedor vertical de la fila
                LinearLayout fila = new LinearLayout(InformesActivity.this);
                fila.setOrientation(LinearLayout.VERTICAL);
                fila.setPadding(32, 20, 32, 20);

                // Fondo rojo claro para gastos, verde claro para ingresos
                if (mov.getTipo().equals("gasto")) {
                    fila.setBackgroundColor(Color.parseColor("#FFF5F5"));
                } else {
                    fila.setBackgroundColor(Color.parseColor("#F0FFF4"));
                }

                // ── Línea 1: descripción (izquierda) + importe (derecha) ──
                LinearLayout lineaSuperior = new LinearLayout(InformesActivity.this);
                lineaSuperior.setOrientation(LinearLayout.HORIZONTAL);

                TextView tvNotas = new TextView(InformesActivity.this);
                // Prioridad: descripción > nombre categoría > tipo genérico
                String textoNotas;
                if (mov.getNotas() != null && !mov.getNotas().isEmpty()) {
                    textoNotas = mov.getNotas();
                } else if (mov.getCategoria() != null && !mov.getCategoria().isEmpty()) {
                    textoNotas = mov.getCategoria();
                } else {
                    textoNotas = mov.getTipo().equals("gasto") ? "Gasto" : "Ingreso";
                }
                tvNotas.setText(textoNotas);
                tvNotas.setTextSize(16);
                tvNotas.setTextColor(Color.parseColor("#2C3E50"));
                tvNotas.setTypeface(null, android.graphics.Typeface.BOLD);
                tvNotas.setLayoutParams(new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)); // ocupa el espacio disponible
                lineaSuperior.addView(tvNotas);

                TextView tvImporte = new TextView(InformesActivity.this);
                if (mov.getTipo().equals("gasto")) {
                    tvImporte.setText("-" + String.format("%.2f", mov.getImporte()) + " €");
                    tvImporte.setTextColor(Color.parseColor("#E74C3C")); // rojo
                } else {
                    tvImporte.setText("+" + String.format("%.2f", mov.getImporte()) + " €");
                    tvImporte.setTextColor(Color.parseColor("#27AE60")); // verde
                }
                tvImporte.setTextSize(16);
                tvImporte.setTypeface(null, android.graphics.Typeface.BOLD);
                lineaSuperior.addView(tvImporte);
                fila.addView(lineaSuperior);

                // ── Línea 2: categoría › subcategoría (solo para gastos) ──
                if (mov.getTipo().equals("gasto")) {
                    StringBuilder detalleCat = new StringBuilder();
                    if (mov.getCategoria() != null && !mov.getCategoria().isEmpty()) {
                        detalleCat.append(mov.getCategoria());
                    }
                    if (mov.getSubcategoria() != null && !mov.getSubcategoria().isEmpty()) {
                        detalleCat.append(" › ").append(mov.getSubcategoria());
                    }
                    if (detalleCat.length() > 0) {
                        TextView tvCategoria = new TextView(InformesActivity.this);
                        tvCategoria.setText(detalleCat.toString());
                        tvCategoria.setTextSize(13);
                        tvCategoria.setTextColor(Color.parseColor("#0288D1")); // azul
                        tvCategoria.setPadding(0, 2, 0, 0);
                        fila.addView(tvCategoria);
                    }
                }

                // ── Línea 3: usuario · fecha en gris ──
                TextView tvDetalle = new TextView(InformesActivity.this);
                tvDetalle.setText(mov.getNombreUsuario() + "  ·  " + mov.getFecha());
                tvDetalle.setTextSize(13);
                tvDetalle.setTextColor(Color.parseColor("#888888")); // gris
                tvDetalle.setPadding(0, 4, 0, 0);
                fila.addView(tvDetalle);

                return fila;
            }
        };

        listViewMovimientos.setAdapter(adapter);
        listViewMovimientos.setOnItemClickListener((parent, view, position, id) ->
                manejarClickMovimiento(listaMovimientos.get(position)));
    }

    /**
     * MANEJARCLICKMOVIMIENTO
     * Controla qué opciones ve el usuario al pulsar un movimiento:
     * - Administrador → puede Modificar y Eliminar cualquier movimiento
     * - Gerente/Trabajador → solo pueden Modificar sus propios movimientos
     * - Si pulsan un movimiento ajeno → mensaje de aviso
     */
    private void manejarClickMovimiento(Movimiento mov) {
        if (!"Administrador".equals(rolUsuario)) {
            if (mov.getIdUsuario() != idUsuario) {
                // Intento de modificar un movimiento ajeno — avisamos y no hacemos nada
                Toast.makeText(this,
                        "Solo puedes modificar tus propios movimientos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Es su propio movimiento — solo puede Modificar
            new AlertDialog.Builder(this)
                    .setTitle("¿Qué quieres hacer?")
                    .setItems(new String[]{"Modificar"}, (dialog, which) ->
                            modificarMovimiento(mov))
                    .show();
            return;
        }

        // Solo el Administrador (giuseppe) llega aquí — puede Modificar y Eliminar
        new AlertDialog.Builder(this)
                .setTitle("¿Qué quieres hacer?")
                .setItems(new String[]{"Modificar", "Eliminar"}, (dialog, which) -> {
                    if (which == 0) {
                        modificarMovimiento(mov);
                    } else {
                        confirmarEliminar(mov);
                    }
                })
                .show();
    }

    // Navega a RegistrarGastoActivity o RegistrarIngresoActivity en modo edición
    private void modificarMovimiento(Movimiento mov) {
        if (mov.getTipo().equals("gasto")) {
            Intent intent = new Intent(this, RegistrarGastoActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("rol_usuario", rolUsuario);
            intent.putExtra("id_gasto", mov.getIdOriginal()); // id del gasto a editar
            intent.putExtra("modo_edicion", true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, RegistrarIngresoActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_ingreso", mov.getIdOriginal());
            intent.putExtra("modo_edicion", true);
            startActivity(intent);
        }
    }

    // Muestra diálogo de confirmación antes de eliminar
    private void confirmarEliminar(Movimiento mov) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar movimiento")
                .setMessage("¿Estás segura de eliminar este movimiento?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarMovimiento(mov))
                .setNegativeButton("No", null) // no hace nada al pulsar No
                .show();
    }

    // Busca el gasto o ingreso por id y lo elimina de la BD
    private void eliminarMovimiento(Movimiento mov) {
        if (mov.getTipo().equals("gasto")) {
            gastoRepository.getAllGastos().observe(this, gastos -> {
                for (Gasto gasto : gastos) {
                    if (gasto.getId_gasto() == mov.getIdOriginal()) {
                        gastoRepository.delete(gasto);
                        Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            });
        } else {
            ingresoRepository.getAllIngresos().observe(this, ingresos -> {
                for (Ingreso ingreso : ingresos) {
                    if (ingreso.getId_ingreso() == mov.getIdOriginal()) {
                        ingresoRepository.delete(ingreso);
                        Toast.makeText(this, "Ingreso eliminado", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            });
        }
    }
}