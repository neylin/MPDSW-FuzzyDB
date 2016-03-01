/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzy;

import fuzzy.database.Connector;
import fuzzy.helpers.Logger;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import fuzzy.common.operations.Operation;
import fuzzy.type3.translator.StatementTranslator;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;

/**
 *
 * @author timauryc
 */
public class Helper {

    
    protected static Connector connector;
    public static void setConnector(Connector connector) {
        Helper.connector = connector;
    }
    
    public String parseError = "Error ocurrido en el parser";
    public String translateError = "Error ocurrido en la traduccion";
    public String deParseError = "Error ocurrido en el deParser";
    public String columnsError = "Error ocurrido al comparar el numero de columnas de las tuplas";
    public String rowsError = "Error ocurrido al comparar el numro de filas de las tuplas";
    public String labelError = "Error ocurrido al comparar los labels de las columnas";
    public String tupleError = "Error ocurrido al comparar los valores de las tuplas";
    public String queryMessage;
    public String query;

    /*public Helper(){
    
    }*/
    
    public Helper(String queryMessage, String query){
        this.queryMessage = queryMessage;
        this.query = query;
    }
    
    public static void cleanDomainMetaData(String domainName)
        throws SQLException {
        connector.executeRawUpdate("DELETE FROM information_schema_fuzzy.domains "
                + "WHERE domain_name = '" + domainName + "'");
        
    }
    
    public static void cleanSchemaMetaData(String schemaName)
        throws SQLException {
        connector.executeRawUpdate("DELETE FROM information_schema_fuzzy.columns "
                + "WHERE table_schema = '" + schemaName + "'");
        connector.executeRawUpdate("DELETE FROM information_schema_fuzzy.domains "
                + "WHERE table_schema = '" + schemaName + "'");
    }   
        
    public void compareResults(ResultSet actual, ResultSet expected) throws SQLException{
        //HashSet<String> set1 = new HashSet<String>();
        //HashSet<String> set2 = new HashSet<String>();
        //Getting the resultSetMetaData
        ResultSetMetaData act = actual.getMetaData();
        ResultSetMetaData exp = actual.getMetaData();
        //Checking number collumns
        int columns1 = act.getColumnCount();
        int columns2 = exp.getColumnCount();
        //if(columns1 != columns2)return false;
        assertEquals(this.queryMessage  + "\n" + this.query  + "\n" +  this.columnsError, columns1, columns2);
        
        //Comparing column labels
        for (int i = 1 ; i <= columns1 ; ++i ){
            //if(!act.getColumnLabel(i).equals(exp.getColumnLabel(i))) return false;
            assertEquals(this.queryMessage  + "\n" + this.query  + "\n" + this.labelError, act.getColumnName(i), exp.getColumnName(i));
        }
        
        //Checking number rows
        actual.last();
        int rows1 = actual.getRow();
        expected.last();
        int rows2 = expected.getRow();
        assertEquals(this.queryMessage  + "\n" + this.query  + "\n" + this.rowsError, rows1, rows2);
        //if(rows1 != rows2)return false;
        
        //Iterating trough values and comparing
        actual.beforeFirst();
        expected.beforeFirst();
        
        Set expectedSet = new HashSet();
        Set actualSet = new HashSet();
        
        while (actual.next()&&expected.next()){
            String expectedElements = ""; 
            String actualElements = "";
            
            for (int i = 1 ; i <= columns1 ; ++i ){
                expectedElements +=  " null " + expected.getObject(i).toString();
                actualElements +=  " null " + actual.getObject(i).toString();
                //set1.add(actual.getObject(i).toString());
                //set2.add(expected.getObject(i).toString());
                //if(!actualColumn.equals(expectedColumn))return false;
            }
            
            expectedSet.add(expectedElements);
            actualSet.add(actualElements);
            //System.out.println(expectedSet);
        }
        
        assertTrue(this.queryMessage  + "\n" +  this.query  + "\n" + this.tupleError , expectedSet.containsAll(actualSet));
    }
    
    public static Statement parse(String sql) throws JSQLParserException {
        StringReader stringReader = new StringReader(sql);
        try {
            CCJSqlParserManager p = new CCJSqlParserManager();        
            return p.parse(stringReader);
        } finally {
            stringReader.close();
        }
    }
    
    public static String translateAndFailOnErrors(String sql, String parseMessage, String translator, String deParseMessage) {
        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        StringReader stringReader = new StringReader(sql);
        try {
            s = p.parse(stringReader);
        } catch (JSQLParserException e) {
            System.out.println(sql);
            assertTrue(parseMessage, false);
           // e.getCause().printStackTrace();
        } finally {
            stringReader.close();
        }
        
        List<Operation> operations = new ArrayList<Operation>();
        StatementTranslator st = new StatementTranslator(connector, operations);
        try {
            s.accept(st);
        } catch (Exception ex) {
            System.out.println(sql);
            assertTrue(translator, false);
            /*Logger.getLogger(BasicSelectTest.class.getName()).log(Level.SEVERE, null, ex);
            return "";*/
        }
        
        StringBuffer sb = new StringBuffer();
        StatementDeParser sdp = new StatementDeParser(sb);
        try {
            s.accept(sdp);
        } catch (Exception ex) {
            System.out.println(sql);
            assertTrue(deParseMessage, false);
            /*Logger.getLogger(BasicSelectTest.class.getName()).log(Level.SEVERE, null, ex);
            return "";*/
        }
        
        return sb.toString();
    }
    
    public static void parseAndTranslate(String sql) throws Exception {
        Statement s = parse(sql);
        
        List<Operation> operations = new ArrayList<Operation>();
        StatementTranslator st = new StatementTranslator(connector, operations);
        s.accept(st);
    }
    
    public static void executeDDLAndFailOnErrors(String sql) throws Exception {
        CCJSqlParserManager p = new CCJSqlParserManager();
        Statement s = null;
        StringReader stringReader = new StringReader(sql);
        try {
            s = p.parse(stringReader);
        } catch (JSQLParserException e) {
            Logger.debug(e.getCause().getMessage());
            e.getCause().printStackTrace();
        } finally {
            stringReader.close();
        }
        
        List<Operation> operations = new ArrayList<Operation>();
        StatementTranslator st = new StatementTranslator(connector, operations);
        s.accept(st);
        
        for (Operation o : operations) {
            o.execute();
        }
    }
    
    public static void validateMetaData(String schemaName, String domainName, 
            String[] labels, String[][] similarities) {
        try {
            ResultSet rs = connector.executeRawQuery("SELECT domain_id FROM information_schema_fuzzy.domains "
                                             + "WHERE table_schema = '" + schemaName
                                             + "' AND domain_name = '" + domainName
                                             + "'");
            
            assertTrue("Expecting domain '" + domainName + "' in schema '" + schemaName + "'", rs.next());
            Integer domainId = rs.getInt("domain_id");
            HashMap<String, Integer> labelIds = new HashMap<String, Integer>(labels.length);
            for (int i = 0 ; i < labels.length ; ++i) {
                rs = connector.executeRawQuery("SELECT label_id "
                        + "FROM information_schema_fuzzy.labels "
                        + "WHERE domain_id = " + domainId + " "
                        + "AND label_name = '" + labels[i] + "'");
                assertTrue("Expecting label '" + labels[i] + "' in domain '" + domainName + "'", rs.next());
                labelIds.put(labels[i], rs.getInt("label_id"));
            }
            
            for (int i = 0 ; i < similarities.length ; ++i) {
                rs = connector.executeRawQuery("SELECT value "
                        + "FROM information_schema_fuzzy.similarities "
                        + "WHERE label1_id = " + labelIds.get(similarities[i][0]) + " "
                        + "AND label2_id = " + labelIds.get(similarities[i][1]) + " "
                        + "AND derivated = '" + similarities[i][3] + "'");
                assertTrue("Expecting " 
                        + (similarities[i][3].equalsIgnoreCase("false") ? "non-derivated" : "derivated")
                        + " similarity between '" + similarities[i][0] + "' and "
                        + "'" + similarities[i][1] +"'", rs.next());
                assertEquals("Wrong similarity value between '" + similarities[i][0] + "' and "
                        + "'" + similarities[i][1] + "'", 
                        rs.getBigDecimal("value").compareTo(new BigDecimal(similarities[i][2])), 0);
            }
        } catch (SQLException ex) {
            Assert.fail("Error running the test. Try again. Exception: " + ex.toString());
        }
    }
    
    public static void createMetaData(String database, String domainName, String[] labels, String[][] similarities) throws SQLException {
        ResultSet rs = connector.executeRawQuery("SELECT domain_id FROM information_schema_fuzzy.domains "
                + "WHERE table_schema = '" + database + "' "
                + "AND domain_name = '" + domainName + "'");
        Integer domainId = null;
        try {
            if (rs.next()){
                domainId = rs.getInt("domain_id");
            }
        } catch (SQLException ex) {}
        
        if (domainId == null){
            domainId = connector.executeRawInsert("INSERT INTO information_schema_fuzzy.domains "
                 + "VALUES (DEFAULT, '" + database + "', '" + domainName + "')");
                
        }
        
        for (int i = 0 ; i < labels.length ; ++i) {
            connector.executeRawInsert("INSERT INTO information_schema_fuzzy.labels "
                    + "VALUES (DEFAULT, " + domainId + ", '" + labels[i] + "')");
        }
        
        for (int i = 0 ; i < similarities.length ; ++i) {
            Integer label1Id = null;
            Integer label2Id = null;
            ResultSet rs1 = connector.executeRawQuery("SELECT label_id FROM information_schema_fuzzy.labels WHERE domain_id = " + domainId + " AND label_name = '" + similarities[i][0] + "'");
            ResultSet rs2 = connector.executeRawQuery("SELECT label_id FROM information_schema_fuzzy.labels WHERE domain_id = " + domainId + " AND label_name = '" + similarities[i][1] + "'");
            
            rs1.next();
            rs2.next();
            label1Id = rs1.getInt("label_id");
            label2Id = rs2.getInt("label_id");            
            
            String sql = "INSERT INTO information_schema_fuzzy.similarities "
                    + "VALUES (" + label1Id + ", "
                    + label2Id + ", "
                    + similarities[i][2] + ", '"
                    + similarities[i][3] + "')";
            connector.executeRawUpdate(sql);
        }        
    }
    
    public static void createData(String database, String table, String[] columnNames, String[] columnTypes, String[] columnConstraints, String[][] rows) throws SQLException {
        connector.executeRawUpdate("CREATE SCHEMA IF NOT EXISTS " + database);
        StringBuilder createTableSql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + database + "." + table + " (");
        StringBuilder insertIntoSql = new StringBuilder("INSERT INTO " + database + "." + table + "(");
        boolean somethingInBuffer = false;
        for (int i = 0 ; i < columnNames.length && 
                i < columnTypes.length && 
                i < columnConstraints.length ; ++i) {
            createTableSql.append(somethingInBuffer ? ", " : "").append(columnNames[i]).append(" ").append(columnTypes[i]).append(" ").append(columnConstraints[i]);
            insertIntoSql.append(somethingInBuffer ? ", " : "").append(columnNames[i]);
            somethingInBuffer = true;
        }
        createTableSql.append(")");
        connector.executeRawUpdate(createTableSql.toString());
        
        insertIntoSql.append(") VALUES ");
        somethingInBuffer = false;
        for (int i = 0 ; i < rows.length ; ++i) {
            insertIntoSql.append(somethingInBuffer ? ", " : "");
            somethingInBuffer = true;
            boolean somethingInBuffer2 = false;
            for (int j = 0 ; j < rows[i].length ; ++j) {
                insertIntoSql.append(somethingInBuffer2 ? ", " : "(").append(rows[i][j] != null && !rows[i][j].equals("DEFAULT") ? "'" : "").append(rows[i][j]).append(rows[i][j] != null && !rows[i][j].equals("DEFAULT") ? "'" : "");
                somethingInBuffer2 = true;
            }
            insertIntoSql.append(")");
        }
        
        connector.executeRawUpdate(insertIntoSql.toString());
    }
    
    public static void validateData(String schemaName, String tableName, String[] columnNames, String[][] rows) {
        try {
            StringBuilder selectStatement = new StringBuilder("SELECT ");
            boolean somethingInBuffer = false;
            for (int i = 0 ; i < columnNames.length ; ++i) {
                selectStatement.append(somethingInBuffer ? "," : "").append(columnNames[i]);
                somethingInBuffer = true;
            }
            selectStatement.append(" FROM ").append(schemaName).append(".").append(tableName);
            ResultSet rs = connector.executeRawQuery(selectStatement.toString());
            
            Set<List<String>> actual = new HashSet(rows.length);
            Set<List<String>> expected = new HashSet(rows.length);
            
            int i = 0;
            while (rs.next() && i < rows.length){
                List<String> actualList = new ArrayList<String>();
                List<String> expectedList = Arrays.asList(rows[i]);
                for (int j = 0 ; j < columnNames.length ; ++j) {
                    actualList.add(rs.getString(j + 1));
                }
                actual.add(actualList);
                expected.add(expectedList);
            }
            for (List<String> e : expected) {
                assertTrue(actual.contains(e));
            }
        } catch (SQLException ex) {
            Assert.fail("Error running the test. Try again. Exception: " + ex.toString());
        }
    }
}
