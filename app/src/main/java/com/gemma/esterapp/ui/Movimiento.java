package com.gemma.esterapp.ui;

/**
 * MOVIMIENTO
 * Clase auxiliar que mezcla gastos e ingresos en una sola lista para InformesActivity.
 * NO es una entidad de base de datos — no tiene @Entity y Room no la conoce.
 * Es un modelo de presentación (DTO) que solo existe en la capa UI.
 * Está en el paquete model por simplicidad, pero técnicamente debería estar
 * en un subpaquete model.ui o directamente en el paquete ui.
 */
public class Movimiento {

    // Tipo del movimiento — determina el color y las opciones disponibles
    // Valores posibles: "gasto" o "ingreso"
    private String tipo;

    // Fecha en formato DD/MM/YYYY (ya convertida de YYYY-MM-DD de la BD)
    private String fecha;

    // Importe del movimiento en euros
    private double importe;

    // Descripción del gasto o tipo de ingreso ("Efectivo" / "Tarjeta")
    private String notas;

    // Nombre del usuario que registró el movimiento (resuelto desde mapaUsuarios)
    private String nombreUsuario;

    // Id original del gasto o ingreso en su tabla — usado para modificar o eliminar
    private int idOriginal;

    // Id del usuario que registró — usado para el control de permisos
    // (ester y francesca solo pueden modificar sus propios movimientos)
    private int idUsuario;

    // Nombre de la categoría (solo para gastos, "" para ingresos)
    // Resuelto desde mapaCategorias en InformesActivity
    private String categoria;

    // Nombre de la subcategoría (solo para gastos que la tienen, "" si no aplica)
    // Resuelto desde mapaSubcategorias en InformesActivity
    private String subcategoria;

    // Constructor completo — usado en InformesActivity al construir la lista
    public Movimiento(String tipo, String fecha, double importe,
                      String notas, String nombreUsuario,
                      int idOriginal, int idUsuario,
                      String categoria, String subcategoria) {
        this.tipo = tipo;
        this.fecha = fecha;
        this.importe = importe;
        this.notas = notas;
        this.nombreUsuario = nombreUsuario;
        this.idOriginal = idOriginal;
        this.idUsuario = idUsuario;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
    }

    // GETTERS — solo lectura, la lista no modifica los movimientos
    public String getTipo() { return tipo; }
    public String getFecha() { return fecha; }
    public double getImporte() { return importe; }
    public String getNotas() { return notas; }
    public String getNombreUsuario() { return nombreUsuario; }
    public int getIdOriginal() { return idOriginal; }  // id para modificar o eliminar
    public int getIdUsuario() { return idUsuario; }    // id para control de permisos
    public String getCategoria() { return categoria; }
    public String getSubcategoria() { return subcategoria; }

    // Único setter — permite actualizar el nombre si el mapa de usuarios cambia
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
}