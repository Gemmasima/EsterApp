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


 /* GENERAR INFORME ACTIVITY
    Pantalla para generar un CSV, solo accesible para el Admin y el Gerente.
    Permite seleccionar el rango de fecha y filtrar por tipo gasto, ingreso o todos
    y aplicar filtro por categoria de gasto o tipo de ingreso */

public class GenerarInformeActivity extends AppCompatActivity {

    // Elementos visuales conectados al XML activity_generar_informe.xml
    private Button btnSeleccionarFechas;        // abre el calendario
    private TextView tvRangoFechas;             // muestra el rango seleccionado
    private Spinner spinnerTipo;                // Gastos / Ingresos / Todos
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
    private Button btnGenerarCSV; //genera el CSV y abre el codigo para compartirlo
    private Button btnGuardarCSV; // para guardar el CSV en la tablet fisicamente
    private ImageButton iconoHome; //boton barra inferior
    private ImageButton iconoInformes;

    // Repositorios para consultar gastos e ingresos en la BD, private pq solo los usa esta clase GenerarInformeActivity
    private GastoRepository gastoRepository;
    private IngresoRepository ingresoRepository;

    // Declaradas Id y rol del usuario que ha iniciado la sesion. Estos valores se reciben desde InformesActivity
    private int idUsuario;
    private String rolUsuario;

    // Fechas seleccionadas en el picker en formato año-mes-dia para poder filtrar en la BD
    private String fechaDesde = null;
    private String fechaHasta = null;

    // Aumentin de onCreate (metodo que crea y prepara la pantalla) extendida de AppCompatActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //ejecuta el onecreate original antes del aumentin
        setContentView(R.layout.activity_generar_informe); //conecta esta clase con su XML de diseño

        /* Al abrir la pantalla generar informes, InformesActivity envia el id y el rol del usuario mediante de un Intent.
           el -1 es el valor por defecto del id como medida de seguridad, en este caso no sirve pq los usuarios siempre
           se logean antes de entrar a la app pero es una buena practica.
           getIntExtra extrae el id como int y el getStringExtra extra el rol como string*/

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        rolUsuario = getIntent().getStringExtra("rol_usuario");

        /* Las var estan declaradas al inicio pero no instanciadas.
           findViewById las instancia asignando a cada una su elemento visual del XML, buscandolo por su id*/
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
        btnGuardarCSV = findViewById(R.id.btnGuardarCSV);
        iconoHome = findViewById(R.id.iconoHome);
        iconoInformes = findViewById(R.id.iconoInformes);


        gastoRepository = new GastoRepository(getApplication());
        ingresoRepository = new IngresoRepository(getApplication());

        // Crea una lista con 3 opciones que tendrá el spinner (0=gatos, 1=ingreso, 2=todos)
        List<String> tipos = Arrays.asList("Gastos", "Ingresos", "Todo");
        /*Crea el adaptador, este hace de puente entre la lista de datos y el spinner, es decir
          transforma los textos en opciones visuales para que el usuario las veo y seleccione */
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipos);

        //Define el diseño visual de las opciones cuando el spinner se despliega
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Asigna el adaptador al spinner **a partir de aqui muestra las opciones**
        spinnerTipo.setAdapter(adapterTipo);

        // detecta cuando el usuario cambia la opcion del spinner y muestra u oculta el chechbox correspondiente
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //Aumentin de onItemSelected. se ejecuta automatic. cada vez que el usuario cambia la opcion del spinner
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position == 0 significa que el usuario ha seleccionado gastos
                if (position == 0) {
                    layoutCategorias.setVisibility(View.VISIBLE); //muestra el checklist de categorias
                    layoutTipoIngreso.setVisibility(View.GONE); // oculta el checklist de tipo de ingreso
                // position == 1 significa que ha seleccionado ingreso
                } else if (position == 1) {
                    layoutCategorias.setVisibility(View.GONE);
                    layoutTipoIngreso.setVisibility(View.VISIBLE);
                } else {
                 // si no es 0 ni 1 es todos=2 en el array
                    layoutCategorias.setVisibility(View.GONE); //oculta categorias
                    layoutTipoIngreso.setVisibility(View.GONE); //oculta tipo ingreso
                }
            }
            //este metodo esta vacio, pero hace que en el spinner siempre haya una opcion seleccionada
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // cuando el usuario pulsa el boton de fechas llama al metodo abrirDataPicker
        btnSeleccionarFechas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { abrirDatePicker(); }
        });

        // cuando pulsa Generar se llama al metodo generarCSV que genera el CSV
        btnGenerarCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { generarCSV(); }
        });

        //cuando pulsa Guardar, guarda el CSV en la carpeta Documentos de la tablet
        btnGuardarCSV.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) { guardarCSV(); }
        });

        // cuando se pulsa la casita navega a menuactivity
        iconoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GenerarInformeActivity.this, MenuActivity.class);
                intent.putExtra("id_usuario", idUsuario); //envia el id del usuario
                startActivity(intent); //abre menuactivity
                finish();
            }
        });

        // cuando se pulsa informes navega a informes
        iconoInformes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GenerarInformeActivity.this, InformesActivity.class);
                intent.putExtra("id_usuario", idUsuario);  //envia intent
                intent.putExtra("rol_usuario", rolUsuario);
                startActivity(intent); //abre informes
                finish();
            }
        });
    }

    /* ABRIR DATE PICKER
        Abre el calendario de rango de fechas. Se selecciona el rango. Las fechas se
        convierten y se guardan dos formatos: año-mes-dia para poder filtrar en la BD
        y dia-mes-año para mostrar al usuario por pantalla. */

    private void abrirDatePicker() {
        //Construye el calendario
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Selecciona el periodo")
                        .build();
        //Se ejecuta automatic. cuando el usuario selecciona las fechas y pulsa OK
        picker.addOnPositiveButtonClickListener(selection -> {
            /* El calendario devuelve las fechas en milisegundos y hay que convertirlas a texto
             se usa el formato de la BD pq estan guardadas en ella. */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDesde = sdf.format(new Date(selection.first));   // fecha inicio convertida a año-mes-dia
            fechaHasta = sdf.format(new Date(selection.second));

            /* Se crea un segundo formato dia-mes-año solo para mostrar el rango al usuario
            por comodidad para el usuario */
            SimpleDateFormat sdfMostrar = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String textoRango = "Del " + sdfMostrar.format(new Date(selection.first))
                    + " al " + sdfMostrar.format(new Date(selection.second));

            //Escribe el rango de fechas en el textview y se ve en pantalla
            tvRangoFechas.setText(textoRango);
            tvRangoFechas.setVisibility(View.VISIBLE);
        });

        // Muestra el calendario es pantalla.
        picker.show(getSupportFragmentManager(), "date_picker");
    }


     /* -- GENERAR CSV --
        Valida que se haya seleccionado un rango y llama al metodo correspondiente según el tipo elegido en el spinner. */
    private void generarCSV() {
        if (fechaDesde == null || fechaHasta == null) {
            Toast.makeText(this, "Por favor selecciona un período", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = spinnerTipo.getSelectedItem().toString();

        if (tipo.equals("Gastos")) {
            generarCSVGastos(true);
        } else if (tipo.equals("Ingresos")) {
            generarCSVIngresos(true);
        } else {
            generarCSVTodo(true); // Todo: gastos + ingresos sin filtro
        }
    }

    /* -- GUARDAR CSV --
    *   Valida que se haya seleccionado un rango y llama al metodo correspondiente según el tipo elegido en el spinner
    * A diferencia de generarCSV() pasa false para que el archivo se guarde en Documentos sin abrir el dialogo de compartir*/
    private void guardarCSV() {
        if (fechaDesde == null || fechaHasta == null) {
            Toast.makeText(this, "Por favor selecciona un período", Toast.LENGTH_SHORT).show();
            return;
        }
        String tipo = spinnerTipo.getSelectedItem().toString();
        if (tipo.equals("Gastos")) {
            generarCSVGastos(false);
        } else if (tipo.equals("Ingresos")) {
            generarCSVIngresos(false);
        } else {
            generarCSVTodo(false);
        }
    }


    /* GENERAR CSV GASTOS
        Este metodo genera el CSV de la categoria gastos. Primero consulta en la BD todos los gastos entre las fechas
        seleccionadas. Despues comprueba que categorias se han marcado. Si es "todas" incluye todos los gastos sin
        filtrar, sino incluye las seleccionadas.*/

    private void generarCSVGastos(boolean compartir) {
        gastoRepository.getGastosByRangoFechas(fechaDesde, fechaHasta).observe(this, gastos -> {

            // Si no esta marcado todas
            List<String> categoriasFiltro = new ArrayList<>();
            if (cbTodoCategorias.isChecked()) {
                categoriasFiltro = null; // null = sin filtro, se incluyen todas las categorias
            } else {
                if (cbProveedoresMateriales.isChecked())
                    categoriasFiltro.add("Proveedores de materiales");
                if (cbProveedoresServicios.isChecked())
                    categoriasFiltro.add("Proveedores de servicios");
                if (cbImpuestos.isChecked()) categoriasFiltro.add("Impuestos");
                if (cbSalarios.isChecked()) categoriasFiltro.add("Salarios");
                if (cbVehiculos.isChecked()) categoriasFiltro.add("Vehículos");
            }

            // Se construye el CSV con cabecera y una línea por gasto
            StringBuilder csv = new StringBuilder();
            csv.append("Fecha,Notas,Categoria,Importe\n"); // cabecera del documento, las columnas

            double total = 0.0; //se irán sumando los importes de todos los gastos
            for (Gasto gasto : gastos) { //Bucle que recorre uno a uno todos los gastos que ha devuelto la consulta de la BD
                csv.append(gasto.getFecha()).append(","); //Escribe la fecha
                csv.append(gasto.getNotas()).append(",");
                csv.append(gasto.getId_categoria()).append(",");
                csv.append(String.format("%.2f", gasto.getImporte())).append("\n"); //Escribe el importe del gasto con 2 decimales=(%.2f)
                total += gasto.getImporte(); // Suma el importe al total acumulado
            }
            csv.append("Total,,,").append(String.format("%.2f", total)).append("\n"); //Escribe el total acumulado de todos los importes con 2 decim.

            procesarCSV(csv.toString(), "gastos", compartir); // toString convierte el StringBuilder, que ha ido acumulando todas las lineas con append(), en un unico String para pasarlo a procesarCSV
        });
    }

    /* GENERAR CSV INGRESOOS
        Este metodo genera el CSV de la categoria ingresos. Primero consulta en la BD los ingresos entre las fechas seleccionadas.
        Despues comprueba que tipo de ingreso se ha marcado. Si es todos (efectivo+targeta) sino incluye solo un tipo segun lo
        que haya seleccionado el usuario */

    private void generarCSVIngresos(boolean compartir) {
        //construye el CSV con cabecera y una linea por ingreso
        ingresoRepository.getIngresosByRangoFechas(fechaDesde, fechaHasta).observe(this, ingresos -> {
            StringBuilder csv = new StringBuilder();
            csv.append("Fecha,Tipo,Notas,Importe\n"); // cabecera del documento, las columnas

            double total = 0.0; //se irán sumando los importes de todos los ingresos
            for (Ingreso ingreso : ingresos) { //Bucle que recorre uno a uno todos los ingresos
                // Si no esta marcado todos se filtra por tipo
                if (!cbTodoIngresos.isChecked()) {
                    if (ingreso.getTipoingreso().equals("Efectivo") && !cbEfectivo.isChecked())
                        continue; // saltamos este ingreso
                    if (ingreso.getTipoingreso().equals("Tarjeta") && !cbTarjeta.isChecked())
                        continue;
                }
                csv.append(ingreso.getFecha()).append(","); //Escribe la fecha
                csv.append(ingreso.getTipoingreso()).append(",");
                csv.append(ingreso.getNotas()).append(",");
                csv.append(String.format("%.2f", ingreso.getImporte())).append("\n"); //Escribe el importe del ingreso con 2 decimales (%.2f)
                total += ingreso.getImporte(); //Suma el importe al total acumulado
            }
            csv.append("Total,,,").append(String.format("%.2f", total)).append("\n"); //Escribe el total acumulado de todos los importes

            procesarCSV(csv.toString(), "ingresos", compartir); // toString convierte el StringBuilder, que ha ido acumulando todas las lineas con append(), en un unico String para pasarlo a procesarCSV
        });
    }

    /* GENERAR CSV TODOs
        Este metodo genera el CSV combinando gastos + ingresos. Primero consulta la BD todos los ingre y gast. Entre las
        fechas seleccionadas sin ningun filtro. Escribe una linea por cada ingreso con (+) y cada gasto con (-). Al final
        añade un total de ingresos y gastos y el balance resultante. */

    private void generarCSVTodo(boolean compartir) {
        //Consulta todos los ingresos/gastos entre las fechas selecionadas
        ingresoRepository.getIngresosByRangoFechas(fechaDesde, fechaHasta).observe(this, ingresos -> {
            gastoRepository.getGastosByRangoFechas(fechaDesde, fechaHasta).observe(this, gastos -> {
                StringBuilder csv = new StringBuilder(); //Construye el CSV con cabecera y una linea por movimiento
                csv.append("Fecha,Tipo,Notas,Importe\n"); //Cabecera del doc., las columnas

                double totalIngresos = 0.0; //se irán sumando los importes de todos los ingresos
                for (Ingreso ingreso : ingresos) { //bucle que recorre uno a uno los ingresos
                    csv.append(ingreso.getFecha()).append(","); //Escribe la fecha
                    csv.append("Ingreso").append(",");
                    csv.append(ingreso.getNotas()).append(",");
                    csv.append("+").append(String.format("%.2f", ingreso.getImporte())).append("\n"); // Escribe el importe con (+) y 2 decimales (%.2f)
                    totalIngresos += ingreso.getImporte(); //Suma el importe al total acumulado de ingresos
                }

                double totalGastos = 0.0; //Se irán sumando los importes de todos los gastos
                for (Gasto gasto : gastos) { //bucle que recorre uno a uno los gastos
                    csv.append(gasto.getFecha()).append(","); //Escribe la fecha
                    csv.append("Gasto").append(",");
                    csv.append(gasto.getNotas()).append(",");
                    csv.append("-").append(String.format("%.2f", gasto.getImporte())).append("\n"); // Escribe el importe con (-) y 2 decimales (%.2f)
                    totalGastos += gasto.getImporte(); //Suma el importe al total acumulado de gastos
                }

                // Resumen final con totales y balance
                csv.append("Total ingresos,,,+")
                        .append(String.format("%.2f", totalIngresos)).append("\n"); //Escribe el total acumulado de ingresos con 2 dec.
                csv.append("Total gastos,,,-")
                        .append(String.format("%.2f", totalGastos)).append("\n");
                csv.append("Balance,,,")
                        .append(String.format("%.2f", totalIngresos - totalGastos)).append("\n"); //Calcula y escribe el balance restando el uno al otro

                procesarCSV(csv.toString(), "todo", compartir); // toString convierte el StringBuilder, que ha ido acumulando todas las lineas con append(), en un unico String para pasarlo a procesarCSV
            });
        });
    }

    /* PROCESAR CSV
    Guarda el archivo CSV en la carpeta GazzolaShop dentro de Documentos.
    Si compartir es true abre el dialogo del sistema para compartirlo.
    Si compartir es false solo guarda el archivo y muestra un mensaje de confirmacion. */
    private void procesarCSV(String contenido, String tipo, boolean compartir) {
        // bloque try-catch. por si algo sale mal salta el catch
        try {
            // Crea el nombre del archivo con esas variables para identificarlo, concatenando.
            String nombreArchivo = "informe_" + tipo + "_"
                    + fechaDesde + "_" + fechaHasta + ".csv";

            // Crea el archivo fisico en la carpeta privada de la app dentro de Documentos
            // FileProvider necesita esta ruta para poder compartir el archivo de forma segura
            File archivo = new File(
                    getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), nombreArchivo);


            // Abre el archivo, escribe el contenido del CSV y lo cierra
            FileWriter writer = new FileWriter(archivo);
            writer.write(contenido);
            writer.close();

            if (compartir) {
            /* FileProvider crea un URI seguro para acceder al archivo. Obligatorio desde Android 7.
               Sin FileProvider Android bloquea el acceso al archivo por seguridad */
                Uri uri = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", archivo);
                // Crea el Intent de compartir y abre el dialogo del sistema para que el usuario elija la app
                Intent intentCompartir = new Intent(Intent.ACTION_SEND);
                intentCompartir.setType("text/csv"); // indica que se comparte un CSV para que el SO sepa que apps pueden abrirlo
                intentCompartir.putExtra(Intent.EXTRA_STREAM, uri); // adjunta el archivo al Intent usando el URI
                intentCompartir.putExtra(Intent.EXTRA_SUBJECT, "Informe Gazzola Shop"); // asunto para cuando se comparte por email
                intentCompartir.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // permiso temporal para que la app elegida pueda leer el archivo
                startActivity(Intent.createChooser(intentCompartir, "Compartir informe")); // lanza el dialogo
            } else {
                // Solo guarda, muestra confirmacion en pantalla
                Toast.makeText(this, "Informe guardado en Documentos/GazzolaShop", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            // avisa al usuario de que algo ha fallado al generar el archivo
            Toast.makeText(this, "Error al generar el archivo", Toast.LENGTH_SHORT).show();
        }
    }
}