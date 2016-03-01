package fuzzy.type2.operations;

import fuzzy.database.Connector;
import fuzzy.common.operations.Operation;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Savepoint;

public class AddFuzzyType2ColumnOperation extends Operation {

    private String  schemaName;
    private String  tableName;
    private String  columnName;
    private Integer domainId;

    public AddFuzzyType2ColumnOperation(Connector connector,
            String schemaName, String tableName, String columnName, Integer domainId) {
        super(connector);
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.domainId = domainId;
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }

        /*
         * Suponiendo que se agregó la siguiente columna:
         * CREATE TABLE test_schema.test_table (
         *    ...
         *    col1 fuzzy_domain
         *    ...
         * )
         * El id de fuzzy_domain es 666
         * Y es sobre Integer con rango del 1 al 100
         */

        /*
         * INSERT INTO information_schema_fuzzy.columns2
         * VALUES ('test_schema', 'test_table', 'col1', 666)
         */
        String insert_column = "INSERT INTO information_schema_fuzzy.columns2 "
                + "VALUES ("
                + "'" + this.schemaName + "', "
                + "'" + this.tableName + "', "
                + "'" + this.columnName + "', "
                + domainId
                + ")";

        // Se agregaron las restricciones de integridad via ALTER TABLE porque
        // no era posible hacerlo modificando el AST de la consulta original.
        // pues JSqlParser no lo modeló. Y no vale la pena ponerse a implementarlo
        // en el parser.

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

        // Buscar el dominio a ver si fue definido sobre un rango
        // En cuyo caso también hay que agregar un CHECK para validarlo.
        String find_domain_range = "SELECT start, finish "
                + "FROM information_schema_fuzzy.domains2 "
                + "WHERE id = " + this.domainId + " "
                + "LIMIT 1";
        ResultSet rs = this.connector.executeRawQuery(find_domain_range);
        rs.next();
        String lower_bound = rs.getString("start");
        String upper_bound = rs.getString("finish");

        String check_value;
        if (null != lower_bound && null != upper_bound) {
            /*
             * ALTER TABLE test_schema.test_table ADD CONSTRAINT col1_value_range
             * CHECK (1 <= ALL (col1.value) AND 100 >= ALL (col1.value))
             */
            check_value = "ALTER TABLE " + this.schemaName + "." + this.tableName + " "
                    + "ADD CONSTRAINT " + this.columnName + "_value_range "
                    + "CHECK ( "
                    + lower_bound + " <= ALL ((" + this.columnName + ").value) AND "
                    + upper_bound + " >= ALL ((" + this.columnName + ").value) )";
        } else {
            check_value = "";
        }

        /* Check if valid trapezoid */
        String valid_trapezoid = "ALTER TABLE " + this.schemaName + "." + this.tableName + " "
                + "ADD CONSTRAINT " + this.columnName + "_valid_trapezoid "
                + " CHECK ((" + this.columnName + " IS NULL) OR ((" + this.columnName + ").type = TRUE)"
                + " OR ("
                + "((" + this.columnName + ").type = FALSE AND array_length((" + this.columnName + ").value, 1) = 4 AND ("
                + "("
                + "(" + this.columnName + ").value[1] IS NOT NULL "
                + "AND (" + this.columnName + ").value[2] IS NOT NULL "
                + "AND (" + this.columnName + ").value[3] IS NOT NULL "
                + "AND (" + this.columnName + ").value[4] IS NOT NULL "
                + "AND ((" + this.columnName + ").value[1] < (" + this.columnName + ").value[2] OR (" + this.columnName + ").value[1] = (" + this.columnName + ").value[2]) "
                + "AND ((" + this.columnName + ").value[2] < (" + this.columnName + ").value[3] OR (" + this.columnName + ").value[2] = (" + this.columnName + ").value[3]) "
                + "AND ((" + this.columnName + ").value[3] < (" + this.columnName + ").value[4] OR (" + this.columnName + ").value[3] = (" + this.columnName + ").value[4]) "
                + ")"
                + "OR"
                + "("
                + "(" + this.columnName + ").value[1] IS NULL "
                + "AND (" + this.columnName + ").value[2] IS NULL "
                + "AND (" + this.columnName + ").value[3] IS NOT NULL "
                + "AND (" + this.columnName + ").value[4] IS NOT NULL "
                + "AND ((" + this.columnName + ").value[3] < (" + this.columnName + ").value[4] OR (" + this.columnName + ").value[3] = (" + this.columnName + ").value[4]) "
                + ") "
                + "OR "
                + "( "
                + "(" + this.columnName + ").value[1] IS NOT NULL "
                + "AND (" + this.columnName + ").value[2] IS NOT NULL "
                + "AND (" + this.columnName + ").value[3] IS NULL "
                + "AND (" + this.columnName + ").value[4] IS NULL "
                + "AND ((" + this.columnName + ").value[1] < (" + this.columnName + ").value[2] OR (" + this.columnName + ").value[1] = (" + this.columnName + ").value[2])"
                + ") "
                + "))))";

        Savepoint sp = this.beginTransaction();
        try {
            this.connector.executeRaw(insert_column);
            this.connector.executeRaw(check_possibility);
            this.connector.executeRaw(check_matching_lengths);
            this.connector.executeRaw(check_normalization);
            if (null != lower_bound && null != upper_bound) {
                this.connector.executeRaw(check_value);
            }
            this.connector.executeRaw(valid_trapezoid);
            this.commitTransaction();
        } catch (SQLException e) {
            this.rollback(sp);
            throw e;
        }
    }
}
