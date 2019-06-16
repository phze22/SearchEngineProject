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
class ScoreTest {
    private Score TF;
    private Score TFIDF;
    private List<Website> sites = new ArrayList<>();
    private InvertedIndex idx = new InvertedIndexHashMap();


    @BeforeEach
    void setUp(){
        sites.add(new Website("1.com","example1", Arrays.asList("word1", "word2", "word3", "word1")));
        sites.add(new Website("2.com","example2", Arrays.asList("word2", "word3")));
        sites.add(new Website("3.com","example3", Arrays.asList("word3", "word4", "word5")));
        sites.add(new Website("wikipedia.org/test1","example4", Arrays.asList("term1", "term2")));
        sites.add(new Website("wikipedia.org/test2","example5", Arrays.asList("term1", "term3", "term4")));
        idx.build(sites);
        TF = new TFScore();
        TFIDF = new TFIDFScore();
    }

    @Test
    void TFTest(){
        //manual calculation of the tf score
        assertEquals(2.0, TF.getScore("word1", sites.get(0), idx),0.00001);
        assertEquals(0.0, TF.getScore("noMatch", sites.get(0), idx),0.00001);
    }

    @Test
    void TFIDFTest(){
        //manual calculation of the tfidf score
        assertEquals(1.39794, TFIDF.getScore("word1", sites.get(0), idx), 0.00001);
        assertEquals(0.0, TFIDF.getScore("noMatch",sites.get(0),idx), 0.00001);
    }

    @Test
    void OkapiBM25(){
        Score OkapiBM25 = new OkapiBM25();
        assertEquals(0.8577, OkapiBM25.getScore("word1",sites.get(0),idx), 0.000001);
        assertEquals(0.0, OkapiBM25.getScore("noMatch",sites.get(0),idx), 0.000001);
    }
}
