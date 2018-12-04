/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ok3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * Recibir informacion del servidor
 *
 * @author YUYAN
 */
public class TrabajadorThread implements Runnable
{
    TrabajadorMain tm;
    //CONTROL
    private static int coordenadasOksEnviados = 0;
    private static int oksRecibidos = 0;

    TrabajadorThread(TrabajadorMain tm) 
    {
        this.tm = tm;
    }

    public void run() 
    {
        try 
        {
            Socket socket = new Socket("192.168.0.2", Constante.PORT);
            String msg = "ACK1;" + getCoordenadaAleatoria();
            int cuentaTrabajadores = 0, cuentaCoordenadas = 0;
            boolean noAcabar = true;
            String[] s;
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            long startTime = tm.enviaCoordenadas(msg, out);
            ++cuentaCoordenadas;
            
            while(noAcabar)
            {
                //CONTROL
                System.out.println("\nCoordenadas recibidas y oks enviados: " + coordenadasOksEnviados);
                System.out.println("Oks recibidos : " + oksRecibidos + "\n");
                
                do
                {
                    if(cuentaTrabajadores == Constante.MAX_TRABAJADOR - 1 && (cuentaCoordenadas < Constante.ITERACIONES))
                    {
                        cuentaTrabajadores = 0;
                        startTime = tm.enviaCoordenadas("ACK1;" + getCoordenadaAleatoria(), out);
                        ++cuentaCoordenadas;
                    }
                    msg = in.readUTF();
                }
                while(!msg.substring(0,3).equals("ACK"));
                
                s = msg.split(";");
                switch(s[0])
                {
                     case "ACK1":
                        System.out.println("Mensaje(coordenada) recibido: " + msg);
                        //CONTROL
                        ++coordenadasOksEnviados;
                        msg = "ACK2;OK;" + s[3] + ";" + s[2];
                        tm.escribir(msg, out);
                        
                     break;
                     case "ACK2":
                        //if(!s[0].equals("ACK1"))
                        //{
                            System.out.println("Mensaje(ok) recibido: " + msg);
                            ++cuentaTrabajadores;

                            //CONTROL
                            ++oksRecibidos;
                            System.out.println(cuentaTrabajadores);
                            if(cuentaTrabajadores == (Constante.MAX_TRABAJADOR/Constante.MAX_TRABAJADOR_GRUPO) - 1)
                            {
                                long num = (System.currentTimeMillis() - startTime);
                               System.out.println("El trabajador ha terminado la iteracion " + tm.getIteracion() + " con un tiempo de ejecucion de " + num + " milisegundos.");
                               out.writeUTF("ACK;" + num);
                               //System.out.println("Enviado + " + num);
                           
                               tm.sumaIteracion();
                            }
                       // }
                     break;
                }
            }
        }
        catch (Exception e) 
        {
            System.out.println("Excepcion : " + e);
        }
    }
    
    public static String getCoordenadaAleatoria()
    {
        Random random = new Random();
        int ptox, ptoy, ptoz;
        ptox = random.nextInt(Constante.MAX_POSICION+2)%(Constante.MAX_POSICION+1);
        ptoy =random.nextInt(Constante.MAX_POSICION+2)%(Constante.MAX_POSICION+1);
        ptoz = random.nextInt(Constante.MAX_POSICION+2)%(Constante.MAX_POSICION+1);
        String coordenada = "(" + ptox +", " + ptoy + ", " + ptoz + ") ";
        return coordenada;
    }
}
