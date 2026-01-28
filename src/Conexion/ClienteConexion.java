package Conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClienteConexion {
    private final String HOST;
    private final int PUERTO;

    private Socket socket = null;
    private PrintWriter escritor = null;
    private BufferedReader lector = null;

    // Constructor
    public ClienteConexion(String HOST, int PUERTO) {
        this.HOST = HOST;
        this.PUERTO = PUERTO;
    }

    /**
     * Establece la conexión del cliente con el servidor
     * @return true/false si se conecta o no
     */
    public boolean establecer_conexion(){
        try{
            InetSocketAddress dir = new InetSocketAddress(HOST, PUERTO);
            socket = new Socket();
            socket.connect(dir, 5000);

            // Para enviar mensajes a otros escritores
            escritor = new PrintWriter(socket.getOutputStream(), true);
            // Para recibir mensajes de otros escritores
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;

        } catch (IOException e) {
            System.out.println("Error al establecer la conexión del cliente: "+e.getMessage());
            return false;
        }
    }

    /**
     * Cierra la conexión del cliente con el servidor
     */
    public void cerrar_conexion(){
        try {
            // Verifica si está conectado y lo cierra
            if (socket != null && !socket.isClosed()) {
                System.out.println("Conexión cerrada");
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión del cliente"+e.getMessage());
        }
    }

    /**
     * Función de testeo para comprobar que los mensajes se enviaban
     * @param mensaje datos a enviar
     */
    public void enviar_recibir(String mensaje){
        System.out.println(mensaje);
        escritor.println(mensaje);
        //return lector.readLine(); // Esto ya no es necesario aquí
    }

    /**
     * Proporciona el lector para recibir mensajes del servidor.
     * @return BufferedReader para la entrada del socket.
     */
    public BufferedReader getLector() {
        return lector;
    }

    /**
     * Proporciona el escritor para enviar mensajes al servidor.
     * @return PrintWriter para la salida del socket.
     */
    public PrintWriter getEscritor() {
        return escritor;
    }


}
