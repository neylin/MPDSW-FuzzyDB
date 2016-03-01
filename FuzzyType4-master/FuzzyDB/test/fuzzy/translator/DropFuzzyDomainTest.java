/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.Helper;
import fuzzy.database.Connector;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

/**
 *
 * @author bishma-stornelli
 */
public class DropFuzzyDomainTest {

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
    
    @Test
    public void dropUnexistingDomain() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Unknown domain '" + domainName + "_invalid'");
        Helper.executeDDLAndFailOnErrors("DROP FUZZY DOMAIN " + domainName + "_invalid");
    }
    
    @Test
    public void dropExistingDomain() throws Exception {
        String sql = "SELECT domain_id FROM information_schema_fuzzy.domains "
                + "WHERE table_schema = '" + database + "' AND domain_name = '" + domainName + "'";
        ResultSet rs = connector.executeRawQuery(sql);
        assertTrue("Error executing test. Data is not being pre-loading", rs.next());
        Integer domainId = rs.getInt("domain_id");
        
        Helper.executeDDLAndFailOnErrors("DROP FUZZY DOMAIN " + domainName);
        
        assertFalse("It's not dropping the domain",
                connector.executeRawQuery(sql).next());
        
        assertFalse("It's not dropping labels",
                connector.executeRawQuery("SELECT * FROM information_schema_fuzzy.labels WHERE domain_id = " + domainId).next());
        
        // TODO check it's dropping similarities
    }
    
    // TODO create another domain with same labels and check it's dropping only labels of the dropped domain
    
}
