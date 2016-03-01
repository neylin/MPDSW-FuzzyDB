/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.common.operations;

import fuzzy.database.Connector;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 *
 * @author bishma-stornelli
 */
public abstract class Operation {

    protected Connector connector;

    public Operation(Connector connector) {
        this.connector = connector;
    }
    
    /**
     * Executes the operation
     * 
     * @throws SQLException 
     */
    public abstract void execute() throws SQLException;

    /**
     * Start a transaction in the current connection. If there's no connection
     * a new connection will be opened.
     * 
     * @return a Savepoint to rollback the transaction if needed.
     */
    protected Savepoint beginTransaction() throws SQLException {
        /*
        * The entire transaction API of Operation can be dropped.
        * It doesn't make any sense for a single Operation to be encapsulated
        * in a transaction, because what matters is for the entire set to
        * be in one.
        * Connector didn't wrap the executing of a translation in a transaction,
        * so I suppose that's why this API was created.
        * Connector now wraps the entire translation in a transaction, so
        * this isn't needed anymore.
        */
        /*this.connector.getConnection().setAutoCommit(false);
        return this.connector.getConnection().setSavepoint();*/
        return null;
    }

    /**
     * Commit the current transaction.
     */
    protected void commitTransaction() throws SQLException {
        //this.connector.getConnection().commit();
    }
    
    protected void rollback(Savepoint sp) throws SQLException {
        //this.connector.getConnection().rollback(sp);
    }
}
