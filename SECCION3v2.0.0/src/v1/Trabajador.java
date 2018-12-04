/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ok3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author YUYAN
 */
public class Trabajador 
{
    private String coordenada;
    private Socket socket;
    private static int id_trabajador_prox;
    private final int id_trabajador;
    private  int id_grupo;
    
    private DataInputStream lee;
    private DataOutputStream esc;
   
    public Trabajador(Socket soc) throws IOException {
        socket = soc;
        id_trabajador = id_trabajador_prox;
        id_grupo = id_trabajador / Constante.MAX_TRABAJADOR_GRUPO;
        id_trabajador_prox += 1;
        
        lee= new DataInputStream(socket.getInputStream());  
	esc = new DataOutputStream(socket.getOutputStream()); 
    }
    
    public void setLiberarRecurso() throws IOException{
        socket.close();
        lee.close();
        esc.close();
    }
    public int getIdGrupo(){
        return id_grupo;
    }
    
    public DataInputStream getLectura() {
        return lee;
    }

    public void setLectura(DataInputStream br) {
        lee = br;
    }

    public int getIdTrabajador() {
        return id_trabajador;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket so) {
        socket = so;
    }

    public void setEscritura(DataOutputStream pw) {
        esc = pw;
    }

    public DataOutputStream getEscritura() {
        return esc;
    }
    
    public void setCoordenada(String c){
        coordenada = c;
    }

    public String getCoordenada() {
        return coordenada;
    }

    @Override
    public String toString() {
        String s = "Trabajador [id " + id_trabajador + ", coordenada "
                + coordenada + ", socket: " + socket + "]";
        return s;
    }
}
