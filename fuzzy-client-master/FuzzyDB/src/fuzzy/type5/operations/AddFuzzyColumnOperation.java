/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;

/**
 *
 * @author hector
 */
public class AddFuzzyColumnOperation extends Operation {

    private String schemaName;
    private String tableName;
    private String columnName;
    private Integer domainId;
    private Integer type3domainId;
    
    public AddFuzzyColumnOperation(Connector connector,
        String schemaName, String tableName, String columnName, 
        Integer domainId, Integer type3domainId) {
        
        super(connector);

        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.domainId = domainId;
        this.type3domainId = type3domainId;
    }

    @Override
    public void execute() throws SQLException {
        
        /*
        * Suponiendo que se agrego la siguiente columna:
        * CREATE TABLE test_table (
        *    ...
        *    col_name fuzzy_domain_type5
        *    ...
        * )
        * El id de fuzzy_domain_type5 es 666
        */

        /*
        * INSERT INTO information_schema_fuzzy.columns
        * VALUES ('column_id','test_schema', 'test_table', 'col_name', 666)
        */        
        String insertColumnCatalog = "INSERT INTO information_schema_fuzzy.columns5 "
                                   + "VALUES ("
                                   + "'" + this.schemaName + "' ," // table_schema
                                   + "'" + this.tableName + "' ,"  // table_name
                                   + "'" + this.columnName + "' ," // columnName
                                   + " " + this.domainId + ");";   // domain_id
        

        /*
        * ALTER TABLE test_schema.test_table 
        * ADD CONSTRAINT col1_possibility_range
        * CHECK (0.0 <= ALL (col1.odd) AND 1.0 >= ALL (col1.odd))
        */
        String check_possibility = "ALTER TABLE " + this.schemaName + "." + this.tableName + " "
                                 + "ADD CONSTRAINT " + this.columnName + "_possibility_range "
                                 + "CHECK ( " 
                                 + "0.0 <= ALL ((" + this.columnName + ").odd) AND "
                                 + "1.0 >= ALL ((" + this.columnName + ").odd) )";

    
        /*
        * ALTER TABLE test_schema.test_table
        * ADD CONSTRAINT col1_matching_lengths
        * CHECK (array_length(col1.odd, 1) == array_length(col1.value, 1))
        */
        String check_matching_lengths = "ALTER TABLE " + this.schemaName + "." + this.tableName + " "
                                      + "ADD CONSTRAINT " + this.columnName + "_matching_lengths "
                                      + "CHECK ( " 
                                      + "array_length((" + this.columnName + ").odd, 1) = "
                                      + "array_length((" + this.columnName + ").value, 1) )";

        /*
        * ALTER TABLE test_schema.test_table
        * ADD CONSTRAINT col1_normalization
        * CHECK (1.0 = ANY (col1.odd))
        */
        String check_normalization = "ALTER TABLE " + this.schemaName + "." + this.tableName + " "
                                   + "ADD CONSTRAINT " + this.columnName + "_normalization "
                                   + "CHECK ( " 
                                   + "1.0 = ANY ((" + this.columnName + ").odd) )";

        String find_domain_labels = "SELECT label_name "
                                 + "FROM information_schema_fuzzy.labels "
                                 + "WHERE domain_id = " + this.type3domainId;
        
        ResultSet rs = this.connector.executeRawQuery(find_domain_labels);
        ArrayList<String> labels = new ArrayList<String>();
        while (rs.next())
            labels.add(rs.getString("label_name"));
        
        // Validar que las etiqutas sean validas en el dominio tipo3
        String check_valid_labels = "ALTER TABLE " + this.schemaName + "." + this.tableName + " "
                                 + "ADD CONSTRAINT " + this.columnName + "_valid_labels "
                                 + "CHECK ( ((" + this.columnName + ").value ) <@ ( ARRAY "
                                 + arrayToString(labels) + "))";
        
        // Validar que todas las etiquetas del arreglo de labels sean distintas
        String check_diff_labels = "ALTER TABLE " + this.schemaName + "." + this.tableName + " "
                                 + "ADD CONSTRAINT " + this.columnName + "_diff_labels "
                                 + "CHECK ( "
                                 + "array_length((" + this.columnName + ").odd, 1) = "
                                 + "array_length(( information_schema_fuzzy.array_unique((" + this.columnName + ").value)), 1))";
        Logger.debug(check_diff_labels);
        Savepoint sp = this.beginTransaction();
        try {
            this.connector.executeRaw(insertColumnCatalog);
            this.connector.executeRaw(check_possibility);
            this.connector.executeRaw(check_matching_lengths);
            this.connector.executeRaw(check_normalization);
            this.connector.executeRaw(check_valid_labels);
            this.connector.executeRaw(check_diff_labels);
            this.commitTransaction();
        } catch (SQLException e) {
            this.rollback(sp);
            throw e;
        }
    }
    
    private String arrayToString(ArrayList<String> al) {
        String s = "[";
        for (int i = 0; i < al.size(); i++)
            s += "'" + al.get(i) + "'" + (i == al.size()-1? "]" : ", ");
        
        return s;
    }
}
