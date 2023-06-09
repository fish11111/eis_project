package org.project;

import me.tongfei.progressbar.ProgressBar;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent a source of articles which are read from the API response of the "The Guardian" API endpoint.
 */
public final class APISource implements ArticleSource{
    /** This is used to actually connect to the API endpoint. */
    private HTTPClient client;
    /** This is used to facilitate setting and storing the URL. */
    private URLSetter urlSetter;
    /** This is used to parse the field of an article in the response of the API endpoint which is formatted in JSON.*/
    private APIParser jsonParser;
    /** This is used to elaborate a given maximum number of article (the number of available ones could be bigger or lower).*/
    private int maxArticle;
    /** This is used to start the research in the response from the API endpoint starting by the first group of articles.*/
    public final int INITIALCOUNT = 1;
    /** This is used to set a max number of articles present in the response of the API endpoint.*/
    private int pageSize = 100;
    /** This is used to keep track of the article List to eventually return it when asked. */
    private List<APIArticle> articles;
    /** This variable is true if the user want to display a progress bar in terminal, else its value is false. */
    private boolean graphical = true;


    /**
     * This constructor sets the url and the max number of article to return, initialize the URL setter {@link URLSetter} with first value
     * and the articles and the {@link APISource#client}.
     *
     * @param APIKey which needs to be a valid API key of the "The Guardian" API page.
     * @param tags which are used to specify a set of articles related to the strings passed.
     * @param queries which are used to search a specific set of words inside all possible articles.
     * @param maxArticle which is the max number of article to return (so the actual number could be lowed)
     * @throws IOException if the connection went wrong and the HTTP client was not able to connect to the endpoint.
     * @throws JSONException if the API response does not have the expected fields.
     */
    public APISource(String APIKey, String[] tags, String[] queries, int maxArticle, boolean graphical) throws IOException, JSONException {
        // Setting the max number of articles to read (could be less or more).
        this.maxArticle = maxArticle;
        if(maxArticle < pageSize){
            pageSize = maxArticle;
        }
        // Setting URL based on the fields required for the API request.
        urlSetter = new URLSetter(APIKey, INITIALCOUNT, pageSize, queries, tags);
        // Initializing the articles variable.
        articles = new ArrayList<APIArticle>();
        // Initializing the HTTP client for the connection.
        client = new HTTPClient();
        // Initializing the API parser.
        jsonParser = new APIParser();
        this.graphical = graphical;
        // This reads all the articles from the JSON response of the API endpoint.
        readArticle();
    }


    /**
     * This method reads the Articles from the API JSON response and parses (from HTML) the content of the head and body fields of the articles.
     *
     * @throws IOException if the connection went wrong and the HTTP client was not able to connect to the endpoint.
     * @throws JSONException if the API response does not have the expected fields.
     */
    private void readArticle() throws IOException, JSONException {
        int articleCount = 0;
        URL URL = null;
        String APIString = "";
        // Defining the step the progress bar need to do each time.
        long barStep = 10;
        if(maxArticle%pageSize != 0){
            barStep = pageSize*100/(maxArticle+(pageSize-maxArticle%pageSize));
        }else{
            barStep = pageSize*100/(maxArticle);
        }

        // This is the process bar printed on screen to show the evolution of the articles retrieving process.
        if(graphical) {
            try (ProgressBar pb = new ProgressBar("Retrieving articles...", 100)) {
                while (articleCount < maxArticle) {
                    // Incrementing the Article page in the response for the current cycle if not first.
                    if (articleCount > 0) {
                        urlSetter.incrementPage();
                    }
                    // Setting URL based on the fields required for the API request.
                    URL = urlSetter.getURL();
                    //System.out.println("from " + URL + " :");

                    // Getting the response from the API endpoint.
                    client.connect(URL);
                    APIString = client.getHttpString();

                    // Parsing the response from JSON format.
                    jsonParser.parse(APIString);
                    // Adding the Articles read in the new Article page.
                    articles.addAll(jsonParser.getArticles());

                    // Setting the max number of Articles to analyze, based on the number of available ones.
                    if (jsonParser.getTotal() < maxArticle) {
                        maxArticle = jsonParser.getTotal();
                        if (maxArticle < pageSize) {
                            pageSize = maxArticle;
                            barStep = 100;
                        } else {
                            if (maxArticle % pageSize != 0) {
                                barStep = pageSize * 100 / (maxArticle + (pageSize - maxArticle % pageSize));
                            } else {
                                barStep = pageSize * 100 / (maxArticle);
                            }
                        }
                        if (maxArticle == 0) {
                            // Ending printing the process bar.
                            pb.stepTo(pb.getMax());
                            break;
                        }
                    }

                    // Incrementing the article count since we request 100 articles per time.
                    articleCount += pageSize;
                    // Adding progress to progress bar.
                    pb.stepBy(barStep);
                }
                if (pb.getCurrent() != pb.getMax()) {
                    // Ending printing the process bar.
                    pb.stepTo(pb.getMax());
                }
            }
            System.out.println("[Found " + maxArticle + " article/s...]");
        }else{
            while (articleCount < maxArticle) {
                // Incrementing the Article page in the response for the current cycle if not first.
                if (articleCount > 0) {
                    urlSetter.incrementPage();
                }
                // Setting URL based on the fields required for the API request.
                URL = urlSetter.getURL();
                //System.out.println("from " + URL + " :");

                // Getting the response from the API endpoint.
                client.connect(URL);
                APIString = client.getHttpString();

                // Parsing the response from JSON format.
                jsonParser.parse(APIString);
                // Adding the Articles read in the new Article page.
                articles.addAll(jsonParser.getArticles());

                // Setting the max number of Articles to analyze, based on the number of available ones.
                if (jsonParser.getTotal() < maxArticle) {
                    maxArticle = jsonParser.getTotal();
                    if (maxArticle < pageSize) {
                        pageSize = maxArticle;
                    }
                    if (maxArticle == 0) {
                        break;
                    }
                }

                // Incrementing the article count since we request 100 articles per time.
                articleCount += pageSize;
            }

        }
        client.closeConnection();
        // Reducing the size of Articles to the actual number of Articles read.
        articles = new ArrayList<APIArticle>(articles.subList(0, maxArticle));
    }
    /**
     * This method is used to simply return the elaborated List of Articles {@link APISource#articles}
     *
     * @return Article List
     */
    @Override
    public List<APIArticle> getArticles() {
        return articles;
    }
}
