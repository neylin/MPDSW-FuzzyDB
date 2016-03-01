
package fuzzy.common.translator;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import java.sql.SQLException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Stores a reference to a Table and to it's parent
 */
public class TableRef {

    /**
     * Parent node (AST) type of a Table or SubSelect in an AST.
     */
    public enum ParentType {PLAIN_SELECT, SUB_JOIN, JOIN}

    /**
     * Table node (AST) type.
     */
    public enum TableType {SUB_SELECT, TABLE}

    protected Connector connector;
    protected Object parent;//PlainSelect, SubJoin, Join
    protected ParentType parentType;
    protected FromItem table; //Table, SubSelect
    protected TableType tableType;

    public TableRef(Connector connector, Object parent, FromItem table) throws Exception {
        this.connector = connector;
        this.table = table;

        if (table instanceof Table) {
            tableType = TableType.TABLE;
        } else if (table instanceof SubSelect) {
            tableType = TableType.SUB_SELECT;
        } else {
            throw new Exception("Invalid table class specified");
        }
        setParent(parent);
    }

    /**
     * Retrieves the qualification identifier for columns which want to refer
     * to this table.
     * 
     * @return retrieves the unique id
     */
    public String getId() throws SQLException {
        String id = table.getAlias();
        if (TableType.TABLE == tableType && null == id) {
            String schemaName = Helper.getSchemaName(connector, (Table)table);
            id = (!schemaName.isEmpty() ? schemaName + "." : "") + ((Table)table).getName();
            id = id.toLowerCase(); // to handle case insensitive
        }
        return id;
    }
    
    public Table getTable(){
        return (Table)this.table;
    }
    
    public SubSelect getSubSelect(){
        return (SubSelect)this.table;
    }
    
    public TableType getTableType(){
        return this.tableType;
    }

    public PlainSelect getPlainSelectParent() {
        return (PlainSelect)this.parent;
    }

    public SubJoin getSubJoinParent() {
        return (SubJoin)this.parent;
    }
    
    Object getParent() {
        return this.parent;
    }

    public final void setParent(Object parent) throws Exception {

        this.parent = parent;

        if (parent instanceof PlainSelect) {
            parentType = ParentType.PLAIN_SELECT;
        } else if (parent instanceof SubJoin) {
            parentType = ParentType.SUB_JOIN;
        } else if (parent instanceof Join) {
            parentType = ParentType.JOIN;
        } else {
            throw new Exception("Invalid parent class specified");
        }
    }

    public Join getJoinParent() {
        return (Join)this.parent;
    }

    public ParentType getParentType(){
        return this.parentType;
    }
}
