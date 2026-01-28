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

    public ClienteConexion(String HOST, int PUERTO) {
        this.HOST = HOST;
        this.PUERTO = PUERTO;
    }

    public boolean establecer_conexion(){
        try{
            InetSocketAddress dir = new InetSocketAddress(HOST, PUERTO);
            socket = new Socket();
            socket.connect(dir, 5000);

            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;

        } catch (IOException e) {
            System.out.println("Error al establecer la conexión del cliente: "+e.getMessage());
            return false;
        }
    }

    public void cerrar_conexion(){
        try {
            if (socket != null && !socket.isClosed()) {
                System.out.println("Conexión cerrada");
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión del cliente"+e.getMessage());
        }
    }

    public void enviar_recibir(String mensaje){
//        if (escritor == null || lector == null) {
//            return "Error: Conexión no establecida.";
//        }
        System.out.println(mensaje);
        escritor.println(mensaje);
        //return lector.readLine();

    }

}
