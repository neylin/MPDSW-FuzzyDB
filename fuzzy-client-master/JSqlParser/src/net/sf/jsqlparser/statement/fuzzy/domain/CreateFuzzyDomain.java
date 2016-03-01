package net.sf.jsqlparser.statement.fuzzy.domain;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.Select;

/**
 * A "CREATE FUZZY DOMAIN" statement
 */
public class CreateFuzzyDomain implements Statement {

    private String name;
    private ExpressionList values;
    private ExpressionList similarityList;
    private Column column;
    private Select select;

    public CreateFuzzyDomain(String name, ExpressionList values,
            ExpressionList similarityList) {
        
        if ( name != null ) {
            this.name = name.toLowerCase();
        }
        
        this.values = values;
        this.similarityList = similarityList;
    }

    public CreateFuzzyDomain(String name, Column column) {
        
        if ( name != null ) {
            this.name = name.toLowerCase();
        }
        
        this.column = column;
    }
    
    public CreateFuzzyDomain(String name, Select select) {
        
        if ( name != null ) {
            this.name = name.toLowerCase();
        }
        
        this.select = select;
    }

    public void accept(StatementVisitor statementVisitor) throws Exception {
        statementVisitor.visit(this);
    }

    /**
     * The name of the fuzzy domain to be created
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if ( name != null ) {
            this.name = name.toLowerCase();
        }
    }

    /**
     * A list of {@link Expression}s of this fuzzy domain. Only strings not
     * validated by the parser
     */
    public ExpressionList getValues() {
        return values;
    }

    public void setValues(ExpressionList values) {
        this.values = values;
    }

    public ExpressionList getSimilarities() {
        return similarityList;
    }

    public void setSimilarities(ExpressionList similarityList) {
        this.similarityList = similarityList;
    }

    public String toString() {
        String sql = "CREATE FUZZY DOMAIN " + name + " " + values.toString();
        if (similarityList.getExpressions().size() > 0) {
            sql += " SIMILARITY {" + similarityList.toString(false) + "}";
        }
        return sql;
    }

    public boolean isFromColumn() {
        return column != null && 
                select == null && values == null && similarityList == null;
    }

    public Column getFromColumn() {
        return this.column;
    }

    public Select getSelect() {
        return select;
    }

    public void setSelect(Select select) {
        this.select = select;
    }
    
    public boolean isFromSelect(){
        return select != null && 
                column == null && values == null && similarityList == null;
    }
}