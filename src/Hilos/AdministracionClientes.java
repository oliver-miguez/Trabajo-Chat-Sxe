package Hilos;

import Core.CoreCliente;
import Core.CoreServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Administra los mensajes de cada cliente y cierra el cliente si es necesario
 */
public class AdministracionClientes implements Runnable {
    private final Socket socketCliente;
    private final Core.CoreServidor servidor; // Referencia al servidor para difundir mensajes y gestión
    private PrintWriter escritor; // Declarar como campo de instancia

    // Constructor
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

    @Override
    public void run() {
        String datos_recibidos;
        try {
            // Recibir los datos de cada cliente
            BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            // Envía mensajes al resto
            escritor = new PrintWriter(socketCliente.getOutputStream(), true);

            // Ajustes del mensaje recibido
            while (true){
                datos_recibidos = lector.readLine();

                if (datos_recibidos == null){
                    break;
                }
               datos_recibidos = datos_recibidos.trim();

                // Administra el cierre del cliente cuando sale del chat
                if (datos_recibidos.equals("/salir")){
                    System.out.println("Cliente "+ CoreCliente.nombre + " solicitó salir del chat");
                    break;
                }

                // Difunde el mensaje al resto de clientes y servidor
                this.servidor.difundirMensaje(datos_recibidos, this);
                System.out.println(datos_recibidos);
            }

        }catch (IOException e){
            System.out.println("Error con la ejecución del hilo de cliente: " + e.getMessage());
        }
        finally {
            // Eliminar este hilo de cliente de la lista del servidor
            servidor.eliminarHiloCliente(this);
            // Resta al contador de clientes activos en el chat
            CoreServidor.contador_clientes.decrementAndGet();
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
