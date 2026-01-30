package Core;

import Hilos.AdministracionClientes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // Importar para la lista concurrente
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Recibe a cada uno de los clientes y gestiona la lógica del chat.
 */
public class CoreServidor {
    private static final int PUERTO = 6666;
    private static final int MAX_CLIENTES = 10;

    // Contador de hilos para evitar bloqueos y posibles bugs
    public static java.util.concurrent.atomic.AtomicInteger contador_clientes = new java.util.concurrent.atomic.AtomicInteger(0);

    // Lista segura para hilos para mantener un registro de todos los clientes conectados.
    private final CopyOnWriteArrayList<AdministracionClientes> listaHilosClientes;

    /**
     * Constructor para inicializar el servidor y la lista de clientes.
     */
    public CoreServidor() {
        this.listaHilosClientes = new CopyOnWriteArrayList<>();
    }

    /**
     * Añade un hilo de cliente a la lista de clientes conectados.
     * @param hiloCliente El hilo de AdministracionClientes a añadir.
     */
    public void agregarHiloCliente(AdministracionClientes hiloCliente) {
        listaHilosClientes.add(hiloCliente);
        System.out.println("Cliente añadido. Clientes conectados: " + listaHilosClientes.size());
    }

    /**
     * Elimina un hilo de cliente de la lista de clientes conectados.
     * Esto ocurre cuando un cliente se desconecta.
     * @param hiloCliente El hilo de AdministracionClientes a eliminar.
     */
    public void eliminarHiloCliente(AdministracionClientes hiloCliente) {
        listaHilosClientes.remove(hiloCliente);
        System.out.println("Cliente eliminado. Clientes conectados: " + listaHilosClientes.size());
    }

    /**
     * Difunde un mensaje a todos los clientes conectados, excepto al remitente.
     * @param mensaje El mensaje a difundir.
     * @param remitente El hilo del cliente que envió el mensaje original.
     */
    public void difundirMensaje(String mensaje, AdministracionClientes remitente) {
        for (AdministracionClientes hiloCliente : listaHilosClientes) {
            if (hiloCliente != remitente) {
                hiloCliente.enviarMensaje(mensaje);
            }
        }
    }

    /**
     * Devuelve una lista de los nombres de todos los clientes conectados actualmente.
     * @return Una lista de Strings con los nombres de los clientes.
     */
    public List<String> getNombresClientes(){
        List<String> nombres = new CopyOnWriteArrayList<>();
        for (AdministracionClientes hiloCliente : listaHilosClientes) {
            nombres.add(hiloCliente.getNombreCliente());
        }
        return nombres;
    }

    /**
     * Punto de entrada principal para la aplicación del servidor.
     * Inicializa el servidor, lo pone a la escucha de nuevas conexiones de clientes,
     * y gestiona el ciclo de vida de los hilos de los clientes.
     * @param args Argumentos de la línea de comandos (no se utilizan).
     * @throws IOException Si ocurre un error de E/S al crear el socket del servidor.
     */
    public static void main(String[] args) throws IOException {
        // Crear una instancia de CoreServidor para gestionar el estado.
        CoreServidor servidor = new CoreServidor();
        boolean encendido = true;
        // Administra los hilos que se pueden ejecutar
        ExecutorService poolHilos = Executors.newFixedThreadPool(MAX_CLIENTES);
        ServerSocket socketServidor = new ServerSocket();
        try {
            // Puerto al que se conecta
            InetSocketAddress dir = new InetSocketAddress(PUERTO);
            socketServidor.bind(dir);

            // Tiempo que el socket del servidor se mantendrá abierto hasta que se conecte 1 cliente mínimo
            // Al acabarse este tiempo si no se conecto nadie, saltará SocketTimeoutException
            socketServidor.setSoTimeout(10000);
            System.out.println("Servidor a la escucha...");

            while (encendido) {
                try {
                    // Muestra un texto inicial para indicar que no hay ningún cliente conectado
                    if (contador_clientes.get() == 0) {
                        System.out.println("No hay nadie por aquí");
                    }
                    // Acepta al cliente
                    Socket socketCliente = socketServidor.accept();
                    // Amplía el contador de clientes y lo muestra por consola
                    contador_clientes.incrementAndGet();
                    // Muestra en pantalla el total de clientes conectados
                    System.out.println("Actualmente hay : " + contador_clientes.get()+ " usuarios en este chat");

                    // Atender a cada hilo independientemente
                    AdministracionClientes administracionClientes = new AdministracionClientes(socketCliente, servidor);
                    servidor.agregarHiloCliente(administracionClientes);
                    // Asigna una hilo a cada cliente para administrar tareas
                    poolHilos.execute(administracionClientes);

                }catch (SocketTimeoutException e){
                    // Cuando no se conecta durante un 10 segundos un cliente salta esta Exception, cerrando el servidor
                    if(contador_clientes.get()==0){
                        System.out.println("Cerrando el servidor...");
                        encendido = false; // Evita que vuelva a ejecutar el while
                    }
                    else{
                        System.out.println();// Mientras el tiempo de espera hasta que se cierre el servidor(10s) no muestra nada
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
            if (!socketServidor.isClosed()) {
                socketServidor.close();
            }
            System.out.println("Servidor. Fin del programa.");
        }
    }
}
