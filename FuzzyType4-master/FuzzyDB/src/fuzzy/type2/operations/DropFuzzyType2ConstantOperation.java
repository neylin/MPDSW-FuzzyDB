package fuzzy.type2.operations;

import java.sql.SQLException;
/* fuzzy imports*/
import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Printer;

/**
 * Drops a Type-2 Fuzzy type. This includes all the queries and operations
 * required for the custom ordering of this type.
 */
public class DropFuzzyType2ConstantOperation extends Operation {

    private final String domain;
    private final String name;

    /**
     * Creates a new instance.
     *
     * @param connector Connector instance used to interface with the database.
     * @param domain Domain that has to be dropped.
     * @param name
     */
    public DropFuzzyType2ConstantOperation(Connector connector, String domain,
            String name) {
        super(connector);
        this.domain = domain;
        this.name = name;
    }

    /**
     * Drops the necessary operators for fuzzy Type II orderings.
     *
     * @param schemaName the schema name which is related to.
     * @throws java.sql.SQLException
     */
    public void dropConstant(String schemaName) throws SQLException {
        String dropConstant = "DELETE FROM information_schema_fuzzy.constants2 "
                + "WHERE constant_schema = '" + schemaName + "' "
                + "AND domain_name = '" + this.domain + "' "
                + "AND constant_name = '" + this.name + "';";
        connector.executeRawUpdate(dropConstant);
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getSchema();
        dropConstant(schemaName);
    }
}