package Hilos;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Hilo que escucha continuamente los mensajes entrantes del servidor
 * y los muestra en la consola del cliente.
 */
public class HiloEscuchaCliente implements Runnable {
    private BufferedReader lectorServidor;

    /**
     * Constructor.
     * @param lectorServidor El BufferedReader para leer desde el servidor.
     */
    public HiloEscuchaCliente(BufferedReader lectorServidor) {
        this.lectorServidor = lectorServidor;
    }

    @Override
    public void run() {
        String mensajeRecibido;
        try {
            while ((mensajeRecibido = lectorServidor.readLine()) != null) {
                System.out.println(mensajeRecibido); // Mostrar el mensaje en la consola del cliente
            }
        } catch (IOException e) {
            System.out.println( e.getMessage());
        } finally {
            try {
                if (lectorServidor != null) {
                    lectorServidor.close();
                }
            } catch (IOException e) {
                System.err.println("Error al cerrar el lector del servidor: " + e.getMessage());
            }
        }
    }
}
