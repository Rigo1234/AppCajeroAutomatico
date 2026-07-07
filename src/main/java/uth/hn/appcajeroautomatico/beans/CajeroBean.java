package uth.hn.appcajeroautomatico.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.*;
import java.io.Serializable;

@Named
@SessionScoped // Mantiene los datos del usuario activos durante toda su navegación
public class CajeroBean implements Serializable {

    // Ruta del archivo .txt donde se almacenan los datos de los usuarios
    private static final String RUTA_ARCHIVO = "C:\\Users\\sevil\\Documents\\II Periodo UTH 2026\\Programacion Web II\\Proyectos Clase\\AppCajeroAutomatico\\usuarios.txt";

    // Datos del formulario de Login
    private String usuarioIngresado;
    private String contrasenaIngresada;

    // Datos del usuario que inició sesión
    private String usuarioActivo;
    private int cuentaActiva;
    private double saldoActivo;
    private boolean logueado = false;

    // Variable para capturar cuánto quiere retirar
    private double montoRetirar;
    private String mensajeError;

    // --- MÉTODOS DE OPERACIÓN ---

    // 1. Validación de Sesión (Login)
    public String iniciarSesion() {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                String user = datos[0];
                String pass = datos[1];
                int cuenta = Integer.parseInt(datos[2]);
                double saldo = Double.parseDouble(datos[3]);

                // Validamos credenciales
                if (user.equals(usuarioIngresado) && pass.equals(contrasenaIngresada)) {
                    this.usuarioActivo = user;
                    this.cuentaActiva = cuenta;
                    this.saldoActivo = saldo;
                    this.logueado = true;
                    this.mensajeError = null;

                    // Redirige al menú principal del ATM (Tu opción 2)
                    return "menu_principal?faces-redirect=true";
                }
            }
            this.mensajeError = "Usuario o PIN incorrectos.";
        } catch (IOException e) {
            this.mensajeError = "Error al conectar con el sistema del cajero.";
            e.printStackTrace();
        }
        return null; // Si falla, se queda en el login mostrando el error
    }

    // 2. Operación de Retiro
    public void realizarRetiro() {
        if (montoRetirar <= 0) {
            this.mensajeError = "Monto inválido.";
            return;
        }
        if (montoRetirar > saldoActivo) {
            this.mensajeError = "Fondos insuficientes.";
            return;
        }

        // Restamos el dinero localmente
        this.saldoActivo -= montoRetirar;
        this.mensajeError = "Retiro exitoso. ¡Retire su dinero!";

        // Guardamos el nuevo saldo físicamente en el archivo .txt
        actualizarArchivoTxt();

        // 4. Limpiar el campo del monto para el próximo intento
        this.montoRetirar = 0;
        // 5. ¡Redirigir al index!
        FacesContext context = FacesContext.getCurrentInstance();

        // Le indicamos que vaya al index de forma limpia (?faces-redirect=true)
        context.getApplication().getNavigationHandler()
                .handleNavigation(context, null, "index?faces-redirect=true");
    }

    // 3. Guardar cambios en el archivo .txt
    private void actualizarArchivoTxt() {
        File archivoOriginal = new File(RUTA_ARCHIVO);
        File archivoTemporal = new File(RUTA_ARCHIVO + ".tmp");

        try (BufferedReader br = new BufferedReader(new FileReader(archivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemporal))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                // Si es la línea del usuario activo, escribimos su nuevo saldo
                if (datos[0].equals(usuarioActivo)) {
                    bw.write(usuarioActivo + "," + datos[1] + "," + datos[2] + "," + saldoActivo);
                } else {
                    bw.write(linea); // Si es otro usuario, se queda igual
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reemplazamos el archivo viejo por el nuevo con los saldos actualizados
        if (archivoOriginal.delete()) {
            archivoTemporal.renameTo(archivoOriginal);
        }
    }

    // --- GETTERS Y SETTERS ---
    public String getUsuarioIngresado() { return usuarioIngresado; }
    public void setUsuarioIngresado(String usuarioIngresado) { this.usuarioIngresado = usuarioIngresado; }
    public String getContrasenaIngresada() { return contrasenaIngresada; }
    public void setContrasenaIngresada(String contrasenaIngresada) { this.contrasenaIngresada = contrasenaIngresada; }
    public String getUsuarioActivo() { return usuarioActivo; }
    public double getSaldoActivo() { return saldoActivo; }
    public double getMontoRetirar() { return montoRetirar; }
    public void setMontoRetirar(double montoRetirar) { this.montoRetirar = montoRetirar; }
    public String getMensajeError() { return mensajeError; }

    public int getCuentaActiva() {
        return cuentaActiva;
    }
}
