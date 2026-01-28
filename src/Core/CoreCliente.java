package Core;

import Conexion.ClienteConexion;
import Hilos.HiloEscuchaCliente; // Importar el nuevo hilo de escucha

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;

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


        if (!cliente.establecer_conexion()){
            System.out.println("No se puedo conectar con el servidor. Cerrando");
            sc.close(); // Asegurarse de cerrar el scanner
            return;
        }

        System.out.println("Introduce tu nickname: ");
        String nickname = sc.nextLine();
        nombre = nickname;

        // Iniciar hilo para escuchar mensajes del servidor
        BufferedReader lectorServidor = cliente.getLector();
        Thread hiloEscucha = new Thread(new HiloEscuchaCliente(lectorServidor));
        hiloEscucha.start();

        PrintWriter escritorServidor = cliente.getEscritor();

        System.out.println("Escribe tu mensaje (o '/salir' para desconectar):");
        while (true) {
            mensaje_chat = sc.nextLine();

            if (mensaje_chat.equalsIgnoreCase("/salir")) {
                escritorServidor.println(nombre + " se ha desconectado.");
                break;
            }
            escritorServidor.println(nombre + ": " + mensaje_chat);
        }

        //Cerrar Cliente
        cliente.cerrar_conexion();
        sc.close();
        hiloEscucha.interrupt(); // Interrumpir el hilo de escucha al salir
    }
}
