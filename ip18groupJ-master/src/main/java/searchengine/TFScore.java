package searchengine;


/**
 * The TFScore implements the Score interface and provides the means for calculating a website's term frequency score.
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
public class TFScore implements Score {

    /**
     * Calculates a given website's term frequency score based on a query word.
     *
     * @param query the search query given by the user
     * @param website the website which score is to be calculated
     * @param idx the data structure used
     * @return the term frequency score
     */
    @Override
    public double getScore(String query, Website website, Index idx) {

        int count = 0;

        for (String word: website.getWords()){
            if(word.equals(query)){
                count++;
            }
        }
       return count;
    }
}
