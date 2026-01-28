package Hilos;

import Core.CoreCliente;
import Core.CoreServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AdministracionClientes implements Runnable {

    private final Socket socketCliente;

    public AdministracionClientes(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        String datos_recibidos;
        try {
            System.out.println("Un usuario se ha conectado...");
            BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream(),true);

            String ip_cliente = socketCliente.getInetAddress().getHostAddress();
            //System.out.println("Cliente conectado: "+ ip_cliente);

            while (true){
                datos_recibidos = lector.readLine();

                if (datos_recibidos == null){
                    break;
                }
               datos_recibidos = datos_recibidos.trim();

                if (datos_recibidos.equals("/salir")){
                    System.out.println("Cliente "+ CoreCliente.nombre + " solicitó salir del chat");
                    break;
                }

                System.out.println(datos_recibidos);
            }

        }catch (IOException e){
            System.out.println("Error con la ejecución del hilo");
        }
        finally {
            // Resta al contador de clientes activos en el chat
            CoreServidor.contador_clientes.decrementAndGet();
            // Asegurarse de que cierre el socket del cliente
            try {
                if (socketCliente != null && !socketCliente.isClosed()) {
                    socketCliente.close();
                }
            } catch (IOException e) {
                System.out.println("Erro al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }
}
