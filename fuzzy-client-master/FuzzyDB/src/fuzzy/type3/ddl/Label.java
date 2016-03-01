package fuzzy.type3.ddl;

/**
 * This class represents a label of a domain in memory
 */
public class Label<T> extends Metadata {
    
    private int id;
    private Domain domain;
    private T name;
    
    public Label(int id, Domain domain, T name) {
        super(0 != id);
        this.id = id;
        this.domain = domain;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public T getName() {
        return name;
    }

    public Domain getDomain() {
        return domain;
    }
}
