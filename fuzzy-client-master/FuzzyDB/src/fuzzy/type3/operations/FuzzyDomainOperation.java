/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.type3.ddl.Domain;
import fuzzy.type3.ddl.Label;
import fuzzy.type3.ddl.Relation;
import fuzzy.type3.ddl.Similarity;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bishma-stornelli
 */
abstract public class FuzzyDomainOperation extends Operation {
    
    protected Relation<String> relation;
    
    FuzzyDomainOperation(Connector connector) {
        super(connector);
    }

    public void addLabel(String label) throws Exception {
        relation.addLabel(label);
    }

    public void addSimilarity(String label1, String label2, double value) throws Exception {
        relation.addSimilarity(label1, label2, value);
    }

    public void dropLabel(String label) throws Exception {
        relation.dropLabel(label);
    }

    public void dropSimilarity(String label1, String label2) throws Exception {
        relation.dropSimilarity(label1, label2);
    }

    public void calculate() throws Exception {
        relation.calculate();
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getSchema();

        Domain domain = relation.getDomain();
        if (0 == domain.getId()) {
            String sql = "INSERT INTO information_schema_fuzzy.domains "
                            + "VALUES (DEFAULT, (select current_schema()), '"//TODO escape
                            + domain.getName() + "', 3, NULL)";
            domain.setId(connector.executeRawInsert(sql));
        }
        List<String> labelsToCreate = new ArrayList<String>();
        List<String> labelsToDelete = new ArrayList<String>();
        for (Label<String> l : (Iterable<Label<String>>)relation.getLabels()) {
            if (l.isToBeCreated()) {
                labelsToCreate.add(l.getName());
                String sql = "INSERT INTO information_schema_fuzzy.labels "
                                + "VALUES (DEFAULT, "
                                + l.getDomain().getId() +",'"
                                + l.getName() + "')";//TODO escapar
                l.setId(connector.executeRawInsert(sql));
            } else if (l.isToBeDropped()) {
                labelsToDelete.add(l.getName());
                String sql = "DELETE FROM information_schema_fuzzy.labels "
                                + "WHERE label_id = " + l.getId();
                Logger.debug(sql);
                connector.executeRawUpdate(sql);
            }
        }
        if (labelsToCreate.size() > 0){
            String sql = "INSERT INTO information_schema_fuzzy.labels "
                    + "VALUES ";
            for (int i = 0 ; i < labelsToCreate.size() ; ++i) {
                String label = labelsToCreate.get(i);
                sql += "(DEFAULT, " + domain.getId() + ", '" + label + "')";
                if (i != labelsToCreate.size() - 1) {
                    sql += ",";
                }
            }
        }
        
        for (Similarity<String> s : (Iterable<Similarity<String>>)
                                                   relation.getSimilarities()) {
            if (s.isToBeCreated()) {
                String sql = "INSERT INTO information_schema_fuzzy.similarities "
                                + "VALUES ("
                                + s.getLabel1().getId() +","
                                + s.getLabel2().getId() + ","
                                + s.getValue() + ", "
                                + (s.getDerivated() == 0 ? "FALSE" : "TRUE") + ")";
                connector.executeRawUpdate(sql);
            } else if (s.isToBeAltered()) {
                String sql = "UPDATE information_schema_fuzzy.similarities "
                                + "SET derivated=" + s.getDerivated()
                                + " WHERE label1_id = " + s.getLabel1().getId()
                                + " AND label2_id = " + s.getLabel2().getId();
                connector.executeRawUpdate(sql);
            } else if (s.isToBeDropped()) {
                String sql = "DELETE FROM information_schema_fuzzy.similarities "
                                + "WHERE label1_id = " + s.getLabel1().getId()
                                + " AND label2_id = " + s.getLabel2().getId();
                connector.executeRawUpdate(sql);
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final FuzzyDomainOperation other = (FuzzyDomainOperation) obj;
        if ((this.relation == null) ? (other.relation != null) : !this.relation.equals(other.relation)) {
            return false;
        }
        return true;
    }
    
    
}
