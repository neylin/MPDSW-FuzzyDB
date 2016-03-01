/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.Helper;
import fuzzy.database.Connector;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author bishma-stornelli
 */
public class AlterFuzzyDomainTest {
    
    protected static Connector connector;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private final String database = "fuzzy_ddl_test";
    
    private final String domainName = "d";
    
    private final String existingLabels[] = {
            "label1",
            "label2",
            "label3"
        };
    
    private final String existingSimilarities[][] = {
            {"label1", "label1", "1", "1"},
            {"label1", "label2", "1", "0"}, // BASE
            {"label1", "label3", "0.6", "1"},
            {"label2", "label1", "1", "1"},
            {"label2", "label2", "1", "1"},
            {"label2", "label3", "0.6", "0"}, // BASE
            {"label3", "label1", "0.6", "1"},
            {"label3", "label2", "0.6", "1"},
            {"label3", "label3", "1", "1"}
        };
    
    public AlterFuzzyDomainTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        connector = new Connector();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    // Executed once before each test
    @Before
    public void setUp() throws SQLException {
        connector.executeRawUpdate("CREATE SCHEMA " + database);
        connector.setSchema(database);
        Helper.setConnector(connector);
        Helper.createMetaData(database, domainName, existingLabels, existingSimilarities);
    }
    
    // Executed once after each test
    @After
    public void tearDown() throws SQLException {
        connector.setSchema("information_schema");
        connector.executeRawUpdate("DROP SCHEMA " + database + " CASCADE");
        Helper.cleanSchemaMetaData(database);      
    }
    
    
    /**************************************************************************
     * TESTS FOR ALTER FUZZY DOMAIN d ADD VALUES
     **************************************************************************/
    
    // Add duplicated value for existing value
    @Test
    public void addDuplicatedExistingValues() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Duplicate label value 'label3'");
        Helper.executeDDLAndFailOnErrors("ALTER FUZZY DOMAIN " + domainName + " ADD VALUES "
                + "('label4', 'label3')");
    }
    // Add duplicated value in the same statement
    @Test
    public void addDuplicatedNewValues() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Duplicate label value 'label4'");
        Helper.executeDDLAndFailOnErrors("ALTER FUZZY DOMAIN " + domainName + " ADD VALUES "
                + "('label4', 'label4')");
    }
    // Add correct value    
    @Test
    public void addCorrectValue() throws Exception {
        Helper.executeDDLAndFailOnErrors("ALTER FUZZY DOMAIN " + domainName + " ADD VALUES "
                + "('label4', 'label5')");
        
        String[] tempLabels = {"label4", "label5"},
                 newLabels = new String[existingLabels.length + 2];
        System.arraycopy(tempLabels, 0, newLabels, 0, 2);
        System.arraycopy(existingLabels, 0, newLabels, 2, existingLabels.length);
        
        String[][] tempSimilarities = {{"label4", "label4", "1", "1"},
                                       {"label5", "label5", "1", "1"}},
                 newSimilarities = new String[existingSimilarities.length + 2][4];
        System.arraycopy(tempSimilarities, 0, newSimilarities, 0, 2);
        System.arraycopy(existingSimilarities, 0, newSimilarities, 2, existingSimilarities.length);
        
        Helper.validateMetaData(database, domainName, newLabels, newSimilarities);
    }
    
    /**************************************************************************
     * TESTS FOR ALTER FUZZY DOMAIN d DROP VALUES
     **************************************************************************/
    
    // DROP UNEXISTING LABEL
    @Test
    public void dropUnexistingValue() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Label 'label4' doesn't exist in domain");
        Helper.executeDDLAndFailOnErrors("ALTER FUZZY DOMAIN " + domainName + " DROP VALUES "
                + "('label1', 'label4')");
    }
    
    // DROP EXISTING LABEL
    @Test
    public void dropValue() throws Exception {
        Helper.executeDDLAndFailOnErrors("ALTER FUZZY DOMAIN " + domainName + " DROP VALUES "
                + "('label2')");
        String[] labels = {"label1", "label3"};
        String[][] similarities = {
            {"label1", "label1", "1", "1"},
            {"label3", "label3", "1", "1"},
        };
        
        Helper.validateMetaData(database, domainName, 
                labels, 
                similarities);
    }
    
    @Test
    public void complexDropValue() throws Exception {
        String[] extraLabels = {"label4"};
        String[][] extraSimilarities = {
            {"label1", "label4", "1", "0"},
            {"label2", "label4", "1", "1"},
            {"label3", "label4", "0.6", "0"},
            {"label4", "label1", "1", "1"},
            {"label4", "label2", "1", "1"},
            {"label4", "label3", "0.6", "1"},
            {"label4", "label4", "1", "1"}
        };
        
        Helper.createMetaData(database, domainName, extraLabels, extraSimilarities);
        
        Helper.executeDDLAndFailOnErrors("ALTER FUZZY DOMAIN " + domainName + " DROP VALUES "
                + "('label2')");
        
        String[] labels = {"label1", "label3", "label4"};
        String[][] similarities = {
            {"label1", "label1", "1", "1"},
            {"label1", "label3", "0.6", "1"},
            {"label1", "label4", "1", "0"},
            {"label3", "label1", "0.6", "1"},
            {"label3", "label3", "1", "1"},
            {"label3", "label4", "0.6", "0"},
            {"label4", "label1", "1", "1"},
            {"label4", "label3", "0.6", "1"},
            {"label4", "label4", "1", "1"}
        };
        
        Helper.validateMetaData(database, domainName, 
                labels, 
                similarities);
    }
}
