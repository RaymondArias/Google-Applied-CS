package com.google.engedu.ghost;
/*
PAIR PROGRAMMING PROJECT
Raymond Arias & Connor Haskins
7/23/2016
Ghost Text Game
Google Android Applied CS
 */

import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    // singleton class, so we do not need to reload the dictionary every time
    // the activity is recreated
    private static SimpleDictionary sInstance;

    private ArrayList<String> words;
    private Random random = new Random();

    // function to access the singleton
    public static SimpleDictionary get(InputStream wordListStream) throws IOException {
        // if the dictionary is uninitialized
        if(sInstance == null){
            // initialize the dictionary
            sInstance = new SimpleDictionary(wordListStream);
        }
        // else return the already initialized dictionary
        return sInstance;
    }

    // constructor
    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        // if the word isn't at least length MIN_WORD_LENGTH
        if(word.length() < MIN_WORD_LENGTH){
            // it is not a valid word, return false
            return false;
        }
        // else, check if that word is within the dictionary
        return words.contains(word);

    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        // if the prefix is empty, choose a random word
        if(prefix.isEmpty()){
            return words.get(random.nextInt(words.size()));
        }

        // binary search to find a valid word using prefix
        int left = 0;
        int right = words.size() - 1;
        int mid = right / 2;
        while(left < right) {
            // access the current middle word
            String itWord = words.get(mid);

            // if the word is greater than the length of the prefix
            // and the word has the prefix argument as a prefix
            if(itWord.length() > prefix.length() &&
                    itWord.substring(0,prefix.length()).equals(prefix)){
                // it is a valid word, and we can return it
                return itWord;
            }

            // PREFIX CANNOT EQUAL ITWORD, THE COMPUTER WOULD HAVE ALREADY DISCOVERED THIS AND WON

            // else we need to either search between (mid,right] or [left,mid)
            // if iterative word is lexicographically after the prefix
            if(itWord.compareTo(prefix) > 0){
                // we need to search between [left,mid)
                right = mid - 1;
            } else {
                // otherwise, we need to search between (mid,right]
                left = mid + 1;
            }
            // mid will always be halfway between left and right
            mid = (right + left) / 2;
        }
        // if no valid words are found with the prefix, return null
        return null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        // if prefix is empty, choose random word
        if(prefix.isEmpty()){
            return words.get(random.nextInt(words.size()));
        }

        // user is entering the odd characters if the fragment is currently an odd length
        // during the computer's turn
        boolean userIsOdd = ((prefix.length() % 2) == 1);

        // arraylists to hold the odd and even length strings
        ArrayList<String> oddStrings = new ArrayList<>();
        ArrayList<String> evenStrings = new ArrayList<>();

        // get the index of any word with the prefix argument as a prefix
        int prefixIndex = getAnyPrefixIndex(prefix);

        // getAnyPrefixIndex returns -1 if no word was found
        if(prefixIndex != -1) {

            // linear search left to find all words with prefix and add to ArrayLists based on length
            int leftIndex = prefixIndex;
            String leftWord = words.get(leftIndex);
            // while the word we are looking at still has the prefix and is longer than the prefix...
            while (leftWord.length() > prefix.length() &&
                    leftWord.substring(0, prefix.length()).equals(prefix)) {
                // add to even Strings if the length is even
                if ((leftWord.length() % 2) == 0) {
                    evenStrings.add(leftWord);
                }
                // add to odd Strings if the length is odd
                else {
                    oddStrings.add(leftWord);
                }
                // decrement left index
                leftIndex--;
                // if leftIndex is less then zero, we need to break
                if (leftIndex < 0) {
                    break;
                }
                // acquire word at current leftIndex for the next loop
                leftWord = words.get(leftIndex);
            }


            // linear search right to find all words with prefix and add to ArrayLists based on length
            int maxIndex = words.size() - 1;
            int rightIndex = prefixIndex + 1;
            String rightWord = words.get(rightIndex);
            // while the word we are looking at still has the prefix and is longer than the prefix...
            while (rightWord.length() > prefix.length() &&
                    rightWord.substring(0, prefix.length()).equals(prefix)) {
                // add to even Strings if the length is even
                if ((rightWord.length() % 2) == 0) {
                    evenStrings.add(rightWord);
                }
                // add to odd Strings if the length is even
                else {
                    oddStrings.add(rightWord);
                }

                // increment right index
                rightIndex++;
                // if right index is larger than the dictionary size
                if (rightIndex > maxIndex) {
                    break;
                }
                // acquire the next word for the next iteration
                rightWord = words.get(rightIndex);
            }

            // if the user is odd, we would prefer words that are odd and would cause the user
            // to complete a given word. Allowing the computer to win.
            // We want to pull from the oddStrings if the user is odd and the list is not empty
            if (userIsOdd && !oddStrings.isEmpty()) {
                return oddStrings.get(random.nextInt(oddStrings.size()));
            }
            // otherwise, pull a word from the even strings
            else if (!evenStrings.isEmpty()) {
                return evenStrings.get(random.nextInt(evenStrings.size()));
            }
        }

        return null;
    }

    public int getAnyPrefixIndex(String prefix){
        // binary search for aqcuiring any word with a given prefix
        int left = 0;
        int right = words.size() - 1;
        int mid = right / 2;
        while(left < right) {
            // get word at mid point
            String itWord = words.get(mid);
            // if the word is longer than the prefix and contains the prefix
            if(itWord.length() > prefix.length() &&
                    itWord.substring(0,prefix.length()).equals(prefix)){
                // return the index of that word
                return mid;
            }

            // otherwise we must check the left or right half of subtree
            // proceed to left half of subtree if the mid point word is lexicographically after prefix
            if(itWord.compareTo(prefix) > 0){
                right = mid - 1;
            }
            // proceed to right half of subtree if the mid point word is lexicographically before prefix
            else {
                left = mid + 1;
            }
            // mid is always halfway point between left and right
            mid = (right + left) / 2;
        }
        // return -1 if no word is found
        return -1;
    }
}
