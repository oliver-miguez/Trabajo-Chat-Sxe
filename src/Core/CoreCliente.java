package Core;

import Conexion.ClienteConexion;
import Hilos.HiloEscuchaCliente; // Importar el nuevo hilo de escucha

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Administra el funcionamiento de los clientes
 */
public class CoreCliente {
    // Conexión
    private static final String HOST = "localhost";
    private static final int PUERTO = 6666;

    // Nombre de cada Hilo
    public static String nombre = "";

    /**
     * Punto de entrada principal para la aplicación del cliente.
     * Establece la conexión con el servidor, solicita un nombre de usuario,
     * inicia un hilo para escuchar los mensajes del servidor y entra en un bucle
     * para leer la entrada del usuario y enviarla al servidor.
     * @param args Argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        ClienteConexion cliente = new ClienteConexion(HOST, PUERTO);
        Scanner sc = new Scanner(System.in);
        String mensaje_chat = "";

        // Evita un error en caso de que no se logre conectar un cliente
        if (!cliente.establecer_conexion()){
            System.out.println("No se puedo conectar con el servidor. Cerrando");
            sc.close();
            return;
        }

        // Nombre de cada usuario
        System.out.println("Introduce tu nickname: ");
        String nickname = sc.nextLine();
        nombre = nickname;

        // Iniciar hilo para escuchar mensajes del servidor
        BufferedReader lectorServidor = cliente.getLector();
        Thread hiloEscucha = new Thread(new HiloEscuchaCliente(lectorServidor));
        hiloEscucha.start();

        PrintWriter escritorServidor = cliente.getEscritor();
        escritorServidor.println(nombre); // Enviar nickname inmediatamente después de obtener el escritor

        System.out.println("Escribe tu mensaje (o '/salir' para desconectar):");
        System.out.println("Escribe '/lista' para ver todos los clientes conectados en el servidor" );
        System.out.println("Escribe '/ping' para verificar la conexión con el servidor" );

        // Administración de introducción de datos o mensaje de cada cliente
        while (true) {
            mensaje_chat = sc.nextLine(); // Permite introducir el mensaje

            // Si el mensaje es salir , cierra el cliente
            if (mensaje_chat.equalsIgnoreCase("/salir")) {
                escritorServidor.println("/salir");
                break;
            } else if (mensaje_chat.equalsIgnoreCase("/lista")) {
                escritorServidor.println("/lista");
            } else if (mensaje_chat.equalsIgnoreCase("/ping")) {
                escritorServidor.println("/ping");
            } else {
                escritorServidor.println(mensaje_chat);
            }
        }

        //Cerrar Cliente
        cliente.cerrar_conexion();
        sc.close();
        hiloEscucha.interrupt(); // Interrumpir el hilo de escucha al salir
    }
}
