
package searchengine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
class IndexTest {
    Index simpleIndex = null;
    InvertedIndex invertedIndexHashMap = null;
    InvertedIndex invertedIndexTreeMap = null;

    @BeforeEach
    void setUp() {
        List<Website> sites = new ArrayList<Website>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word1")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3")));
        simpleIndex = new SimpleIndex();
        invertedIndexHashMap = new InvertedIndexHashMap();
        invertedIndexTreeMap = new InvertedIndexTreeMap();

        simpleIndex.build(sites);
        invertedIndexHashMap.build(sites);
        invertedIndexTreeMap.build(sites);
    }

    @AfterEach
    void tearDown() {
        simpleIndex = null;
        invertedIndexHashMap = null;
        invertedIndexTreeMap = null;
    }

    @Test
    void buildSimpleIndex() {
        assertEquals("SimpleIndex{sites=[Website{title='example1', url='example1.com', words=[word1, word2, word1]}, Website{title='example2', url='example2.com', words=[word2, word3]}]}", simpleIndex.toString());
    }

    @Test
    void buildInvertedIndexHashMap(){

        // Check that all keys are put into the indexMap without duplicates
        assertEquals(3, invertedIndexHashMap.indexMap.size());
        // Check that the number of associated values (websites) are correct according to each word
        assertEquals(1, invertedIndexHashMap.indexMap.get("word1").size());
        assertEquals(2,invertedIndexHashMap.indexMap.get("word2").size());
    }


    @Test
    void buildInvertedIndexTreeMap(){

        // Check if Treemap was created
        assertEquals("InvertedIndex{keys=[word1, word2, word3]values=[[Website{title='example1', url='example1.com', words=[word1, word2, word1]}], [Website{title='example1', url='example1.com', words=[word1, word2, word1]}, Website{title='example2', url='example2.com', words=[word2, word3]}], [Website{title='example2', url='example2.com', words=[word2, word3]}]]}",invertedIndexTreeMap.toString());

        // Check if a given word from a website appears as a key in the treeMap and the size of the values is correct.
        assertEquals(1,invertedIndexTreeMap.indexMap.get("word3").size());

        // Check that all keys are put into the indexMap
        assertEquals(3, invertedIndexTreeMap.indexMap.size());


    }

    // Helper method for lookup test for lookUpIntervedIndexHashMap and lookUpIntervedIndexTreeMap
    // Check that each key is associated to the correct value (number of websites)
    private void lookup(Index index) {
        assertEquals(1, index.lookup("word1").size());
        assertEquals(2, index.lookup("word2").size());
        assertEquals(0, index.lookup("word4").size());
        assertEquals(2, index.lookup("word*").size());

    }

    // lookUpIntervedIndexHashMap
    @Test
    void lookupInvertedIndexHashMap(){
        lookup(invertedIndexHashMap);
    }

    // lookUpIntervedIndexTreeMap
    @Test
    void lookupInvertedIndexTreeMap(){
        lookup(invertedIndexTreeMap);
    }

}