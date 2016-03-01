/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.common.translator;

import fuzzy.helpers.Logger;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubJoin;

/**
 *
 * @author Sergio
 */
public class FuzzyColumn {

    /**
     * When all fuzzy columns are identified, this reference allows the
     * translator to find the source table in the FROM clause.
     * With it, adds the JOIN to information_schema_fuzzy.labels
     */
    protected TableRef tableRef;

    /**
     * The name used publicly to reference to this column. The physical name
     * of the column
     */
    protected String publicName;

    /**
     * A reference to the name of the real internal column containing the value.
     * Typically L1.label_name (another table containing the real value)
     */
    protected Column fuzzyLabelColumn;
    
    /**
     * If this fuzzyColumn appears in a fuzzy GROUP BY, groupBySimilarityColumn
     * is the alias used in the join againts similarities needed by the GROUP BY
     * and "value" (e.g. S1.value)
     */
    protected Column groupBySimilarityColumn;
    
    /**
     * TODO document this
     */
    protected Column fuzzyLabelIdColumn;
   
    public FuzzyColumn(TableRef tableRef, String publicName) {
        this.tableRef = tableRef;
        this.publicName = publicName;
    }
    
    public TableRef getTableRef(){
        
        return this.tableRef;
    }

    public String getPublicName() {
        return publicName;
    }
    
    public void includeGroupByJoin(AliasGenerator aliasGenerator) throws Exception {
        if (fuzzyLabelColumn != null) {
            return;
        }
        String labelsAlias = aliasGenerator.getNewLabelAlias();
        String similarityAlias = aliasGenerator.getNewSimilarityAlias();
        
        String sql = "SELECT ignore FROM ignore"
                + " JOIN information_schema_fuzzy.labels AS " + labelsAlias
                + " JOIN information_schema_fuzzy.similarities AS " + similarityAlias
                + " ON ((" + similarityAlias + ".label1_id = " + labelsAlias + ".label_id"
                + " AND " + similarityAlias + ".label2_id = " + this.getPublicName()
                + ") AND " + similarityAlias + ".value <> 0)";
        
        Logger.debug("Parsing:\n" + sql);
        // It's easier to parse what I want to replace than building it
        
        CCJSqlParserManager pa = new CCJSqlParserManager();
        net.sf.jsqlparser.statement.select.Select s = null;
        try {
            s = (Select) pa.parse(new StringReader(sql));
        } catch (JSQLParserException ex) {
            Logger.severe("ERROR ERROR ERROR ERROR");
            throw new SQLException("Error en consulta formada para traducir ORDER BY");
        }
        
//        Table parentOfColumn = tableRef.getTable();
        
        PlainSelect plainSelect = (PlainSelect) s.getSelectBody();
        Join labelJoin = (Join) plainSelect.getJoins().get(0);
        Join similarityJoin = (Join) plainSelect.getJoins().get(1);
        
        switch (tableRef.getParentType()) {
            case PLAIN_SELECT:
                // FROM something
                // FROM something JOIN labels JOIN similarities
                if (tableRef.getPlainSelectParent().getJoins() == null) {
                    tableRef.getPlainSelectParent().setJoins(new ArrayList());
                }
                tableRef.getPlainSelectParent().getJoins().add(labelJoin);
                tableRef.getPlainSelectParent().getJoins().add(similarityJoin);
                break;
            case SUB_JOIN:
                // (a JOIN b)
                // (((a JOIN b) JOIN labels) JOIN similarities)
                //        3      2             1
                SubJoin subJoin1 = tableRef.getSubJoinParent();
                SubJoin subJoin2 = new SubJoin();
                SubJoin subJoin3 = new SubJoin();
                
                subJoin3.setLeft(subJoin1.getLeft());
                subJoin3.setJoin(subJoin1.getJoin());
                
                subJoin2.setLeft(subJoin3);
                subJoin2.setJoin(labelJoin);
                
                subJoin1.setLeft(subJoin2);
                subJoin1.setJoin(similarityJoin);
                break;
            case JOIN:
                // JOIN something
                // JOIN ((something JOIN labels) JOIN similarities)
                // parent        grandChild     child
                Join parent = tableRef.getJoinParent();
                SubJoin child = new SubJoin();
                SubJoin grandChild = new SubJoin();
                child.setLeft(grandChild);
                child.setJoin(similarityJoin);
                grandChild.setLeft(parent.getRightItem());
                grandChild.setJoin(labelJoin);
                parent.setRightItem(child);
                break;
        }
        
//        SubJoin subJoin = new SubJoin();
//        subJoin.setLeft(parentOfColumn);
//        subJoin.setJoin(similarityJoin);
//        
//        updateTableRefParent(subJoin);
//        
//        tableRef.setParent(subJoin);
//        
//        subJoin = new SubJoin();
//        subJoin.setLeft(parentOfColumn);
//        subJoin.setJoin(labelJoin);
//        
//        updateTableRefParent(subJoin);
//        
//        tableRef.setParent(subJoin);
        
        this.fuzzyLabelColumn = new Column(new Table(null, labelsAlias), "label_name");
        this.fuzzyLabelIdColumn = new Column(new Table(null, labelsAlias), "label_id");
        this.groupBySimilarityColumn = new Column(new Table(null, similarityAlias), "value");
    }
    
    public void includeFuzzyLabelLeftJoin(AliasGenerator aliasGenerator) throws Exception {
        if (fuzzyLabelColumn != null) {
            return;
        }
        // first translation
        SubJoin subJoin = new SubJoin();
        // set the table to the left of this new SubJoin
        subJoin.setLeft(tableRef.getTable());
        // the table is overwritten by this SubJoin
        switch (tableRef.getParentType()){
            case PLAIN_SELECT:
                tableRef.getPlainSelectParent().setFromItem(subJoin);
                break;
            case SUB_JOIN:
                tableRef.getSubJoinParent().setLeft(subJoin);
                break;
            case JOIN:
                tableRef.getJoinParent().setRightItem(subJoin);
                break;
        }
        // create new LEFT JOIN for FUZZY_LABEL
        Join join = new Join();
        subJoin.setJoin(join);
        join.setLeft(true);
        // create table FUZZY_LABEL and register it under tableRefSet
        Table fuzzyLabel = new Table("information_schema_fuzzy", "labels");
        fuzzyLabel.setAlias(aliasGenerator.getNewLabelAlias());
        join.setRightItem(fuzzyLabel);
        // create ON condition
        EqualsTo equalsTo = new EqualsTo();
        String alias = tableRef.getTable().getAlias();
        Table table = null == alias ? tableRef.getTable() :
                                      new Table(null, alias);
        Column left = new Column(table, this.getPublicName());
        equalsTo.setLeftExpression(left);
        equalsTo.setRightExpression(new Column(
                                new Table(null, fuzzyLabel.getAlias()),
                                "label_id"));
        join.setOnExpression(equalsTo);
        tableRef.setParent(subJoin);
        // this is the column containing the real value
        this.fuzzyLabelColumn = new Column(new Table(null, fuzzyLabel.getAlias()), "label_name");        
    }

    public Column getFuzzyLabelColumn() {
        return fuzzyLabelColumn;
    }

    public String getQualifiedName() {
        return getQualifiedName(publicName, tableRef.getTable());
    }
    
    public String getQualifiedName(boolean includeSchema) {
        return getQualifiedName(publicName, tableRef.getTable(), includeSchema);
    }

    public static String getQualifiedName(Column column) {
        return getQualifiedName(column.getColumnName(), column.getTable());
    }

    //TODO move to a helper
    private static String getQualifiedName(String name, Table table) {
        return getQualifiedName(name, table, true);
    }
    
    private static String getQualifiedName(String name, Table table, boolean includeSchema) {
        String qualifiedName = "";
        if (null != table) {
            if (null != table.getAlias()) {
                qualifiedName = table.getAlias() + ".";
            } else if (null != table.getName()) {
                qualifiedName = table.getName() + ".";
                if (includeSchema && null != table.getSchemaName()) {
                    qualifiedName = table.getSchemaName() + "." + qualifiedName;
                }
            }
        }
        
        // ta.c || [s.]t.c
        return qualifiedName + name;
    }
    
    private void updateTableRefParent(SubJoin subJoin) {
        // This replace the parent in the FROM by subJoin
        switch (tableRef.getParentType()){
            case PLAIN_SELECT:
                tableRef.getPlainSelectParent().setFromItem(subJoin);
                break;
            case SUB_JOIN:
                tableRef.getSubJoinParent().setLeft(subJoin);
                break;
            case JOIN:
                tableRef.getJoinParent().setRightItem(subJoin);
                break;
        }
    }

    public Column getGroupBySimilarityColumn() {
        return this.groupBySimilarityColumn;
    }

    public String getJoinForOrderBy() {
        if (this.fuzzyLabelIdColumn != null) {
            return this.fuzzyLabelIdColumn.toString();
        } else {
            return this.getQualifiedName();
        }
    }
}
