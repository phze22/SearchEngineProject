package searchengine;


/**
 * The TFIDFScore implements the Score interface and provides the means for calculating a website's term frequency-
 * inverse document frequency (TFIDF) score
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
public class TFIDFScore implements Score {


    /**
     * Calculates a given website's TFIDF score based on a word.
     *
     * @param word the search word given by the user
     * @param website the website for which the score is to be calculated
     * @param idx the data structure used
     * @return the TFIDF score
     */
    @Override
    public double getScore(String word, Website website, Index idx) {

        // Creates a new TFScore class to calculate the TFScore using its getScore() method
        double TFScore = new TFScore().getScore(word, website, idx);

        if(TFScore!=0.0){

            double docsContainingWord = ((InvertedIndex)idx).getNumberOfWebsites(word);

            // Calculates the IDF score
            double IDFScore = Math.log10(((InvertedIndex) idx).getCorpusSize() / docsContainingWord);

            // Calculates the TFIDF score
            double TFIDFScore = IDFScore *TFScore;

            return TFIDFScore;
        }
        else return 0.0;
    }
}
