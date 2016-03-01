package net.sf.jsqlparser.statement.table;


public class DropColumn extends AlterOperation {
    
    private String columnOld;

    public DropColumn(String columnOld) {
        super(TYPE.DROP);
        this.columnOld = columnOld;
    }

    public String getColumnOld() {
        return columnOld;
    }

}
