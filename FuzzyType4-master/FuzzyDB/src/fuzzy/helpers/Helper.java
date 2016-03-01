package fuzzy.helpers;

import fuzzy.database.Connector;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.jsqlparser.schema.Table;

/**
 *
 * @author bishma-stornelli
 */
public class Helper  {
    

    public static String getSchemaName(Connector c, Table table) throws SQLException {
        return table.getSchemaName() != null ? table.getSchemaName() : getSchemaName(c);
    }
    
    public static String getSchemaName(Connector c) throws SQLException {
        return c.getSchema();
    }

    public static String getDomainNameForColumn(Connector c,
                           Table table, String columnName) throws SQLException {
        String schemaName = Helper.getSchemaName(c, table);
        String tableName = table.getName();
        String sql = "SELECT domain_name "
                + "FROM information_schema_fuzzy.domains AS D JOIN "
                + "information_schema_fuzzy.columns AS C ON (D.domain_id = C.domain_id) "
                + "WHERE C.table_schema = '" + schemaName + "' "
                + "AND C.table_name = '" + tableName + "' "
                + "AND C.column_name = '" + columnName +"'";
        
        Logger.debug("Looking for domain name with query:\n" + sql);
        
        ResultSet rs = c.executeRawQuery(sql);
        SQLException e = null;
        try {
            if (rs.first()) {
                return rs.getString("domain_name");
            }
        } catch (SQLException ex) {
            e = ex;
        }
        throw new SQLException("Domain name not found for " + schemaName + "." + tableName + "." + columnName, "42000", 3020, e);
    }

    
}
