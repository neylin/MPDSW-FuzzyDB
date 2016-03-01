/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.Helper;
import fuzzy.database.Connector;
import fuzzy.helpers.Printer;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.ResultSet;
import junit.framework.AssertionFailedError;
import static org.junit.Assert.*;


/**
 *
 * @author timauryc
 */
public class GroupByTest {

    protected static Connector connector;
    
    public GroupByTest(){
    
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        connector = new Connector();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws SQLException { 
        connector.setSchema("test_repuestos");
        Helper.setConnector(connector);
    }

    @After
    public void tearDown() {
    }
    
   
    //Group of queries meant to have success
    
    // @Test
    private String[] Input = {
        "SELECT nombre FROM test_repuestos.repuestos GROUP BY ciudad",
        "SELECT ciudad FROM test_repuestos.repuestos GROUP BY ciudad",
        "SELECT * FROM test_repuestos.repuestos GROUP BY ciudad",
        "SELECT nombre, direccion, telefono, ciudad, concesionario FROM test_repuestos.repuestos GROUP BY ciudad", 
        "SELECT nombre AS n, direccion AS d, telefono AS t, ciudad AS c, concesionario AS co FROM test_repuestos.repuestos GROUP BY ciudad", 
        "SELECT * FROM test_repuestos.repuestos AS t  GROUP BY ciudad",
        "SELECT t.nombre, t.direccion, t.telefono, t.ciudad, t.concesionario FROM test_repuestos.repuestos AS t GROUP BY ciudad", 
        "SELECT test_repuestos.repuestos.nombre, test_repuestos.repuestos.direccion, test_repuestos.repuestos.telefono, test_repuestos.repuestos.ciudad, test_repuestos.repuestos.concesionario FROM test_repuestos.repuestos GROUP BY ciudad", 
        "SELECT ciudad FROM test_repuestos.repuestos WHERE ciudad  = 'Caracas' OR ciudad = 'Barcelona' GROUP BY ciudad",
        "SELECT ciudad FROM test_repuestos.repuestos WHERE ciudad  LIKE 'B%' GROUP BY ciudad",       
        "SELECT nombre FROM test_repuestos.repuestos GROUP BY SIMILAR ciudad",
        "SELECT ciudad, COUNT(*) FROM test_repuestos.repuestos GROUP BY SIMILAR ciudad ",
        "SELECT ciudad FROM test_repuestos.repuestos GROUP BY SIMILAR ciudad HAVING COUNT(*)>2",
        "SELECT * FROM test_repuestos.repuestos GROUP BY SIMILAR ciudad",
        "SELECT nombre, direccion, telefono, ciudad, concesionario FROM test_repuestos.repuestos GROUP BY SIMILAR ciudad",
        "SELECT direccion, nombre, ciudad, telefono, concesionario FROM test_repuestos.repuestos GROUP BY SIMILAR ciudad",
        "SELECT direccion AS d , nombre AS n, ciudad AS  c, telefono AS t, concesionario AS co FROM test_repuestos.repuestos AS t GROUP BY SIMILAR ciudad",
        "SELECT t.direccion AS d , t.nombre AS n, t.ciudad AS  c, t.telefono AS t, t.concesionario AS co FROM test_repuestos.repuestos AS t GROUP BY SIMILAR ciudad",
        "SELECT ciudad FROM test_repuestos.repuestos GROUP BY SIMILAR ciudad HAVING ciudad LIKE 'C%'",
        "SELECT ciudad FROM test_repuestos.repuestos WHERE ciudad LIKE 'C%' GROUP BY SIMILAR ciudad ",
        "SELECT ciudad FROM test_repuestos.repuestos WHERE concesionario IN (SELECT nombre FROM concesionarios WHERE dueno LIKE 'B%') GROUP BY SIMILAR ciudad",
        //Esta funciona bien si la paso directamente a la db (si el atributo databaseName de conector esta especificado en test_repuestos), por aca no esta funcionando (ver luego)
        "SELECT ciudad, dueno FROM test_repuestos.repuestos JOIN concesionarios ON concesionario = concesionarios.nombre GROUP BY SIMILAR ciudad",
        
        //A partir de aca no esta usando el schema_name
        "SELECT nombre FROM repuestos GROUP BY ciudad",
        "SELECT ciudad FROM repuestos GROUP BY ciudad",
        "SELECT * FROM repuestos GROUP BY ciudad",
        "SELECT nombre, direccion, telefono, ciudad, concesionario FROM repuestos GROUP BY ciudad", 
        "SELECT nombre AS n, direccion AS d, telefono AS t, ciudad AS c, concesionario AS co FROM repuestos GROUP BY ciudad", 
        "SELECT * FROM repuestos AS t  GROUP BY ciudad",
        "SELECT t.nombre, t.direccion, t.telefono, t.ciudad, t.concesionario FROM repuestos AS t GROUP BY ciudad", 
        "SELECT repuestos.nombre, repuestos.direccion, repuestos.telefono, repuestos.ciudad, repuestos.concesionario FROM repuestos GROUP BY ciudad", 
        "SELECT ciudad FROM repuestos WHERE ciudad  = 'Caracas' OR ciudad = 'Barcelona' GROUP BY ciudad",
        "SELECT ciudad FROM repuestos WHERE ciudad  LIKE 'B%' GROUP BY ciudad",       
        "SELECT nombre FROM repuestos GROUP BY SIMILAR ciudad",
        "SELECT ciudad, COUNT(*) FROM repuestos GROUP BY SIMILAR ciudad ",
        "SELECT ciudad FROM repuestos GROUP BY SIMILAR ciudad HAVING COUNT(*)>2",
        "SELECT * FROM repuestos GROUP BY SIMILAR ciudad",
        "SELECT nombre, direccion, telefono, ciudad, concesionario FROM repuestos GROUP BY SIMILAR ciudad",
        "SELECT direccion, nombre, ciudad, telefono, concesionario FROM repuestos GROUP BY SIMILAR ciudad",
        "SELECT direccion AS d , nombre AS n, ciudad AS  c, telefono AS t, concesionario AS co FROM repuestos AS t GROUP BY SIMILAR ciudad",
        "SELECT t.direccion AS d , t.nombre AS n, t.ciudad AS  c, t.telefono AS t, t.concesionario AS co FROM repuestos AS t GROUP BY SIMILAR ciudad",
        "SELECT ciudad FROM repuestos GROUP BY SIMILAR ciudad HAVING ciudad LIKE 'C%'",
        "SELECT ciudad FROM repuestos WHERE ciudad LIKE 'C%' GROUP BY SIMILAR ciudad",
        "SELECT ciudad FROM repuestos WHERE concesionario IN (SELECT nombre FROM concesionarios WHERE dueno LIKE 'B%') GROUP BY SIMILAR ciudad",
        "SELECT ciudad, dueno FROM repuestos JOIN concesionarios ON concesionario = concesionarios.nombre  GROUP BY SIMILAR ciudad",
    };
    
    
    //Expected output for the queries translation, used for resultSets comparation
    private String[] expectedOutput = {
        "SELECT nombre FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT L1.label_name AS ciudad FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre AS n, direccion AS d, telefono AS t, L1.label_name AS c, concesionario AS co FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (test_repuestos.repuestos AS t LEFT JOIN information_schema_fuzzy.labels AS L1 ON t.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT t.nombre, t.direccion, t.telefono, L1.label_name AS ciudad, t.concesionario FROM (test_repuestos.repuestos AS t LEFT JOIN information_schema_fuzzy.labels AS L1 ON t.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT L1.label_name AS ciudad FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) WHERE (L1.label_name  = 'Caracas' OR  L1.label_name  = 'Barcelona') GROUP BY L1.label_name",
        "SELECT L1.label_name AS ciudad FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) WHERE L1.label_name LIKE 'B%' GROUP BY L1.label_name",  
        "SELECT nombre FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",        
        "SELECT L1.label_name, SUM(S1.value) FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT L1.label_name FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name HAVING SUM(S1.value) > 2",
        "SELECT nombre, direccion, telefono,  L1.label_name as ciudad, concesionario FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono,  L1.label_name as ciudad, concesionario FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT direccion, nombre, L1.label_name as ciudad, telefono, concesionario FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT direccion AS d, nombre AS n, L1.label_name as c, telefono AS t, concesionario AS c FROM test_repuestos.repuestos AS t JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = t.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT t.direccion AS d, t.nombre AS n, L1.label_name as c, t.telefono AS t, t.concesionario AS c FROM test_repuestos.repuestos AS t JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = t.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT L1.label_name FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name HAVING L1.label_name LIKE 'C%'",
        "SELECT L1.label_name FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) WHERE L1.label_name LIKE 'C%' GROUP BY L1.label_name",
        "SELECT L1.label_name FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) WHERE concesionario IN (SELECT nombre FROM concesionarios WHERE dueno LIKE 'B%') GROUP BY L1.label_name",
        "SELECT L1.label_name, dueno FROM test_repuestos.repuestos JOIN information_schema_fuzzy.labels AS L1  JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad) AND S1.value <> 0) JOIN test_repuestos.concesionarios ON concesionario = test_repuestos.concesionarios.nombre GROUP BY L1.label_name",
        //A partir de aca no esta usando el schema_name
        "SELECT nombre FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT L1.label_name AS ciudad FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre AS n, direccion AS d, telefono AS t, L1.label_name AS c, concesionario AS co FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (repuestos AS t LEFT JOIN information_schema_fuzzy.labels AS L1 ON t.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT t.nombre, t.direccion, t.telefono, L1.label_name AS ciudad, t.concesionario FROM (repuestos AS t LEFT JOIN information_schema_fuzzy.labels AS L1 ON t.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono, L1.label_name AS ciudad, concesionario FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) GROUP BY L1.label_name",
        "SELECT L1.label_name AS ciudad FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) WHERE (L1.label_name  = 'Caracas' OR  L1.label_name  = 'Barcelona') GROUP BY L1.label_name",
        "SELECT L1.label_name AS ciudad FROM (repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON repuestos.ciudad = L1.label_id) WHERE L1.label_name LIKE 'B%' GROUP BY L1.label_name",  
        "SELECT nombre FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",        
        "SELECT L1.label_name, SUM(S1.value) FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT L1.label_name FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name HAVING SUM(S1.value) > 2",
        "SELECT nombre, direccion, telefono,  L1.label_name as ciudad, concesionario FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT nombre, direccion, telefono,  L1.label_name as ciudad, concesionario FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT direccion, nombre, L1.label_name as ciudad, telefono, concesionario FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT direccion AS d, nombre AS n, L1.label_name as c, telefono AS t, concesionario AS c FROM repuestos AS t JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = t.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT t.direccion AS d, t.nombre AS n, L1.label_name as c, t.telefono AS t, t.concesionario AS c FROM repuestos AS t JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = t.ciudad) AND S1.value <> 0) GROUP BY L1.label_name",
        "SELECT L1.label_name FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) GROUP BY L1.label_name HAVING L1.label_name LIKE 'C%'",
        "SELECT L1.label_name FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) WHERE L1.label_name LIKE 'C%' GROUP BY L1.label_name",
        "SELECT L1.label_name FROM repuestos JOIN information_schema_fuzzy.labels AS L1 JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) WHERE concesionario IN (SELECT nombre FROM concesionarios WHERE dueno LIKE 'B%') GROUP BY L1.label_name",
        "SELECT L1.label_name, dueno FROM repuestos JOIN information_schema_fuzzy.labels AS L1  JOIN information_schema_fuzzy.similarities AS S1 ON ((S1.label1_id = L1.label_id AND S1.label2_id = repuestos.ciudad) AND S1.value <> 0) JOIN concesionarios ON concesionario = concesionarios.nombre GROUP BY L1.label_name",
    };
    
    
    //Fail messages for Input
    private String[] queryDesc = {
        "Fallo de consulta 1, consulta basica del GROUP BY por etiquetas con columna no difusa en el SELECT ",
        "Fallo de consulta 2, consulta del GROUP BY por etiquetas con columna difusa en el SELECT ",
        "Fallo de consulta 3, consulta del GROUP BY por etiquetas con (*) en el SELECT ",
        "Fallo de consulta 4, consulta del GROUP BY por etiquetas con todas las columnas solicitadas en el SELECT",
        "Fallo de consulta 5, consulta del GROUP BY por etiquetas con todas las columnas solicitadas (utilizando alias) en el SELECT",
        "Fallo de consulta 6, consulta del GROUP BY por etiquetas con (*) en el SELECT y alias en la tabla ",
        "Fallo de consulta 7, consulta del GROUP BY por etiquetas con todas las columnas solicitadas en el SELECT, utilizando aliasTabla.columna",
        "Fallo de consulta 8, consulta del GROUP BY por etiquetas con todas las columnas solicitadas en el SELECT, utilizando esquemaTabla.tabla.columna",
        "Fallo de consulta 9, consulta del GROUP BY por etiquetas con fuzzy column en el select y uso del WHERE sobre columna difusa",
        "Fallo de consulta 10, consulta del GROUP BY por etiquetas con fuzzy column en el select y uso del WHERE y LIKE sobre columna difusa",
        "Fallo de consulta 11, consulta basica del GROUP BY por similitud",
        "Fallo de consulta 12, consulta del GROUP BY por similitud con uso del count",
        "Fallo de consulta 13, consulta del GROUP BY por similitud con uso del HAVING COUNT (*)",
        "Fallo de consulta 14, consulta del GROUP BY por similitud con uso del wildCard (*)",
        "Fallo de consulta 15, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla",
        "Fallo de consulta 16, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla en orden determinado",
        "Fallo de consulta 17, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla en orden determinado y uso de aliases",
        "Fallo de consulta 18, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla en orden determinado y uso de aliases (columnas y tabla)",
        "Fallo de consulta 19, consulta del GROUP BY por similitud con uso de HAVING  ",
        "Fallo de consulta 20, consulta del GROUP BY por similitud con uso de WHERE",
        "Fallo de consulta 21, consulta del GROUP BY por similitud con uso de WHERE",
        "Fallo de consulta 22, consulta del GROUP BY por similitud con uso de JOIN con la tabla concesionario",
        //A partir de aca no esta usando el schema_name
        "Fallo de consulta 23, consulta basica del GROUP BY por etiquetas con columna no difusa en el SELECT ",
        "Fallo de consulta 24, consulta del GROUP BY por etiquetas con columna difusa en el SELECT ",
        "Fallo de consulta 25, consulta del GROUP BY por etiquetas con (*) en el SELECT ",
        "Fallo de consulta 26, consulta del GROUP BY por etiquetas con todas las columnas solicitadas en el SELECT",
        "Fallo de consulta 27, consulta del GROUP BY por etiquetas con todas las columnas solicitadas (utilizando alias) en el SELECT",
        "Fallo de consulta 28, consulta del GROUP BY por etiquetas con (*) en el SELECT y alias en la tabla ",
        "Fallo de consulta 29, consulta del GROUP BY por etiquetas con todas las columnas solicitadas en el SELECT, utilizando aliasTabla.columna",
        "Fallo de consulta 30, consulta del GROUP BY por etiquetas con todas las columnas solicitadas en el SELECT, utilizando esquemaTabla.tabla.columna",
        "Fallo de consulta 31, consulta del GROUP BY por etiquetas con fuzzy column en el select y uso del WHERE sobre columna difusa",
        "Fallo de consulta 32, consulta del GROUP BY por etiquetas con fuzzy column en el select y uso del WHERE y LIKE sobre columna difusa",
        "Fallo de consulta 33, consulta basica del GROUP BY por similitud",
        "Fallo de consulta 34, consulta del GROUP BY por similitud con uso del count",
        "Fallo de consulta 35, consulta del GROUP BY por similitud con uso del HAVING COUNT (*)",
        "Fallo de consulta 36, consulta del GROUP BY por similitud con uso del wildCard (*)",
        "Fallo de consulta 37, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla",
        "Fallo de consulta 38, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla en orden determinado",
        "Fallo de consulta 39, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla en orden determinado y uso de aliases",
        "Fallo de consulta 40, consulta del GROUP BY por similitud con referencia directa a las columnas de la tabla en orden determinado y uso de aliases (columnas y tabla)",
        "Fallo de consulta 41, consulta del GROUP BY por similitud con uso de HAVING  ",
        "Fallo de consulta 42, consulta del GROUP BY por similitud con uso de WHERE",
        "Fallo de consulta 43, consulta del GROUP BY por similitud con uso de WHERE",
        "Fallo de consulta 44, consulta del GROUP BY por similitud con uso de JOIN con la tabla concesionario",
    };
    
    //Results array for meant to success input
    private String[] Results = new String[Input.length];
    
    //NonSucces imput strings, they should fail
    private String[] NonInput = {};
    
    //Expected behavior from NonSuccesInput
    private String[] NonSuccessBitacora = {};
    
    
    
    //Testing SuccesInput by comparing resultSets
        
    @Test
    public void testSuccesInput() throws SQLException {
        for (int i = 0 ; i < Input.length; ++i) {
            try {
                Helper testHelper = new Helper(queryDesc[i], Input[i]);
                //Getting the resultSet
                connector.execute(Input[i]);
                ResultSet actual = connector.getResultSet();
                ResultSet expected = connector.executeRawQuery(expectedOutput[i]);
                testHelper.compareResults(actual,expected);
            } catch (SQLException e) {
                Printer.printSQLErrors(e);
                System.out.println(queryDesc[i]);
                assertTrue("Translation "+ i + "failed", false);
            } catch (AssertionFailedError a) {
                System.out.println("Original: " + Input[i]);
                try {
                    System.out.println("Translated: " + connector.translate(Input[i]).sql);
                } catch (SQLException _) {}
                System.out.println("Expected: " + expectedOutput[i]);
                System.out.println(queryDesc[i]);
                throw a;
            }
        }
    }
    
    
    /*@Test
    public void testNonSuccesInput() throws SQLException {
        Helper testHelper = new Helper();
        for (int i = 0 ; i < NonInput.length; ++i) {
            System.out.println("RESULT:" + NonSuccessBitacora[i]);
            testHelper.translateAndFailOnErrors(Input[i]);
        }
        
        testHelper.showResults(Results);
    }*/

}
