/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzy.common.operations;

import fuzzy.database.Connector;
import java.sql.SQLException;

/**
 *
 * @author gmljosea
 */
public class RawSQLOperation extends Operation {
    
    private String sql;    
    
    public RawSQLOperation(Connector connector, String sql) {
        super(connector);
        this.sql = sql;
    }
    
    @Override
    public void execute() throws SQLException {
        this.connector.executeRaw(sql);
    }
}
