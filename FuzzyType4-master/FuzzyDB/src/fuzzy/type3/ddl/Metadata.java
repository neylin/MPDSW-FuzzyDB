package fuzzy.type3.ddl;

/**
 * Class created to keep common functionality between Labels and Similarities
 * together. Both can be created and dropped.
 */
abstract class Metadata {

    protected enum ACTION {CREATE, ALTER, DROP};
    protected ACTION action;
    protected boolean exists;

    Metadata(boolean exists) {
        this.action = ACTION.CREATE;
        this.exists = exists;
    }

    /**
     * The label/similarity can be set to be created when it's previously
     * set to be dropped. In that case, the label/similarity still exists
     * but it doesn't need to be created again.
     * 
     * @return if the label/similarity should be created
     */
    public boolean isToBeCreated() {
        return !exists && ACTION.CREATE == action;
    }

    public boolean isToBeAltered() {
        return ACTION.ALTER == action;
    }

    public boolean isToBeDropped() {
        return ACTION.DROP == action;
    }

    public void setToBeCreated() {
        action = ACTION.CREATE;
    }

    public void setToBeAltered() throws Exception {
        action = ACTION.ALTER;
    }

    public void setToBeDropped() {
        action = ACTION.DROP;
    }
}
