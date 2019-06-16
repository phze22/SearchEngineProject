package searchengine;

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
class SearchEngineTest {
    private SearchEngine se;

    @BeforeEach
    void SetUp(){
        List<Website> sites = new ArrayList<>();
        Index idx = new InvertedIndexTreeMap();
        sites.add(new Website("page1.com", "Page1", Arrays.asList("word1", "word2")));
        sites.add(new Website("page2.com", "Page2", Arrays.asList("word3", "word4")));
        sites.add(new Website("page2.com", "Page2", Arrays.asList("word1", "word5")));
        se = new SearchEngine(sites);
        Score score = new TFIDFScore();
        idx.build(sites);
    }

    @Test
    void checkSearch(){
        assertEquals(2, se.search("word1").size());
        assertEquals(0, se.search("").size());
    }

}