package net.sf.jsqlparser.statement.fuzzy.domain;

public class BoundedOrderedDomain extends OrderedDomain {

    private String type;
    private String lowerBound;
    private String upperBound;

    public BoundedOrderedDomain(String type, String lowerBound, String upperBound) {
        this.type = type;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getType() {
        return this.type;
    }

    public String getLowerBound() {
        return this.lowerBound;
    }

    public String getUpperBound() {
        return this.upperBound;
    }

    public String toString() {
        return this.lowerBound + ".." + this.upperBound;
    }

}