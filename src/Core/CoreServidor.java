package Core;

import Hilos.AdministracionClientes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Recibe a cada uno de los clientes
 */
public class CoreServidor {
    private static final int PUERTO = 6666;
    private static final int MAX_CLIENTES = 10;

    // Contador de hilos para evitar bloqueos y posibles bugs
    public static java.util.concurrent.atomic.AtomicInteger contador_clientes = new java.util.concurrent.atomic.AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        boolean encendido = true;
        ExecutorService poolHilos = Executors.newFixedThreadPool(MAX_CLIENTES);
        ServerSocket servidor = new ServerSocket();
        try {
            InetSocketAddress dir = new InetSocketAddress(PUERTO);
            servidor.bind(dir);

            // Cada 5 segundos, el accept "despierta" aunque no haya clientes
                servidor.setSoTimeout(5000);
                System.out.println("Servidor a la escucha...");


            while (encendido) {
                try {
                    if (contador_clientes.get() == 0) {
                        System.out.println("No hay nadie por aquí");
                    }
                    Socket socket = servidor.accept();
                    contador_clientes.incrementAndGet();
                    System.out.println("Actualmente hay : " + contador_clientes.get()+ " usuarios en este chat");
                    //System.out.println("Parece que no hay nadie por aquí");
                    //System.out.println("Se está conectado un cliente");

                    // Atender a cada hilo independientemente
                    AdministracionClientes administracionClientes = new AdministracionClientes(socket);
                    // Asigna una hilo a cada cliente para administrar tareas
                    poolHilos.execute(administracionClientes);

                }catch (SocketTimeoutException e){
                    //System.out.println("Tiempo de espera agotado");
                    if(contador_clientes.get()==0){
                        System.out.println("Cerrando el servidor...");
                        encendido = false;
                    }
                    else{
                        System.out.println(".");
                    }
                } // Permite volver a mostrar por pantalla que no hay ningún cliente
            }

        } catch (Exception e) {
            System.out.println("Error general en en código: " + e.getMessage());
        }finally {

            System.out.println("Iniciando proceso de apagado...");
            poolHilos.shutdown();
            System.out.println("Avisando a los hilos que deben terminar...");
            try {
                // Espera un máximo de 5 segundos a que los hilos terminen
                if (!poolHilos.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    poolHilos.shutdownNow();
                }
            } catch (InterruptedException e) {
                poolHilos.shutdownNow();
            }
            if (!servidor.isClosed()) {
                servidor.close();
            }
            System.out.println("Servidor. Fin del programa.");
        }
    }
}
