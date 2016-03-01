/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Logger;
import fuzzy.type3.translator.Translator;
import java.sql.SQLException;

/**
 *
 * @author bishma-stornelli
 */
public class DropFuzzyDomainOperation extends Operation {

    private final String domain;

    public DropFuzzyDomainOperation(Connector connector, String domain) {
        super(connector);
        this.domain = domain;
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getSchema();

        Logger.debug("Starting DROP FUZZY DOMAIN " + domain + " operation");
        String sql = "DELETE FROM information_schema_fuzzy.domains " +
            "WHERE table_schema = (select current_schema())" + 
            "AND domain_name = '" + domain + "'";
        
        int rows = connector.executeRawUpdate(sql);
        /*if (rows == 0) {
            String c = connector.getSchema();
            if (c == null || c.isEmpty()) {
                Logger.debug("No database selected");
                throw Translator.ER_NO_DB_ERROR;
            }
            Logger.debug("Unknown domain '" + domain + "'");
            throw Translator.FR_UNKNOWN_DOMAIN(domain);
        }*/
    }
    
}
