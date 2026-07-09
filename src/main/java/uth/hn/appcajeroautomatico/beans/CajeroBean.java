package uth.hn.appcajeroautomatico.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped // Mantiene los datos del usuario activos durante toda su navegación
public class CajeroBean implements Serializable {

    // Ruta del archivo .txt donde se almacenan los datos de los usuarios
    private static final String RUTA_ARCHIVO = "C:\\Users\\sevil\\Documents\\II Periodo UTH 2026\\Programacion Web II\\Proyectos Clase\\AppCajeroAutomatico\\src\\main\\webapp\\resources\\cajero-logs\\usuarios.txt";
    private static final String CARPETA_LOGS = "C:\\Users\\sevil\\Documents\\II Periodo UTH 2026\\Programacion Web II\\Proyectos Clase\\AppCajeroAutomatico\\src\\main\\webapp\\resources\\cajero-logs\\";

    // Datos del formulario de Login
    private String usuarioIngresado;
    private String contrasenaIngresada;

    // Datos del usuario que inició sesión
    private String usuarioActivo;
    private int cuentaActiva;
    private double saldoActivo;
    private boolean logueado = false;

    // Variable para capturar cuánto quiere retirar/deposiar
    private double montoRetirar;
    private double montoDepositar;
    //validaciones
    private String mensajeError;


    // Validación de Sesión (Login)
    public String iniciarSesion() {
        //Validar primero que el usuario no haya dejado los campos vacíos en la pantalla
        if (usuarioIngresado == null || usuarioIngresado.trim().isEmpty() ||
                contrasenaIngresada == null || contrasenaIngresada.trim().isEmpty()) {
            this.mensajeError = "Por favor, ingrese usuario y PIN.";
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Saltamos líneas vacías para que no rompan el programa
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(",");

                // 🚨 PROTECCIÓN CRÍTICA: Validamos que la línea tenga los 4 datos requeridos
                if (datos.length < 4) {
                    System.out.println("Línea ignorada por formato incorrecto: " + linea);
                    continue; // Salta a la siguiente línea en lugar de romper el programa
                }

                String user = datos[0].trim();
                String pass = datos[1].trim();

                try {
                    // Protegemos la conversión de números por si el archivo tiene texto donde no debe
                    int cuenta = Integer.parseInt(datos[2].trim());
                    double saldo = Double.parseDouble(datos[3].trim());

                    // Validamos credenciales
                    if (user.equals(usuarioIngresado.trim()) && pass.equals(contrasenaIngresada.trim())) {
                        this.usuarioActivo = user;
                        this.cuentaActiva = cuenta;
                        this.saldoActivo = saldo;
                        this.logueado = true;
                        this.mensajeError = null;

                        // Redirige al menú principal del ATM
                        return "menuOpciones?faces-redirect=true";


                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error de formato numérico en la línea: " + linea);
                    // Si una fila está corrupta, el programa no muere, solo sigue buscando en las demás
                }
            }

            // Si recorrió todo el archivo y no encontró coincidencia
            this.mensajeError = "Usuario o PIN incorrectos.";

        } catch (IOException e) {
            this.mensajeError = "Error al conectar con el sistema del cajero.";
            e.printStackTrace();
        }

        return null; // Si falla, se queda en el login mostrando el error
    }

    //Operación de Retiro
    public void realizarRetiro() {
        //validaciones
        if (montoRetirar <= 0) {
            this.mensajeError = "Monto inválido.";
            return;
        }

        if (montoRetirar%100 !=0) {
            this.mensajeError = "\"El monto a retirar debe ser 100 o múltiplo de 100 (Ej: 100, 200, 500).";
            return;
        }

        if (montoRetirar > saldoActivo) {
            this.mensajeError = "Fondos insuficientes.";
            return;
        }

        // Restamos el dinero localmente
        this.saldoActivo -= montoRetirar;
        this.mensajeError = "Retiro exitoso. ¡Retire su dinero!";

        //Guardamos la transacción en el TXT exclusivo de este usuario
        registrarMovimiento(cuentaActiva, "****RETIRO*****", montoRetirar,usuarioActivo);
        // Guardamos el nuevo saldo físicamente en el archivo .txt
        actualizarArchivoTxt();


        // limpiar datos de campos y formularios
        limpiarCampos();
        //Ir a siguiente pantalla ***pendiente configurar***
        consultarSaldo();
    }

    //Operación de Deposito
    public void realizarDeposito() {
        if (montoDepositar <= 0) {
            this.mensajeError = "Monto inválido.";
            return;
        }
        if (montoDepositar%100 !=0) {
            this.mensajeError = "\"El monto a depositar debe ser 100 o múltiplo de 100 (Ej: 100, 200, 500).";
            return;
        }

        // Sumamos el dinero localmente
        this.saldoActivo += montoDepositar;
        this.mensajeError = "Deposito exitoso. ¡Gracias por confiar en nosotros!";

        //Guardamos la transacción en el TXT exclusivo de este usuario
        registrarMovimiento(cuentaActiva, "***DEPOSITO***", montoDepositar, usuarioActivo);
        // Guardamos el nuevo saldo físicamente en el archivo .txt
        actualizarArchivoTxt();


        // limpiar datos de campos y formularios
        limpiarCampos();
        //Ir a siguiente pantalla
        consultarSaldo();
    }

    private void registrarMovimiento(int cuenta, String tipoOperacion, Double monto, String usuario) {
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Estructura de la línea: FECHA_HORA | TIPO | MONTO
        String lineaRegistro = String.format("%s | %s | L. %.2f", fechaHora, tipoOperacion, monto);

        // Nombre de archivo dinámico por usuario
        String rutaArchivo = CARPETA_LOGS + usuario + cuenta + ".txt";

        try (FileWriter fw = new FileWriter(rutaArchivo, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(lineaRegistro);
            System.out.println("Se registró un " + tipoOperacion + " en el archivo de la cuenta " + cuenta);

        } catch (IOException e) {
            System.err.println("Error al escribir el historial del usuario: " + e.getMessage());
        }
    }

    //Guardar cambios en el archivo .txt
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

    //Consultar movimientos
    public List<String> getMovimientosPorUsuario() {
        List<String> historial = new ArrayList<>();

        // Construimos la ruta exacta del archivo del usuario actual
        String rutaArchivo = CARPETA_LOGS + usuarioActivo + this.cuentaActiva + ".txt";
        File archivo = new File(rutaArchivo);

        // Validamos primero si el archivo existe
        if (!archivo.exists()) {
            historial.add("No se encontraron transacciones registradas para esta cuenta.");
            return historial;
        }

        // Leemos el archivo línea por línea
        try (FileReader fr = new FileReader(archivo);
             BufferedReader br = new BufferedReader(fr)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                historial.add(linea); // Añadimos cada línea del archivo a la lista
            }

        } catch (IOException e) {
            System.err.println("Error al leer el historial del usuario: " + e.getMessage());
            historial.add("Error al cargar el historial. Intente más tarde.");
        }

        return historial;
    }

    public void limpiarCampos() {
        // Restablecemos todas las variables y formulario del login
        this.montoRetirar = 0;
        this.montoDepositar = 0;
        this.usuarioIngresado = "";
        this.contrasenaIngresada = "";
        this.logueado = false;
        this.mensajeError = null; // Limpia también los mensajes de error/éxito

    }

    public void indexNav(){
        //¡Redirigir al index!
        FacesContext context = FacesContext.getCurrentInstance();

        // Le indicamos que vaya al index de forma limpia
        context.getApplication().getNavigationHandler().handleNavigation(context, null, "index?faces-redirect=true");
    }

    public void consultarSaldo(){
        //¡Redirigir al index!
        FacesContext context = FacesContext.getCurrentInstance();

        // Le indicamos que vaya al index de forma limpia
        context.getApplication().getNavigationHandler().handleNavigation(context, null, "operacionExitosa?faces-redirect=true");
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
    public double getMontoDepositar() { return montoDepositar; }
    public void setMontoDepositar(double montoDepositar) { this.montoDepositar = montoDepositar; }
    public String getMensajeError() { return mensajeError; }
    public int getCuentaActiva() {
        return cuentaActiva;
    }
}
