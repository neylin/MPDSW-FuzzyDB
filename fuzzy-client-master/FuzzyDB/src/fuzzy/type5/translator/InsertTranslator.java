/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Error;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import fuzzy.helpers.Memory;
import fuzzy.common.translator.Translator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.sf.jsqlparser.expression.ArrayExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 *
 * @author josegregorio
 */
public class InsertTranslator extends Translator {
    
    public InsertTranslator(Connector connector, List<Operation> operations){
        super(connector, operations);
    }
    
    
    public class sfuzzy implements Comparable {
        
        public Expression e1, e2;
        
        sfuzzy(Expression ee1, Expression ee2){
            e1 = ee1;
            e2 = ee2;
        }
        
        @Override
        public int compareTo(Object fruit) {
            sfuzzy f2 = (sfuzzy)fruit;
            String s1 = e2.toString();
            String s2 = f2.e2.toString();
            return s1.compareTo(s2);
        }
        
    }
    /*
     * sort the elements of FuzzyByExtention for making easy the comparations
     */
    private void sort(ArrayExpression e1, ArrayExpression e2){
        List l1 = e1.getExpressions().getExpressions();
        List l2 = e2.getExpressions().getExpressions();
        ArrayList<sfuzzy> ve = new ArrayList<sfuzzy>();
        for(int i = 0; i < l1.size(); i++){
            sfuzzy f1 = new sfuzzy((Expression)l1.get(i), (Expression)l2.get(i));
            ve.add(f1);
        }
        
        Collections.sort(ve);
        l1.clear();
        l2.clear();
        for(int i = 0; i < ve.size(); i++){
            if(ve.get(i).e1.toString().equals("0.0")){
                
                continue;
            }
            
            l1.add(ve.get(i).e1);
            l2.add(ve.get(i).e2);
        }
        
    }
    
    
    public void translate(Insert insert) throws SQLException {
        String schemaName;
        
        String tableName = insert.getTable().getName();
        List values = ( (ExpressionList) insert.getItemsList() ).getExpressions();
        List<String> columnNames;
        List columns = insert.getColumns();
        int size = values.size();
        
        try {
            schemaName = Helper.getSchemaName(connector);
        } catch (SQLException ex) {
            Logger.debug(InsertTranslator.class.getName() + ": " + Error.getError("getSchemaT5"));
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
                Logger.debug(InsertTranslator.class.getName() + ": " + Error.getError("getAllCols"));
                return;
            }
            
            columnNames = new ArrayList<String>(allColumns);
        }
                
        if ( size != columnNames.size() ) {
            Logger.debug(InsertTranslator.class.getName() + ": " + Error.getError("colValSiz"));
            throw new SQLException(InsertTranslator.class.getName() + ": " + Error.getError("colValSiz"));
        }

        int i = 0;
        RowExpression fuzzyExt;
        boolean isFuzzy;
        
        for (String column : columnNames) {

            try {
                isFuzzy = Memory.isFuzzyType5Column(connector, schemaName, tableName, column);
            } catch (SQLException ex) {
                Logger.debug(InsertTranslator.class.getName() + ": " + Error.getError("fuzzyColQuery"));
                return;
            }

            if ( isFuzzy ) {

                // RowExpression beacause it was translated by Type2ExpTranslator
                // The only change needed is to delete the last boolean in the row
                if ( values.get(i) instanceof RowExpression ) {
                    fuzzyExt = (RowExpression) values.get(i);
                    List<Expression> le = 
                        fuzzyExt.getExpressions().getExpressions();
                    
                    le.remove(le.size()-1); // Remove the last boolean
                    ArrayExpression a1, a2;
                    a1 = (ArrayExpression)le.get(0);
                    a2 = (ArrayExpression)le.get(1);
                    sort(a1, a2);
                    le.set(0, a1);
                    le.set(1, a2);
                }
            }

            i++;
        }
    }
}
