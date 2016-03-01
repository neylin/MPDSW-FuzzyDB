package fuzzy.type3.translator;

import fuzzy.common.translator.Translator;
import fuzzy.common.translator.FuzzyColumnSet;
import fuzzy.common.translator.FuzzyColumn;
import fuzzy.common.translator.TableRefList;
import fuzzy.common.translator.TableRef;
import fuzzy.common.translator.AliasGenerator;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import static fuzzy.common.translator.TableRef.ParentType.JOIN;
import static fuzzy.common.translator.TableRef.ParentType.PLAIN_SELECT;
import static fuzzy.common.translator.TableRef.ParentType.SUB_JOIN;
import static fuzzy.common.translator.TableRef.TableType.SUB_SELECT;
import java.io.StringReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

/**
 * A class to de-parse (that is, transform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.select.Select}
 */
public class SelectTranslator implements SelectVisitor, OrderByVisitor, SelectItemVisitor, FromItemVisitor {

    protected Connector connector;
    protected ExpressionVisitor expressionVisitor;
    protected List listOfSelectItems;
    protected List<SelectItem> selectItems;
    private final AliasGenerator aliasGenerator;
    /**
     * Keep a reference to all tables in the FROM clause
     */
    private TableRefList tableRefSet;
    /**
     * Keep a reference to all fuzzy columns found in SELECT, WHERE, GROUP BY, HAVING, ORDER BY
     */
    private FuzzyColumnSet fuzzyColumnSet;

    public SelectTranslator(Connector connector) {
        this.connector = connector;
        this.aliasGenerator = new AliasGenerator();
    }

    SelectTranslator(Connector connector, AliasGenerator aliasGenerator) {
        this.connector = connector;
        this.aliasGenerator = aliasGenerator;
    }

    public LinkedList<String> getSelectItems() {
        LinkedList<String> columns = new LinkedList<String>();
        for (int i = 0; i < selectItems.size(); ++i) {
            SelectExpressionItem sei = (SelectExpressionItem) selectItems.get(i);
            if (null == sei.getAlias()) {
                columns.add(sei.toString());
            } else {
                columns.add(sei.getAlias());
            }
        }
        return columns;
    }

    @Override
    public void visit(PlainSelect plainSelect) throws Exception {
        Logger.debug("Explorando tablas y columnas difusas");
        //tableRefSet.debugDump();
        // used variables
        
        
        
        this.selectItems = (List<SelectItem>) plainSelect.getSelectItems();
        Expression where = plainSelect.getWhere();
        List<Expression> groupByColumnReferences = (List<Expression>) plainSelect.getGroupByColumnReferences();
        Expression having = plainSelect.getHaving();
        List<OrderByElement> orderByElements = (List<OrderByElement>) plainSelect.getOrderByElements();

        // create table set references using the FROM
        this.tableRefSet = new TableRefList(connector, plainSelect);

        // create column set references using the SELECT
        this.fuzzyColumnSet = new FuzzyColumnSet(connector, tableRefSet, plainSelect, 3);


        ExpressionTranslator eT = new ExpressionTranslator(connector, tableRefSet, fuzzyColumnSet, selectItems, aliasGenerator);
        expressionVisitor = eT;

        // translate SubSelects
        for (TableRef tableRef : tableRefSet.getList()) {
            if (tableRef.getTableType() == SUB_SELECT) {
                Logger.debug("Visiting a sub-select with the ExpressionTranslator");
                tableRef.getSubSelect().accept((ItemsListVisitor) eT);
            }
        }

        // translate GROUP BY
        // This must be translated first because if it's a fuzzy group by it adds a JOIN
        // to the tableRef of the fuzzyColumn that replaces the LEFT JOIN used by
        // SELECT, ORDER BY, WHERE, etc.
        List<Integer> selectItemsVisited = new ArrayList<Integer>();
        List<Integer> orderByElementsVisited = new ArrayList<Integer>();
        if (groupByColumnReferences != null) {
            Logger.debug("Translating GROUP BY expression");
            List<Expression> aggregationFunctionParameters = new ArrayList<Expression>();
            GroupByExpressionTranslator groupByExpressionTranslator =
                    new GroupByExpressionTranslator(fuzzyColumnSet, aliasGenerator, aggregationFunctionParameters);
            for (Expression e : groupByColumnReferences) {
                e.accept(groupByExpressionTranslator);
            }

            // Now I look for COUNT in SELECT and in HAVING
            if (having != null) {
                having.accept(groupByExpressionTranslator);
            }

            /* if aggregationFunctionParameters has elements it means that the
             * GROUP BY is fuzzy. Functions expressions in selectItems and orderBy must be
             * visited to translate them and to validate that only COUNT is used.
             * 
             * Only functions must be visited since all other columns will be visited
             * after finish with GROUP BY translation.
             */
            if (!aggregationFunctionParameters.isEmpty()) {
                
                Logger.debug("Translating COUNT(*) in Select Items");
                for (int index = 0; index < selectItems.size(); ++index) {
                    SelectItem si = selectItems.get(index);
                    if (si instanceof SelectExpressionItem) {
                        Expression e = ((SelectExpressionItem) si).getExpression();
                        if (e instanceof Function) {
                            si.accept(groupByExpressionTranslator);
                            selectItemsVisited.add(index);
                        }
                    }
                }

                Logger.debug("Translating COUNT(*) in Order By Elements");
                if (orderByElements != null) {
                    for (int index = 0; index < orderByElements.size(); ++index) {
                        OrderByElement orderByElement = orderByElements.get(index);
                        if (orderByElement.getExpression() instanceof Function) {
                            orderByElement.getExpression().accept(groupByExpressionTranslator);
                            orderByElementsVisited.add(index);
                        }
                    }
                }

            }
            Logger.debug("GROUP BY Translated");
        }

        /* Since GROUP BY may have visited some selectItems, I should only visit
         * those not yet visited.
         */
        for (int i = 0; i < selectItems.size(); ++i) {
            if (selectItemsVisited.contains(i)) {
                continue;
            }
            SelectItem selectItem = selectItems.get(i);
            eT.setBaseIndex(i);
            Logger.debug("Visiting " + selectItem.toString());
            selectItem.accept(eT);
            Logger.debug("After visit: " + selectItem.toString());
            if (selectItem instanceof AllColumns
                    || selectItem instanceof AllTableColumns) {
                selectItems.remove(i);
            }
        }

        //translate where
        if (null != where) {
            where.accept(eT);
        }

        if (orderByElements != null) {
            for (int i = 0 ; i < orderByElements.size() ; ++i) {
                if (orderByElementsVisited.contains(i)) {
                    continue;
                }
                orderByElements.get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(Union union) throws Exception {
        for (Iterator iter = union.getPlainSelects().iterator(); iter.hasNext();) {
            PlainSelect plainSelect = (PlainSelect) iter.next();
            plainSelect.accept(this);
        }
//
//		if (union.getOrderByElements() != null) {
//			deparseOrderBy(union.getOrderByElements());
//		}
//
//		if (union.getLimit() != null) {
//			deparseLimit(union.getLimit());
//		}
    }

    @Override
    public void visit(OrderByElement orderBy) throws Exception {
        Expression expression = orderBy.getExpression();
        Expression fuzzyStart = orderBy.getFuzzyStart();
        if (fuzzyStart != null) {
            // Fuzzy Order By: 
            // expression must be a Column and the parser check this.
            // This is not the table I need because it doesn't have the table setted.
            Column column = (Column) expression;
            FuzzyColumn fuzzyColumn = this.fuzzyColumnSet.get(column);

            if (fuzzyColumn == null) {
                return;// exception in type 5
            }

            Expression orderByExpression = joinWithFuzzySimilarities(fuzzyColumn, fuzzyStart);
            orderBy.setExpression(orderByExpression);
            orderBy.setFuzzyStart(null);
            orderBy.setAsc(false);

        } else {
            // Clasic Order By but we have to change the label id by the label name
            orderBy.getExpression().accept(expressionVisitor);
        }

    }

    public void visit(Column column) throws Exception {
//		buffer.append(column.getWholeColumnName());
    }

    @Override
    public void visit(AllColumns allColumns) throws Exception {
//		buffer.append("*");
    }

    @Override
    public void visit(AllTableColumns allTableColumns) throws Exception {
//		buffer.append(allTableColumns.getTable().getWholeTableName() + ".*");
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) throws Exception {
//		selectExpressionItem.getExpression().accept(expressionVisitor);
//		if (selectExpressionItem.getAlias() != null) {
//			buffer.append(" AS " + selectExpressionItem.getAlias());
//		}
    }

    @Override
    public void visit(SubSelect subSelect) throws Exception {
//		buffer.append("(");
//		subSelect.getSelectBody().accept(this);
//		buffer.append(")");
    }

    @Override
    public void visit(Table tableName) throws Exception {
//		buffer.append(tableName.getWholeTableName());
//		String alias = tableName.getAlias();
//		if (alias != null && !alias.isEmpty()) {
//			buffer.append(" AS " + alias);
//		}
    }

    public void deparseOrderBy(List orderByElements) {
//		buffer.append(" ORDER BY ");
//		for (Iterator iter = orderByElements.iterator(); iter.hasNext();) {
//			OrderByElement orderByElement = (OrderByElement) iter.next();
//			orderByElement.accept(this);
//			if (iter.hasNext()) {
//				buffer.append(", ");
//			}
//		}
    }

    public void deparseLimit(Limit limit) {
        // LIMIT n OFFSET skip 
//		buffer.append(" LIMIT ");
//		if (limit.isRowCountJdbcParameter()) {
//			buffer.append("?");
//		} else if (limit.getRowCount() != 0) {
//			buffer.append(limit.getRowCount());
//		} else {
//			/*
//			 from mysql docs:
//			 For compatibility with PostgreSQL, MySQL also supports the LIMIT row_count OFFSET offset syntax.
//			 To retrieve all rows from a certain offset up to the end of the result set, you can use some large number
//			 for the second parameter. 
//			 */
//			buffer.append("18446744073709551615");
//		}
//
//		if (limit.isOffsetJdbcParameter()) {
//			buffer.append(" OFFSET ?");
//		} else if (limit.getOffset() != 0) {
//			buffer.append(" OFFSET " + limit.getOffset());
//		}
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }

    @Override
    public void visit(SubJoin subjoin) throws Exception {
//		buffer.append("(");
//		subjoin.getLeft().accept(this);
//		buffer.append(" ");
//		translateJoin(subjoin.getJoin());
//		buffer.append(")");
    }

    public void translateJoin(Join join) {
//		if (join.isSimple())
//			buffer.append(", ");
//		else
//		{
//	
//			if (join.isRight())
//				buffer.append("RIGHT ");
//			else if (join.isNatural())
//				buffer.append("NATURAL ");
//			else if (join.isFull())
//				buffer.append("FULL ");
//			else if (join.isLeft())
//				buffer.append("LEFT ");
//			
//			if (join.isOuter())
//				buffer.append("OUTER ");
//			else if (join.isInner())
//				buffer.append("INNER ");
//
//			buffer.append("JOIN ");
//
//		}
//		
//		FromItem fromItem = join.getRightItem();
//		fromItem.accept(this);
//		if (join.getOnExpression() != null) {
//			buffer.append(" ON ");
//			join.getOnExpression().accept(expressionVisitor);
//		}
//		if (join.getUsingColumns() != null) {
//			buffer.append(" USING ( ");
//			for (Iterator iterator = join.getUsingColumns().iterator(); iterator.hasNext();) {
//				Column column = (Column) iterator.next();
//				buffer.append(column.getWholeColumnName());
//				if (iterator.hasNext()) {
//					buffer.append(" ,");
//				}
//			}
//			buffer.append(")");
//		}
    }

    protected Expression joinWithFuzzySimilarities(FuzzyColumn fuzzyColumn, Expression fuzzyExpression) throws Exception {
        String domainAlias     = this.aliasGenerator.getNewDomainAlias();
        String labelAlias      = this.aliasGenerator.getNewLabelAlias();
        String similarityAlias = this.aliasGenerator.getNewSimilarityAlias();
        
        String domainName      = Helper.getDomainNameForColumn(connector,
                                                              fuzzyColumn.getTableRef().getTable(),
                                                              fuzzyColumn.getPublicName());
        
        String queryLabelId =
                "(SELECT label_id FROM information_schema_fuzzy.labels AS " + labelAlias + " "
                + "WHERE " + labelAlias + ".label_name = " + fuzzyExpression.toString() + " "
                + "AND " + labelAlias + ".domain_id = ("
                + "SELECT " + domainAlias + ".domain_id FROM "
                + "information_schema_fuzzy.domains AS " + domainAlias + " "
                + "WHERE " + domainAlias + ".domain_name = '" + domainName + "'"
                + ")) ";

        ResultSet result = connector.executeRawQuery(queryLabelId);
        
        if ( !result.next() ) {
            throw Translator.FR_LABEL_DO_NOT_EXISTS(fuzzyExpression.toString());
        }
        
        int labelId = result.getInt(1);
        
        String sql = "SELECT nothing "
                + "FROM nothing "
                + "LEFT JOIN "
                + "information_schema_fuzzy.similarities AS " + similarityAlias + " "
                + "ON (" + similarityAlias + ".label1_id = " + labelId
                + "AND " + similarityAlias + ".label2_id = " 
                + fuzzyColumn.getJoinForOrderBy()
                + ") "
                + "ORDER BY COALESCE(" + similarityAlias + ".value, 0)";
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

        TableRef tableRef = fuzzyColumn.getTableRef();
        Table parentOfColumn = tableRef.getTable();

        Logger.debug("Translating: " + parentOfColumn.toString());

        PlainSelect plainSelect = (PlainSelect) s.getSelectBody();
        Join similarityJoin = (Join) plainSelect.getJoins().get(0);

        // This replace the parent in the FROM by subJoin
        switch (tableRef.getParentType()) {
            case PLAIN_SELECT:
                // FROM something
                // FROM something JOIN labels JOIN similarities
                if (tableRef.getPlainSelectParent().getJoins() == null) {
                    tableRef.getPlainSelectParent().setJoins(new ArrayList());
                }
                tableRef.getPlainSelectParent().getJoins().add(similarityJoin);
                break;
            case SUB_JOIN:
                // (a JOIN b)
                // ((a JOIN b) JOIN similarities)
                //       3      1
                SubJoin subJoin1 = tableRef.getSubJoinParent();
                SubJoin subJoin3 = new SubJoin();
                
                subJoin3.setLeft(subJoin1.getLeft());
                subJoin3.setJoin(subJoin1.getJoin());
                
                subJoin1.setLeft(subJoin3);
                subJoin1.setJoin(similarityJoin);
                break;
            case JOIN:
                // JOIN something
                // JOIN (something JOIN similarities)
                // parent         child
                Join parent = tableRef.getJoinParent();
                SubJoin child = new SubJoin();
                child.setLeft(parent.getRightItem());
                child.setJoin(similarityJoin);
                parent.setRightItem(child);
                break;
        }

        return ((OrderByElement) plainSelect.getOrderByElements().get(0)).getExpression();
    }
}