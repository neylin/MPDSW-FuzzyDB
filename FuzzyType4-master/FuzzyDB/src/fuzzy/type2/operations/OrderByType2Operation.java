package fuzzy.type2.operations;

import java.sql.SQLException;
/* imports from fuzzy */
import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Printer;

/**
 * Setups a new Type-2 Fuzzy type in the schema. This includes all the queries
 * required for the custom ordering of this type.
 */
public class OrderByType2Operation extends Operation {

    private final int ordering;

    /**
     * Creates a new instance.
     *
     * @param connector Connector instance used to interface with the database.
     * @param ordering
     */
    public OrderByType2Operation(Connector connector, int ordering) {
        super(connector);
        this.ordering = ordering;
    }

    @Override
    public void execute() throws SQLException {

        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }

        if (this.ordering == 2) {
            String orderByCentroid = "UPDATE information_schema_fuzzy.current_orderings2 SET ordering = 2;";
            this.connector.executeRaw(orderByCentroid);
        } else if (this.ordering == 1) {
            String orderByChoquet = "UPDATE information_schema_fuzzy.current_orderings2 SET ordering = 1;";
            this.connector.executeRaw(orderByChoquet);
        }
    }
}
