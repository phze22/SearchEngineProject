package searchengine;

/**
 * The score interface provides the means for calculating a website's ranking score.
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */

public interface Score {

    /**
     * Calculates a given website's ranking score based on a query word.
     *
     * @param query the search query given by the user
     * @param website the website which score is to be calculated
     * @param index the data structure used
     * @return the score
     */
    double getScore(String query, Website website, Index index);

}
