package fuzzy.type3.translator;

import fuzzy.common.translator.FuzzyColumnSet;
import fuzzy.common.translator.FuzzyColumn;
import fuzzy.common.translator.TableRefList;
import fuzzy.common.translator.TableRef;
import fuzzy.common.translator.AliasGenerator;
import fuzzy.database.Connector;
import fuzzy.helpers.Memory;
import static fuzzy.common.translator.TableRef.TableType.SUB_SELECT;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * 
 */
public class ExpressionTranslator extends ExpressionColumnVisitor {

    private Connector connector;
    /**
     * Conjunto de columnas difusas
     */
    private FuzzyColumnSet fuzzyColumnSet;
    /**
     * Referencias a objetos dentro del FROM
     */
    private TableRefList tableRefSet;
    private List<SelectItem> selectItems;

    private SelectExpressionItem currentSei;
    private int baseIndex;
    private HashMap<String, LinkedList<String>> subSelectColumns;
    protected AliasGenerator aliasGenerator;

    ExpressionTranslator(Connector connector, TableRefList tableRefSet, FuzzyColumnSet fuzzyColumnSet,
            List<SelectItem> selectItems, AliasGenerator aliasGenerator) {
        this.connector = connector;
        this.fuzzyColumnSet = fuzzyColumnSet;
        this.tableRefSet = tableRefSet;
        this.selectItems = selectItems;
        this.aliasGenerator = aliasGenerator;

        this.subSelectColumns = new HashMap<String, LinkedList<String>>();
    }

    public void setBaseIndex(int baseIndex){
        this.baseIndex = baseIndex;
    }

    @Override
    public void visit(Column column) throws Exception {
        FuzzyColumn fuzzyColumn = null;
        if ((fuzzyColumn = fuzzyColumnSet.get(column)) != null) {
            keepCurrentSelectExpressionAlias();
                fuzzyColumn.includeFuzzyLabelLeftJoin(aliasGenerator);
                column.setTable(fuzzyColumn.getFuzzyLabelColumn().getTable());
                column.setColumnName(fuzzyColumn.getFuzzyLabelColumn().getColumnName());
        }
    }

    @Override
    public void visit(AllColumns ac) throws Exception {
        for (TableRef tableRef : tableRefSet.getList()) {
            addAllColumns(tableRef);
        }
    }

    @Override
    public void visit(AllTableColumns atc) throws Exception {
        TableRef tableRef = tableRefSet.get(atc.getTable());
        if (null == tableRef) {
            throw new SQLException("Unknown table '" + atc.getTable().getName() + "'",
                    "42S02", 1051);
        }
        addAllColumns(tableRef);
    }

    private void addAllColumns(TableRef tableRef) throws Exception {
        int j = 0;
        if (tableRef.getTableType() == SUB_SELECT) {
            SubSelect subSelect = tableRef.getSubSelect();
            LinkedList<String> columnNames = subSelectColumns.get(subSelect.getAlias());
            for (String columnName : columnNames) {
                ++ j;
                SelectExpressionItem sei = new SelectExpressionItem();
                this.selectItems.add(this.baseIndex + j, sei);
                Column column = new Column(new Table(null, subSelect.getAlias()), columnName);
                sei.setExpression(column);
            }
        } else {
            HashSet<String> columns = Memory.getColumns(connector, tableRef.getTable());
            for (String columnName : columns) {
                ++ j;
                SelectExpressionItem sei = new SelectExpressionItem();
                this.selectItems.add(this.baseIndex + j, sei);
                String alias = tableRef.getTable().getAlias();
                Table table = null == alias ? tableRef.getTable() :
                                              new Table(null, alias);
                Column column = new Column(table, columnName);
                FuzzyColumn fuzzyColumn = fuzzyColumnSet.get(column);
                if (null != fuzzyColumn) {
                    fuzzyColumn.includeFuzzyLabelLeftJoin(aliasGenerator);
                    sei.setExpression(fuzzyColumn.getFuzzyLabelColumn());
                    sei.setAlias(fuzzyColumn.getPublicName());
                } else {
                    sei.setExpression(column);
                }
            }
        }
    }

    @Override
    public void visit(SelectExpressionItem sei) throws Exception {
        // keep SelectItem outter name in case there's no alias
        if (null == sei.getAlias()) {
            currentSei = sei;
        }
        sei.getExpression().accept(this);
        currentSei = null;
    }

    @Override
    public void visit(SubSelect subSelect) throws Exception{
        SelectTranslator sT = new SelectTranslator(connector, aliasGenerator);
        subSelect.getSelectBody().accept(sT);
        subSelectColumns.put(subSelect.getAlias(), sT.getSelectItems());
    }

    /**
     * Only called when indeed a fuzzy column is found in the expression
     */
    private void keepCurrentSelectExpressionAlias(){
        if (null != currentSei) {
            if (null == currentSei.getAlias()) {
                if (currentSei.getExpression() instanceof Column) {
                    currentSei.setAlias(
                            ((Column)currentSei.getExpression()).getColumnName());
                } else {
                    currentSei.setAlias(currentSei.getAlias());
                }
            }
            // once alias is updated, there's no need to keep the expression anymore
            currentSei = null;
        }
    }
}