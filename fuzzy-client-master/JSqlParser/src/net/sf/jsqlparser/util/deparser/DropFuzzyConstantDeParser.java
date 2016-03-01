package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.drop.Drop;

/**
 * A class to de-parse (that is, transform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.create.table.CreateTable}
 */
public class DropFuzzyConstantDeParser {

    protected StringBuffer buffer;

    /**
     * @param buffer the buffer that will be filled with the select
     */
    public DropFuzzyConstantDeParser(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public void deParse(Drop drop) {
        buffer.append("DROP FUZZY CONSTANT ")
                .append(drop.getName())
                .append(" ")
                .append(drop.getParameters().get(0));
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }
}
