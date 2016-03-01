package fuzzy.type3.translator;

import fuzzy.common.translator.Translator;
import fuzzy.database.Connector;
import fuzzy.type3.operations.ChangeColumnOperation;
import fuzzy.common.operations.Operation;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import fuzzy.type3.operations.AddFuzzyColumnOperation;
import fuzzy.type3.operations.CreateConstraintsForNewColumnOperation;
import fuzzy.type3.operations.RemoveFuzzyColumnsOperation;
import java.sql.SQLException;

import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.table.AddColumn;
import net.sf.jsqlparser.statement.table.AlterOperation;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.statement.table.ChangeColumn;
import net.sf.jsqlparser.statement.table.ColumnDefinition;
import net.sf.jsqlparser.statement.table.DropColumn;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.create.table.CreateTable}
 */
public class AlterTableTranslator extends Translator {

    AlterTableTranslator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    public void translate(AlterTable alterTable)
        throws SQLException {
        Table table = alterTable.getTable();
        ChangeColumnOperation cco = new ChangeColumnOperation(connector);
        for (AlterOperation alterOperation : alterTable.getAlterOperations()) {
            switch (alterOperation.getType()) {
                case CHANGE:
                    ChangeColumn changeColumn = (ChangeColumn)alterOperation;
                    ColumnDefinition columnDefinition = changeColumn.getColumnDefinition();
                    cco.setDataType(columnDefinition.getColDataType().getDataType());
                    cco.setNewColumnName(columnDefinition.getColumnName());
                    cco.setOldColumnName(changeColumn.getColumnOld());
                    cco.setSchemaName(alterTable.getTable().getSchemaName());
                    cco.setTableName(table.getName());
                    String options = "";
                    if (columnDefinition.getColumnSpecStrings() != null){
                        for (Object o : columnDefinition.getColumnSpecStrings()) {
                            options += ((String)o) + " ";
                        }
                    }
                    cco.setOptions(options);
                    operations.add(cco);
                    break;
                case ADD:
                {
                    AddColumn addColumn = (AddColumn)alterOperation;
                    String schemaName = Helper.getSchemaName(this.connector, table);
                    String dataType = addColumn.getColumnDefinition().getColDataType().getDataType();
                    String columnName = addColumn.getColumnDefinition().getColumnName();
                    Integer domainId;
                    if ((domainId = getFuzzyDomainId(schemaName, dataType, "3")) != null) {
                        operations.add(
                                new AddFuzzyColumnOperation(
                                        connector, schemaName, table.getName(),
                                        columnName, domainId
                                )
                        );
                        
                        operations.add(
                                new CreateConstraintsForNewColumnOperation(
                                        connector, schemaName, table.getName(), columnName
                                )
                        );
                        
                        addColumn.getColumnDefinition().getColDataType().setDataType("INTEGER");
                    }
                    break;
                }
                case DROP:
                {
                    DropColumn dropColumn = (DropColumn)alterOperation;
                    String schemaName = Helper.getSchemaName(this.connector, table);
                    String columnName = dropColumn.getColumnOld();
                    operations.add(new RemoveFuzzyColumnsOperation(connector, schemaName, table.getName(), columnName));
                    break;
                }
            }
        }
    }
}
