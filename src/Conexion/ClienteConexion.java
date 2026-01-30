package Conexion;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Permite conectar al servidor clientes.
 * MODIFICADO: Ahora utiliza SSLSocket para una conexión cifrada y segura.
 */
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
     * Establece la conexión segura (SSL/TLS) del cliente con el servidor.
     * @return true/false si se conecta o no.
     */
    public boolean establecer_conexion(){
        try{
            // INICIO DE LA CONFIGURACIÓN SSL PARA EL CLIENTE
            // Cargar el TrustStore: Usamos el mismo keystore.jks, pero ahora como almacén de confianza.
            // El cliente lo usa para verificar que el certificado del servidor es de confianza.
            char[] password = "admin123".toCharArray(); // La misma contraseña que usé en el comando keytool.
            KeyStore ts = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("keystore.jks"); // El archivo debe estar en la raíz del proyecto.
            ts.load(fis, password);

            // Configurar el TrustManagerFactory para gestionar los certificados de confianza.
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            // Configurar el SSLContext para usar el protocolo TLS y nuestros TrustManagers.
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, tmf.getTrustManagers(), null);

            // Usar la fábrica de SSLSocket para crear el socket seguro y conectarlo.
            SSLSocketFactory ssf = sc.getSocketFactory();
            socket = (SSLSocket) ssf.createSocket(HOST, PUERTO);

            // Para enviar mensajes a otros escritores
            escritor = new PrintWriter(socket.getOutputStream(), true);
            // Para recibir mensajes de otros escritores
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;

        } catch (Exception e) { // Capturamos Exception para cubrir errores de SSL (ej. certificado no confiable)
            System.out.println("Error al establecer la conexión SSL del cliente: "+e.getMessage());
            e.printStackTrace();
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
