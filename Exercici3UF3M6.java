
package exercici3uf3m6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import org.xmldb.api.*;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class Exercici3UF3M6 {
	public static String driver = "org.exist.xmldb.DatabaseImpl";
	public static Collection col = null;
	public static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/pruebas";
	public static String usu = "admin";
	public static String usuPass = "admin";
	
	public static void main(String[] args) throws XMLDBException {
		Scanner teclado = new Scanner(System.in);
		String driver = "org.exist.xmldb.DatabaseImpl";
                boolean entrarMenu = true;
                int seleccioMenu = 0;

		try {
			Class cl = Class.forName(driver);
			Database database = (Database) cl.newInstance();
			DatabaseManager.registerDatabase(database);
		} catch (Exception e) {
			System.out.println("Error en inicialitzar la base de datos");
			e.printStackTrace();
		} col = DatabaseManager.getCollection(URI, usu, usuPass);
		if (col==null){
			System.out.println("*** LA COL·LECCIÓ NO EXISTEIX ***");
		}

		XPathQueryService servei =(XPathQueryService) col.getService("XPathQueryService", "1.0");
                
                while(entrarMenu){
                    System.out.println("1 = Mostrar empleats de departament");
                    System.out.println("2 = Insertar un departament");
                    System.out.println("3 = Esborrar un deparament");
                    System.out.println("4 = Modificar un departament");             
                    seleccioMenu = teclado.nextInt();
                    
                    if (seleccioMenu == 1){
                        MostrarEmpleatsPerDepartament(teclado,servei);
                    }else if (seleccioMenu == 2){
                        insereixdep(teclado,servei);
                    }else if (seleccioMenu == 3){
                        esborradep(teclado,servei);
                    }else if (seleccioMenu == 4){
                        modificaDep(teclado);
                    }else{
                        System.out.println("Has sortit del programa");
                        entrarMenu = false;
                    }
                }
		col.close();
	}

	private static void MostrarEmpleatsPerDepartament(Scanner teclado,XPathQueryService servei) throws XMLDBException{
		System.out.println("Diga el nombre del departamento");
		String departament = teclado.next();
		String xquerySent = "for $num in /departamentos/DEP_ROW[DNOMBRE='"+departament+"']/DEPT_NO"+" let $emp := /EMPLEADOS/EMP_ROW[DEPT_NO=$num] return $emp" ;
		ResourceSet resultatQuery = servei.query(xquerySent);
		ResourceIterator i;

		i = resultatQuery.getIterator();
		if (!i.hasMoreResources())
			System.out.println("LA CONSULTA NO TORNA RES");
		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			System.out.println((String)r.getContent());
		}
	}

        private static void insereixdep(Scanner teclado,XPathQueryService servei) throws XMLDBException {
		System.out.println("Nom del departament");
		String departament = teclado.next();
		System.out.println("Numero del departament");
		int numDepartament = teclado.nextInt();
		System.out.println("Lloc del departament");
		String llocDepartament = teclado.next();
		ResourceSet result = servei.query("update insert\n" +"<DEP_ROW><DEPT_NO>"+numDepartament+"</DEPT_NO>"+ "<DNOMBRE>"+departament+"</DNOMBRE>"
						+ "<LOC>"+llocDepartament+"</LOC></DEP_ROW> into /departamentos");
		System.out.println("S'ha insertat el departament");
	}
        
	private static void esborradep(Scanner teclado,XPathQueryService servei) throws XMLDBException {
		System.out.println("Num de departament a esborrar");
		int numDepartament = teclado.nextInt();
		ResourceSet result = servei.query("update delete /departamentos/DEP_ROW[DEPT_NO="+numDepartament+"]");
		System.out.println("S'ha esborrat el departament " + numDepartament);
	}

	private static void modificaDep(Scanner teclado)throws XMLDBException{
		System.out.println("Num del departament a modificar");
		int numDep = teclado.nextInt();
		System.out.println("Num que tindra ara el departament");
		int numNouDep = teclado.nextInt();
		System.out.println("nombre departamento:");
		String nomDepartament = teclado.nextLine();
		
		System.out.println("Lloc del departament");
		String llocDep = teclado.nextLine();
		
		String query = "update replace /departamentos/DEP_ROW[DEPT_NO ="+ numDep +"] with <DEP_ROW><DEPT_NO>" + numNouDep + "</DEPT_NO>" + "<DNOMBRE>" + nomDepartament + "</DNOMBRE><LOC>" + llocDep +"</LOC></DEP_ROW>";
		ferConsulta(query);
	}
	
	private static void ferConsulta(String query)throws XMLDBException {
		try {
			Class cl = Class.forName(driver);
			Database database = (Database) cl.newInstance();
			DatabaseManager.registerDatabase(database);
		} catch(Exception e) {
			System.out.println("Error en inicialitzar la base de dades eXist");
			e.printStackTrace();
		}
		col = DatabaseManager.getCollection(URI, usu, usuPass);
		if(col == null) 
			System.out.println("*** LA COLLECCIÓ NO EXISTEIX ***");
		XPathQueryService servei = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		ResourceSet result = servei.query(query);
		ResourceIterator i;
		i = result.getIterator();
		if(!i.hasMoreResources())
			System.out.println("LA CONSULTA NO TORNA RES");
		while(i.hasMoreResources()) {
			Resource r = i.nextResource();
			System.out.println((String)r.getContent());
		}

		col.close();
	}

}