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

        System.out.println("Escribe tu mensaje (o '/salir' para desconectar):");

        // Administración de introducción de datos o mensaje de cada cliente
        while (true) {
            mensaje_chat = sc.nextLine(); // Permite introducir el mensaje

            // Si el mensaje es salir , cierra el cliente
            if (mensaje_chat.equalsIgnoreCase("/salir")) {
                escritorServidor.println(nombre + " se ha desconectado.");
                break;
            }
            escritorServidor.println(nombre + ": " + mensaje_chat); // El PrinterWriter recibe los datos y se utilizan para enviarlos al Servidor
        }

        //Cerrar Cliente
        cliente.cerrar_conexion();
        sc.close();
        hiloEscucha.interrupt(); // Interrumpir el hilo de escucha al salir
    }
}
