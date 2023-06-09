package org.project;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class allows to set the url for a "The Guardian" API endpoint with some given parameters
 * (tags, query, page, page size, api key, base url ) for the API request.
 * It is used to keep track of the parameters and to get a new URL.
 *
 * Note: this class also do some coherence checks on the given parameters.
 */
public final class URLSetter {
    /** Representation of a URL that is built in this class.*/
    private URL URL;
    /** Value of the API key of the media Group API endpoint.*/
    private final String APIKey;
    /** Queries used to search among the set of elements in the response.*/
    private final String[] queries;
    /** Fields used to determine the specific set of element given in the response.*/
    private final String[] tags;
    /** Base URL without any specification.*/
    private final String BASE_URL = "https://content.guardianapis.com/search?";
    /** Current page, a generic group of elements in the response.*/
    private int page;
    /** Number of articles that should be included in the API response.*/
    private final int pageSize;

    /**
     * Takes all the information needed to create a valid URL based on the passed parameters .
     *
     * @param pageSize sets {@link URLSetter#pageSize}
     * @param APIKey sets {@link URLSetter#APIKey}
     * @param page sets {@link URLSetter#page}
     * @param queries sets {@link URLSetter#queries}
     * @param tags sets {@link URLSetter#tags}
     * @throws IllegalArgumentException if there is no coherence in pageSize and page variables.
     */
    public URLSetter( String APIKey, int page, int pageSize, String[] queries , String[] tags) throws IllegalArgumentException, MalformedURLException{
        // Note: API key cannot be empty string
        if(!APIKey.equals("")){
            this.APIKey = APIKey;
        }else{
            throw new IllegalArgumentException();
        }
        this.tags = tags;
        // Note: page number must be less or equals to page size number.
        if(page > pageSize){
            throw new IllegalArgumentException("Page number must be lower the page size!");
        }
        // Note: page size and page numbers must be positive.
        if(page > 0) {
            this.page = page;
        }else{
            throw new IllegalArgumentException("Page number must be positive!");
        }
        if(pageSize > 0) {
            this.pageSize = pageSize;
        }else{
            throw new IllegalArgumentException("Page size number must be positive!");
        }
        this.queries = queries;
        // Trying to build the URL.
        buildUrl();
    }

    /** Used by the URLSetter to build and store the final {@link URLSetter#URL}.  */
    private void buildUrl() throws MalformedURLException{
        /* Setting the parameters. */
        // Setting a base URL.
        String URLString = BASE_URL;
        // Setting page size value.
        URLString += "page-size=" + pageSize;
        // Setting page value.
        URLString += "&page=" + page;
        // Setting relevant fields in URL.
        URLString += "&show-fields=body,headline,wordcount";
        // Setting tags and queries.
        if(tags.length != 0) {
            URLString += "&tag=";
            URLString += tags[0];
            for (int i = 1; i < tags.length; i++) {
                tags[i] = tags[i].replace(" ", "");
                URLString += "/"+tags[i];
            }
        }
        if(queries.length != 0) {
            URLString += "&q=";
            for (int i = 0; i < queries.length; i++) {
                queries[i] = queries[i].replace(" ", "");
                if (i == (queries.length - 1)) {
                    URLString += queries[i];
                } else {
                    URLString += queries[i] + "%20AND%20";      //This is used to specify that all queries need to be present.
                }
            }
        }
        // Setting APIKey.
        URLString += "&api-key=" + APIKey;

        URL = new URL(URLString);
    }

    /**
     * Increment page number to show the next elements keeping the same parameter.
     */
    public void incrementPage() throws MalformedURLException,IllegalArgumentException{
        // Checking for coherence with page size(max possible value of page).
        if((page+1) > pageSize){
            throw new IllegalArgumentException("Page number must be less then the page size!");
        }
        page++;
        buildUrl();
    }

    /**
     * Returns the elaborated URL with all the given specifications.
     *
     * @return {@link URLSetter#URL} which is the baseURL completed.
     */
    public URL getURL(){
        return URL;
    }
}
