package webcrawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is responsible for writing the websites found by the webcrawler to a flat .txt file format.
 */
public class CrawlerWriter {

    // Map Website-URL (String) to list of words (List<String>)
    private Map<String, List<String>> siteToWords;

    /**
     * Creates a {@code CrawlerWriter} object.
     * Crawls the the given url plus the amount of related sites with respect to the given maximum amount of websites to crawl.
     * @param url the url string we want to start the crawling on
     * @param maxPages the amount of pages we want our webcrawler to crawl
     */
    public CrawlerWriter(String url, int maxPages) {

        // Create new webCrawler
        WebCrawler webCrawler = new WebCrawler(maxPages);
        // Search for a URL to crawl
        webCrawler.search(url);
        // set Map to the result map from the crawl with Websites mapped to list of words
        this.siteToWords = webCrawler.getUrlWordMap();
    }

    /**
     * Writes the result from the webcrawl to a flat file format.
     * @param filename the name of the .txt file we want to write the result to
     */
    public void writeToFile(String filename){

        // Write websites in siteToWords map to flat txt file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for(String url: siteToWords.keySet()){
                writer.newLine();
                // Start each new website with PAGE: followed by the url. Same format as given in the assignment.
                writer.write("*PAGE:"+url);
                // Loop over all words in the list of words.
                for (String word:siteToWords.get(url)){
                    writer.newLine();
                    writer.write(word);
                }
            }
            writer.close();
        }

        catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the URL of the website you wish to start the crawl on (https format)");
        String siteURl = sc.nextLine();
        System.out.println("Enter the number of pages you wish the crawler to visit");
        int pages = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter the path and name of the file you wish to write on (path in relation to this file)");
        String fileName = sc.nextLine();
        CrawlerWriter crawlerWriter = new CrawlerWriter(siteURl,pages);
        crawlerWriter.writeToFile(fileName);
    }
}