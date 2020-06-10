
import java.util.ArrayList;

import java.sql.Connection;  

import ElsMeusBeans.Comanda;
import ElsMeusBeans.Venda;
import ElsMeusBeans.Producte;
import ElsMeusBeans.BaseDades;
import java.sql.SQLException;
import java.util.Scanner;

public class Exemple {
    
    static BaseDades bd;
    
    public static void main(String[] args) throws SQLException, Exception {

        String urldb = "jdbc:sqlite:C:\\Users\\Izan\\Desktop\\BaseSQL.db.sqbpro";
        String usuari = "root";
        String contrasenya = "";
        String driver = "com.mysql.jdbc.Driver";
        Scanner teclado = new Scanner(System.in);
        boolean entrarMenu = true;
        int opcionMenu = 0;
        
        //Es crear un objecte BaseDades
        bd = new BaseDades(urldb, usuari, contrasenya, driver);
        bd.setCrearConnexio();//Es crea la connexió a la base de dades
        
        if (bd.getCrearConnexio()) {
            System.out.println("Connectat");
            
            System.out.println("======================================");
            System.out.println("LLISTA INICIAL DE PRODUCTES");
            VeureProductes(bd);  
            
             while(entrarMenu){
                System.out.println("1 = Vendre un producte");
                System.out.println("2 = Afegir un producte");
                
                opcionMenu = teclado.nextInt();
                
                if(opcionMenu == 1){
                    //Crea una venda
                    System.out.println("======================================");                   
            
                    System.out.println("Introduix la id del producte a vendre");
                    int idproducte = teclado.nextInt();
                    System.out.println("Introdueix la quantitat del producte a vendre");
                    int quantitatProducte = teclado.nextInt();
                    System.out.println("ES CREA VENDA DE ID" + idproducte +  "AMB QUANTITAT " + quantitatProducte);
                    CrearVenda(bd, idproducte, quantitatProducte);//Si no hi ha estoc no es crea venda
            
                    System.out.println("======================================");
                    System.out.println("LLISTA DE PRODUCTES DESPRÉS DE CREAR VENDA");
                    VeureProductes(bd);
            
                    System.out.println("======================================");
                    System.out.println("LLISTA DE VENDES");
                    VeureVendes(bd);
            
                    System.out.println("======================================");
                    System.out.println("LLISTA DE COMANDES");
                    VeureComandes(bd);
                    entrarMenu = false;
                }else if(opcionMenu == 2){
                    addProducte(teclado);
                    entrarMenu = false;
                }else{
                    System.out.println("Opcion no valida, vuelve a seleccionar una opcion valida");
                }
            }    
        } else {
            System.out.println("No connectat a res");
        }
        //Tancar connexio
        bd.tancarConnexio();
        
    }//Fi main
    
    //--------------------------------------------------------------------------
    //Mostrar els productes
    private static void VeureProductes (BaseDades bd) {
        ArrayList <Producte> llista = new ArrayList <Producte>();
        llista = bd.consultaPro("SELECT * FROM PRODUCTE");
        if (llista != null)
            for (int i = 0; i<llista.size(); i++) {
                Producte p = (Producte) llista.get(i);
                System.out.println("ID=>"+p.getIdproducte()+": "+p.getDescripcio()+"* Estoc: "+p.getStockactual()+"* Pvp: "+p.getPvp()+" Estoc Mínim: "+p.getStockminim());
            }
    }//Fi VeureProductes
    
    //--------------------------------------------------------------------------
    //S'insereix una venda
    private static void CrearVenda (BaseDades bd, int idproducte, int quantitat) {
        Producte prod = bd.consultarUnProducte(idproducte);
        java.sql.Date dataActual = getCurrentDate();//Data SQL
        if (prod != null) {
            if (bd.actualitzarStock(prod, quantitat, dataActual)>0) {//Hi ha estoc
                String taula = "VENDES";
                int idvenda = bd.obtenirUltimID(taula);
                Venda ven = new Venda(idvenda, prod.getIdproducte(), dataActual, quantitat);

                if (bd.inserirVenda(ven)>0)
                    System.out.println("VENDA INSERIDA...");
                    
            } else
                System.out.println("NO ES POT FER LA VENDA, NO HI HA ESTOC...");
                
        } else {
            System.out.println("NO HI HA EL PRODUCTE");
            
        }
    }//Fi CrearVenda
    
     //-------------------------------------------------------------------------
    //Veure comandes creades
    private static void VeureComandes (BaseDades bd) {
        ArrayList <Comanda> llista = new ArrayList <Comanda>();
        llista = bd.consultaCom("SELECT * FROM COMANDES");
        if (llista != null)
            for (int i = 0; i<llista.size(); i++) {
                Comanda c = (Comanda) llista.get(i);
                Producte prod = bd.consultarUnProducte(c.getIdproducte());
                 System.out.println("ID Comanda=>"+c.getNumcomanda()+"* Producte: "+prod.getDescripcio()+"* Quantitat: "+c.getQuantitat()+"* Data: "+c.getData());
            }
    }//Fi VeureComandes
    
     //-------------------------------------------------------------------------
    //Veure vendes creades
    private static void VeureVendes (BaseDades bd) {
        ArrayList <Venda> llista = new ArrayList <Venda>();
        llista = bd.consultaVen("SELECT * FROM VENDES");
        if (llista != null)
            for (int i = 0; i<llista.size(); i++) {
                Venda p = (Venda) llista.get(i);
                Producte prod = bd.consultarUnProducte(p.getIdproducte());
                System.out.println("ID Venda =>"+p.getNumvenda()+"* Producte: "+prod.getDescripcio()+"* Quantitat: "+p.getQuantitat()+"* Data: "+p.getDatavenda());
            }
    }//Fi VeureVendes
    
    //--------------------------------------------------------------------------
    //Obté la data actual
    private static java.sql.Date getCurrentDate() {
        java.util.Date avui = new java.util.Date();
        return new java.sql.Date(avui.getTime());
    }//Fi getCurrentDate

    private static void addProducte(Scanner teclado) throws SQLException, Exception {
        System.out.print("Introduce la id del producto a añadir");
        int idProd = teclado.nextInt();     
        System.out.print("Introduce una descripcion");
        String descripcionProducto = teclado.nextLine();
        
        System.out.print("Cantidad que hay del producto en stock");
        int stockProducto = teclado.nextInt();
        
        System.out.print("Introduce un stock minimo para el producto");
        int stockMinimo = teclado.nextInt();
        
        System.out.print("Introduce el precio del producto");
        float precio = teclado.nextFloat();
        
        Connection con = bd.getConnexio();
        java.sql.Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO PRODUCTE (ID, DESCRIPCIO, STOCKACTUAL, STOCKMINIM, PVP)" + " VALUES (" + idProd + " , \"" + descripcionProducto +"\" , " + stockProducto +" , " + stockMinimo + " , " + precio + ")");
    }

    private BaseDades BaseDades() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
}//Fi Exemple