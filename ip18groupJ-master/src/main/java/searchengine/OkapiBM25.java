package searchengine;

/**
 * The OkapiBM25 implements the Score interface and provides the means for calculating a website's Okapi BM25 score
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
public class OkapiBM25 implements Score {


    /**
     * Calculates a given website's Okapi BM25 score based on a query word.
     * @param query the search query given by the user
     * @param website the website which score is to be calculated
     * @param idx the data structure used
     * @return the Okapi BM25 score
     */
    @Override
    public double getScore(String query, Website website, Index idx) {

        // sets the values for b and k
        double b = 0.75;
        double k = 1.2;

        //creates a new TFScore() and new TFIDFScore() to calculate the scores
        double TFScore = new TFScore().getScore(query, website, idx);

        if(TFScore!=0.0){
            double TFIDFScore = new TFIDFScore().getScore(query, website, idx);
            double IDFScore = TFIDFScore/TFScore;

            //calculates length of the document's words
            double documentLength = website.getWords().size();

            double OkabiBM25 = IDFScore * (TFScore * (k + 1) / (TFScore + k * (1 - b + b * documentLength / ((InvertedIndex)idx).getAverageDocumentLength())));

            return OkabiBM25;}

        else return 0.0;
    }
}
