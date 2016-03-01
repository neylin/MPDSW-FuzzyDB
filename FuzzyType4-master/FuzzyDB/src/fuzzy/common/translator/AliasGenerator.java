/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.common.translator;

import fuzzy.helpers.Logger;

/**
 * Generator of alias for tables of fuzzy metadata. Useful when adding JOINs to 
 * the FROM clause of a SELECT
 * 
 * @author bishma-stornelli
 */
public class AliasGenerator {
    
    protected int domainIndex = 0;
    protected int labelIndex = 0;
    protected int similarityIndex = 0;
    protected final String domainAliasBase = "D";
    protected final String labelAliasBase = "L";
    protected final String similarityAliasBase = "S";

    public AliasGenerator() {
        Logger.debug("Creating new AliasGenerator");
    }
    
    /** Generate a new alias for the labels table of the metadata.
     * 
     * @return a new alias for aliasing information_schema_fuzzy.labels
     */
    public String getNewLabelAlias() {
        return labelAliasBase + ++labelIndex;
    }
    
    /** Generate a new alias for the similarities table of the metadata.
     * 
     * @return a new alias for aliasing information_schema_fuzzy.similarities
     */
    public String getNewSimilarityAlias() {
        return similarityAliasBase + ++similarityIndex;
    }

    public String getNewDomainAlias() {
        return this.domainAliasBase + ++this.domainIndex;
    }
    
}
