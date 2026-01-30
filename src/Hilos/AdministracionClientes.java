package Hilos;

import Core.CoreCliente;
import Core.CoreServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Administra los mensajes de cada cliente y cierra el cliente si es necesario
 */
public class AdministracionClientes implements Runnable {
    private final Socket socketCliente;
    private final Core.CoreServidor servidor; // Referencia al servidor para difundir mensajes y gestión
    private PrintWriter escritor; // Declarar como campo de instancia
    private String nombreCliente; // Almacena el nombre del cliente

    /**
     * Constructor para la clase AdministracionClientes.
     * @param socketCliente El socket del cliente que se ha conectado.
     * @param servidor La instancia del servidor principal, para comunicación y gestión.
     */
    public AdministracionClientes(Socket socketCliente, Core.CoreServidor servidor) {
        this.socketCliente = socketCliente;
        this.servidor = servidor;
    }

    /**
     * Envía un mensaje a este cliente específico.
     * @param mensaje El mensaje a enviar.
     */
    public void enviarMensaje(String mensaje) {
        if (escritor != null) {
            escritor.println(mensaje);
        }
    }

    /**
     * Devuelve el nombre de este cliente.
     * @return El nombre del cliente.
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Método principal que se ejecuta en el hilo del cliente.
     * Lee el nombre del cliente, y luego entra en un bucle para leer y procesar
     * los mensajes entrantes. Gestiona los comandos especiales como /salir, /lista, y /ping.
     * Al finalizar, se encarga de la limpieza y desconexión del cliente.
     */
    @Override
    public void run() {
        String datos_recibidos;
        try {
            BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            escritor = new PrintWriter(socketCliente.getOutputStream(), true);

            // La primera línea es el nombre del cliente
            nombreCliente = lector.readLine();
            if (nombreCliente == null) {
                return; // Cliente desconectado prematuramente
            }
            System.out.println("Cliente " + nombreCliente + " conectado.");
            servidor.difundirMensaje("Servidor: " + nombreCliente + " se ha unido al chat.", this);

            while (true){
                datos_recibidos = lector.readLine();

                if (datos_recibidos == null){
                    break;
                }

                datos_recibidos = datos_recibidos.trim();

                if (datos_recibidos.equalsIgnoreCase("/salir")){
                    break; // Salir del bucle, se gestionará en finally
                }
                else if(datos_recibidos.equalsIgnoreCase("/lista")){
                    List<String> nombresClientes = servidor.getNombresClientes();
                    enviarMensaje("Clientes conectados: " + String.join(", ", nombresClientes));
                }
                else if (datos_recibidos.equalsIgnoreCase("/ping")) {
                    enviarMensaje("pong");
                }
                else{
                    servidor.difundirMensaje(nombreCliente + ": " + datos_recibidos, this);
                    System.out.println(nombreCliente + ": " + datos_recibidos);
                }
            }

        }catch (IOException e){
            System.out.println("Error con la ejecución del hilo de cliente " + nombreCliente + ": " + e.getMessage());
        }
        finally {
            // Eliminar este hilo de cliente de la lista del servidor
            servidor.eliminarHiloCliente(this);
            // Resta al contador de clientes activos en el chat
            CoreServidor.contador_clientes.decrementAndGet();
            System.out.println("Cliente " + nombreCliente + " desconectado.");
            servidor.difundirMensaje("Servidor: " + nombreCliente + " se ha desconectado.", this);
            // Asegurarse de que cierre el socket del cliente
            try {
                if (socketCliente != null && !socketCliente.isClosed()) {
                    socketCliente.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }
}
