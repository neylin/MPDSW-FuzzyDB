/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.type3.operations.ChangeColumnOperation;
import org.junit.Ignore;
import fuzzy.Helper;
import java.sql.ResultSet;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import fuzzy.database.Connector;
import java.sql.SQLException;
import java.util.HashMap;
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
public class ChangeColumnOperationTest {

    protected static Connector connector;
    protected final String schemaName = "fuzzy_ddl_test";
    protected final String tableName = "people";
    protected final String columnNames[] = {"id", "name", "birthdate"};
    protected final String columnTypes[] = {"SERIAL", "VARCHAR(64)", "DATE"};
    protected final String columnConstraints[] = {"PRIMARY KEY", "", ""};
    protected final String rows[][] = {
        {"DEFAULT", "Michael Jordan", "1963-02-17"},
        {"DEFAULT", "Jennifer Aniston", "1969-02-11"},
        {"DEFAULT", "Milla Jovovich", "1975-12-17"},
        {"DEFAULT", "Buddah", null},
        {"DEFAULT", null, null},};
    @Rule
    public ExpectedException exception = ExpectedException.none();

    ;
    
    public ChangeColumnOperationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws SQLException {
        connector = new Connector();
        Helper.setConnector(connector);
        Helper.createData(schemaName, tableName, columnNames, columnTypes, columnConstraints, rows);
        connector.setSchema("fuzzy_ddl_test");
        String domainName = "names";
        String labels[] = {"Michael Jordan", "Jennifer Aniston", "Milla Jovovich", "Buddah"};
        String similarities[][] = {
            {"Michael Jordan", "Michael Jordan", "1", "1"},
            {"Jennifer Aniston", "Jennifer Aniston", "1", "1"},
            {"Milla Jovovich", "Milla Jovovich", "1", "1"},
            {"Buddah", "Buddah", "1", "1"},};
        Helper.createMetaData(schemaName, domainName, labels, similarities);
    }

    @After
    public void tearDown() throws SQLException {
        connector.setSchema("information_schema");
        connector.executeRawUpdate("DROP SCHEMA fuzzy_ddl_test CASCADE");
        Helper.cleanSchemaMetaData("fuzzy_ddl_test");
    }

    /**
     * Change without selecting an schema should throw an error
     */
    @Test
    @Ignore
    public void testExecute1() throws Exception {
        System.out.println("Change without selecting an schema should throw an error");
        exception.expect(SQLException.class);
        exception.expectMessage("No database selected");
        Connector connector1 = new Connector();
        ChangeColumnOperation instance = new ChangeColumnOperation(connector1);
        instance.setTableName("dummy");
        instance.setOldColumnName("dummmy");
        instance.setNewColumnName("dummy");
        instance.setDataType("names");
        instance.execute();
        // TODO validate it doesn't alter values of table or create new metadata
    }

    /**
     * Change unexisting table should throw an error
     */
    @Test
    @Ignore
    public void testExecute2() throws Exception {
        System.out.println("Change unexisting table should throw an error");
        exception.expect(SQLException.class);
        exception.expectMessage("Table 'fuzzy_ddl_test.dummy' doesn't exist");
        ChangeColumnOperation instance = new ChangeColumnOperation(connector);
        instance.setTableName("dummy");
        instance.setOldColumnName("dummmy");
        instance.setNewColumnName("dummy");
        instance.setDataType("names");
        instance.execute();
        // TODO validate it doesn't alter values of table or create new metadata
    }

    /**
     * Change unexisting column should throw an error
     */
    @Test
    @Ignore
    public void testExecute3() throws Exception {
        System.out.println("Change unexisting column should throw an error");
        exception.expect(SQLException.class);
        exception.expectMessage("Unknown column 'dummy' in 'people'");
        ChangeColumnOperation instance = new ChangeColumnOperation(connector);
        instance.setTableName(tableName);
        instance.setOldColumnName("dummy");
        instance.setNewColumnName("dummy");
        instance.setDataType("names");
        instance.execute();
        // TODO validate it doesn't alter values of table or create new metadata
    }

    /**
     * Change to duplicated column should throw an error
     */
    @Test
    @Ignore
    public void testExecute4() throws Exception {
        System.out.println("Change to duplicated column should throw an error");
        exception.expect(SQLException.class);
        exception.expectMessage("Duplicate column name 'birthdate'");
        ChangeColumnOperation instance = new ChangeColumnOperation(connector);
        instance.setTableName(tableName);
        instance.setOldColumnName("name");
        instance.setNewColumnName("birthdate");
        instance.setDataType("names");
        instance.execute();
        // TODO validate it doesn't alter values of table or create new metadata
    }

    /**
     * Change a superset of a domain should throw an error
     */
    @Test
    @Ignore
    public void testExecute5() throws Exception {
        System.out.println("Change a superset of a domain should throw an error");
        exception.expect(SQLException.class);
        exception.expectMessage("Label 'Han Solo' doesn't exist in domain");
        assertEquals("Error preparing test. Try again",
                new Integer(1),
                connector.executeRawUpdate("INSERT INTO people (name) VALUES ('Han Solo')"));
        ChangeColumnOperation instance = new ChangeColumnOperation(connector);
        instance.setTableName(tableName);
        instance.setOldColumnName("name");
        instance.setNewColumnName("name");
        instance.setDataType("names");
        instance.execute();
        // TODO validate it doesn't alter values of table or create new metadata
    }

    /**
     * Change to an unexisting domain should throw an error
     */
    @Test
    @Ignore
    public void testExecute6() throws Exception {
        System.out.println("Change to an unexisting domain should throw an error");
        exception.expect(SQLException.class);
        exception.expectMessage("Unknown domain 'persons'");
        ChangeColumnOperation instance = new ChangeColumnOperation(connector);
        instance.setTableName(tableName);
        instance.setOldColumnName("name");
        instance.setNewColumnName("name");
        instance.setDataType("persons");
        instance.execute();
        // TODO validate it doesn't alter values of table or create new metadata
    }

    /**
     * Correct change should change column values to label ids of existing domain
     */
    @Test
    @Ignore
    public void testExecute7() throws Exception {
        System.out.println("Correct change should change column values to label ids of existing domain");
        ChangeColumnOperation instance = new ChangeColumnOperation(connector);
        instance.setTableName(tableName);
        instance.setOldColumnName("name");
        instance.setNewColumnName("full_name");
        instance.setDataType("names");
        instance.execute();
        
        ResultSet rs = connector.executeRawQuery("SELECT label_id, label_name FROM information_schema_fuzzy.labels"
                + " WHERE domain_id = ("
                + "SELECT domain_id "
                + "FROM information_schema_fuzzy.domains "
                + "WHERE domain_name = 'names' AND table_schema = '" + schemaName + "')");
        HashMap<String, String> labelId = new HashMap<String, String>();
        while (rs.next()) {
            labelId.put(rs.getString("label_name"), rs.getString("label_id"));
        }
        
        String expectedColumnNames[] = {"id", "full_name", "birthdate"};

        String expectedRows[][] = {
            {"1", labelId.get("Michael Jordan"), "1963-02-17"},
            {"2", labelId.get("Jennifer Aniston"), "1969-02-11"},
            {"3", labelId.get("Milla Jovovich"), "1975-12-17"},
            {"4", labelId.get("Buddah"), null},
            {"5", null, null}};
        
        Helper.validateData(schemaName, tableName, expectedColumnNames, expectedRows);
        
        rs = connector.executeRawQuery("SELECT COUNT(*) FROM information_schema_fuzzy.columns"
                + " WHERE table_schema = '" + schemaName + "' "
                + "AND table_name = '" + tableName + "' "
                + "AND column_name = 'full_name' "
                + "AND domain_id = ("
                + "SELECT domain_id "
                + "FROM information_schema_fuzzy.domains "
                + "WHERE domain_name = 'names' AND table_schema = '" + schemaName + "')");
        assertTrue(rs.next());
        assertEquals("Metadata in columns is not being updated", 1, rs.getInt(1));
    }

    /**
     * It creates and execute a CreateConstraintsForNewColumnOperation
     */
    @Test
    @Ignore
    public void testExecute8() throws Exception {
        System.out.println("It creates and execute a CreateConstraintsForNewColumnOperation");
//        ChangeColumnOperation instance = null;
//        instance.execute();
        // TODO mock CreateConstraintsForNewColumnOperation
        // If we test that the constraint is being created we should be testing 
        // the CreateConstraintsForNewColumnOperation class.
        // We should test that an instance of CreateConstraintsForNewColumnOperation
        // is created, the right parameters are setted and the method execute is called.
        // I don't know how to do that with java
        //fail("Test not implemented yet");
    }

    /**
     * It creates and execute an InsertNewColumnsOperation
     */
    @Test
    @Ignore
    public void testExecute9() throws Exception {
        System.out.println("It creates and execute an InsertNewColumnsOperation");
//        ChangeColumnOperation instance = null;
//        instance.execute();
        // TODO mock InsertNewColumnsOperation
        // If we test that the constraint is being created we should be testing 
        // the InsertNewColumnsOperation class.
        // We should test that an instance of InsertNewColumnsOperation
        // is created, the right parameters are setted and the method execute is called.
        // I don't know how to do that with java
        //fail("Test not implemented yet");
    }
    
    /**
     * It should throw an error when changing non-varchar columns to fuzzy domain
     */
    @Test
    @Ignore
    public void testExecute10() throws Exception {
        System.out.println("It should throw an error when changing non-varchar columns to fuzzy domain");
        // TODO implement this method
        //fail("Test not implemented yet");
    }

    /**
     * Test of setOptions method, of class ChangeColumnOperation.
     */
    @Test
    @Ignore
    public void testGetAndSetOptions() {
        System.out.println("getAndSetOptions");
        String options = "NOT NULL FOREIGN KEY";
        ChangeColumnOperation instance = new ChangeColumnOperation(null);
        instance.setOptions(options);
        String result = instance.getOptions();
        assertEquals(options, result);
    }

    /**
     * Test of setDataType method, of class ChangeColumnOperation.
     */
    @Test
    @Ignore
    public void testGetAndSetDataType() {
        System.out.println("getAndSetDataType");
        String dataType = "VARCHAR";
        ChangeColumnOperation instance = new ChangeColumnOperation(null);
        instance.setDataType(dataType);
        String result = instance.getDataType();
        assertEquals(dataType, result);
    }

    /**
     * Test of setNewColumnName method, of class ChangeColumnOperation.
     */
    @Test
    @Ignore
    public void testGetAndSetNewColumnName() {
        System.out.println("getAndSetNewColumnName");
        String newColumnName = "nombreciudad";
        ChangeColumnOperation instance = new ChangeColumnOperation(null);
        instance.setNewColumnName(newColumnName);
        String result = instance.getNewColumnName();
        assertEquals(newColumnName, result);
    }

    /**
     * Test of setOldColumnName method, of class ChangeColumnOperation.
     */
    @Test
    @Ignore
    public void testGetAndSetOldColumnName() {
        System.out.println("getAndSetOldColumnName");
        String oldColumnName = "nombreciudad";
        ChangeColumnOperation instance = new ChangeColumnOperation(null);
        instance.setOldColumnName(oldColumnName);
        String result = instance.getOldColumnName();
        assertEquals(oldColumnName, result);
    }

    /**
     * Test of setTableName method, of class ChangeColumnOperation.
     */
    @Test
    @Ignore
    public void testGetAndSetTableName() {
        System.out.println("getAndSetTableName");
        String tableName = "ciudades";
        ChangeColumnOperation instance = new ChangeColumnOperation(null);
        instance.setTableName(tableName);
        String result = instance.getTableName();
        assertEquals(tableName, result);
    }

    /**
     * Test of setSchemaName method, of class ChangeColumnOperation.
     */
    @Test
    @Ignore
    public void testGetAndSetSchemaName() {
        System.out.println("getAndSetSchemaName");
        String schemaName = "infoguia";
        ChangeColumnOperation instance = new ChangeColumnOperation(null);
        instance.setSchemaName(schemaName);
        String result = instance.getSchemaName();
        assertEquals(schemaName, result);
    }
}
