/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PaquetePrincipal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author valen
 */
public class Hilo implements Runnable {

    private final Socket socCliente;

    public Hilo(Socket socCliente) {
        this.socCliente = socCliente;
    }

    @Override
    public void run() {
        System.out.println("Atendiendo al cliente...");

        try (
            InputStreamReader inSR = new InputStreamReader(socCliente.getInputStream());
            BufferedReader bufLeer = new BufferedReader(inSR);
            PrintWriter printWriter = new PrintWriter(socCliente.getOutputStream(), true)
        ) {
            String peticion = bufLeer.readLine();
            
            if (peticion == null || !peticion.startsWith("GET")) {
                return;
            }

            // Extraer ruta de la petición GET
            String ruta = peticion.split(" ")[1]; // Extrae lo que está entre "GET" y "HTTP/1.1"

            switch (ruta) {
                case "/":
                    responderCliente(printWriter, Paginas.html_index, Mensajes.lineaInicial_OK);
                    break;
                case "/quijote":
                    responderCliente(printWriter, Paginas.html_quijote, Mensajes.lineaInicial_OK);
                    break;
                default:
                    responderCliente(printWriter, Paginas.html_noEncontrado, Mensajes.lineaInicial_NotFound);
                    break;
            }

        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, "Error en la conexión con el cliente", ex);
        } finally {
            try {
                socCliente.close();
                System.out.println("Cliente atendido y conexión cerrada.");
            } catch (IOException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, "Error cerrando el socket", ex);
            }
        }
    }

    private void responderCliente(PrintWriter printWriter, String html, String status) {
        printWriter.println(status);
        printWriter.println(Paginas.primeraCabecera);
        printWriter.println("Content-Length: " + html.length());
        printWriter.println(); // Línea en blanco obligatoria en HTTP
        printWriter.println(html);
    }
}
