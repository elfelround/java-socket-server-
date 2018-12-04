/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ok3;

import java.io.DataInputStream;

/**
 *
 * @author YUYAN
 */
public class ServidorThread extends Thread
{
    private Trabajador trabajador = null;
    private Servidor servidor = null;
    private int posicion = 0;
    
    //CONTROL
    private static int coordenadasRecibidas = 0;
    private static int oks = 0;
    

    public ServidorThread(Trabajador t, Servidor s, int p) 
    {
        super();
        trabajador = t;
        servidor = s;
        posicion = p;
    }

    public void run() 
    {
        try 
        {
            boolean noAcabar = true;
            String msg = "";
            String[] s;
            
            DataInputStream in = trabajador.getLectura();
            
            while (noAcabar) 
            {
                //CONTROL
                System.out.println("\nCoordenadas recibidas por el servidor: " + coordenadasRecibidas);
                System.out.println("Oks recibidos y reenviados por el servidor: " + oks + "\n");
                
                msg = in.readUTF();
                while(!msg.substring(0,3).equals("ACK"))
                {
                    msg = in.readUTF();
                }
                
                s = msg.split(";");
                switch(s[0])
                {
                    case "ACK1":
                        System.out.println("Coordenada recibida del trabajador " + trabajador.getIdTrabajador() + ": " + msg);
                        trabajador.setCoordenada(s[1]);
                        
                        //CONTROL
                        ++coordenadasRecibidas;
                        
                        servidor.enviarATodos(posicion);
                    break;
                     case "ACK2":
                        if(!s[0].equals("ACK1"))
                        {
                            System.out.println("Mensaje OK reenviado: " + msg);
                            //reenviar a trabajador(de esta clase)
                            servidor.enviar(msg, s[3]);
                        }
                        //CONTROL
                        ++oks;
                     break;
                     default:
                        // System.out.println("tiempo recibido es : " + s[1]);
                         long num = Long.parseLong(s[1]);
                         servidor.sumarTiempo(num, trabajador.getIdGrupo());
                }
                
              //  System.out.println(" tiempo es " + servidor.getTime());
            }
        }
        catch (Exception e) 
        {
            System.out.println(" Excepcion: " + e);
        }
    }
}
