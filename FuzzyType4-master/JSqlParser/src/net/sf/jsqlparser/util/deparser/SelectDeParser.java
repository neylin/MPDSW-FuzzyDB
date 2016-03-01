package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Top;
import net.sf.jsqlparser.statement.select.Union;

/**
 * A class to de-parse (that is, transform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.select.Select}
 */
public class SelectDeParser implements SelectVisitor, OrderByVisitor, SelectItemVisitor, FromItemVisitor {

    protected StringBuffer buffer;
    protected ExpressionVisitor expressionVisitor;

    public SelectDeParser() {
    }

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse
     * expressions. It has to share the same<br>
     * StringBuffer (buffer parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the select
     */
    public SelectDeParser(ExpressionVisitor expressionVisitor, StringBuffer buffer) {
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
    }

    public void visit(PlainSelect plainSelect) {
        buffer.append("SELECT ");
        Top top = plainSelect.getTop();
        if (top != null) {
            top.toString();
        }
        if (plainSelect.getDistinct() != null) {
            buffer.append("DISTINCT ");
            if (plainSelect.getDistinct().getOnSelectItems() != null) {
                buffer.append("ON (");
                for (Iterator iter = plainSelect.getDistinct().getOnSelectItems().iterator(); iter.hasNext();) {
                    SelectItem selectItem = (SelectItem) iter.next();
                    try {
                        selectItem.accept(this);
                    } catch (Exception e) {
                    }
                    if (iter.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(") ");
            }

        }

        for (Iterator iter = plainSelect.getSelectItems().iterator(); iter.hasNext();) {
            SelectItem selectItem = (SelectItem) iter.next();
            try {
                selectItem.accept(this);
            } catch (Exception e) {
            }
            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }

        buffer.append(" ");

        if (plainSelect.getFromItem() != null) {
            buffer.append("FROM ");
            try {
                plainSelect.getFromItem().accept(this);
            } catch (Exception e) {
            }
        }

        if (plainSelect.getJoins() != null) {
            for (Iterator iter = plainSelect.getJoins().iterator(); iter.hasNext();) {
                Join join = (Join) iter.next();
                deparseJoin(join);
            }
        }

        if (plainSelect.getWhere() != null) {
            buffer.append(" WHERE ");
            try {
                plainSelect.getWhere().accept(expressionVisitor);
            } catch (Exception e) {
            }
        }

        if (plainSelect.getGroupByColumnReferences() != null) {
            buffer.append(" GROUP BY ");
            for (Iterator iter = plainSelect.getGroupByColumnReferences().iterator(); iter.hasNext();) {
                Expression columnReference = (Expression) iter.next();
                try {
                    columnReference.accept(expressionVisitor);
                } catch (Exception e) {
                }
                if (iter.hasNext()) {
                    buffer.append(", ");
                }
            }
        }

        if (plainSelect.getHaving() != null) {
            buffer.append(" HAVING ");
            try {
                plainSelect.getHaving().accept(expressionVisitor);
            } catch (Exception e) {
            }
        }

        if (plainSelect.getOrderByElements() != null) {
            deparseOrderBy(plainSelect.getOrderByElements());
        }

        if (plainSelect.getLimit() != null) {
            deparseLimit(plainSelect.getLimit());
        }

    }

    public void visit(Union union) {
        for (Iterator iter = union.getPlainSelects().iterator(); iter.hasNext();) {
            buffer.append("(");
            PlainSelect plainSelect = (PlainSelect) iter.next();
            try {
                plainSelect.accept(this);
            } catch (Exception e) {
            }
            buffer.append(")");
            if (iter.hasNext()) {
                buffer.append(" UNION ");
            }

        }

        if (union.getOrderByElements() != null) {
            deparseOrderBy(union.getOrderByElements());
        }

        if (union.getLimit() != null) {
            deparseLimit(union.getLimit());
        }

    }

    public void visit(OrderByElement orderBy) {
        try {
            orderBy.getExpression().accept(expressionVisitor);
        } catch (Exception e) {
        }

        String asc = null;

        if (orderBy.isAsc()) {
            buffer.append(" ASC");
        } else {
            buffer.append(" DESC");
        }
    }

    public void visit(Column column) {
        buffer.append(column.getWholeColumnName());
    }

    public void visit(AllColumns allColumns) {
        buffer.append("*");
    }

    public void visit(AllTableColumns allTableColumns) {
        buffer.append(allTableColumns.getTable().getWholeTableName() + ".*");
    }

    public void visit(SelectExpressionItem selectExpressionItem) {
        try {
            selectExpressionItem.getExpression().accept(expressionVisitor);
        } catch (Exception e) {
        }
        if (selectExpressionItem.getAlias() != null) {
            buffer.append(" AS " + selectExpressionItem.getAlias());
        }

    }

    public void visit(SubSelect subSelect) {
        buffer.append("(");
        try {
            subSelect.getSelectBody().accept(this);
        } catch (Exception e) {
        }
        buffer.append(")");
    }

    public void visit(Table tableName) {
        buffer.append(tableName.getWholeTableName());
        String alias = tableName.getAlias();
        if (alias != null && !alias.isEmpty()) {
            buffer.append(" AS " + alias);
        }
    }

    public void deparseOrderBy(List orderByElements) {
        buffer.append(" ORDER BY ");
        for (Iterator iter = orderByElements.iterator(); iter.hasNext();) {
            OrderByElement orderByElement = (OrderByElement) iter.next();
            try {
                orderByElement.accept(this);
            } catch (Exception e) {
            }
            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }
    }

    public void deparseLimit(Limit limit) {
        // LIMIT n OFFSET skip 
        buffer.append(" LIMIT ");
        if (limit.isRowCountJdbcParameter()) {
            buffer.append("?");
        } else if (limit.getRowCount() != 0) {
            buffer.append(limit.getRowCount());
        } else {
            /*
             from mysql docs:
             For compatibility with PostgreSQL, MySQL also supports the LIMIT row_count OFFSET offset syntax.
             To retrieve all rows from a certain offset up to the end of the result set, you can use some large number
             for the second parameter. 
             */
            buffer.append("18446744073709551615");
        }

        if (limit.isOffsetJdbcParameter()) {
            buffer.append(" OFFSET ?");
        } else if (limit.getOffset() != 0) {
            buffer.append(" OFFSET " + limit.getOffset());
        }

    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }

    public void visit(SubJoin subjoin) {
        buffer.append("(");
        try {
            subjoin.getLeft().accept(this);
        } catch (Exception e) {
        }
        buffer.append(" ");
        deparseJoin(subjoin.getJoin());
        buffer.append(")");
    }

    public void deparseJoin(Join join) {

        if (join.isSimple()) {
            buffer.append(", ");
        } else {

            if (buffer.charAt(buffer.length() - 1) != ' ') {
                buffer.append(" ");
            }

            if (join.isRight()) {
                buffer.append("RIGHT ");
            } else if (join.isNatural()) {
                buffer.append("NATURAL ");
            } else if (join.isFull()) {
                buffer.append("FULL ");
            } else if (join.isLeft()) {
                buffer.append("LEFT ");
            }

            if (join.isOuter()) {
                buffer.append("OUTER ");
            } else if (join.isInner()) {
                buffer.append("INNER ");
            }

            buffer.append("JOIN ");

        }

        FromItem fromItem = join.getRightItem();
        try {
            fromItem.accept(this);
        } catch (Exception e) {
        }
        if (join.getOnExpression() != null) {
            buffer.append(" ON ");
            try {
                join.getOnExpression().accept(expressionVisitor);
            } catch (Exception e) {
            }
        }
        if (join.getUsingColumns() != null) {
            buffer.append(" USING ( ");
            for (Iterator iterator = join.getUsingColumns().iterator(); iterator.hasNext();) {
                Column column = (Column) iterator.next();
                buffer.append(column.getWholeColumnName());
                if (iterator.hasNext()) {
                    buffer.append(" ,");
                }
            }
            buffer.append(")");
        }

    }
}
