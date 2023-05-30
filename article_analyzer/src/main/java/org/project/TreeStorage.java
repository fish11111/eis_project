package org.project;

import java.util.*;

/**
 * This class is used to keep the tokens associated with their frequency in insertion operations.
 * It inserts tokens and orders them based on their frequencies. It has few more features
 * such as removing all tokens, printing them, printing a given token frequency.
 */
public class TreeStorage implements TokensStorage{
    /** This map is used to store the tokens using them as indexes and using the associated values to store their frequency.*/
    private HashMap<String, Integer> tokens = null;

    /**
     * This constructor initializes an empty hash map {@link TreeStorage#tokens}.
     */
    public TreeStorage() {
        // This initializes the map containing the entries of tokens(String) as indexes and their frequency(int) as values.
        tokens = new HashMap<String, Integer>();
    }

    /**
     * This method allows to print all the tokens with their "frequency".
     */
    public void printTokens(){
        Iterator<Map.Entry<String,Integer>> iter = tokens.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,Integer> pair = iter.next();
            System.out.println(pair.getKey()+" "+pair.getValue());
        }
    }

    /**
     * This method print the frequency of the given string (0 if not contained).
     *
     * @param str This string is searched inside the map.
     */
    public void printFrequency(String str) {
        int frequency = 0;
        frequency = tokens.getOrDefault(str,0);
        System.out.println(frequency);
    }

    /**
     * This method removes the tokens.
     */
    public void removeTokens(){
        tokens = new HashMap<String, Integer>();
    }

    /**
     * This method inserts a new list of tokens in the hashmap.
     *
     * @param list list of tokens to insert in the storage.
     */
    @Override
    public void enterTokens(List<String> list) {
        // Checks document size.
        if(list.size() == 0){
            return;
        }
        // This variable stores the frequency of the tokens to insert in the map
        int value = 0;
        for (String c : list) {
            // This line either gets the previous frequency of the token (if present) or 0.
            value = tokens.getOrDefault(c, 0);
            // Inserts the token incrementing its frequency.
            tokens.put(c, value + 1);
        }
    }

    /**
     * This method returns a Set of Ordered entries of Integers and String Lists.
     *
     * @param maxSize which is the max quantity of tokens present in the returned map.
     * @return a TreeMap which contains the entries of tokens values indexed with their frequency.
     */
    @Override
    public Set<Map.Entry<Integer, List<String>>> getOrderedTokens(int maxSize){

        // Initializing the new Map with reverse order.
        SortedMap<Integer, List<String>> reverseMap = new TreeMap<Integer, List<String>>(Collections.reverseOrder());
        Iterator<Map.Entry<String,Integer>> iter = tokens.entrySet().iterator();
        Map.Entry<String,Integer> pair;

        /* Building the reverseMap iterating through the HashMap used in this class to store frequency-tokens entries. */
        List<String> stringList;
        while(iter.hasNext()){
            pair = iter.next();
            // This checks if the list is already contained.
            stringList = reverseMap.get(pair.getValue());
            // If there was no List with that specific frequency a new one is created and initialized with the current token.
            if(stringList == null){
                stringList = new ArrayList<String>();
                stringList.add(pair.getKey());
                reverseMap.put(pair.getValue(),stringList);
            }else{
                // If a List with that specific frequency was present then it adds the current token to it.
                reverseMap.get(pair.getValue()).add(pair.getKey());
            }
        }

        /* Reducing the token size to maxSize or smaller than the number of tokens entered within the reverse map. */

        // This variable is used to keep track of the number of tokens encountered.
        int counter = 0;
        Iterator<Map.Entry<Integer, List<String>>> listIter = reverseMap.entrySet().iterator();
        Map.Entry<Integer, List<String>> listPair;
        // This checks if it already reached the max possible number of tokens.
        boolean max = false;
        while(listIter.hasNext()){
            listPair = listIter.next();
            if(!max) {
                // Orders the list of strings contained in each entry by lexicographical order.
                Collections.sort(listPair.getValue(), Collections.reverseOrder());
                // Adding the dimension of the current String List to the counter.
                counter += listPair.getValue().size();
                if (counter > maxSize) {
                    int index = counter - maxSize;
                    // Removing eventual exceeding tokens from the List.
                    while (index > 0) {
                        index--;
                        listPair.getValue().remove(index);
                    }
                    max = true;
                    counter = maxSize;
                }
            }else {
                //removes the exceeding Lists if already reached the max number of tokens.
                listIter.remove();
            }
        }

        // Returning the set of ordered entries.
        return reverseMap.entrySet();
    }
}
