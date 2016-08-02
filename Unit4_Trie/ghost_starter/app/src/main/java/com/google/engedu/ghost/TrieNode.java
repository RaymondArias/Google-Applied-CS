/*
PAIR PROGRAMMING PROJECT
Raymond Arias & Connor Haskins
7/30/2016
Ghost Text Game
Google Android Applied CS
 */

package com.google.engedu.ghost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TrieNode {
    private boolean isWord;
    private HashMap<Character, TrieNode> children;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String word) {
        // start at the root of the entire trie
        TrieNode currentRoot = this;
        // while the while there is still a letter left to enter
        for(int i = 0; i < word.length(); i++){
            // if the current root already contains a subtrie using the current char as key
            if(currentRoot.children.containsKey(word.charAt(i))){
                // climb down the trie
                currentRoot = currentRoot.children.get(word.charAt(i));
            } else { // else created a new trie
                TrieNode newTrieNode = new TrieNode();
                // put that trie in the children hashmap using the current char as a key
                currentRoot.children.put(word.charAt(i), newTrieNode);
                // climb down the trie to the newly created trie
                currentRoot = newTrieNode;
            }
        }
        // at the end of adding all the trienodes, indicate that the last node represents
        // the end of a word.
        currentRoot.isWord = true;
    }


    public boolean isWord(String word) {
        // start the currentRoot at the root of the entire trie
        TrieNode currentRoot = this;
        // while there are still letters in the string
        for(int i = 0; i < word.length(); i++) {
            // if the current char exists as a key in the hashmap
            if(currentRoot.children.containsKey(word.charAt(i))) {
                // set the current node to that child node
                currentRoot = currentRoot.children.get(word.charAt(i));
            } else {
                // else the string is not a word, and return false
                return false;
            }
        }

        // return true if the last node traversed too using the string is a word
        return currentRoot.isWord;
    }

    public String getAnyWordStartingWith(String prefix) {
        // if prefix is empty, choose random char
        if(prefix.isEmpty()){
            Random rand = new Random();
            int randInt = rand.nextInt(26) + 97;
            prefix += (char)randInt;
            return prefix;
        }

        // iterate through the trie to get to the TrieNode at the end of prefix
        TrieNode currentRoot = this;
        for(int i = 0; i < prefix.length(); i++) {
            if(currentRoot.children.containsKey(prefix.charAt(i))) {
                currentRoot = currentRoot.children.get(prefix.charAt(i));
            } else {
                return null;
            }
        }

        // while the currentRoot is not a word
        while(!currentRoot.isWord) {
            // get the keys of the hashmap
            List<Character> keys = new ArrayList<Character>(currentRoot.children.keySet());
            // get the first child and assign it to current root
            currentRoot = currentRoot.children.get(keys.get(0));
            // add the key to our prefix string
            prefix += keys.get(0);
        }

        // return prefix string that now represents a word in the Trie
        return prefix;
    }

    public String getGoodWordStartingWith(String prefix) {
        // random used for empty and alternate version of function (commented out)
        Random rand = new Random();

        // if prefix is empty, choose random char
        if(prefix.isEmpty()){
            int randInt = rand.nextInt(26) + 97;
            prefix += (char)randInt;
            return prefix;
        }

        // iterate through the trie to get to the TrieNode at the end of prefix
        TrieNode currentRoot = this;
        for(int i = 0; i < prefix.length(); i++) {
            if(currentRoot.children.containsKey(prefix.charAt(i))) {
                currentRoot = currentRoot.children.get(prefix.charAt(i));
            } else {
                return null;
            }
        }

        // if the prefix is greater than 2, there is a chance to win at current length + 2
        // (The game indicates that no words less than 4 can determine a winner)
        if(prefix.length() > 2){
            // character to represent the best child key currently found
            // '\0' is a special key used to determine whether the current node has no children
            // or the computer must choose a losing word
            char bestKeySoFar = '\0';
            // integer to represent the child with the most potential winning children TrieNodes
            int mostWinningWords = 0;

            // iterate through all of the children
            Iterator it = currentRoot.children.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                TrieNode currentNode = (TrieNode)pair.getValue();
                // if the child is not a node
                if(!currentNode.isWord) {
                    // get the amound of winningWords stemming from that child
                    int currentWinningWords = getWinningDescendants(currentNode);
                    // if that amount is greater than the previous best
                    if (currentWinningWords >= mostWinningWords) {
                        // update bestKeySoFar to the new best key found
                        bestKeySoFar = (char) pair.getKey();
                        // update the mostWinningWords to the new current winning words
                        mostWinningWords = currentWinningWords;
                    }
                }
            }

            // if the current node is still our special character
            if(bestKeySoFar == '\0'){
                // the current prefix leads to no words, so return null
                if(currentRoot.children.isEmpty()){
                    return null;
                }
                // the computer must choose a completed word and lose
                List<Character> keys = new ArrayList<Character>(currentRoot.children.keySet());
                return prefix + keys.get(0);
            }

            // return the prefix + bestKeySoFar
            // it does not return a completed word, but returns exactly as much characters as the
            // GhostActivity needs
            return prefix + bestKeySoFar;
        }

        // return getAnyWord if there is no way to win on the next turn
        return getAnyWordStartingWith(prefix);

    }

    /*
    winning descendants represents words that are +2n length from the games current state
    if the computer is going to be adding a letter that will created an odd length prefix
    the computer should work towards words that are of even length, so the user is forced
    to complete a word

    the function is always called on TrieNodes that are +2n+1 length from the current state of
    the game, both initially and recursively
    */
    private int getWinningDescendants(TrieNode node){
        // number of winning descendants is set to zero
        int winningDescendants = 0;

        // set up to iterate through all children in the hashmap
        Iterator it = node.children.entrySet().iterator();
        // while there are children
        while (it.hasNext()) {
            // get the key value pair
            Map.Entry pair = (Map.Entry) it.next();
            // create a TrieNode called currentNode and assign it the value of the child
            TrieNode currentNode = ((TrieNode) pair.getValue());
            // if the child is a word, increment winning descendants
            if (currentNode.isWord) {
                winningDescendants++;
            } else { // otherwise iterate through the children of the current node
                Iterator it2 = currentNode.children.entrySet().iterator();
                while(it2.hasNext()){
                    Map.Entry pair2 = (Map.Entry) it2.next();
                    // add the winning descendents of that child to the total by recursively
                    // calling getWinningDescendants on the current node, which is always
                    // +2n+1 length from the current state of the game
                    winningDescendants += getWinningDescendants((TrieNode)pair.getValue());
                }
            }
        }

        // return number of winning descendants
        return winningDescendants;
    }
}
