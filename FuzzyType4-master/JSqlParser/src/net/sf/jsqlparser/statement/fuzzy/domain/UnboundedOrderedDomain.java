package net.sf.jsqlparser.statement.fuzzy.domain;

public class UnboundedOrderedDomain extends OrderedDomain {
 
    private String type;

    public UnboundedOrderedDomain(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public String getLowerBound() {
        return null;
    }

    public String getUpperBound() {
        return null;
    }

    // No tiene sentido ponerse con la locura de los visitor para algo tan
    // pequeño y sencillo como el subdominio del fuzzy tipo 2.
    public String toString() {
        return this.type;
    }

}