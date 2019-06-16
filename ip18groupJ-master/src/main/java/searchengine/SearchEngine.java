package searchengine;

import java.util.List;
import java.util.ArrayList;

/**
 * The search engine. Upon receiving a list of websites, it performs
 * the necessary configuration (i.e. building an index and a query
 * handler) to then be ready to receive search queries.
 *
 * @author Willard Rafnsson
 * @author Martin Aumüller
 * @author Leonid Rusnac
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
public class SearchEngine {
    private QueryHandler queryHandler;

    /**
     * Creates a {@code SearchEngine} object from a list of websites.
     *
     * @param sites the list of websites
     */
    public SearchEngine(List<Website> sites) {
        Index idx = new InvertedIndexHashMap();
        Score score = new OkapiBM25();
        idx.build(sites);
        queryHandler = new QueryHandler(idx, score);
    }

    /**
     * Returns the list of websites matching the query.
     *
     * @param query the query
     * @return the list of websites matching the query
     */
    public List<Website> search(String query) {
        if (query == null || query.isEmpty() ) {
            return new ArrayList<>();
        }
        List<Website> resultList = queryHandler.getMatchingWebsites(query);
        return resultList;
    }
}
