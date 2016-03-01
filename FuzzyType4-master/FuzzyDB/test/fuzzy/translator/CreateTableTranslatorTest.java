/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.type3.translator.CreateTableTranslator;
import fuzzy.database.Connector;
import fuzzy.type3.operations.AddFuzzyColumnOperation;
import fuzzy.common.operations.Operation;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import net.sf.jsqlparser.statement.table.CreateTable;
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
public class CreateTableTranslatorTest {

    protected static Connector connector;
    
    public CreateTableTranslatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        connector = new Connector();
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
     * Test of translate method, of class CreateTableTranslator.
     */
    @Test
    public void testTranslateOneColumn() throws Exception {
        String sql = "CREATE TABLE test_repuestos.example_autoincrement ("
                + "id SERIAL NOT NULL,"
                + "data ciudad NOT NULL, PRIMARY KEY (id));";

        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        try {
            s = p.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.getCause().printStackTrace();
        }
        
        List<Operation> operations = new ArrayList<Operation>();
        CreateTableTranslator st = new CreateTableTranslator(connector, operations);
        st.translate((CreateTable)s);
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        
        s.accept(sdp);
        
        String sqlTranslated = sb.toString();
        assertEquals("Traduccion del create table incorrecta",
                ("CREATE TABLE test_repuestos.example_autoincrement ( "
                + "id SERIAL NOT NULL,"
                + "data INTEGER NOT NULL,PRIMARY KEY (id) ) ").toLowerCase(), 
                sqlTranslated.toLowerCase().replaceAll("\n", ""));
        
        assertEquals("Traducción debería agregar una operación",
                1, operations.size());
        
        Operation o = new AddFuzzyColumnOperation(connector, "test_repuestos", 
                "example_autoincrement",
                "data", 1);
        
        assertEquals("Operación agregada es incorrecta",
                operations.get(0), o);
        
        
    }
    
    /**
     * Test of translate method, of class CreateTableTranslator.
     */
    @Test
    public void testTranslateTwoColumns() throws Exception {
        String sql = "CREATE TABLE test_repuestos.example_autoincrement ("
                + "id SERIAL NOT NULL,"
                + "data ciudad NOT NULL,"
                + "data2 ciudad, PRIMARY KEY (id));";

        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        try {
            s = p.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.getCause().printStackTrace();
        }
        
        List<Operation> operations = new ArrayList<Operation>();
        CreateTableTranslator st = new CreateTableTranslator(connector, operations);
        st.translate((CreateTable)s);
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        
        s.accept(sdp);
        
        String sqlTranslated = sb.toString();
        assertEquals("Traduccion del create table incorrecta",
                ("CREATE TABLE test_repuestos.example_autoincrement ( "
                + "id SERIAL NOT NULL,"
                + "data INTEGER NOT NULL,"
                + "data2 INTEGER,PRIMARY KEY (id) ) ").toLowerCase(), 
                sqlTranslated.toLowerCase().replaceAll("\n", ""));
        
        assertEquals("Traducción debería agregar una operación",
                2, operations.size());
        
        AddFuzzyColumnOperation o = new AddFuzzyColumnOperation(connector, "test_repuestos", 
                "example_autoincrement",
                "data", 1);
        
        assertEquals("Operación agregada es incorrecta",
                operations.get(0), o);
        
        o.setColumnName("data2");
        assertEquals("Operación agregada es incorrecta",
                operations.get(1), o);
        
        
    }
    
    /**
     * Test of translate method, of class CreateTableTranslator.
     */
    @Test
    public void testTranslateDefaultValue() throws Exception {
        String sql = "CREATE TABLE test_repuestos.example_autoincrement ("
                + "id SERIAL NOT NULL,"
                + "data ciudad DEFAULT 'Maracay', PRIMARY KEY (id));";

        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        try {
            s = p.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.getCause().printStackTrace();
        }
        
        List<Operation> operations = new ArrayList<Operation>();
        CreateTableTranslator st = new CreateTableTranslator(connector, operations);
        st.translate((CreateTable)s);
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        
        s.accept(sdp);
        
        String sqlTranslated = sb.toString();
        assertEquals("Traduccion del create table incorrecta",
                ("CREATE TABLE test_repuestos.example_autoincrement ( "
                + "id SERIAL NOT NULL,"
                + "data INTEGER DEFAULT 2,PRIMARY KEY (id) ) ").toLowerCase(), 
                sqlTranslated.toLowerCase().replaceAll("\n", ""));
        
        assertEquals("Traducción debería agregar una operación",
                1, operations.size());
        
        Operation o = new AddFuzzyColumnOperation(connector, "test_repuestos", 
                "example_autoincrement",
                "data", 1);
        
        assertEquals("Operación agregada es incorrecta",
                operations.get(0), o);
        
        
    }
    
    @Test
    public void testTranslateWithoutSchema() throws Exception {
        String sql = "CREATE TABLE example_autoincrement ("
                + "id SERIAL NOT NULL,"
                + "data ciudad NOT NULL, PRIMARY KEY (id));";

        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        try {
            s = p.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.getCause().printStackTrace();
        }
        
        connector.setSchema("test_repuestos");

        List<Operation> operations = new ArrayList<Operation>();
        CreateTableTranslator st = new CreateTableTranslator(connector, operations);
        st.translate((CreateTable)s);
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        
        s.accept(sdp);
        
        String sqlTranslated = sb.toString();
        assertEquals("Traduccion del create table incorrecta",
                ("CREATE TABLE example_autoincrement ( "
                + "id SERIAL NOT NULL,"
                + "data INTEGER NOT NULL,PRIMARY KEY (id) ) ").toLowerCase(), 
                sqlTranslated.toLowerCase().replaceAll("\n", ""));
        
        assertEquals("Traducción debería agregar una operación",
                1, operations.size());
        
        Operation o = new AddFuzzyColumnOperation(connector, "test_repuestos", 
                "example_autoincrement",
                "data", 1);
        
        assertEquals("Operación agregada es incorrecta",
                operations.get(0), o);
        
        
    }
}
