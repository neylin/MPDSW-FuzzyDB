package fuzzy.type2.translator;

import fuzzy.database.Connector;
import java.util.Iterator;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import fuzzy.common.translator.FuzzyColumnSet;
import fuzzy.common.translator.TableRefList;
import fuzzy.helpers.Printer;
import fuzzy.type2.operations.OrderByType2Operation;

public class SelectType2Translator implements SelectVisitor {

    protected Connector connector;
    private boolean mainselect;
    private Expression leftExpression;
    private Expression rightExpression;

    public SelectType2Translator(Connector connector) {
        this.connector = connector;
        this.mainselect = false;
    }

    public SelectType2Translator(Connector connector, boolean mainselect) {
        this.connector = connector;
        this.mainselect = mainselect;
    }

    @Override
    public void visit(PlainSelect plainSelect) throws Exception {
        TableRefList tableRefSet = new TableRefList(connector, plainSelect);
        FuzzyColumnSet fuzzyColumnSet = new FuzzyColumnSet(connector, tableRefSet, plainSelect, 2);
        FuzzyType2ExpTranslator translator = new FuzzyType2ExpTranslator(this.connector, this.mainselect, fuzzyColumnSet, tableRefSet);

        for (SelectItem item : (List<SelectItem>) plainSelect.getSelectItems()) {
            item.accept(translator);
        }

        translator.setMainselect(false);
        Expression where = plainSelect.getWhere();
        if (null != where) {
            where.accept(translator);
        }

        OrderByType2Operation orderBType2Operation = new OrderByType2Operation(this.connector, plainSelect.getOrdering());
        orderBType2Operation.execute();
    }

    @Override
    public void visit(Union union) throws Exception {
        for (Iterator iter = union.getPlainSelects().iterator(); iter.hasNext();) {
            PlainSelect plainSelect = (PlainSelect) iter.next();
            plainSelect.accept(this);
        }
    }
}
