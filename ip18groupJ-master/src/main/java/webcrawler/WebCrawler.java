package webcrawler;

import java.util.*;

/**
 * This class is responsible for keeping track of what websites we want to crawl and what websites we have already looked at.
 */
public class WebCrawler {

    // Number of maximum pages to search for
    private int maxPages;

    // Set to keep track of websites visited
    private Set<String> sitesVisited;

    // List of sites to be visited
    private List<String> sitesToVisit;

    // Map to store URL mapped to list of words
    private Map<String, List<String>> urlWordMap;

    /**
     * Creates a {@code Crawler} object.
     * Sets up the set to keep track of sitesVisited.
     * Sets up the list to keep track of sitesToVisit.
     * Sets up the map to store URL mapped to the list of words retrieved by the crawler
     * @param maxPages the maximum amount of pages we want to crawl.
     */
    public WebCrawler(int maxPages){
        this.maxPages = maxPages;
        this.sitesVisited = new HashSet<>();
        this.sitesToVisit = new ArrayList<>();
        this.urlWordMap = new HashMap<>();
    }


    /**
     * Uses a spider object to crawl a website
     * the url given as the parameter is put in a map as the key, where all all the words found on the website
     * is stored as values in the map.
     * @param url the url string we want to start the crawling on
     */
    public void search(String url)
    {

        while(this.sitesVisited.size() < maxPages)
        {
            // Local URL to keep track of what we look at
            String currentUrl;

            // Create new spider to crawl
            Spider spider = new Spider();

            // Check to see if this is the first site we look at
            if(this.sitesVisited.isEmpty())
            {
                // if that is the case, set the url given as a parameter to the local variable
                currentUrl = url;
                // add url to list of sites we have looked at.
                this.sitesVisited.add(url);
            }

            // If it is not the first website we look at, call nextUrl method that returns a new url from the list of sites we have not visited (looked at) yet.
            else
            {
                // Set the url we want to look at, to a url we haven't looked at yet.
                currentUrl = this.nextUrl();
            }

            // Crawl the url
            spider.crawl(currentUrl);

            // Map website-URL to list of words extracted from the URL.
            urlWordMap.put(currentUrl,spider.getWordsOnSite());

            // Add the websites found on the currentUrl to the list of sites to visit.
            this.sitesToVisit.addAll(spider.getLinks());
        }

        System.out.println("\n**Done** Visited " + this.sitesVisited.size() + " web page(s)");
    }


    /**
     * Looks at the list of websites found by the webcrawler and returns the first url in that list.
     * Deletes the returned website so that the list - sitesToVisit - only contains websites that have not been crawled by the webcrawler.
     * @return the next Url from the list of websites that have not been visited yet.
     */
    // Find next URL
    private String nextUrl() {
        // local variable to store the nextUrl
        String nextUrl;

        // Get the first URL in our siteToVisit list
        nextUrl = this.sitesToVisit.get(0);

        // Remove the site since it will now be visited
        sitesToVisit.remove(0);

        // If we have visited the nextURL, add it to sitesVisited
        if (this.sitesToVisit.contains(nextUrl)) {
            this.sitesVisited.add(nextUrl);
        }

        // Return the nextURL
        return nextUrl;
    }

    public Map<String, List<String>> getUrlWordMap() {
        return urlWordMap;
    }
}
