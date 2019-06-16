package searchengine;
import java.util.HashMap;

/**
 * Extends the inverted index and uses a HashMap data structure for mapping words to lists of websites,
 * which contain the word.
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
public class InvertedIndexHashMap extends InvertedIndex {
    public InvertedIndexHashMap() {
        this.indexMap = new HashMap<>();
    }

}
