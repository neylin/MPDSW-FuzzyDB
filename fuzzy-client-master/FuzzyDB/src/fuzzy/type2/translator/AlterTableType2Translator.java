package fuzzy.type2.translator;

import fuzzy.common.translator.Translator;
import fuzzy.type3.translator.*;
import fuzzy.database.Connector;
import fuzzy.common.operations.Operation;
import fuzzy.helpers.Helper;
import fuzzy.type2.operations.AddFuzzyType2ColumnOperation;
import fuzzy.type2.operations.RemoveFuzzyType2ColumnsOperation;
import java.sql.SQLException;

import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.table.AddColumn;
import net.sf.jsqlparser.statement.table.AlterOperation;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.statement.table.DropColumn;


public class AlterTableType2Translator extends Translator {

    AlterTableType2Translator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    public void translate(AlterTable alterTable)
        throws SQLException {
        Table table = alterTable.getTable();
        for (AlterOperation alterOperation : alterTable.getAlterOperations()) {
            switch (alterOperation.getType()) {
                case ADD:
                {
                    AddColumn addColumn = (AddColumn)alterOperation;
                    String schemaName = Helper.getSchemaName(this.connector, table);
                    String dataType = addColumn.getColumnDefinition().getColDataType().getDataType();
                    String columnName = addColumn.getColumnDefinition().getColumnName();
                    Integer domainId = null;
                    if ((domainId = getFuzzyType2DomainId(schemaName, dataType)) != null) {
                        operations.add(new AddFuzzyType2ColumnOperation(connector, schemaName, table.getName(), columnName, domainId));
                    }
                    break;
                }
                case DROP:
                {
                    DropColumn dropColumn = (DropColumn)alterOperation;
                    String schemaName = Helper.getSchemaName(this.connector, table);
                    String columnName = dropColumn.getColumnOld();
                    operations.add(new RemoveFuzzyType2ColumnsOperation(connector, schemaName, table.getName(), columnName));
                    break;
                }
            }
        }
    }
}
