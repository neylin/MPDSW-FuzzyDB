/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.ddl;

import fuzzy.helpers.Logger;

/**
 *
 * @author andras
 */
public class Similarity<T> extends Metadata {

    private Label<T> label1;
    private Label<T> label2;
    private double value;
    private int derivated;


    /**
     * Similarity relation between two labels.
     * 
     * @param label1 left label
     * @param label2 rigth label
     * @param value similarity value
     * @param derivated if this similarity is derivated from other similarities
     * @param exists if this similarity already exists in database
     */
    public Similarity(Label<T> label1, Label<T> label2, double value, int derivated, boolean exists) {
        super(exists);
        this.label1 = label1;
        this.label2 = label2;
        this.value = value;
        this.derivated = derivated;
    }

    public Label<T> getLabel1() {
        return label1;
    }

    public Label<T> getLabel2() {
        return label2;
    }

    public double getValue() {
        return value;
    }

    public int getDerivated() {
        return derivated;
    }

    public void setDerivated(int derivated) {
        this.derivated = derivated;
    }
/*
    @Override
    public void setToBeCreated() {
        super.setToBeCreated();
        Logger.debug("setToBeCreated: " + label1.getId() + " " + label2.getId());
    }

    @Override
    public void setToBeDropped() {
        super.setToBeDropped();
        Logger.debug("setToBeDropped: " + label1.getId() + " " + label2.getId());
    }


    public void setToBeAltered() throws Exception {
        super.setToBeCreated();
        throw new Exception("wtf");
    }*/
}
