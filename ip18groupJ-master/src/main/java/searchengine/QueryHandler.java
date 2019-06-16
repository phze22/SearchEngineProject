package searchengine;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is responsible for processing and answering queries to our search engine.
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */

public class QueryHandler {

    /**
     * The index the QueryHandler uses for answering queries.
     */
    private Index idx;

    /**
     *The ranking algorithm used for calculating website scores.
     */
    private Score score;

    /**
     * Creates a {@code QueryHandler} object with given index and given score.
     * @param idx The index used by the QueryHandler.
     * @param score The ranking algorithm used by the QueryHandler.
     */
    public QueryHandler(Index idx, Score score) {
        this.idx = idx;
        this.score = score;
    }

    /**
     * getMachingWebsites answers queries of following types:
     * site query, single-word query, multiple word query.
     * A site query, starting with "site:", just checks matches on the mentioned website.
     * Multiple word querries can be an Or-qery, which has the form ("subquery1 OR subquery2 OR subquery3 ...") and
     * a "subquery" has the form "word1 word2 word3 ...". A website matches
     * a subquery if all the words occur on the website. A website
     * matches the Or-query, if it matches at least one subquery.
     * A query can have prefix words, which ends with "*".
     * The method returns in this case websites which contain words with the prefix-substring.
     * @param query the whole query string entered by the user
     * @return the list of websites that matches the query, ranked by their score.
     */
    public List<Website> getMatchingWebsites(String query) {

        // Checks if the query starts with "site:"
        if (query.matches("^(site:)(\\S*)\\s+(.+)")){
            return processSiteQuery(query);
        }
        // Checks if the query contains more than one word
        else if (query.matches("\\S*\\s+\\S.*")){
            return processMultipleWordQuery(query);
        }
        // Else processes the single word
        else {
            return processSingleWordQuery(query);
        }
    }

    /**
     * Processes a query containing a single word.
     * @param query the query string
     * @return the list of websites that match the word, ranked by their scores.
     */

     List<Website> processSingleWordQuery(String query) {
        String trimmedQuery = query.trim().toLowerCase();
        //new
        List<Website> matchingWebsite = new ArrayList<> (this.idx.lookup(trimmedQuery)); // get list of matching websites for word
        Map<Website, Double> results = new HashMap<>();

        for(Website website : matchingWebsite){
            double sc = prefixCheck(website, trimmedQuery);
            results.put(website, sc);
        }
        // return a list Websites sorted by their value
        return sortResults(results);
    }

    /**
     * Processes a query containing multiple words. The query may or may not contain OR-statements.
     * @param query the query string
     * @return The list of websites ranked by their score. Each website matches all the words in the subquery and
     * if applicable, at least one OR-statement.
     */

     List<Website> processMultipleWordQuery(String query) {
        // Creates a new list for the websites to store query-result
        Map<Website, Double> results = new HashMap<>();

        // Splits the query string at " OR " into an array of strings
        String [] splitAtOR = query.split("(\\sOR(\\s|$))");

        // Loop through each subquery in the splitAtOR array
        for (String aSplitAtOR : splitAtOR) {

            // Split given subquery of the splitAtOR array at a whitespace into an array of single words
            String[] splitWords = aSplitAtOR.trim().toLowerCase().split("\\s+");

            List<Website> websitesInCommon = getWebsitesInCommon(splitWords);

            Map<Website, Double> websiteToScore = createScoreForSubquery(websitesInCommon, splitWords);

            // puts sorted websites of each element in the splitAtOR into final result list
            results.putAll(getHighestScoreForOrQuery(results, websiteToScore));
        }
        return sortResults(results);
    }

    /**
     * Processes a query containing a "site:"-statement.
     * @param query The query string.
     * @return The list of websites that match the given URL and query word, ranked by their scores.
     */

    List<Website> processSiteQuery(String query) {
        // Creates a new list for the websites we wish to return
        List<Website> result = new ArrayList<>();

        // Compile pattern if the string query matches the pattern
        Pattern pattern = Pattern.compile("^(site:)(\\S*)\\s+(.+)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.matches()) {

            String url = matcher.group(2).toLowerCase(); // Store the URL as string
            String queryLine = matcher.group(3); // Store queryLine as string

            List<Website> websites = getMatchingWebsites(queryLine);

            for (Website website : websites) { // Loops through matching websites
                // Puts the websites url to lower case, so that the "website:" search is not case sensitive
                String lowerCaseURL = website.getUrl().toLowerCase();
                // Checks if the website's URL contains the queryLine URL
                // and that the queryLine URL is of the correct format,
                // and that the website is not already in the list of results
                if (lowerCaseURL.contains(url) && url.matches("\\w*\\.*\\w+\\.\\w+\\S*") && !result.contains(website)) {
                    result.add(website);
                }
            }
        }
        else {
            System.out.println("The pattern does not match the query query");
        }
        return result;
    }

    /**
     * Sorts the websites in a given websitesToScore map according to their score in descending order.
     * @param websitesToScore the map with websites as keys and scores as values.
     * @return the list of websites sorted by their score.
     */
    private List<Website> sortResults(Map<Website, Double> websitesToScore){
        List<Website> resultSorted = websitesToScore.entrySet().stream().sorted((x,y) -> y.getValue().compareTo(x.getValue())).map( Map.Entry::getKey).collect(Collectors.toList());
        return resultSorted;
    }

    /**
     * Sorts the result of matches for an OR-query by their maximum value of its subqueries.
     * @param results The list containing the unsorted websites mapped to their value for an Or-query.
     * @param websiteToScore the wordsToMatchingWebsitesMap containing the intersection of matching websites for a multiple word query mapped
     *                       to their score.
     * @return The list of websites for an Or-query linked to their highest score.
     */
    private Map<Website, Double> getHighestScoreForOrQuery(Map<Website, Double> results, Map<Website, Double> websiteToScore){

        for(Website website : websiteToScore.keySet()){
            // For OR between two or more multiple-word queries, the highest score of the intersecting queries
            // is added as a value to the website. If the values are equal, the one already in the wordsToMatchingWebsitesMap is kept.
            if (results.containsKey(website) && results.get(website)<websiteToScore.get(website)) {
                results.put(website, websiteToScore.get(website));
            }
            if(!results.containsKey(website)){results.put(website, websiteToScore.get(website));}
        }
        return results;
    }

    /**
     * Calculates the scores of a subquery by its sum of the word's scores.
     * @param websitesInCommon the list of websitesInCommon that match the subquery
     * @param splitWords the string array of all splitWords in a subquery
     * @return a wordsToMatchingWebsitesMap, mapping websitesInCommon to their scores for given subquery
     */
    private Map<Website,Double> createScoreForSubquery(List<Website> websitesInCommon, String[] splitWords) {
        // creates a new wordsToMatchingWebsitesMap for all websitesInCommon mapped to score of the subquery
        Map<Website, Double> websiteToScore = new HashMap<>();

        // goes through all the websitesInCommon and calculates the score, when website is already contained
        // the score is added to the current score
        for (Website website : websitesInCommon) {

            for (String word : splitWords) {

                Double sc = prefixCheck(website, word);

                //in case of multiple splitWords the score is the sum of both scores
                if (websiteToScore.containsKey(website)) {
                    websiteToScore.put(website, websiteToScore.get(website) + sc);
                } else {
                    websiteToScore.put(website, sc);
                }
            }
        }
        return websiteToScore;
    }

    /**
     * Checks if given word is a prefix search. If so, calls getScorePrefixSearch, if not, calls getScore.
     * @param website One of the websites that the subquery words have in common.
     * @param word One word of the subquery.
     * @return Score for the given word.
     */
    Double prefixCheck(Website website, String word){
        double sc;
        if(word.endsWith("*")){
            sc = getScorePrefixSearch(website, word);
        }
        else{
            // gets score for each website and puts it into a wordsToMatchingWebsitesMap with website and score
            sc = this.score.getScore(word, website, this.idx);
        }
        return sc;
    }

    /**
     * Gets the score for a prefix query by the highest score of all prefix words on the website.
     * @param website One of the websites that the subquery words have in common.
     * @param prefix the prefix query
     * @return Highest score for given website.
     */
    private Double getScorePrefixSearch (Website website, String prefix){

        List<String> wordsOnWebsite = website.getWords();
        double highestScore = 0;
        String prefixWord = prefix.substring(0, prefix.length()-1);


        for (String word : wordsOnWebsite){
            if(word.startsWith(prefixWord)){
                double siteScore = this.score.getScore(word, website, this.idx);
                if (highestScore == 0 || highestScore < siteScore){
                    highestScore = siteScore;
                }
            }
        }
        return highestScore;
    }

    /**
     * Creates a list of the websites that all the words in a given subquery have in common.
     * @param splitWords An array with the subquery splitted into separate words.
     * @return List of all the websites the words of the given subquery have in common.
     */
    private List<Website> getWebsitesInCommon(String[] splitWords){

        List<Website> websitesInCommon = new ArrayList<>();

        // Loops through each element in the subquery and adds overlapping websites to a list
        int check = 0; // makes sure that addAll method is only run once, even if the list is still empty.
        for (String splitWord : splitWords) {
            // Checks if the sitesWithAllWords list is empty
            if (websitesInCommon.isEmpty() && check == 0) {
                // Adds all websites the contains word at index z to the sitesWithAllWords list
                websitesInCommon.addAll(this.idx.lookup(splitWord));
                check++;
            } else {
                // Look at the words and check for websites that each word has in common with the others and retain the websites.
                websitesInCommon.retainAll(this.idx.lookup(splitWord));
            }
        }
        return websitesInCommon;
    }
}
