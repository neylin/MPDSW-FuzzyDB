package fuzzy.type3.ddl;

import fuzzy.database.Connector;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a domain in memory
 */
public class Domain {

    private Connector connector;
    private int id = 0;
    private String name;
    private String tableSchema;


    /**
     * Domain tipically instantiated by name. Afterwards is seen if it wants
     * to be matched with an existing one in the database.
     * 
     * @param name Domain name
     */
    public Domain(Connector connector, String name) {
        this.connector = connector;
        this.name = name;
    }


    /**
     * The domain is loaded from database based on name and set if exists
     * @throws SQLException if an sql error ocurrs during search for domain
     */
    public void load() throws SQLException {
        String sql = "SELECT domain_id, table_schema FROM information_schema_fuzzy.domains "
                + "WHERE domain_name='" + name + "'";
        ResultSet rs = connector.executeRawQuery(sql);
        // register columns read from database
        if (rs.next()) {
            id = rs.getInt("domain_id");
            tableSchema = rs.getString("table_schema");
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }
}
