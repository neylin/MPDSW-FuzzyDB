/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.type3.operations.AddFuzzyColumnOperation;
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
public class AddFuzzyColumnOperationTest {
    
    public AddFuzzyColumnOperationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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

    /**
     * Test of execute method, of class AddFuzzyColumnOperation.
     */
    @Test
    public void testGetQuery() {
        System.out.println("testGetQuery");
        AddFuzzyColumnOperation instance = new AddFuzzyColumnOperation(null, "test_repuestos", 
                "example_autoincrement",
                "data", 1);
        assertEquals("Query generado incorrecto.",
                ("INSERT INTO information_schema_fuzzy.columns"
                + " VALUES ('test_repuestos','example_autoincrement','data',1)").toLowerCase(),
                instance.getQuery().toLowerCase());
    }

    
}
