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
class QueryHandlerTest {
    private QueryHandler qh = null;
    private List<Website> sites = null;
    private Score score;
    private InvertedIndex idx;


    // Set up sites for the queryhandler test
    @BeforeEach
    void setUp() {
        sites = new ArrayList<>();
        sites.add(new Website("1.com","example1", Arrays.asList("word1", "word2")));
        sites.add(new Website("2.com","example2", Arrays.asList("word2", "word3")));
        sites.add(new Website("3.com","example3", Arrays.asList("word3", "word4", "word5")));
        sites.add(new Website("wikipedia.org/test1","example4", Arrays.asList("term1", "term2")));
        sites.add(new Website("wikipedia.org/test2","example5", Arrays.asList("term1", "term3", "term4")));
        sites.add(new Website("4.com","Website4", Arrays.asList("this", "is", "a", "a", "sample")));
        sites.add(new Website("5.com","Website5", Arrays.asList("this", "is", "is", "another", "example", "example", "example")));
        sites.add(new Website("6.com","Website6", Arrays.asList("rabbit")));
        sites.add(new Website("7.com","Website6", Arrays.asList("flow","flow","flower")));


        // The type of InvertedIndex is set to InvertedIndexTreeMap since that is the one used in the QueryHandler.
        // The test cases are set up so that they should pass for both indices.
        idx = new InvertedIndexHashMap();

        // Calculate Score
        score = new OkapiBM25();

        // Set up the sites using the InvertedIndexTreeMao
        idx.build(sites);

        // Create new QueryHandler Object with given index and score
        qh = new QueryHandler(idx, score);
    }

    @Test
    void testSingleWord() {


        //checks if size of actual result list equals expected result list
        assertEquals(1, qh.getMatchingWebsites("word1").size());
        //checks if title of website list at index 0 matches actual title
        assertEquals("example1", qh.getMatchingWebsites("word1").get(0).getTitle());
        //checks if all websites are listed, when word occurs more than once
        assertEquals(2, qh.getMatchingWebsites("word2").size());
        //checks if search word can be entered in upper case
        assertEquals(2, qh.getMatchingWebsites("WORD2").size());
        // checks if the sorting of the list is correct, that website5 appears first because of the higher score
        assertEquals("Website5", qh.getMatchingWebsites("is").get(0).getTitle());
        // checks if a prefix search returns the correct number of websites
        assertEquals(3, qh.getMatchingWebsites("word*").size());
    }

    @Test
    void testSortingPrefixWords() {
        assertEquals(score.getScore("flow", sites.get(8), idx), qh.prefixCheck(sites.get(8), "flo*"), 0.000001);
        //assertEquals(); how many to expect for something*
    }
    /**
     * checks if expected result is return for multiple words search
     */
    @Test
     void testMultipleWords() {
         assertEquals(1, qh.getMatchingWebsites("word1 word2").size());
         assertEquals(1, qh.getMatchingWebsites("word3 word4").size());
         assertEquals(1, qh.getMatchingWebsites("word4 word3 word5").size());
         //retrieves the website with the higher score for query "this is"
         assertEquals(sites.get(6),qh.getMatchingWebsites("this is").get(0));
        // checks if same word can typed in without overlapping results
         assertEquals(sites.get(2), qh.getMatchingWebsites("word4 word4").get(0));
     }

    /**
     * Checks if expected result is returned for one or several search words connected with OR
     */
     @Test
     void testORQueries() {
         //in upper case
         assertEquals(3, qh.getMatchingWebsites("WORD2 OR WORD3").size());
         // in lower case
         assertEquals(2, qh.getMatchingWebsites("word1 OR word4").size());
         //tests if OR-queries can be combined with multiple word searches
         assertEquals(2, qh.getMatchingWebsites("word1 word2 OR word3 word4").size());
         // Corner case: Does code remove duplicates?
         assertEquals(1, qh.getMatchingWebsites("word1 OR word1").size());
         // checks if searching twice for the same word returns no duplicates
         assertEquals(sites.get(2), qh.getMatchingWebsites("word4 OR word4").get(0));
         //checks if "or" in lower case is not mistaken for OR
         assertEquals(0, qh.getMatchingWebsites("word4 or word4").size());
         // if sorting works with OR
         List<Website> tmpList = new ArrayList<>(Arrays.asList(sites.get(3), sites.get(4), sites.get(5), sites.get(6))); //expected order
         assertEquals(tmpList, qh.processMultipleWordQuery("this OR term1"));
         // checks if OR searches function correctly with prefix searches
         assertEquals(5, qh.getMatchingWebsites("word* OR term*").size());


     }





    @Test
    void testSiteQueries() {
        assertEquals(sites.get(3), qh.getMatchingWebsites("site:wikipedia.org/test1 term1").get(0));
        assertEquals(2, qh.getMatchingWebsites("site:wikipedia.org term1").size());
        // checks if site-query works for multiple search words
        assertEquals(1, qh.getMatchingWebsites("site:wikipedia.org term1 term2").size());
        // checks if site-query works OR-search
        assertEquals(2, qh.getMatchingWebsites("site:wikipedia.org term1 OR term4").size());
        assertEquals(1, qh.getMatchingWebsites("site:wikipedia.org term4 OR term5").size());
        assertEquals(0, qh.getMatchingWebsites("site: faultylink  term4").size());
        assertEquals(0, qh.getMatchingWebsites("wikipedia.org term4").size());
    }

     // Test for problematic input
    @Test
    void testCornerCases() {
        assertEquals(3, qh.getMatchingWebsites("    WORD2     OR    WORD3").size());
        //Checks if "OR" is not recognized in a word
        assertEquals(0, qh.getMatchingWebsites("word2 ORword3").size());
        assertEquals(0, qh.getMatchingWebsites("word2ORword3").size());
        assertEquals(1, qh.getMatchingWebsites("word1      word2").size());
        //assertEquals(2, qh.getMatchingWebsites("OR word2 OR").size());
        assertEquals(0, qh.getMatchingWebsites("site:word1").size());
    }
}
