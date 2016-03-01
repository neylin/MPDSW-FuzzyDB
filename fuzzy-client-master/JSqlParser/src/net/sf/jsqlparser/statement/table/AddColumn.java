package net.sf.jsqlparser.statement.table;


public class AddColumn extends AlterOperation {
    
    private ColumnDefinition columnDefinition;

    public AddColumn(ColumnDefinition colDef) {
        super(TYPE.ADD);
        this.columnDefinition = colDef;
    }

    public ColumnDefinition getColumnDefinition() {
        return columnDefinition;
    }
}
