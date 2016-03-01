/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.database;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andras
 */
public class ConnectorTest {
    
    protected static Connector connector;

    @BeforeClass
    public static void setUpClass() throws Exception {
        connector = new Connector();
    }

    @Test
    public void setCatalogTest() throws Exception {
        connector.setSchema("test_repuestos");
        assertEquals("Base de datos no fue cambiada",
                "test_repuestos", connector.getSchema());
    }
}
