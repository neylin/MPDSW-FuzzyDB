/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.Helper;
import fuzzy.database.Connector;
import java.sql.SQLException;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author bishma-stornelli
 */
public class CreateFuzzyDomainTest {

    protected static Connector connector;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();;
    
    public CreateFuzzyDomainTest() {
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
        connector.executeRawUpdate("CREATE SCHEMA fuzzy_ddl_test");
        connector.setSchema("fuzzy_ddl_test");
        Helper.setConnector(connector);
    }
    
    // Executed once after each test
    @After
    public void tearDown() throws SQLException {
        connector.setSchema("information_schema");
        connector.executeRawUpdate("DROP SCHEMA fuzzy_ddl_test CASCADE");
        Helper.cleanSchemaMetaData("fuzzy_ddl_test");      
    }
    
    // CREATE FUZZY DOMAIN name AS VALUES () => ERROR
    @Test
    public void emptyValuesList() throws JSQLParserException {
        exception.expect(JSQLParserException.class);        
        Helper.parse("CREATE FUZZY DOMAIN name AS VALUES ()");
    }
    
    // CREATE FUZZY DOMAIN <name> AS VALUES (label1, label1) => ERROR
    @Test
    public void duplicatedLabelValue() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Duplicate label value 'l1'");
        Helper.parseAndTranslate("CREATE FUZZY DOMAIN pepito AS VALUES ('l1', 'l1')");
    }
    
    @Test
    public void basicCreate() throws Exception {
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d AS VALUES ('label')");
        
        String labels[] = {"label"};
        String similarities[][] = { {"label", "label", "1", "1"} };
        Helper.validateMetaData("fuzzy_ddl_test", "d", labels, similarities);
    }
    
    @Test
    public void createWithTwoLabels() throws Exception {
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d AS VALUES ('label1', 'label2')");
        
        String labels[] = {
            "label1", 
            "label2"
        };
        String similarities[][] = { 
            {"label1", "label1", "1", "1"},
            {"label2", "label2", "1", "1"}
        };
        Helper.validateMetaData("fuzzy_ddl_test", "d", labels, similarities);
    }
    
    @Test
    public void duplicatedDomain() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Can't create domain 'd'; domain exists");
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d AS VALUES ('label')");
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d AS VALUES ('label')");
    }
    
    @Test
    public void singleSimilarity() throws Exception {
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2') SIMILARITY { "
                + "('label1', 'label2') / 0.5 "
                + "}");
        
        String labels[] = {
            "label1", 
            "label2"
        };
        String similarities[][] = { 
            {"label1", "label1", "1", "1"},
            {"label2", "label2", "1", "1"},
            {"label1", "label2", "0.5", "0"},
            {"label2", "label1", "0.5", "1"}
        };
        Helper.validateMetaData("fuzzy_ddl_test", "d", labels, similarities);
    }
    
    @Test
    public void labelDoNotExists() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Label 'label3' doesn't exist in domain");
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2') SIMILARITY { "
                + "('label3', 'label4') / 0.5 "
                + "}");
    }
    
    @Test
    public void similarityValueUnderflow() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Invalid similarity value '-0.1'; must be between 0.0 and 1.0");
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2') SIMILARITY { "
                + "('label1', 'label2') / -0.1 "
                + "}");
    }
    
    @Test
    public void similarityValueOverflow() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Invalid similarity value '1.1'; must be between 0.0 and 1.0");
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2') SIMILARITY { "
                + "('label1', 'label2') / 1.1 "
                + "}");
    }
    
    @Test
    public void integerSimilarityValue() throws Exception {
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2') SIMILARITY { "
                + "('label1', 'label2') / 1 "
                + "}");
        
        String labels[] = {
            "label1", 
            "label2"
        };
        String similarities[][] = { 
            {"label1", "label1", "1", "1"},
            {"label2", "label2", "1", "1"},
            {"label1", "label2", "1", "0"},
            {"label2", "label1", "1", "1"}
        };
        Helper.validateMetaData("fuzzy_ddl_test", "d", labels, similarities);
    }
    
    @Test
    public void duplicatedSimilarity() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Duplicate similarity for ('label1', 'label2')");
        
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2') SIMILARITY { "
                + "('label1', 'label2') / 1.0, "
                + "('label1', 'label2') / 1.0"
                + "}");
    }
    
    @Test
    public void noSymmetricRelation() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Resulting similarity relation doesn't hold symmetry for  ('label2', 'label1') (two different values found: 0.0, 1.0)");
        
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2') SIMILARITY { "
                + "('label1', 'label2') / 1.0, "
                + "('label2', 'label1') / 0.0"
                + "}");
    }
    
    @Test
    public void noReflexiveRelation() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Resulting similarity relation doesn't hold reflexivity for label 'label1'");
        
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1') SIMILARITY { "
                + "('label1', 'label1') / 0.5 "
                + "}");
    }
    
    @Test
    public void noTransitiveRelation() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Resulting similarity relation doesn't hold transitivity for  ('label2', 'label3') (two different values derived: 0.6, 0.4)");
        
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2', 'label3') SIMILARITY { "
                + "('label1', 'label2') / 1.0, "
                + "('label2', 'label3') / 0.6,"
                + "('label1', 'label3') / 0.4"
                + "}");
    }
    
    @Test
    public void correctCalculationOfTransitivity() throws Exception {        
        Helper.executeDDLAndFailOnErrors("CREATE FUZZY DOMAIN d "
                + "AS VALUES ('label1', 'label2', 'label3') SIMILARITY { "
                + "('label1', 'label2') / 1.0, "
                + "('label2', 'label3') / 0.6"
                + "}");
        
        String labels[] = {
            "label1", 
            "label2",
            "label3"
        };
        String similarities[][] = { 
            // Reflexivity
            {"label1", "label1", "1", "1"},
            {"label1", "label2", "1", "0"},
            {"label1", "label3", "0.6", "1"},
            {"label2", "label1", "1", "1"},
            {"label2", "label2", "1", "1"},
            {"label2", "label3", "0.6", "0"},
            {"label3", "label1", "0.6", "1"},
            {"label3", "label2", "0.6", "1"},
            {"label3", "label3", "1", "1"}
        };
        Helper.validateMetaData("fuzzy_ddl_test", "d", labels, similarities);
    }
    
}
