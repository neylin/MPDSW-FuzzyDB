/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

/**
 *
 * @author josegregorio
 */

import fuzzy.common.translator.Translator;
import fuzzy.database.Connector;
import fuzzy.common.operations.Operation;
import fuzzy.helpers.Helper;
import fuzzy.type5.operations.AddFuzzyColumnOperation;
import fuzzy.type5.operations.RemoveFuzzyColumnsOperation;
import java.sql.SQLException;

import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.table.AddColumn;
import net.sf.jsqlparser.statement.table.AlterOperation;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.statement.table.DropColumn;


public class AlterTableTranslator extends Translator {

    AlterTableTranslator(Connector connector, List<Operation> operations) {
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
                    Integer domainId, type3domainId;
                    if ((domainId = getFuzzyDomainId(schemaName, dataType, "5")) != null) {
                        type3domainId = Helper.getType3DomainIdRelated(connector, domainId);
                        
                        operations.add(
                                new AddFuzzyColumnOperation(
                                        connector, schemaName, table.getName(),
                                        columnName, domainId, type3domainId
                                )
                        );
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

