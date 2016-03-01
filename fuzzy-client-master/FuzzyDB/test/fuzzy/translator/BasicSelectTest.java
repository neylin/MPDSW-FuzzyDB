/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.helpers.Logger;
import java.sql.SQLException;
import fuzzy.database.Connector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bishma-stornelli
 */
public class BasicSelectTest {

    protected static Connector connector;

    public BasicSelectTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        connector = new Connector();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    private String[] input = {
        //"SELECT concesionario, dueno FROM test_repuestos.repuestos  JOIN test_repuestos.concesionarios",
        "SELECT test_repuestos.repuestos.ciudad FROM test_repuestos.repuestos",
        "SELECT test_repuestos.repuestos.ciudad AS c FROM test_repuestos.repuestos",
        "SELECT test_repuestos.repuestos.nombre AS n FROM test_repuestos.repuestos",
        "SELECT * FROM test_repuestos.repuestos",
        "SELECT nombre AS n FROM test_repuestos.repuestos",
        "SELECT nombre AS n, telefono as t, direccion as d, ciudad as c, concesionario as con from test_repuestos.repuestos",
        "select telefono, direccion, concesionario, ciudad, nombre from test_repuestos.repuestos",
        "Select * from test_repuestos.repuestos where ciudad = \"Barcelona\"",
        "select nombre from test_repuestos.repuestos where ciudad = \"Barcelona\"",
        "select telefono, direccion, ciudad, concesionario, nombre from test_repuestos.repuestos where ciudad = \"Barcelona\" or concesionario = \"Mazda\"",
        "select telefono, direccion, ciudad, concesionario, nombre from test_repuestos.repuestos where ciudad = \"Valencia\" and concesionario = \"Mazona\"",
        "SELECT r1.ciudad, r2.ciudad FROM test_repuestos.repuestos AS r1, test_repuestos.repuestos AS r2 LIMIT 1",
    
    };
    private String[] expectedOutput = {
        //"SELECT concesionario, dueno FROM test_repuestos.repuestos JOIN test_repuestos.concesionarios",
        "SELECT L1.label_name AS ciudad FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id)",
        "SELECT L1.label_name AS c FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id)",
        "SELECT test_repuestos.repuestos.nombre AS n FROM test_repuestos.repuestos",
        "SELECT test_repuestos.repuestos.nombre, test_repuestos.repuestos.direccion, test_repuestos.repuestos.telefono, L1.label_name AS ciudad, test_repuestos.repuestos.concesionario FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id)",
        "SELECT nombre AS n FROM test_repuestos.repuestos",
        "SELECT nombre AS n, telefono AS t, direccion AS d, L1.label_name AS c, concesionario AS con FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id)",
        "SELECT telefono, direccion, concesionario, L1.label_name AS ciudad, nombre FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id)",
        "SELECT test_repuestos.repuestos.nombre, test_repuestos.repuestos.direccion, test_repuestos.repuestos.telefono, L1.label_name AS ciudad, test_repuestos.repuestos.concesionario FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) WHERE L1.label_name = \"Barcelona\"",
        "SELECT nombre FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) WHERE L1.label_name = \"Barcelona\"",
        "SELECT telefono, direccion, L1.label_name AS ciudad, concesionario, nombre FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) WHERE L1.label_name = \"Barcelona\" OR concesionario = \"Mazda\"",
        "SELECT telefono, direccion, L1.label_name AS ciudad, concesionario, nombre FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) WHERE L1.label_name = \"Valencia\" AND concesionario = \"Mazona\"",
        "SELECT L1.label_name AS ciudad, L2.label_name AS ciudad FROM (test_repuestos.repuestos AS r1 LEFT JOIN information_schema_fuzzy.labels AS L1 ON r1.ciudad = L1.label_id), (test_repuestos.repuestos AS r2 LEFT JOIN information_schema_fuzzy.labels AS L2 ON r2.ciudad = L2.label_id) LIMIT 1",
        
    };
    
    @Test
    public void testAll() throws SQLException {
        for (int i = 0 ; i < input.length ; ++i) {
            String translatedInput = connector.translate(input[i]).sql;
            Logger.debug(translatedInput);
            //expectedOutput[i].equalsIgnoreCase(translatedInput) -> Aca estoy comparando expected output con trasnlated output
            assertTrue("Example " + i + " failed.", expectedOutput[i].equalsIgnoreCase(translatedInput));
        }
    }
}
