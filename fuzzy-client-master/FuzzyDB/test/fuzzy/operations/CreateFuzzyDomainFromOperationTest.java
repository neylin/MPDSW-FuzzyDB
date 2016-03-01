/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.type3.operations.CreateFuzzyDomainFromColumnOperation;
import fuzzy.Helper;
import fuzzy.database.Connector;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;

/**
 *
 * @author bishma-stornelli
 */
public class CreateFuzzyDomainFromOperationTest {
    
    protected static Connector connector;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();;
    
    public CreateFuzzyDomainFromOperationTest() {
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
        Helper.setConnector(connector);
        connector.executeRawUpdate("CREATE SCHEMA fuzzy_ddl_test");
        connector.executeRawUpdate("CREATE SCHEMA fuzzy_ddl_test1");
        connector.setSchema("fuzzy_ddl_test");
        connector.executeRawUpdate("CREATE TABLE fuzzy_ddl_test.people ("
                + "id SERIAL PRIMARY KEY, "
                + "name VARCHAR(64), "
                + "height DECIMAL, "
                + "birthdate DATE, "
                + "comments TEXT)");
        connector.executeRawUpdate("INSERT INTO fuzzy_ddl_test.people(name, height, birthdate) "
                + "VALUES ('Michael Jordan', 1.98, '1963-02-17'),"
                + "('Jennifer Aniston', 1.64, '1969-02-11'),"
                + "('Milla Jovovich', 1.74, '1975-12-17'),"
                + "('Buddah', NULL, NULL),"
                + "(NULL, 2.35, NULL)");
    }
    
    @After
    public void tearDown() throws SQLException {
        connector.setSchema("information_schema");
        connector.executeRawUpdate("DROP SCHEMA fuzzy_ddl_test CASCADE");
        connector.executeRawUpdate("DROP SCHEMA fuzzy_ddl_test1 CASCADE");
        Helper.cleanSchemaMetaData("fuzzy_ddl_test");      
        Helper.cleanSchemaMetaData("fuzzy_ddl_test1");      
    }
    
    @Test
    public void insertFromVarcharMetadata() throws Exception{
        CreateFuzzyDomainFromColumnOperation o;
        o = new CreateFuzzyDomainFromColumnOperation(connector,
                                                     "nombres",
                                                     "public",
                                                     "people",
                                                     "name");
        o.execute();
        
        String schemaName = "fuzzy_ddl_test";
        String domainName = "nombres";
        String labels [] = {"Michael Jordan", "Jennifer Aniston", "Milla Jovovich", "Buddah"};
        String similarities [][] = {
            {"Michael Jordan"   ,"Michael Jordan"   ,"1","1"},
            {"Jennifer Aniston" ,"Jennifer Aniston" ,"1","1"},
            {"Milla Jovovich"   ,"Milla Jovovich"   ,"1","1"},
            {"Buddah"           ,"Buddah"           ,"1","1"},
        };
        
        Helper.validateMetaData(schemaName, domainName, labels, similarities);
    }
    
    @Test
    public void insertFromDateMetadata() throws Exception{
        CreateFuzzyDomainFromColumnOperation o;
        o = new CreateFuzzyDomainFromColumnOperation(connector,
                                                     "dates",
                                                     "public",
                                                     "people",
                                                     "birthdate");
        o.execute();
        
        String schemaName = "fuzzy_ddl_test";
        String domainName = "dates";
        String labels [] = {"1963-02-17", "1969-02-11", "1975-12-17"};
        String similarities [][] = {
            {"1963-02-17"   ,"1963-02-17"   ,"1","1"},
            {"1969-02-11"   ,"1969-02-11"   ,"1","1"},
            {"1975-12-17"   ,"1975-12-17"   ,"1","1"},
        };
        
        Helper.validateMetaData(schemaName, domainName, labels, similarities);
    }
    
    // Excepcion dominio duplicado
    @Test
    public void duplicatedDomain() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Can't create domain 'nombres'; domain exists");
        CreateFuzzyDomainFromColumnOperation o;
        o = new CreateFuzzyDomainFromColumnOperation(connector, 
                                                     "nombres", 
                                                     "public", 
                                                     "people", 
                                                     "name");
        o.execute();
        o.execute();
    }
    
    @Test
    public void emptyValuesList() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Can't create domain without labels; non-null values in 'fuzzy_ddl_test.people.comments' not found");
        CreateFuzzyDomainFromColumnOperation o;
        o = new CreateFuzzyDomainFromColumnOperation(connector, 
                                                     "comments",
                                                     "public",
                                                     "people",
                                                     "comments");
        o.execute();
    }
    
    @Test
    public void noDBSelected() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("No database selected");
        Connector c2 = new Connector();
        CreateFuzzyDomainFromColumnOperation o;
        o = new CreateFuzzyDomainFromColumnOperation(c2, 
                                                     "names",
                                                     "public",
                                                     "people",
                                                     "name");
        o.execute();
    }
    
    @Test
    @Ignore
    public void insertFromAnotherSchemaMetadata() throws Exception{
        // FIXME: Debido a la migración a Postgres, esto seguro no sirve ni de vaina. Hay que refactorizar todo el peo del USE database.
        connector.setSchema("fuzzy_ddl_test1");
        CreateFuzzyDomainFromColumnOperation o;
        o = new CreateFuzzyDomainFromColumnOperation(connector, 
                                                     "nombres",
                                                     "fuzzy_ddl_test1",
                                                     "people",
                                                     "name");
        o.execute();
        
        String schemaName = "fuzzy_ddl_test1";
        String domainName = "nombres";
        String labels [] = {"Michael Jordan", "Jennifer Aniston", "Milla Jovovich", "Buddah"};
        String similarities [][] = {
            {"Michael Jordan"   ,"Michael Jordan"   ,"1","1"},
            {"Jennifer Aniston" ,"Jennifer Aniston" ,"1","1"},
            {"Milla Jovovich"   ,"Milla Jovovich"   ,"1","1"},
            {"Buddah"           ,"Buddah"           ,"1","1"},
        };
        
        Helper.validateMetaData(schemaName, domainName, labels, similarities);
    }
}
