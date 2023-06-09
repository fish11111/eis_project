package org.project;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is a wrapper that contain a tokenizer ("core NLP") from the package edu.stanford.mlp.pipeline.
 * It allows to tokenize a given string of text into its words. If needed, it checks the presence of punctuation
 * and common words, eventually returning a lexically/frequency ordered version to iterate through.
 */
public final class Tokenizer {
    /** Pipeline which divides the text processing into phases (here only one is used). */
    private StanfordCoreNLP pipeline = null;
    /** Enables class methods to take punctuation and current words into account.*/
    private boolean checks = false;
    /** Allows the methods to know which words should be considered when checking for common words.*/
    private List<String> commonWords = null;
    /**Stores the tokens and is capable of returning them in an ordered set. */
    private TokensStorage storage;
    /** Contains the file where {@link Tokenizer#commonWords} are stored.*/
    private final String FILENAME = "resources" + File.separator + "blacklist" + File.separator + "words.txt";
    /** Contains the regex for common punctuation plus few more characters that
     *  are used to split the tokens (see {@link TreeStorage#getOrderedTokens(int)}).*/
    private final String REGEX = "[\\p{Punct}\\s.!?”“–—’‘'’…+1234567890-]";

    /**
     * This constructor is used to set:
     *  whether to do checks on the tokens or not ({@link Tokenizer#checks});
     *  common word ({@link Tokenizer#commonWords});
     *  the pipeline ({@link Tokenizer#pipeline});
     *  the storage class {@link Tokenizer#storage}.

     * @param checks checks if there are common words or punctuation in the tokens.
     * @param storage a structure capable of storing tokens or returning them in an ordered set.
     */
    public Tokenizer(boolean checks, TokensStorage storage) {
        // Configures the internal tokenizer core NLP so that it won't print warning messages.
        RedwoodConfiguration.current().clear().apply();

        // Setting the tokens storage
        this.storage = storage;

        //SETTING PIPELINE PROPERTIES

        // This is a variable that stores the properties of the core NLP pipeline.
         Properties properties = new Properties();

        // This sets the pipeline to only tokenize the processed string.
        properties.setProperty("annotators", "tokenize");

        // This sets the pipeline algorithm to neural algorithm.
        properties.setProperty("coref.algorithm", "neural");

        // This removes the unrecognized tokens (some Unicodes are not processable).
        properties.setProperty("tokenize.options", "untokenizable=noneDelete");

        // This initializes the pipeline.
        pipeline = new StanfordCoreNLP(properties);

        // This sets the possibility to do checks on the processed document.
        this.checks = checks;

        // Assigning values to the variable that store common words.
        if(this.checks){
            // This initializes the commonWords String List.
            commonWords = new ArrayList<String>();
            try{
                // Reading the common words from the file at FILENAME address.
                BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
                String word = "";
                while((word = reader.readLine()) != null){
                    commonWords.add(word);
                }
            }catch(IOException e){
                System.out.println("Wordlist not found not doing checks!");
                this.checks = false;
            }
        }
    }


    /**
     * This method is used by this class to insert tokens in the {@link Tokenizer#storage}.
     *
     * @param str which is the string containing text to add to the class variable to obtain a bigger set of tokens.
     * @param searched which is a list of all the queries not to check on when check is activated.
     */
    public void tokenize(String str, List<String> searched){
        // Save string tokens in a list (without duplicates).
        // CoreDocument: Wrapper built around an annotation representing a document.
        CoreDocument document = pipeline.processToCoreDocument(str);

        /*
        * The following lines combine multiple operations:
        * It first converts each element(coreLabel) of the document produced by coreNLP tokenization into a string of lower case characters.
        *
        * Then, the string is split using regex, this produces an array of Strings. ("split(REGEX)")
        *
        * At this point it converts this array into a List using "collect(Collectors.toList()".
        *
        * Multiple associations between a coreLabel and all the inner tokens obtained from the split operation are considered. ("flatMap(List::stream)")
        *
        * The final array containing all the inner tokens of coreLabels is converted into a list.
        *
        * NOTE: Duplicates are eliminated.
         */
        List<String> list = document.tokens().stream().map(coreLabel -> Arrays.stream(coreLabel.toString().toLowerCase().split(REGEX))
                .collect(Collectors.toList())).flatMap(List::stream).distinct().collect(Collectors.toList());
        // Checks if the element in the list is not punctuation, a common word or an empty string.
        if(checks) {
            Iterator<String> iter = list.iterator();
            String elem;
            while (iter.hasNext()) {
                elem = iter.next();
                if ((elem.length() < 3 | Pattern.matches(REGEX, elem) | elem.equals("") | commonWords.contains(elem) ) & !searched.contains(elem)) {
                    iter.remove();
                }
            }
        }
        //Inserts tokens in the tokens storage.
        storage.enterTokens(list);
    }
}
