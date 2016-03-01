package fuzzy.type2.operations;

import java.sql.SQLException;
/* fuzzy imports*/
import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;

/**
 * Drops a Type-2 Fuzzy type. This includes all the queries and operations
 * required for the custom ordering of this type.
 */
public class DropFuzzyType2DomainOperation extends Operation {

    private final String domain;

    /**
     * Creates a new instance.
     *
     * @param connector Connector instance used to interface with the database.
     * @param domain Domain that has to be dropped.
     */
    public DropFuzzyType2DomainOperation(Connector connector, String domain) {
        super(connector);
        this.domain = domain;
    }

    /**
     * Drops the necessary operators for fuzzy Type II orderings.
     *
     * @param schemaName the schema name which is related to.
     * @param typeName the type name of the domain
     * @throws java.sql.SQLException
     */
    public void dropOperatorCatalog(String schemaName, String typeName) throws SQLException {

        String funcNameFormat = schemaName + ".__" + domain + "_%s";

        /*
         * DROP OPERATOR CLASS IF EXISTS public.__<class> USING btree
         */
        String opClassName = String.format(funcNameFormat, "class");
        String dropOpClass = "DROP OPERATOR CLASS IF EXISTS " + opClassName + " USING btree";

        /*
         * DROP OPERATOR IF EXISTS <op> (public.<domain>,public.<domain>)
         */
        String dropOpFormat = "DROP OPERATOR IF EXISTS %s (" + typeName + "," + typeName + ")";
        String dropLowerOp = String.format(dropOpFormat, "<");
        String dropLowerEqOp = String.format(dropOpFormat, "<=");
        String dropEqOp = String.format(dropOpFormat, "=");
        String dropGreaterEqOp = String.format(dropOpFormat, ">=");
        String dropGreaterOp = String.format(dropOpFormat, ">");

        /*
         *  DROP FUNCTION IF EXISTS public.__<function>(public.<domain>, public.<domain>)
         */
        String dropFuncFormat = "DROP FUNCTION IF EXISTS " + funcNameFormat + "(" + typeName + ", " + typeName + ")";
        String dropLowerFunc = String.format(dropFuncFormat, "_lower");
        String dropLowerEqFunc = String.format(dropFuncFormat, "_lower_eq");
        String dropEqFunc = String.format(dropFuncFormat, "_eq");
        String dropGreaterEqFunc = String.format(dropFuncFormat, "_greater_eq");
        String dropGreaterFunc = String.format(dropFuncFormat, "_greater");
        String dropCmpFunc = String.format(dropFuncFormat, "_cmp");

        /* Drop operator class */
        connector.executeRaw(dropOpClass);
        connector.executeRaw(dropCmpFunc);
        /* Drop operators */
        connector.executeRaw(dropLowerOp);
        connector.executeRaw(dropLowerEqOp);
        connector.executeRaw(dropEqOp);
        connector.executeRaw(dropGreaterEqOp);
        connector.executeRaw(dropGreaterOp);
        /* Drop ordering functions */
        connector.executeRaw(dropLowerFunc);
        connector.executeRaw(dropLowerEqFunc);
        connector.executeRaw(dropEqFunc);
        connector.executeRaw(dropGreaterEqFunc);
        connector.executeRaw(dropGreaterFunc);
    }

    public void deleteConstants(String schemaName) throws SQLException {
        String deleteConstant = "DELETE FROM information_schema_fuzzy.constants2 "
                + "WHERE constant_schema = '" + schemaName + "' "
                + "AND domain_name = '" + domain + "';";
        connector.executeRaw(deleteConstant);
    }
    
    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getSchema();
        String fullTypeName = schemaName + "." + domain;

        String updateCatalog = "DELETE FROM information_schema_fuzzy.domains2 "
                + "WHERE table_schema = (select current_schema())"
                + "AND domain_name = '" + domain + "'";

        connector.executeRawUpdate(updateCatalog);

        deleteConstants(schemaName);
        dropOperatorCatalog(schemaName, fullTypeName);
    }
}