package org.project;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.List;

/**
 * This interface is used to represent a general source of articles.
 */
public interface ArticleSource {
    /**
     * This method is used by classes that needs the articles contained in a generic source.
     *
     * @return List<Article> which is the List of Articles elaborated by the generic source.
     */
    List<? extends Article> getArticles();
}
