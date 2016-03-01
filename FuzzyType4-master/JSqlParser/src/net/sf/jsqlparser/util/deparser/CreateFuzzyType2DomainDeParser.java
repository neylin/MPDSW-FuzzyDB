package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyType2Domain;
import net.sf.jsqlparser.statement.fuzzy.domain.OrderedDomain;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.create.table.CreateTable}
 */
public class CreateFuzzyType2DomainDeParser {

    protected StringBuffer buffer;


    public CreateFuzzyType2DomainDeParser(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public void deParse(CreateFuzzyType2Domain createFuzzyDomain) {
        buffer.append("CREATE FUZZY DOMAIN ")
              .append(createFuzzyDomain.getName())
              .append(" AS POSSIBILITY DISTRIBUTION ON ")
              .append(createFuzzyDomain.getOrderedDomain().toString());
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }
}
