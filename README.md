# Chat_SXE

Este es un proyecto de chat simple cliente-servidor desarrollado en Java. Permite a múltiples clientes conectarse a un servidor central y enviarse mensajes en tiempo real.

## Características

-   **Chat multi-cliente:** Múltiples usuarios pueden conectarse y chatear simultáneamente.
-   **Nombres de usuario:** Cada usuario tiene un nombre de usuario único en el chat.
-   **Comandos especiales:**
    -   `/salir`: Desconecta al usuario del chat.
    -   `/lista`: Muestra una lista de todos los usuarios conectados actualmente.
    -   `/ping`: El servidor responde con "pong" para verificar la conexión.

## Estructura del Proyecto

El proyecto está organizado en los siguientes paquetes y clases:

### `Core`

-   **`CoreCliente.java`**: Punto de entrada para la aplicación del cliente. Se encarga de:
    -   Establecer la conexión con el servidor.
    -   Solicitar al usuario un nombre de usuario.
    -   Iniciar un hilo para escuchar los mensajes del servidor.
    -   Leer la entrada del usuario y enviarla al servidor.

-   **`CoreServidor.java`**: Punto de entrada para la aplicación del servidor. Sus responsabilidades son:
    -   Esperar y aceptar nuevas conexiones de clientes.
    -   Crear un hilo (`AdministracionClientes`) para cada cliente conectado.
    -   Gestionar una lista de todos los clientes conectados.
    -   Difundir mensajes a todos los clientes.

### `Conexion`

-   **`ClienteConexion.java`**: Gestiona los detalles de la conexión del cliente con el servidor, incluyendo el establecimiento y cierre del socket, y la obtención de los streams de entrada y salida.

### `Hilos`

-   **`AdministracionClientes.java`**: Un hilo que se ejecuta para cada cliente conectado en el servidor. Se encarga de:
    -   Leer el nombre de usuario del cliente.
    -   Leer y procesar los mensajes y comandos enviados por el cliente.
    -   Gestionar la desconexión del cliente.

-   **`HiloEscuchaCliente.java`**: Un hilo que se ejecuta en el lado del cliente y escucha continuamente los mensajes que vienen del servidor, mostrándolos en la consola del cliente.

## Cómo ejecutar

1.  **Ejecutar el Servidor:**
    -   Compile y ejecute `CoreServidor.java`. El servidor se iniciará y esperará conexiones de clientes en el puerto 6666.

2.  **Ejecutar el Cliente:**
    -   Compile y ejecute `CoreCliente.java`.
    -   Se le pedirá que introduzca un nombre de usuario.
    -   Una vez conectado, puede empezar a enviar mensajes.

## Protocolo de Comunicación

1.  **Conexión y Nombre de Usuario:**
    -   El cliente se conecta al servidor.
    -   El cliente envía su nombre de usuario como el primer mensaje.

2.  **Mensajes de Chat:**
    -   Cualquier mensaje que no sea un comando se considera un mensaje de chat y se difunde a todos los demás clientes con el nombre de usuario del remitente.

3.  **Comandos:**
    -   Los comandos comienzan con una barra (`/`).
    -   El servidor los interpreta y realiza la acción correspondiente.

---

###  Comunicación Segura (SSL/TLS)

Este proyecto ha sido migrado para utilizar comunicación cifrada mediante SSL/TLS, garantizando la confidencialidad e integridad de los mensajes intercambiados entre el cliente y el servidor. Para ello, se utilizan `SSLServerSocket` en el servidor y `SSLSocket` en los clientes.

#### Requisitos para la Ejecución con SSL/TLS

Para que la comunicación cifrada funcione, es necesario un archivo `keystore.jks` que contiene el certificado del servidor y que los clientes utilizan como almacén de confianza.

**Generación del `keystore.jks`:**

El archivo `keystore.jks` ha sido generado automáticamente para este proyecto. Si por alguna razón necesitas generarlo de nuevo, puedes usar el siguiente comando desde la raíz del proyecto, asumiendo que tienes la carpeta `bin` de tu JDK en tu `PATH` o ejecutándolo desde esa carpeta `bin`:

```bash
keytool -genkeypair -alias sxe_chat -keyalg RSA -keysize 2048 -keystore keystore.jks -validity 365 -storepass admin123 -keypass admin123 -dname "CN=localhost" -deststoretype JKS
```

**Contraseña del Keystore/Truststore:**

La contraseña utilizada para el `keystore.jks` (tanto para el servidor como para el cliente) es: `admin123`. Esta contraseña está programada directamente en `CoreServidor.java` y `ClienteConexion.java`.

**Asegúrate de que el archivo `keystore.jks` se encuentre en la raíz del proyecto (`Chat_SXE/keystore.jks`) antes de ejecutar el servidor o los clientes.**