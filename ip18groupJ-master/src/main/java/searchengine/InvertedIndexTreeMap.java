package searchengine;
import java.util.TreeMap;

/**
 * Extends the inverted index and uses a TreeMap data structure for mapping words to lists of websites,
 * which contain the word.
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
public class InvertedIndexTreeMap extends InvertedIndex {
    public InvertedIndexTreeMap() { this.indexMap = new TreeMap<>();
    }
}
