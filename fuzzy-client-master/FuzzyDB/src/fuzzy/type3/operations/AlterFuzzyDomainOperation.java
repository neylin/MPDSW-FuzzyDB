/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.database.Connector;
import fuzzy.type3.ddl.Relation;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author bishma-stornelli
 */
public class AlterFuzzyDomainOperation extends FuzzyDomainOperation {

    public AlterFuzzyDomainOperation(Connector connector, String name) throws Exception {
        super(connector);
        
        relation = new Relation<String>(connector, name, false);

        Statement s;
        String sql;
        ResultSet rs;

        // load labels
        sql = "SELECT label_id, label_name FROM information_schema_fuzzy.labels "
                + "WHERE domain_id=" + relation.getDomain().getId();
        rs = connector.executeRawQuery(sql);
        while (rs.next()) {
            relation.addLabel(rs.getInt("label_id"), rs.getString("label_name"));
        }

        // load similarities
        sql = "SELECT L1.label_name AS label1, L2.label_name AS label2, "
               + "value, derivated FROM "
               + "information_schema_fuzzy.similarities, "
               + "information_schema_fuzzy.labels L1, "
               + "information_schema_fuzzy.labels L2 "
               + "WHERE L1.label_id=label1_id AND "
               + "L2.label_id=label2_id AND "
               + "L1.domain_id = " + relation.getDomain().getId() + " AND "
               + "L2.domain_id = " + relation.getDomain().getId();
        rs = connector.executeRawQuery(sql);
        while (rs.next()) {
            relation.addSimilarity(rs.getString("label1"), rs.getString("label2"),
                                   rs.getDouble("value"), rs.getBoolean("derivated") ? 1 : 0);
        }

        // set to drop derivated
        relation.reset();
    }
}
