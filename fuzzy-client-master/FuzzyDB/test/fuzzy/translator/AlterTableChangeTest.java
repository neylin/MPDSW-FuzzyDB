/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.type3.operations.ChangeColumnOperation;
import fuzzy.Helper;
import fuzzy.database.Connector.TranslationResult;
import fuzzy.operations.*;
import fuzzy.database.Connector;
import java.sql.Connection;
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
public class AlterTableChangeTest {
    
    protected static Connector connector;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();;
    
    public AlterTableChangeTest() {
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
        Helper.cleanSchemaMetaData("fuzzy_ddl_test");      
    }
    
    @Test
    public void createAnOperation() throws Exception{
        TranslationResult translate = connector.translate("ALTER TABLE fuzzy_ddl_test.people CHANGE name names ciudad NOT NULL");
        assertNull(translate.sql);
        assertNotNull(translate.operations);
        assertEquals(1, translate.operations.size());
        assertEquals(ChangeColumnOperation.class, translate.operations.get(0).getClass());
        ChangeColumnOperation o = (ChangeColumnOperation) translate.operations.get(0);
        assertEquals("ciudad", o.getDataType());
        assertEquals("name", o.getOldColumnName());
        assertEquals("names", o.getNewColumnName());
        assertEquals("NOT NULL ", o.getOptions());
        assertEquals("fuzzy_ddl_test", o.getSchemaName());
        assertEquals("people", o.getTableName());
    }
}
