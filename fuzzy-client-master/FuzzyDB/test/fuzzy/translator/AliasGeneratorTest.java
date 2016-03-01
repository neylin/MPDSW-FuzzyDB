/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.common.translator.AliasGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bishma-stornelli
 */
public class AliasGeneratorTest {
    
    public AliasGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getNewLabelAlias method, of class AliasGenerator.
     */
    @Test
    public void testGetNewLabelAlias() {
        AliasGenerator aliasGenerator = new AliasGenerator();
        assertEquals("L1", aliasGenerator.getNewLabelAlias());
        assertEquals("L2", aliasGenerator.getNewLabelAlias());
        assertEquals("L3", aliasGenerator.getNewLabelAlias());
    }

    /**
     * Test of getNewSimilarityAlias method, of class AliasGenerator.
     */
    @Test
    public void testGetNewSimilarityAlias() {        
        AliasGenerator aliasGenerator = new AliasGenerator();
        assertEquals("S1", aliasGenerator.getNewSimilarityAlias());
        assertEquals("S2", aliasGenerator.getNewSimilarityAlias());
        assertEquals("S3", aliasGenerator.getNewSimilarityAlias());
    }
}
