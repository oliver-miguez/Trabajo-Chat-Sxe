package Core;

import Conexion.ClienteConexion;

import java.util.Scanner;

public class CoreCliente {
    private static final String HOST = "localhost";
    private static final int PUERTO = 6666;

    public static String nombre = "";

    public static void main(String[] args) {
        ClienteConexion cliente = new ClienteConexion(HOST, PUERTO);
        Scanner sc = new Scanner(System.in);
        String mensaje_chat = "";
        String opcion_chat;

        if (!cliente.establecer_conexion()){
            System.out.println("No se puedo conectar con el servidor. Cerrando");
            return;
        }

        System.out.println("Introduce tu nickname: ");
        String nickname = sc.nextLine();
        nombre = nickname; // Para enviar al servidor o otros clientes
        mensaje_chat = mensaje_enviar(sc);

        while(!mensaje_chat.equals("/salir")) {
            cliente.enviar_recibir(nombre + ": " + mensaje_chat);
            mensaje_chat = mensaje_enviar(sc);

        }
        // Cuando el cliente sale del chat
        mensaje_chat = nombre + " salió del chat";
        cliente.enviar_recibir(mensaje_chat);

        //Cerrar Cliente
        cliente.cerrar_conexion();
        sc.close();
    }

    public static String mensaje_enviar(Scanner scanner){
        System.out.println(":"); // Apartado donde introducimos el mensaje que queremos enviar
        //scanner.nextLine();
        String msg_chat = scanner.nextLine();
        return msg_chat;
    }



}
