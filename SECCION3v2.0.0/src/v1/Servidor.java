/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ok3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 *
 * @author YUYAN
 */
public class Servidor 
{
    private static ArrayList<Trabajador> list;
    private static long[] tiempos;
    private static int[] contador;
   // private long tiempo;
    private static int cont, contIteracion;
    private long tiempoEnIteracion;
    private long tiempoTotal;
    Servidor()
    {
      //  tiempo = 0;
        //contador = 0;
        tiempoTotal = 0;
        tiempoEnIteracion = 0;
        contIteracion = 0;
        cont = 0;
        tiempos = new long[Constante.MAX_TRABAJADOR_GRUPO];
        contador = new int[Constante.MAX_TRABAJADOR_GRUPO];
        for(int i = 0; i < Constante.MAX_TRABAJADOR_GRUPO; i++){
            tiempos[i] = 0;
            contador[i] = 0;
        }
        try
        {
            list = new ArrayList<Trabajador>();
            ExecutorService pool = newCachedThreadPool(); // PARA HILOS
            ServerSocket servidor = new ServerSocket(Constante.PORT);
            System.out.println("El servidor está establecido...");
   
            list = aceptacionConexion(servidor); 
            iniciar(list, pool);
        }
        catch(IOException ie)
        {
            System.out.println("Error: " + ie.getMessage());
        }
    }
    
    public void iniciar(ArrayList<Trabajador> list, ExecutorService pool)
    {
        for (int i = 0; i < list.size(); ++i) 
        {
            pool.execute(new ServidorThread(list.get(i), this, i));
        }
    }
    
    private static ArrayList<Trabajador> aceptacionConexion(ServerSocket servidor)
    {
        int i = 0;
        ArrayList<Trabajador> list = new ArrayList<Trabajador>();
        try
        {
            while (i < Constante.MAX_TRABAJADOR) 
            {
                Socket socket = servidor.accept();
                //socket.setSoTimeout(5000);
                Trabajador t = new Trabajador(socket);
                System.out.println("Trabajador con id " + t.getIdTrabajador() + " y grupo " + t.getIdGrupo() + " esta conectando");
                list.add(t);
                i++;
            }
        }
        catch(Exception e)
        {
            System.out.println("Excepcion al crear trabajadores: " + e);
        }
            
        return list;
    }

    protected synchronized void enviarATodos(int indice) throws IOException 
    {
        String msg = "";
        for (int i = 0; i < list.size(); ++i)
        {     
            if (list.get(i).getIdTrabajador() != list.get(indice).getIdTrabajador() && list.get(i).getIdGrupo() == list.get(indice).getIdGrupo()) 
            {
                msg = "ACK1;" + list.get(indice).getCoordenada() + ";" + list.get(indice).getIdTrabajador() + ";" + list.get(i).getIdTrabajador();
                System.out.println("Trabajador " + list.get(indice).getIdTrabajador() + " ha enviado: " + msg);
                list.get(i).getEscritura().writeUTF(msg);
                list.get(i).getEscritura().flush();
            }
        }
        
        /*
        ArrayList<Trabajador> aux = new ArrayList<Trabajador>();
        for (int i = 0; i < list.size(); ++i)
        {     
            if (list.get(i).getIdTrabajador() != list.get(indice).getIdTrabajador() && list.get(i).getIdGrupo() == list.get(indice).getIdGrupo()) 
            {
                aux.add(list.get(i));
            }
        }
        
        
        new SocketSend(list.get(indice), aux);
        */
    }

    protected synchronized void enviar(String msg, String indiceString) throws IOException 
    {
        int indice = Integer.parseInt(indiceString);
        list.get(indice).getEscritura().writeUTF(msg);
        list.get(indice).getEscritura().flush();
        //new SocketSend(list.get(indice), msg);
    }
    
    protected synchronized void sumarTiempo(long num, int pos){
      
        tiempos[pos] += num;
        contador[pos]++;
        if(contador[pos] == (Constante.MAX_TRABAJADOR/Constante.MAX_TRABAJADOR_GRUPO ))
        {
            System.out.println("Tiempo total del grupo " + pos + " es : " + tiempos[pos]);
            contador[pos] = 0;
            tiempoEnIteracion+= tiempos[pos];
            tiempos[pos] = 0;
            cont++;
            if(cont == Constante.MAX_TRABAJADOR_GRUPO)
            {
                System.out.println("Ha terminado la iteracion con un tiempo total: " + tiempoEnIteracion);
                System.out.println("Tiempo medio de cada grupo " + (tiempoEnIteracion/Constante.MAX_TRABAJADOR_GRUPO));
                
                tiempoTotal += tiempoEnIteracion;
                tiempoEnIteracion = 0; //Tiempo de cada grupo
                contIteracion ++;
                if(contIteracion == Constante.ITERACIONES)
                    System.out.println("Tiempo total de todas iteraciones es " + contIteracion);
            }
           
            
    }}

}
