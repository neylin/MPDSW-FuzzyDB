package net.sf.jsqlparser.statement.fuzzy.domain;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

/**
 * 
 */
public class CreateFuzzyType2Domain implements Statement {
    
    private String name;
    private OrderedDomain ordered_domain;

    public CreateFuzzyType2Domain(String name, OrderedDomain ordered_domain) {
        this.name = name;
        this.ordered_domain = ordered_domain;
    }

    public void accept(StatementVisitor statementVisitor) throws Exception {
        statementVisitor.visit(this);
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.ordered_domain.getType();
    }

    public String getLowerBound() {
        return this.ordered_domain.getLowerBound();
    }

    public String getUpperBound() {
        return this.ordered_domain.getUpperBound();
    }

    public OrderedDomain getOrderedDomain() {
        return this.ordered_domain;
    }

    public String toString() {
        return "CREATE FUZZY DOMAIN " + name + " AS POSSIBILITY DISTRIBUTION ON " + ordered_domain;
    }
}