package fuzzy.type3.operations;

import fuzzy.common.operations.Operation;
import fuzzy.common.translator.Translator;
import fuzzy.database.Connector;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.jsqlparser.statement.select.Select;

/**
 *
 * @author Hector E. Dominguez B.
 */
public class CreateFuzzyDomainFromSelectOperation extends Operation {
    String domainName;
    Select selectStatement;

    public CreateFuzzyDomainFromSelectOperation(Connector connector, 
            String domainName, Select selectStatement) {
        
        super(connector);
        
        this.domainName      = domainName;
        this.selectStatement = selectStatement;
    }

    @Override
    public void execute() throws SQLException {
        
        if ( this.connector.getSchema().equals("") ) {
            throw new SQLException(fuzzy.helpers.Error.getError("getSchemaT5"));
        }
        
        ResultSet resultSelect = connector.executeRawQuery(selectStatement.toString());
        
        // Se verifica que el resultado sea solo una columna
        if ( resultSelect.getMetaData().getColumnCount() != 1 ) {
            throw Translator.FR_EMPTY_SELECT_RESULT(selectStatement.toString());
        }
        
        String queryCount = "SELECT COUNT(*) FROM (" + 
                selectStatement.toString() + 
                ") AS labels WHERE labels IS NOT NULL";
        
        ResultSet count = connector.executeRawQuery(queryCount);
        
        // Se verifica que existan al menos un elemento distinto de NULL
        if ( count.next() && count.getInt(1) == 0 ) {
            throw Translator.FR_EMPTY_SELECT_RESULT(selectStatement.toString());
        }
        
        String insertDomain = "INSERT INTO information_schema_fuzzy.domains "
                + "VALUES (DEFAULT,'" + connector.getSchema() + "', '" + domainName + "', 3, NULL)";
        
        Integer domainId = null;
        
        try {
            domainId = connector.executeRawInsert(insertDomain);
        } catch (SQLException ex) {
            throw Translator.FR_DUPLICATE_DOMAIN_NAME(domainName);
        }
        
        String columnName = resultSelect.getMetaData().getColumnName(1);
        String values = "";
        while ( resultSelect.next() ) {
            values += " (" + domainId + ",'" + resultSelect.getString(columnName) + "')";
            
            if ( !resultSelect.isLast() ) {
                values += ",";
            }
        }
        
        String insertLabels = "INSERT INTO information_schema_fuzzy.labels " +
                "(domain_id, label_name) VALUES " + values;
        
        String insertSimilarities = "INSERT INTO information_schema_fuzzy.similarities "
                + "SELECT label_id, label_id, 1.0, TRUE "
                + "FROM information_schema_fuzzy.labels "
                + "WHERE domain_id = " + domainId;
        
        try {
            connector.executeRawUpdate(insertLabels);
        } catch (SQLException ex) {
            throw Translator.FR_DUPLICATE_LABEL_ALT2();
        }
        
        connector.executeRawUpdate(insertSimilarities);
    }
    
}
