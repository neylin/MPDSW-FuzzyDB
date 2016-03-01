/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.common.translator;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import static fuzzy.common.translator.TableRef.TableType.TABLE;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 *
 * @author timauryc
 */
public class TableRefList {

    protected Connector connector;
    protected List<TableRef> dict;
    protected HashSet<String> aliases;

    public TableRefList(Connector connector, PlainSelect plainSelect) throws SQLException, Exception {
        this.connector = connector;
        dict = new ArrayList();

        populateList(plainSelect, plainSelect.getFromItem());
        populateList(plainSelect, plainSelect.getJoins());
    }


    /**
     * Fulfils the {@link dict} reading the tables used in the FROM clause of
     * a select statement.
     * 
     * @param parent parent node where the element was found. Useful when a 
     *               Table is found. This is the element to be modified if the
     *               Table is translated to a SubJoin
     * @param element element to 
     */
    private void populateList(Object parent, Object element) throws SQLException, Exception {
        // base case. table found
        if (element instanceof Table) {
            Logger.debug("Inspecting a Table or SubSelect element");
            // SubSelect or Table are both FromItem
            FromItem table = (FromItem) element;

            // register reference found
            TableRef tableRef = new TableRef(connector, parent, table);
            if (
                    (element instanceof Table && this.has(tableRef.getTable()))
 
            ) {
                throw new SQLException("Not unique table/alias: '" + tableRef.getId() + "'",
                        "42000", 1066);
            }
            
            Logger.debug("Adding " + tableRef.getId() + " to TableRefList");
            dict.add(tableRef);

        } else if (element instanceof SubJoin) {
            // SubJoin case
            populateList(element, ((SubJoin) element).getLeft());
            populateList(element, ((SubJoin) element).getJoin());

        } else if (element instanceof Join) {
            // Join case
            populateList(element, ((Join) element).getRightItem());

        } else if (element instanceof List) {
            for (Join join : (List<Join>) element) {
                populateList(element, join);
            }
        } else if (element instanceof SubSelect) {
            throw new UnsupportedOperationException("Using SubSelects in FROM clause is not supported yet. ");
        }
    }

    public List<TableRef> getList() {
        return this.dict;
    }

    public TableRef get(Table table) throws SQLException {
        Logger.debug("Retrieving " + table.toString() + " from TableRefList");
        String id = table.getAlias();
        if (null == id) {
            String schemaName = Helper.getSchemaName(connector, (Table) table);
            id = (!schemaName.isEmpty() ? schemaName + "." : "") + ((Table) table).getName();
        }
        Logger.debug("ID is " + id);
        for (TableRef tableRef : dict) {
            Logger.debug("Comparing againts " + tableRef.getId());
            if (tableRef.getId().equals(id)) {
                return tableRef;
            }
        }
        Logger.debug("TableRef not found by ID. Looking for simple name (" + table.getName() + ")");
        TableRef foundTable = null;
        if (table.getAlias() == null) {
            for (TableRef tableRef : dict) {
                if (tableRef.getTableType() == TABLE) {
                    Table t = tableRef.getTable();
                    Logger.debug("Comparing againts " + t.getName());
                    if (
                            (t.getAlias() != null // SELECT alias.column FROM table AS alias 
                                && t.getAlias().equals(table.getName()) // table.name = alias, t.alias = alias
                            ) || // SELECT table.column FROM table
                            t.getName().equals(((Table) table).getName()) // table.name = table , t.name = table
                            ) {
                        if (null != foundTable) {
                            throw new SQLException("Unknown column in field list is ambiguous",
                                    "23000", 1052);
                        }
                        foundTable = tableRef;
                    }
                }
            }
        }
        Logger.debug("Returning " + foundTable);
        return foundTable;
    }

    public boolean has(Table table) throws SQLException {
        return get(table) != null;
    }

    /**
     * Outputs to stdout a human readable information about the internal data
     * structures content.
     */
    public void debugDump() throws SQLException {
        System.out.println("Is empty TableRefSet: " + dict.isEmpty());
        for (TableRef tableRef : this.getList()) {
            String val = TableRef.TableType.TABLE == tableRef.getTableType()
                    ? tableRef.getTable().getName()
                    : tableRef.getSubSelect().toString();
            System.out.println(tableRef.getId() + " => " + val);
        }
    }
}
