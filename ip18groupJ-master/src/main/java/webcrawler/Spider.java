package webcrawler;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for the actual crawling of a website.
 */
public class Spider {

    // We set up our request user-agent
    private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

    // List of all of strings (HTML elements) we get from a page.
    private List<String> links = new LinkedList<>();

    // The website (Type Document) we look at - called document in Jsoup.
    private Document htmlDocument;

    // List of words found on website
    private List<String> wordsOnSite = new ArrayList<>();


    /**
     * Crawl the website that is given as parameter.
     * It retrieves the header and all links and words from the website we look at.
     * @param url the url we want to crawl.
     */
    public void crawl(String url) {

        // Try statement since the HTTP request might trow an exception.
        try {

            // Connect to the given url using the defined agent
            Connection connection = Jsoup.connect(url).userAgent("\"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1");

            // Retrieve data from the connection and parse it to htmldocument (Type Document)
            this.htmlDocument = connection.get();

            //  Indicate whether connection is good or bad

            // Connection is working
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }

            // Connection is not working
            if(!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
            }

            // Get all links from the htmlDocument
            // The type is Elements, which is a list of Elements, with methods that act on every element in the list.
            Elements linksOnPage = htmlDocument.select("a[href]");

            // Loop over all links (of type Element) found on the page
            for (Element link : linksOnPage) {
                // Add the links to the links list
                this.links.add(link.absUrl("href"));
            }

            // List to store sentences (list of words) found on the website
            List<String> words = new ArrayList<>();

            // Get all paragraphs from the htmlDocument Element
            Elements wordsOnPage = htmlDocument.select("p");

            // Loop over each paragraph and add to list of words
            for(Element word : wordsOnPage){
                words.add(word.text());
            }

            // Concatenate strings in the list of words into one string.
            String paragraphs = String.join(" ",words);

            // List to store every word from paragraph
            ArrayList<String> paragraphWords = new ArrayList<>();

            // Split paragraphs into single words
            String [] wordsSplit = paragraphs.split(" ");

            // Regex so that we only get the word an not commas, dots etc.
            Pattern pattern = Pattern.compile("(\\w+)");

            // Loop iver wordslips (list of single words) in order to extract only the word and remove punctuations
            for(int i = 0; i<wordsSplit.length; i++){

                Matcher matcher = pattern.matcher(wordsSplit[i]);

                if (matcher.matches()) {
                    String word = matcher.group(1).toLowerCase();
                    paragraphWords.add(word);
                }
            }
            // Add the title of the website as the first line
            this.wordsOnSite.add(htmlDocument.title());
            // Add all words found on website afterwards
            this.wordsOnSite.addAll(paragraphWords);
        }


        // Catch Exception if HTTP request was not successful
        catch (IOException ioe) {
            System.out.println("Error in out HTTP request " + ioe);
        }
    }

    public List<String> getLinks() {
        return this.links;
    }

    public List<String> getWordsOnSite() {
        return wordsOnSite;
    }
}