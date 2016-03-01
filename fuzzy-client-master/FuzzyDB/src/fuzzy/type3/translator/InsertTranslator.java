/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.translator;

import fuzzy.common.translator.Translator;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Memory;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 *
 * @author hector
 */
public class InsertTranslator extends Translator {
    
    public InsertTranslator(Connector connector){
        super(connector);
    }
    
    public void translate(Insert insert) throws Exception {
        String schemaName;
        
        String tableName = insert.getTable().getName();
        List values = ( (ExpressionList) insert.getItemsList() ).getExpressions();
        List<String> columnNames;
        List columns = insert.getColumns();
        int size = values.size();
        
        try {
            schemaName = Helper.getSchemaName(connector);
        } catch (SQLException ex) {
            Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting schema name");
            return;
        }
        
        if ( columns != null ) {
            columnNames = new ArrayList<String>();
            for (Object column : columns) {
                columnNames.add( ( (Column)column).getColumnName() );
            }
        } else {
            
            HashSet<String> allColumns;
            
            try {
                allColumns = Memory.getColumns(connector, schemaName, tableName);
            } catch (SQLException ex) {
                Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting all columns");
                return;
            }
            
            columnNames = new ArrayList<String>(allColumns);
        }
                
        if ( size != columnNames.size() ) {
            Logger.debug(InsertTranslator.class.getName() + ": " + "Columns size and Values size are differents");
            throw new SQLException(InsertTranslator.class.getName() + ": " + "Columns size and Values size are differents");
        }

        int i = 0;
        Integer labelId;
        StringValue fuzzyLabel;
        String domainName;
        boolean isFuzzy;
        
        for (String column : columnNames) {

            try {
                isFuzzy = Memory.isFuzzyColumn(connector, schemaName, tableName, column);
            } catch (SQLException ex) {
                Logger.debug(InsertTranslator.class.getName() + ": " + "Error querying if column is fuzzy");
                return;
            }

            if ( isFuzzy ) {

                try {
                    domainName = Helper.getDomainNameForColumn(connector, insert.getTable(), column);
                } catch (SQLException ex) {
                    Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting domain name");
                    return;
                }

                if ( values.get(i) instanceof StringValue ) {
                    fuzzyLabel = (StringValue) values.get(i);

                    try {
                        labelId = getFuzzyLabelId(schemaName, domainName, fuzzyLabel.getValue());
                    } catch (SQLException ex) {
                        Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting label id");
                        return;
                    }

                    fuzzyLabel.setValue(labelId.toString());
                }
            }

            i++;
        }
    }
}