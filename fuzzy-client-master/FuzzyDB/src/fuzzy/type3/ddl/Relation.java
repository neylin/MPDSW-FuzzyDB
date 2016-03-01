package fuzzy.type3.ddl;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import fuzzy.common.translator.Translator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Generic class to calculate a relation between the items kept. This class
 * is capable of calculating the symmetric, transitive and reflexive clousure
 */
public class Relation<T> {

    private Domain domain;
    private HashMap<T, Label<T>> labels;
    private HashMap<T, HashMap<T, Similarity<T>>> similarities;

    /**
     * Relations are bound to a domain from it's creation. It can be a new
     * domain or an existing one which must be loaded from database.
     * 
     * @param name Domain name
     * @param isNew If should relation already exist in database
     * @throws Exception 
     */
    public Relation(Connector connector, String name, boolean isNew) throws Exception{
        domain = new Domain(connector, name);
        domain.load();
        if (isNew && 0 != domain.getId()) {
            Logger.debug("Found domain id " + domain.getId());
            throw Translator.FR_DUPLICATE_DOMAIN_NAME(domain.getName());
        }
        if (isNew) {
            domain.setTableSchema(Helper.getSchemaName(connector));
        }
        if (!isNew && 0 == domain.getId()) {
            throw new Exception("Domain '" + name + "' doesn't exist.");
        }
        labels = new HashMap<T, Label<T>>();
        similarities = new HashMap<T, HashMap<T, Similarity<T>>>();
    }


    public Domain getDomain() {
        return domain;
    }

    public Label<T> getLabel(T label) throws Exception {
        if (!labels.containsKey(label)) {
            throw Translator.FR_LABEL_DO_NOT_EXISTS(label.toString());
        }
        return labels.get(label);
    }

    public Similarity<T> getSimilarity(T label1, T label2) throws Exception {
        if (similarities.containsKey(label1)) {
            HashMap<T, Similarity<T>> subSet = this.similarities.get(label1);
            if (subSet.containsKey(label2)) {
                return subSet.get(label2);
            }
        }
        throw new Exception("Similarity (" + label1 + ", "
                            + label2 + ") doesn't exist in domain " + domain.getName());
    }

    public ArrayList<Similarity<T>> getSimilarities() {
        ArrayList<Similarity<T>> r = new ArrayList<Similarity<T>>();
        for (Entry<T, HashMap<T, Similarity<T>>> subSet : similarities.entrySet()) {
            for (Entry<T, Similarity<T>> e : subSet.getValue().entrySet()) {
                r.add(e.getValue());
            }
        }
        return r;
    }

    public ArrayList<Label<T>> getLabels() {
        ArrayList<Label<T>> r = new ArrayList<Label<T>>();
        for (Entry<T, Label<T>> e : labels.entrySet()) {
                r.add(e.getValue());
        }
        return r;
    }


    /**
     * Add a new label to be created
     * 
     * @param label new label
     * @throws Exception 
     */
    public void addLabel(T label) throws Exception{
        addLabel(0, label);
    }


    /**
     * Add an existing label if id is different from zero
     * @param id existing id
     * @param label label name
     * @throws SQLException 
     */
    public void addLabel(int id, T label) throws SQLException{
        if (labels.containsKey(label)) {
            throw Translator.FR_DUPLICATE_LABEL_VALUE((String)label);
        }
        labels.put(label, new Label<T>(id, domain, label));
    }

    /**
     * Asumes the similarity doesn't exist and is not derivated (is user defined).
     * 
     * @param label1 left label
     * @param label2 right label
     * @param value similarity between this both.
     * @throws Exception 
     */
    public void addSimilarity(T label1, T label2, double value) throws Exception {
        addSimilarity(getLabel(label1), getLabel(label2), value, 0, false);
    }

    /**
     * Asumes the similarity already exists.
     * 
     * @param label1 left label
     * @param label2 right label
     * @param value relation value
     * @param derivated if this relation is derivated from others
     * @throws Exception 
     */
    public void addSimilarity(T label1, T label2, double value, int derivated) throws Exception {
        //Logger.debug("addSimilarity(" + label1 + " " + label2 + " " + value + " " + derivated + ")");
        addSimilarity(getLabel(label1), getLabel(label2), value, derivated, true);
    }

    /**
     * Internal way to add a new similarity
     * @param label1 left label
     * @param label2 rigth label
     * @param value similarity value
     * @param derivated if this similarity is derivated from other similarities
     * @param exists if this similarity already exists in database
     * @throws Exception 
     */
    private void addSimilarity(Label<T> label1, Label<T> label2, double value, int derivated, boolean exists) throws Exception {
        if (value < 0.0 || value > 1.0) {
            throw Translator.FR_INVALID_SIMILARITY_VALUE(value);
        }
        // Invalid valid for reflexivity
        if (label1.getName().equals(label2.getName()) && value != 1.0) {
            throw Translator.FR_INVALID_REFLEXIVITY_FOR_DOMAIN(label1.getName().toString());
        }
        
        HashMap<T, Similarity<T>> subSet = this.similarities.get(label1.getName());
        if (subSet == null) {
            subSet = new HashMap<T, Similarity<T>>();
            similarities.put(label1.getName(), subSet);
        }
        
        Similarity<T> s = subSet.get(label2.getName());
        
        if (s == null) {
            HashMap<T, Similarity<T>> subSetSym;
            // find symmetric
            if ((subSetSym = this.similarities.get(label2.getName())) != null) {            
                Similarity<T> sSym = subSetSym.get(label1.getName());
                if (sSym != null) {
                    // They both exists, check if value is consistent
                    if (value != sSym.getValue()) {
                        throw Translator.FR_INVALID_SYMMETRY_FOR_DOMAIN(
                                label1.getName().toString(),
                                label2.getName().toString(), value, sSym.getValue());
                    }
                }            
            }
            s = new Similarity<T>(label1, label2, value, derivated, exists);
            subSet.put(label2.getName(), s);
            return;
        } else if (0 == derivated && 1 == s.getDerivated()) {
            subSet.get(label2.getName()).setDerivated(1);
            return;
        }
        //previously exists and:
        //1. both are derivated (internal error)
        //2. newest is derivated and oldest isn't (internal error)
        //3. both are not derivated, user error
        throw Translator.FR_DUPLICATE_SIMILARITY(label1.getName().toString(), label2.getName().toString());
    }


    /**
     * Public way to drop a label. Ensures the label exists and isn't to be
     * dropped already
     * 
     * @param label label name to be dropped
     * @throws Exception 
     */
    public void dropLabel(T label) throws Exception {
        Label<T> l = getLabel(label);
        if (l.isToBeDropped()) {
            throw new Exception("Label '" + l.getName() +
                    "' dropped twice in domain " + domain.getName());
        }
        // set to be dropped
        l.setToBeDropped();
    }


    /**
     * Pulic way to drop similarity. Ensures similarity exists and isn't
     * already to be dropped. It must be user defined and not derivated.
     * 
     * @param label1 left label
     * @param label2 right label
     * @throws Exception 
     */
    public void dropSimilarity(T label1, T label2) throws Exception {
        // set to be dropped
        Similarity<T> s = getSimilarity(label1, label2);
        if (1 == s.getDerivated()) {
            throw new Exception("Similarity (" + label1 + ", "
                            + label2 + ") doesn't exist in domain " + domain.getName());
        }
        if (s.isToBeDropped()) {
            throw new Exception("Similarity (" + label1 + ", "
                            + label2 + ") dropped twice in domain " + domain.getName());
        }
        s.setToBeDropped();
    }


    /**
     * Sets all derivated relations to be dropped. When complete relation is
     * calculated again, then derivated relations set to be dropped that still
     * matter will be kept.
     * 
     * @throws Exception 
     */
    public void reset() throws Exception {
        for (Entry<T, HashMap<T, Similarity<T>>> subSet : similarities.entrySet()) {
            for (Entry<T, Similarity<T>> e : subSet.getValue().entrySet()) {
                Similarity<T> s = e.getValue();
                //Logger.debug("resetting: " + s.getLabel1().getId() + " " + s.getLabel2().getId() + " " + s.getValue() + " " + s.getDerivated());
                if (1 == s.getDerivated()) {                    
                    s.setToBeDropped();
                }
            }
        }
    }

    /**
     * Calculates the symmetric, reflexive and transitive clousure of the
     * relation and adds de missing derived relations between labels to complete
     * the relation.
     * 
     * @throws Exception 
     */
    public void calculate() throws Exception {
        boolean changed = true;
        reflexivity();
        while (changed) {
            symmetry();

            changed = false;
            ArrayList<Similarity<T>> list = getSimilarities();
            for (Similarity<T> s : list) {
                if (!s.getLabel1().isToBeDropped() && !s.getLabel2().isToBeDropped()
                              && !s.isToBeDropped() && s.getValue() == 1) {
                    if (transitivity(s.getLabel1(), s.getLabel2())) {
                        changed = true;
                    }
                }
            }
        }
    }


    /**
     * Calculates the symmetric clousure of the relation and adds de missing 
     * derived relations between labels to complete the relation.
     * 
     * @throws Exception 
     */
    private void symmetry() throws Exception {
        ArrayList<Similarity<T>> list = getSimilarities();
        for (Similarity<T> s : list) {
            if (!s.getLabel1().isToBeDropped() && !s.getLabel2().isToBeDropped() && !s.isToBeDropped()) {
                //Logger.debug("symmetry: " + s.getLabel1().getId() + " " + s.getLabel2().getId());
                registerSimilarity(s.getLabel2(), s.getLabel1(), s.getValue());
            }
        }
    }


    /**
     * Calculates the reflexive clousure of the relation and adds de missing 
     * derived relations between labels to complete the relation.
     * 
     * @throws Exception 
     */
    private void reflexivity() throws Exception {
        for (Entry<T, Label<T>> e : labels.entrySet()) {
            if (!e.getValue().isToBeDropped()) {
                //Logger.debug("reflexivity: " + e.getValue().getId());
                registerSimilarity(e.getValue(), e.getValue(), 1);
            }
        }
    }


    /**
     * Given a pair of labels which are to be known of having a relation
     * similarity of one, finds pairs that are related with the first label
     * but not with the second label. The similarity's second label is then
     * related with the second label received in this procedure with the value
     * of the similarity found.
     * 
     * @param label1 left label
     * @param label2 right label
     * @return if new derived relations were found
     * @throws Exception 
     */
    private boolean transitivity(Label<T> label1, Label<T> label2) throws Exception {
        boolean changed = false;
        ArrayList<Similarity<T>> list = new ArrayList<Similarity<T>>();
        for (Entry<T, Similarity<T>> e : similarities.get(label1.getName()).entrySet()) {
            list.add(e.getValue());
        }
        for (Similarity<T> s : list) {
            if (!s.getLabel1().isToBeDropped() && !s.getLabel2().isToBeDropped() && !s.isToBeDropped()) {
                // please notice symmetry has already been covered at this point
                // <a, b, 1> /\ <a, d, x> => <b, d, x>
                //Logger.debug("transitivity: <" + label1.getName() + ", " + label2.getName() + "> /\\ <" + s.getLabel1().getName() + ", " +  s.getLabel2().getName() + ">");
                if (label1 == s.getLabel1() && label2 != s.getLabel2()) {
                    //Logger.debug("transitivity: " + label2.getName() + " " + s.getLabel2().getName());
                    if (registerSimilarity(label2, s.getLabel2(), s.getValue())) {
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }


    /**
     * Given this similarity is derived, is added in case it doesn't exist
     * already. If it exists, is checked that is not to be dropped.
     * If is set to be dropped, but is not derivated, it must be set to be
     * derivated. (delete non derivated and create derivated, is the same).
     * 
     * @param label1 left label
     * @param label2 right label
     * @param value new similarity value
     * @return if new similarity relations was effectively added
     * @throws Exception 
     */
    /**
     * 
     * @throws Exception 
     */
    private boolean registerSimilarity(Label<T> label1, Label<T> label2,
                                                double value) throws Exception {
        boolean changed = false;
        Similarity<T> t = null;
        if (similarities.containsKey(label1.getName())) {
            HashMap<T, Similarity<T>> subSet = this.similarities.get(label1.getName());
            if (subSet.containsKey(label2.getName())) {
                t = subSet.get(label2.getName());
            }
        }
        if (null == t) {
            addSimilarity(label1, label2, value, 1, false);
            changed = true;
        } else {
            if (t.isToBeDropped()) {
                if (1 == t.getDerivated()) {
                    t.setToBeCreated();
                } else {
                    t.setDerivated(1);
                    t.setToBeAltered();
                }
                changed = true;
            } else if (t.getValue() != value) {
                // symmetry and reflexivity checks are done at "add" time.
                // if reached this condition, it comes from a transitivity
                throw Translator.FR_INVALID_TRANISITIVITY_FOR_DOMAIN(
                                label1.getName().toString(),
                                label2.getName().toString(),t.getValue(), value);
            }
        }
        return changed;
    }
}
