/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.common.translator.FuzzyColumn;
import fuzzy.database.Connector;
import java.util.Iterator;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import fuzzy.common.translator.FuzzyColumnSet;
import fuzzy.common.translator.TableRefList;
import fuzzy.common.translator.Translator;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;

import fuzzy.helpers.Error;

public class SelectTranslator implements SelectVisitor {

    protected Connector connector;
    private boolean mainselect;


    public SelectTranslator(Connector connector) {
        this.connector = connector;
        this.mainselect = false;
    }

    public SelectTranslator(Connector connector, boolean mainselect) {
        this.connector = connector;
        this.mainselect = mainselect;
    }
    
    public void translateob(OrderByElement elem, FuzzyColumnSet fuzzyColumnSet) throws SQLException{
        
        Expression expression = elem.getExpression();
        
        if (!(expression instanceof Column)) return; // Was type3
        
        Column column = (Column) expression;

        if(fuzzyColumnSet == null) return; // not fuzzy set
        
        FuzzyColumn fc = fuzzyColumnSet.get(column);
        
        Expression s = elem.getFuzzyStart();
        
        if(fc == null){ // not fuzzy column
            if(s != null){// not fuzzy column and starting from
                throw Translator.FR_NO_FUZZY_COLUMN; // not fu
            } 
            return;
        } 
        
        String domain = Helper.getDomainNameForColumn5(this.connector, fc.getTableRef().getTable(), elem.toString());
        
        if(domain == null) return; // Domain not type 5
        
        if(s == null) throw new SQLException(Error.getError("notStartingFrom"));
        if(!Helper.isLabelOfDomain5(this.connector,domain,s.toString())) 
            throw new SQLException(Error.getError("notLabelOfDomain"));
        Logger.debug("tab: "+fc.getPublicName() + " elem:" + elem + " Domain:"+ domain+ " starting:"+ s);
        
        Function f = new Function();
        f.setName("__"+domain+"_f");
        List<Expression> args = new ArrayList<Expression>();
        args.add(column);
        args.add(s);
        f.setParameters(new ExpressionList(args));
        
        elem.setExpression(f);
        elem.setAsc(false);
    }
    
    
    @Override
    public void visit(PlainSelect plainSelect) throws Exception {
        TableRefList tableRefSet = new TableRefList(connector, plainSelect);
        FuzzyColumnSet fuzzyColumnSet = new FuzzyColumnSet(connector, tableRefSet, plainSelect, 5);

        FuzzyExpTranslator translator = new FuzzyExpTranslator(this.connector, this.mainselect, fuzzyColumnSet);

        for (SelectItem item : (List<SelectItem>) plainSelect.getSelectItems()) {
            item.accept(translator);
        }
        
        
        List<OrderByElement> orderByElements = (List<OrderByElement>) plainSelect.getOrderByElements();
        if(orderByElements != null){
            for (OrderByElement elem : orderByElements) {
                translateob(elem, fuzzyColumnSet);
                
                

            }
        }
        
        
        
        translator.setMainselect(false);
        Expression where = plainSelect.getWhere();
        if (null != where) {
            where.accept(translator);
        }
    }

    @Override
    public void visit(Union union) throws Exception {
        for (Iterator iter = union.getPlainSelects().iterator(); iter.hasNext();) {
            PlainSelect plainSelect = (PlainSelect) iter.next();
            plainSelect.accept(this);
        }
    }

}
