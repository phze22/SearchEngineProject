package searchengine;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract class that implements the Index interface.
 * Builds an inverted index, maps words to websites that contain this word.
 * The class is a superclass of inverted indices using different data structures
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */

abstract public class InvertedIndex implements Index {

    /**
     * Instance variable is a protected Map, so that the subclasses can access the indexMap
     * The indexMap is the data structure for the inverted index. It maps words to lists of websites.
     */
    protected Map<String, List<Website>> indexMap;

    /**
     * the total number of websites in the search engine's corpus.
     */
    private double corpusSize;

    /**
     * the average document length, measured by the number of words on the websites in the search engine's corpus.
     */
    private double averageDocumentLength;

    /**
     * Calculates the number of websites where the given word occurs.
     * @param word a query word
     * @return number of websites where the word occurs.
     */
    public Double getNumberOfWebsites(String word){
        double websitesContainingQuery;
        if (indexMap.containsKey(word)){
            websitesContainingQuery = (double) indexMap.get(word).size();
            return websitesContainingQuery;
        }
        else {
            return 0.0;
        }
}
    /**
     * The build method processes a list of websites into an inverted index data structure.
     * @param sites The list of websites that should be indexed
     */
    @Override
    public void build(List<Website> sites) {

        calculateavgDocumentLength(sites); //with every build,

        for (Website site : sites) {  // Loop through the list of websites

            List<String> wordList = site.getWords(); // Stores the words from each website in a list

            for (String word : wordList) {  // Loop through the list of words
                // Checks if the indexMap contains the word, and if the list of websites does not contain the website yet.
                if (indexMap.containsKey(word) && !(indexMap.get(word).contains(site))) {

                    indexMap.get(word).add(site); // Adds the website to the list of websites that contain the word.
                }
                // Checks if the indexMap does not contain the word.
                if (!indexMap.containsKey(word)) {
                    // Maps the word to a new ArrayList of websites and adds the website to the ArrayList.
                    List<Website> websiteList = new ArrayList<>();
                    websiteList.add(site);
                    indexMap.put(word, websiteList);
                }
                // If the indexMap contains the word and its corresponding websites, do nothing.
            }
        }
    }

    /**
     * Receives a word word and returns a list of all websites that contain the word.
     * @param word The word.
     * @return the list of websites that contain the word.
     */
    @Override
    public List<Website> lookup(String word) {

        List<Website> websites = new ArrayList<>();  // list of websites to store websites that match the word

        if (word.endsWith("*")){

            String prefixStr = word.substring(0, word.length()-1);  // Stores the word in front of "*" as a new prefix string

            for(String keyWord : indexMap.keySet()){

                if (keyWord.startsWith(prefixStr)){ // Checks if the keyWord starts with prefix string
                    websites.addAll(indexMap.get(keyWord));} // Adds all websites that contain the keyWord to the results list
            }
            List<Website> result = websites.stream().distinct().collect(Collectors.toList());
            return result;
        }

        // Checks if the word does not end with "*" and if the indexMap contains the word.
        if (!word.endsWith("*") && indexMap.containsKey(word)){
            // Stores the list of websites that contains the word in the list of results
            return new ArrayList<>(indexMap.get(word));
        }
        // Returns an empty list if the indexMap does not contain the word word
        return new ArrayList<Website>();
    }

    /**
     * Overrides the toString method
     * @return a string containing the keys and values of the indexMap
     */
    @Override
    public String toString() {
        return "InvertedIndex{" +
                "keys=" + indexMap.keySet() + "values=" + indexMap.values()+
                '}';
    }

    /**
     * Calculates the average number of words on each document
     * @param sites a list of websites
     */
    public void calculateavgDocumentLength(List<Website> sites){

        corpusSize = sites.size(); //corpusSize also contains websites without any word

        double totalWords = 0;

        for (Website site : sites) {
            totalWords += site.getWords().size(); //adds
        }

        averageDocumentLength = totalWords / corpusSize;
    }

    /**
     *
     * @return amount of all the documents in the database
     */
    public double getCorpusSize() {
        return corpusSize;
    }

    /**
     * @return average document length
     */
    public double getAverageDocumentLength() {

        return averageDocumentLength;
    }
}
