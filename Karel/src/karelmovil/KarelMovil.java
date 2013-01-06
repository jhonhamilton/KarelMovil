/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package karelmovil;

/**
 *
 * @author abraham
 */

import grammar.Ejecutable;

import java.io.*;

import json.JSONException;
import json.JSONObject;

public class KarelMovil {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        
        System.out.println("Escribe la ruta del archivo:");
        String nombre_archivo = "";
        try{
            nombre_archivo = br.readLine();
        } catch(IOException e){
            System.out.println("Un pinchi error raro");
        }
        File archivo = new File(nombre_archivo);
        KGrammar k;
        try{
            FileReader fr = new FileReader(archivo);
            BufferedReader br_ = new BufferedReader(fr);
            k = new KGrammar(br_, true);
            try{
                k.verificar_sintaxis();
                System.out.println("Sintaxis correcta");
                Ejecutable exe = k.expandir_arbol();
                
                KWorld mundo = new KWorld();
                mundo.conmuta_pared(new KPosition(1, 1), KWorld.NORTE);
                
                KRunner runner = new KRunner(exe, mundo, 200, 200, 200);
                runner.run();
                if(runner.estado == KRunner.ESTADO_OK){
                	System.out.println("Programa ejecutado");
                } else {
                	System.out.print("ERROR: "+runner.mensaje);
                }
                try{
                	JSONObject cosa = runner.getMundo().exporta_mundo();
                	String res = cosa.toString();
                	
                	File f = new File("/home/abraham/resultado.json");
                	FileWriter fw = new FileWriter(f);
                	fw.write(res);
                	fw.close();
                	System.out.println("Mundo exportado");
                } catch (JSONException e){
                	System.out.println(e.getMessage());
                } catch (IOException e){
                	System.out.println(e.getMessage());
                }
            }catch(KarelException e){
                System.out.println(e.getMessage()+" en la línea "+k.token_actual.linea+" columna "+k.token_actual.columna);
            }
        } catch(FileNotFoundException e){
            System.out.println("Archivo no encontrado");
        }
    }
}
