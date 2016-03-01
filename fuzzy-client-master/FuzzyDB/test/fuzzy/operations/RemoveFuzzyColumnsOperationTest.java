/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.type3.operations.RemoveFuzzyColumnsOperation;
import org.junit.Ignore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bishma-stornelli
 */
public class RemoveFuzzyColumnsOperationTest {
    
    public RemoveFuzzyColumnsOperationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }    

    /**
     * Test of execute method, of class RemoveFuzzyColumnsOperation.
     */
    @Ignore
    @Test
    public void testExecute() {
    }

    /**
     * Test of getQuery method, of class RemoveFuzzyColumnsOperation.
     */
    @Test
    public void testGetQueryOnlySchema() {
        RemoveFuzzyColumnsOperation removeFuzzyColumnsOperation = 
                new RemoveFuzzyColumnsOperation(null, "esquema");
        assertEquals("DELETE FROM information_schema_fuzzy.columns "
                + "WHERE table_schema = 'esquema'", removeFuzzyColumnsOperation.getQuery());
    }
    
    /**
     * Test of getQuery method, of class RemoveFuzzyColumnsOperation.
     */
    @Test
    public void testGetQuerySchemaAndTable() {
        RemoveFuzzyColumnsOperation removeFuzzyColumnsOperation =
                new RemoveFuzzyColumnsOperation(null, "esquema", "table");
        assertEquals("DELETE FROM information_schema_fuzzy.columns "
                + "WHERE table_schema = 'esquema' AND table_name = 'table'",
                removeFuzzyColumnsOperation.getQuery());
    }
    
    /**
     * Test of getQuery method, of class RemoveFuzzyColumnsOperation.
     */
    @Test
    public void testGetQueryAllParameters() {
        RemoveFuzzyColumnsOperation removeFuzzyColumnsOperation = 
                new RemoveFuzzyColumnsOperation(null, "esquema", "table", "column");
        assertEquals("DELETE FROM information_schema_fuzzy.columns "
                + "WHERE table_schema = 'esquema' AND table_name = 'table'"
                + " AND column_name = 'column'", 
                removeFuzzyColumnsOperation.getQuery());
    }
}
