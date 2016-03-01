/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.type3.translator.StatementTranslator;
import fuzzy.database.Connector;
import org.junit.Ignore;
import fuzzy.common.operations.Operation;
import fuzzy.type3.operations.RemoveFuzzyColumnsOperation;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bishma-stornelli
 */
public class StatementTranslatorTest {

    protected static Connector connector;
    
    public StatementTranslatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        connector = new Connector();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_CreateTable() {
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_Select() {
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_Delete() {
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_Drop() throws Exception {
        String sql = "DROP TABLE example_autoincrement";

        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        try {
            s = p.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.getCause().printStackTrace();
        }
        
        connector.setSchema("test_repuestos");
        
        List<Operation> operations = new ArrayList<Operation>();
        StatementTranslator st = new StatementTranslator(connector, operations);
        s.accept(st);
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        
        s.accept(sdp);
        
        String sqlTranslated = sb.toString();
        System.out.println(sqlTranslated);
        assertEquals("Traduccion del drop table incorrecta",
                (sql).toLowerCase(), 
                sqlTranslated.toLowerCase().replaceAll("\n", ""));
        
        assertEquals("Traducción debería agregar una operación",
                1, operations.size());
        
        Operation o = new RemoveFuzzyColumnsOperation(connector, "test_repuestos", 
                "example_autoincrement");
        
        assertEquals("Operación agregada es de tipo incorrecto",
                operations.get(0).getClass(), RemoveFuzzyColumnsOperation.class);
        assertEquals("Nombre del esquema de la operacion incorrecta",
                "test_repuestos", 
                ((RemoveFuzzyColumnsOperation)operations.get(0)).getSchemaName());
        assertEquals("Nombre de la tabla de la operacion incorrecta",
                "example_autoincrement", 
                ((RemoveFuzzyColumnsOperation)operations.get(0)).getTableName());
        assertNull("Columna de la operacion incorrecta", 
                ((RemoveFuzzyColumnsOperation)operations.get(0)).getColumnName());
    }
    
    @Ignore
    @Test
    public void testVisit_Drop_MultipleTables() throws Exception {
        String sql = "DROP TABLE table1, table2";

        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        try {
            s = p.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.getCause().printStackTrace();
        }
        
        connector.setSchema("test_repuestos");
        
        List<Operation> operations = new ArrayList<Operation>();
        StatementTranslator st = new StatementTranslator(connector, operations);
        s.accept(st);
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        
        s.accept(sdp);
        
        String sqlTranslated = sb.toString();
        System.out.println(sqlTranslated);
        assertEquals("Traduccion del drop table incorrecta",
                (sql).toLowerCase(), 
                sqlTranslated.toLowerCase().replaceAll("\n", ""));
        
        assertEquals("Traducción debería agregar dos operación",
                2, operations.size());
        
        String[] tables = {"table1", "table2"};
        for (int i = 0 ; i < tables.length ; ++i) {            
            assertEquals("Operación agregada es de tipo incorrecto: " + i,
                    operations.get(i).getClass(), RemoveFuzzyColumnsOperation.class);
            assertEquals("Nombre del esquema de la operacion incorrecta: " + i,
                    "test_repuestos", 
                    ((RemoveFuzzyColumnsOperation)operations.get(i)).getSchemaName());
            assertEquals("Nombre de la tabla de la operacion incorrecta: " + i,
                    tables[i], 
                    ((RemoveFuzzyColumnsOperation)operations.get(i)).getTableName());
            assertNull("Columna de la operacion incorrecta: " + i, 
                    ((RemoveFuzzyColumnsOperation)operations.get(i)).getColumnName());
            
        }
        
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_Insert() {
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_Replace() {
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_Truncate() {
    }

    /**
     * Test of visit method, of class StatementTranslator.
     */
    @Test
    public void testVisit_Update() {
    }
}
