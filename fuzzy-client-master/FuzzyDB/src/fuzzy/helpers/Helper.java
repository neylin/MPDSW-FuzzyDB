package fuzzy.helpers;

import fuzzy.database.Connector;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.jsqlparser.schema.Table;

/**
 *
 * @author bishma-stornelli
 *         Jose Sanchez
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
    
    
    
    /*
     * Returns the domain name of the column of the table if the domain is type 5
     * Otherwise returns null
     */
    public static String getDomainNameForColumn5(Connector c,
                           Table table, String columnName) throws SQLException {
        String schemaName = Helper.getSchemaName(c, table);
        String tableName = table.getName();
        String sql = "SELECT domain_name "
                + "FROM information_schema_fuzzy.domains AS D JOIN "
                + "information_schema_fuzzy.columns5 AS C ON (D.domain_id = C.domain_id) "
                + "WHERE C.table_schema = '" + schemaName + "' "
                + "AND C.table_name = '" + tableName + "' "
                + "AND C.column_name = '" + columnName +"'";
        
        Logger.debug("Looking for domain name with query:\n" + sql);
        
        ResultSet rs = c.executeRawQuery(sql);
        if(rs == null) return null;
        if (rs.first()) {
            return rs.getString("domain_name");
        }
        return null;
        
    }
    
    /*
     * Returns if the label specified is from the domain type 5
     */
    public static boolean isLabelOfDomain5(Connector c, String domainName, String labelName) throws SQLException{
        String schemaName = c.getSchema();
        String sql = "SELECT label_id "
                + "FROM information_schema_fuzzy.domains AS D5, "
                + "information_schema_fuzzy.labels AS L "
                + "WHERE D5.table_schema = '" + schemaName + "' AND "
                + "D5.type3_domain_id = L.domain_id AND "
                + "D5.domain_name = '" +domainName+ "' AND "
                + "L.label_name = " + labelName + ";";
        
        ResultSet rs = c.executeRawQuery(sql);
        if(rs == null) return false;
        if (rs.first()) {
            return true;
        }
        return false;
    }
    
    
    /*
     * Returns if exist a domain type 5 linked to the argument domain
     */
    public static boolean isDomainLinked(Connector c, String schemaName, String domainName)throws SQLException{
        String sql = "SELECT D1.domain_id FROM information_schema_fuzzy.domains AS D1, information_schema_fuzzy.domains AS D2 "
                + "WHERE D2.domain_name = '" + domainName + "' AND D1.type3_domain_id = D2.domain_id";
                
        //+ "AND D2.table_schema = '" + schemaName +"'";
        ResultSet resultSet;
        resultSet = c.executeRawQuery(sql);
        return resultSet != null && resultSet.next();
    }
    
    
    /*
     * Funcion que dado el id de un dominio tipo5 (domainId), retorna el id del
     * dominio tipo3 sobre el cual esta basado el dominio.
     * 
     * Solo retorna valores validos con dominios de tipo5 ya que estos
     * son los unicos para los cuales el atributo 'type3_domain_id' de
     * la tabla 'domains' es diferente de NULL.
     */
    public static Integer getType3DomainIdRelated(Connector c, Integer domainId)
            throws SQLException {
        String sql = "SELECT type3_domain_id "
                + "FROM information_schema_fuzzy.domains AS D "
                + "WHERE D.domain_id = " + domainId;
        
        Logger.debug("Looking for type3domainId id with query:\n" + sql);
        
        ResultSet rs = c.executeRawQuery(sql);
        SQLException e = null;
        try {
            if (rs.first()) {
                return rs.getInt("type3_domain_id");
            }
        } catch (SQLException ex) {
            e = ex;
        }
        throw new SQLException("Error getting Type3DomainId related to domain with id " + domainId , "42000", 3020, e);
    }
    
    /*
     * Return the type of domain in the schema.
     * If domain is not a fuzzy domain then returns null
     */
    public static Integer getDomainType(Connector c, String domain)throws SQLException {
        String schemaName = c.getSchema();
        if (c.isNativeDataType(domain)) {
            return null;
        }
        String sql;
        ResultSet resultSet;
        sql = "SELECT id "
            + "FROM information_schema_fuzzy.domains2 "
            + "WHERE table_schema = '" + schemaName + "' AND domain_name = '"
            + domain + "' "
            + "LIMIT 1";
        resultSet = c.executeRawQuery(sql);
        if (resultSet != null && resultSet.next()) {
            return 2; //resultSet.getInt(1);
        }
        
        sql = "SELECT domain_type "
            + "FROM information_schema_fuzzy.domains "
            + "WHERE table_schema = '" + schemaName + "' AND domain_name = '"
            + domain + "' "
            + "LIMIT 1";
        resultSet = c.executeRawQuery(sql);
        if (resultSet != null && resultSet.next()) {
            return resultSet.getInt(1);
        }
        return null;
    }
    
}
