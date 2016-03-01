/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.common.translator;

import fuzzy.database.Connector;
import fuzzy.common.operations.Operation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** This class helps to translate queries. It parses the user input and translate it
 * if needed. 
 *
 * @author bishma-stornelli
 */
public class Translator {
    
    public static final SQLException FR_NO_FUZZY_COLUMN = new SQLException("Can't use non-fuzzy columns with fuzzy syntax.", "07006", 3000);
    public static final SQLException FR_NON_SUPPORTED_FUNCTION = new SQLException("Non-supported aggregation function when grouping by fuzzy column.", "42000", 3001);
    public static final SQLException FR_INVALID_STARTING_VALUE = new SQLException("Starting value must be a String enclosed with ' or \"", "42000", 3002);
    public static final SQLException ER_NO_DB_ERROR = new SQLException("No database selected", "3D000", 1046);
    
    public static SQLException FR_INVALID_REFLEXIVITY_FOR_DOMAIN(String label) {
        return new SQLException("Resulting similarity relation doesn't hold reflexivity for label '" + label + "'", "42000", 3008);
    }

    public static SQLException FR_INVALID_SYMMETRY_FOR_DOMAIN(String label1, String label2, double value1, double value2) {
        return new SQLException("Resulting similarity relation doesn't hold symmetry for  ('" + label1 + "', '" + label2 + "') (two different values found: " + value1 + ", " + value2 + ")", "42000", 3009);
    }

    public static SQLException FR_INVALID_TRANISITIVITY_FOR_DOMAIN(String label1, String label2, double value1, double value2) {
        return new SQLException("Resulting similarity relation doesn't hold transitivity for  ('" + label1 + "', '" + label2 + "') (two different values derived: " + value1 + ", " + value2 + ")", "42000", 3010);
    }

    public static SQLException FR_DUPLICATE_LABEL_VALUE(String label) {
        return new SQLException("Duplicate label value '" + label + "'", "42000", 3003);
    }
    
    public static SQLException FR_DUPLICATE_LABEL_ALT2() {
        return new SQLException("Duplicate label value in the SELECT statement result. Try SELECT DISTINCT.", "42000", 3003);
    }

    public static SQLException FR_DUPLICATE_DOMAIN_NAME(String domainName) {
        return new SQLException("Can't create domain '" + domainName + "'; domain already exists", "HY000", 3004);
    }
    
    public static SQLException FR_LABEL_DO_NOT_EXISTS(String label) {
        return new SQLException("Label '" + label + "' doesn't exist in domain", "42000", 3005);
    }
    
    public static SQLException FR_INVALID_SIMILARITY_VALUE(double value) {
        return new SQLException("Invalid similarity value '" + value + "'; must be between 0.0 and 1.0", "42000", 3006);
    }

    public static SQLException FR_DUPLICATE_SIMILARITY(String label1, String label2) {
        return new SQLException("Duplicate similarity for ('" + label1 + "', '" + label2 + "')", "42000", 3007);
    }
    
    public static SQLException FR_UNKNOWN_DOMAIN(String domain) {
        return new SQLException("Unknown domain '" + domain + "'", "42000", 3011);
    }

    public static SQLException FR_EMPTY_VALUES_LIST(String schemaName, String tableName, String columnName) {
        return new SQLException("Can't create domain without labels; not found non-null values in '" + (schemaName != null ? schemaName + "." : "") + tableName + "." + columnName + "'", "42000", 3011);
    }
    
    public static SQLException FR_EMPTY_SELECT_RESULT(String select) {
        return new SQLException("Can't create domain; not found a valid result in: " + select , "42000", 3011);
    }


    protected String failureMessage = "";
    protected Connector connector;
    protected List<Operation> operations;
    protected boolean ignoreAST = false;


    public Translator(Connector connector) {
        this.connector = connector;
        //Por el momento no le voy a parar a esto
    }


    public Translator(Connector connector, List<Operation> operations) {
        this.connector = connector;
        this.operations = operations;
    }

    public boolean getIgnoreAST() {
        return this.ignoreAST;
    }

    
    
    public Integer getFuzzyDomainId(String schemaName, String domainName, String domain_type)
        throws SQLException {
        if (Connector.isNativeDataType(domainName)) {
            return null;
        }
        String sql = "SELECT domain_id "
                + "FROM information_schema_fuzzy.domains "
                + "WHERE table_schema = '" + schemaName + "' AND domain_name = '"
                + domainName + "' AND domain_type = '" + domain_type + "' "
                + "LIMIT 1";
        ResultSet resultSet = connector.executeRawQuery(sql);
        if (resultSet != null && resultSet.next()) {
            return resultSet.getInt(1);
        }
        return null;
    }

    public Integer getFuzzyLabelId(String schemaName, String domainName, String labelName)
        throws SQLException {
        String sql = "SELECT label_id "
                + "FROM information_schema_fuzzy.labels AS L "
                + "JOIN information_schema_fuzzy.domains AS D ON (L.domain_id = D.domain_id)"
                + "WHERE D.table_schema = '" + schemaName + "' AND D.domain_name = '"
                + domainName + "' "
                + " AND L.label_name = '"
                + labelName + "' "
                + "LIMIT 1";
        ResultSet resultSet = connector.executeRawQuery(sql);
        if (resultSet != null && resultSet.next()) {
            return resultSet.getInt(1);
        }
        return null;
    }

    public Integer getFuzzyType2DomainId(String schemaName, String domainName)
        throws SQLException {
        if (Connector.isNativeDataType(domainName)) {
            return null;
        }
        String sql = "SELECT id "
                + "FROM information_schema_fuzzy.domains2 "
                + "WHERE table_schema = '" + schemaName + "' AND domain_name = '"
                + domainName + "' "
                + "LIMIT 1";
        ResultSet resultSet = connector.executeRawQuery(sql);
        if (resultSet != null && resultSet.next()) {
            return resultSet.getInt(1);
        }
        return null;
    }
    
}
