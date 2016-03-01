package net.sf.jsqlparser.statement.fuzzy.constant;

import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

/**
 * A "CREATE FUZZY CONSTANT" statement
 */
public class CreateFuzzyConstant implements Statement {

    private String name;
    private String domain;
    private ItemsList itemsList;

    @Override
    public void accept(StatementVisitor statementVisitor) throws Exception {
        statementVisitor.visit(this);
    }

    /**
     * The name of the fuzzy domain to be created
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the values := VALUE
     *
     * @return the value of the constant
     */
    public ItemsList getItemsList() {
        return itemsList;
    }

    public void setItemsList(ItemsList list) {
        itemsList = list;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        String sql = "CREATE FUZZY CONSTANT " + name + " " + domain + ":= " + itemsList.toString() + ";";
        return sql;
    }
}
