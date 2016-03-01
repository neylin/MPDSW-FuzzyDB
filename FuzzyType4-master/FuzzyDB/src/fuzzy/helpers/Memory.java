/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.helpers;

import fuzzy.database.Connector;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import net.sf.jsqlparser.schema.Table;

/**
 *
 * @author Andras
 */
public class Memory {

    /**
     * Contiene todas las columnas no difusas. Los keys son Strings del tipo
     * scheman_name.table_name. Se almacenan en memoria al solicitarlas por primera
     * vez.
     */
    private static HashMap<String, LinkedHashSet<String>> columns = null;
    /**
     * Contiene todas las columnas difusas. Los keys son Strings del tipo
     * scheman_name.table_name. Se almacenan en memoria al solicitarlas por primera
     * vez.
     */
    private static HashMap<String, LinkedHashSet<String>> fuzzyColumns = null;
    private static HashMap<String, LinkedHashSet<String>> fuzzyType2Columns = null;
    
    public static HashSet<String> getColumns(Connector c, Table table) 
        throws SQLException{
        return getColumns(c, Helper.getSchemaName(c, table), table.getName());
    }

    /**
     * Generate the set of column names contained by the specified table
     * 
     * @param schemaName schema name in which the table is stored
     * @param tableName table name of which columns want to be got
     * @return list of columns contained by the specified table
     */
    // TODO este metodo puede no encontrar la columna que busca en cuyo caso muestra
    // una excepcion pero el flujo sigue. Deberia regresarse 
    public static LinkedHashSet<String> getColumns(Connector c, String schemaName, String tableName)
        throws SQLException {
        if (schemaName == null || schemaName.isEmpty()){
            throw new SQLException("No database selected", "3D000", 1046);
        }
        Logger.debug("Buscando columnas de esquema " + schemaName + " y tabla " + tableName);
        if (null == columns) {
            columns = new HashMap<String, LinkedHashSet<String>>();
        }
        if (!columns.containsKey(schemaName + "." + tableName)) {
            Logger.debug("Buscando columnas del esquema: " + schemaName);
            String sql = "SELECT TABLE_NAME, COLUMN_NAME FROM information_schema.COLUMNS "
                    + "WHERE TABLE_SCHEMA='" + schemaName + "'";
            ResultSet rs = c.executeRawQuery(sql);
            // register columns read from database
            while (rs.next()) {
                String tab = rs.getString("TABLE_NAME");
                LinkedHashSet<String> cols = columns.get(schemaName + "." + tab);
                if (null == cols) {
                    cols = new LinkedHashSet<String>();
                }
                cols.add(rs.getString("COLUMN_NAME"));
                columns.put(schemaName +"." + tab, cols);
            }
        }
        LinkedHashSet<String> cols = columns.get(schemaName + "." + tableName);
        Logger.debug("Returning " + cols);
        if (null == cols) {
            throw new SQLException("Unknown table '" + tableName + "' in '" + schemaName + "'",
                       "42S02", 1109);
        }
        return cols;
    }
    
    public static boolean isFuzzyColumn(Connector c, Table table, String column)
        throws SQLException {
        return isFuzzyColumn(c, Helper.getSchemaName(c), table.getName(), column);
    }
    

    /**
     * Given the table and the column, find out if the column is fuzzy
     * 
     * @param tableName table container
     * @param columnName column to check to see if it's fuzzy
     * @return if the column indeed is fuzzy
     */
    public static boolean isFuzzyColumn(Connector c, String schemaName, String tableName, String columnName)
        throws SQLException{
        if (null == fuzzyColumns) {
            fuzzyColumns = new HashMap<String, LinkedHashSet<String>>();
        }
        //TODO design unique data structure to store all columns and whether they're fuzzy or not
        if (!fuzzyColumns.containsKey(schemaName + "." + tableName)) {
            ResultSet rs = c.executeRawQuery("SELECT table_name, column_name "
                                             + "FROM information_schema_fuzzy.columns "
                                             + "WHERE table_schema = '" + schemaName + "'");
            // register columns read from database
            while (rs.next()) {
                String tab = rs.getString("table_name");
                LinkedHashSet<String> cols = fuzzyColumns.get(schemaName + "." + tab);
                if (null == cols) {
                    cols = new LinkedHashSet<String>();
                }
                cols.add(rs.getString("column_name"));
                fuzzyColumns.put(schemaName + "." + tab, cols);
            }
        }
        // if it's in the list, is a fuzzy column
        HashSet<String> cols = fuzzyColumns.get(schemaName + "." + tableName);
        return null != cols && cols.contains(columnName);
    }


    public static boolean isFuzzyType2Column(Connector c, Table table, String columnName)
        throws SQLException {
        String schemaName = null != table.getSchemaName() ? Helper.getSchemaName(c) : table.getSchemaName();
        return isFuzzyType2Column(c, schemaName, table.getName(), columnName);
    }

    /*
    Copia brutal de isFuzzyColumn, cambiando el Hash por otro.
    Eventualmente deberíá hacerse una estructura única para todo, pero bueno,
    esto es lo que hay por ahora y funciona.
    Viva la deuda técnica.
    */
    public static boolean isFuzzyType2Column(Connector c, String schemaName, String tableName, String columnName)
        throws SQLException {

        if (null == fuzzyType2Columns) {
            fuzzyType2Columns = new HashMap<String, LinkedHashSet<String>>();
        }
        
        if (!fuzzyType2Columns.containsKey(schemaName + "." + tableName)) {
            ResultSet rs = c.executeRawQuery("SELECT table_name, name "
                                             + "FROM information_schema_fuzzy.columns2 "
                                             + "WHERE table_schema = '" + schemaName + "'");
            // register columns read from database
            while (rs.next()) {
                String tab = rs.getString("table_name");
                LinkedHashSet<String> cols = fuzzyType2Columns.get(schemaName + "." + tab);
                if (null == cols) {
                    cols = new LinkedHashSet<String>();
                }
                cols.add(rs.getString("name"));
                fuzzyType2Columns.put(schemaName + "." + tab, cols);
            }
        }
        // if it's in the list, is a fuzzy column
        HashSet<String> cols = fuzzyType2Columns.get(schemaName + "." + tableName);
        return null != cols && cols.contains(columnName);
    }
    
    public static void wipeMemory() {
        columns = null;
        fuzzyColumns = null;
        fuzzyType2Columns = null;
    }
    
}
