/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.type3.translator.Translator;
import fuzzy.database.Connector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author bishma-stornelli, timauryc
 */
public class TranslatorTest {

    protected static Connector connector;

    public TranslatorTest() {
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

/*//    @Test
    public void testSomeMethod() throws ClassNotFoundException, SQLException {
        List<Table> tables = new ArrayList<Table>();
        
        Table tabla1 = new Table("test_repuestos", "repuestos");
        //Table tabla2 = new Table();
        //Table tabla3 = new Table();
        
        tables.add(tabla1);
        
        Translator transi = new Translator(connector);
        
        
        boolean result = transi.requiresTranslation(tables, connection);
        
        System.out.print(result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }*/

//    @Test
/*    public void translateCreateTableSentence() {
        // TODO review the generated test code and remove the default call to fail.
        String sql = "CREATE TABLE test_repuestos.example_autoincrement ("
                + "id INT NOT NULL AUTO_INCREMENT,"
                + "data ciudad NOT NULL, PRIMARY KEY (id));";

        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        try {
            s = p.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.getCause().printStackTrace();
        }
        
        StatementTranslator st = new StatementTranslator();
        s.accept(st);
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        
        s.accept(sdp);        

    }*/
    
    @Test
    public void getFuzzyDomainIdTest() throws SQLException {
        Translator t = new Translator(connector);
        Assert.assertEquals(new Integer(1) , t.getFuzzyDomainId("test_repuestos", "ciudad"));
        Assert.assertEquals(null, t.getFuzzyDomainId("test_repuestos", "pepito"));
        Assert.assertEquals(null, t.getFuzzyDomainId("caramelo", "ciudad"));        
        Assert.assertEquals(null, t.getFuzzyDomainId("test_repuestos", "INteger"));
    }
    
    @Test
    public void getFuzzyLabelIdTest() throws SQLException {
        Translator t = new Translator(connector);
        Assert.assertEquals(new Integer(2) , t.getFuzzyLabelId("test_repuestos", "ciudad", "Maracay"));
        Assert.assertEquals(null , t.getFuzzyLabelId("test_repuestos", "ciudad", "Pepito"));
    }
}
