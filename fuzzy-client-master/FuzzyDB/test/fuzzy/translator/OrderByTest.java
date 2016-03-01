/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author timauryc
 */
@Ignore
public class OrderByTest {
    
      public OrderByTest() {
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
    
   
    //Group of queries meant to have success
    
    // @Test
    private String[] SuccessInput = {
        "SELECT nombre FROM test_repuestos.repuestos ORDER BY telefono",
        "SELECT nombre FROM test_repuestos.repuestos ORDER BY ciudad",
        "SELECT nombre, ciudad FROM test_repuestos.repuestos ORDER BY telefono",
        "SELECT nombre, ciudad FROM test_repuestos.repuestos ORDER BY ciudad",
        "SELECT nombre FROM test_repuestos.repuestos ORDER BY ciudad STARTING FROM 'Caracas'",
        /*"SELECT nombre FROM test_repuestos.repuestos ORDER BY ciudad START 'Caracas'",
        "SELECT nombre, ciudad FROM test_repuestos.repuestos ORDER BY SIMILARITY ON ciudad START 'Caracas'",
        "SELECT nombre, ciudad FROM test_repuestos.repuestos ORDER BY SIMILARITY ON ciudad STARTING FROM 'Caracas'",*/
    };
    
    
    //Expected output for the queries translation, used for resultSets comparation
    private String[] expectedOutput = {
        "SELECT nombre FROM test_repuestos.repuestos ORDER BY telefono ASC",
        "SELECT nombre FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) ORDER BY L1.label_name ASC", 
        "SELECT nombre, L1.label_name AS ciudad FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) ORDER BY telefono ASC",
        "SELECT nombre, L1.label_name AS ciudad FROM (test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) ORDER BY L1.label_name ASC",
        "SELECT nombre FROM (((test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L2 ON (L2.label_name = 'Caracas' AND L2.domain_id = (SELECT D1.domain_id FROM information_schema_fuzzy.domains AS D1 WHERE D1.domain_name = 'ciudad'))) LEFT JOIN information_schema_fuzzy.similarities AS S1 ON (S1.label1_id = L2.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad)) LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) ORDER BY IFNULL(S1.value, 0) DESC", 
        /*"SELECT nombre FROM (((test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L2 ON (L2.label_name = 'Caracas' AND L2.domain_id = (SELECT D1.domain_id FROM information_schema_fuzzy.domains AS D1 WHERE D1.domain_name = 'ciudad'))) LEFT JOIN information_schema_fuzzy.similarities AS S1 ON (S1.label1_id = L2.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad)) LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) ORDER BY IFNULL(S1.value, 0) DESC", 
        "SELECT nombre FROM (((test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L2 ON (L2.label_name = 'Caracas' AND L2.domain_id = (SELECT D1.domain_id FROM information_schema_fuzzy.domains AS D1 WHERE D1.domain_name = 'ciudad'))) LEFT JOIN information_schema_fuzzy.similarities AS S1 ON (S1.label1_id = L2.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad)) LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) ORDER BY IFNULL(S1.value, 0) DESC", 
        "SELECT nombre FROM (((test_repuestos.repuestos LEFT JOIN information_schema_fuzzy.labels AS L2 ON (L2.label_name = 'Caracas' AND L2.domain_id = (SELECT D1.domain_id FROM information_schema_fuzzy.domains AS D1 WHERE D1.domain_name = 'ciudad'))) LEFT JOIN information_schema_fuzzy.similarities AS S1 ON (S1.label1_id = L2.label_id AND S1.label2_id = test_repuestos.repuestos.ciudad)) LEFT JOIN information_schema_fuzzy.labels AS L1 ON test_repuestos.repuestos.ciudad = L1.label_id) ORDER BY IFNULL(S1.value, 0) DESC",*/
    };
    
    
    //Fail messages for SuccessInput
    private String[] FailMessages = {
        "Fallo de consulta basica del ORDER BY",
        "BLA",
        "BLA",
        "BLA",
        "BLA",
    };
    
    //Results array for meant to success input
    private String[] Results = new String[SuccessInput.length];
    
    //NonSucces imput strings, they should fail
    private String[] NonSuccessInput = {};
    
    //Expected behavior from NonSuccesInput
    private String[] NonSuccessBitacora = {};
    
    
    
    //Testing SuccesInput by comparing resultSets
        
    /*@Test
    public void testSuccesInput() throws SQLException {
        THelper testHelper = new THelper();
        for (int i = 0 ; i < SuccessInput.length; ++i) {
            String translatedInput = testHelper.translate(SuccessInput[i]);
            //Getting the resultSet
            ResultSet actual = Console.executeQuery(translatedInput);
            ResultSet expected = Console.executeQuery(expectedOutput[i]);
            if (!testHelper.compareResults(actual,expected)){
                Results[i] = "RESULT " + i +" : " + FailMessages[i];
            }
            else Results[i] = "RESULT " + i +" : " + "meant to success example " + i + " passed";
        }
        
        testHelper.showResults(Results);
    }*/

    
    /*@Test
    public void testNonSuccesInput() throws SQLException {
        THelper testHelper = new THelper();
        for (int i = 0 ; i < NonSuccessInput.length; ++i) {
            System.out.println("RESULT:" + NonSuccessBitacora[i]);
            testHelper.translate(SuccessInput[i]);
        }
        
        testHelper.showResults(Results);
    }*/
}
