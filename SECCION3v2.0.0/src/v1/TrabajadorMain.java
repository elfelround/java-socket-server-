/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ok3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 *   Funcionalidad
 *   1.Enviar mensaje 
 *   2.Recibir mensaje 
 * */
public class TrabajadorMain
{
    private int iteracion = 0;
    private int trabajadorAcabado = 0;
    private boolean siguienteIteracion = false;
    
    public static void main(String[] args) 
    {    
       TrabajadorMain tm = new TrabajadorMain();
    }
    
    TrabajadorMain()
    {
        iniciar();
    }

    public void iniciar()
    {
        ExecutorService pool = Executors.newFixedThreadPool(Constante.MAX_TRABAJADOR); // PARA HILOS
        
        for(int i = 0; i < Constante.MAX_TRABAJADOR; i++)
        {
           pool.execute(new TrabajadorThread(this));
        }
    }

    protected synchronized void escribir(String msg, DataOutputStream out) throws IOException 
    {
        System.out.println("Mensaje(ok) enviado: " + msg);
        out.writeUTF(msg);
        out.flush();
    }

    protected synchronized long enviaCoordenadas(String msg, DataOutputStream out) throws IOException
    {
        System.out.println("Mensaje(coordenada) enviado: " + msg);
        out.writeUTF(msg);
        out.flush();
        return System.currentTimeMillis();
    }
    
    protected synchronized int getIteracion() 
    {
        return iteracion;
    }

    protected synchronized void sumaIteracion()
    {
        ++trabajadorAcabado;
        if(trabajadorAcabado == Constante.MAX_TRABAJADOR)
        {
            trabajadorAcabado = 0;
            ++iteracion;
        }
    }

    protected boolean comprobarIteracion() 
    {
        return iteracion < Constante.ITERACIONES;
    }
}
