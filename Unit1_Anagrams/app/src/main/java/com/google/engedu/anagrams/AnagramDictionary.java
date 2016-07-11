package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 5;
    private static final int MAX_WORD_LENGTH = 10;
    private Random random = new Random(System.currentTimeMillis());
    private ArrayList<String> startList;
    private HashSet<String> wordSet;
    private HashMap <String, ArrayList<String>> lettersToWord;
    private HashMap <Integer, ArrayList<String>> sizeToWords;
    private Integer wordLength = DEFAULT_WORD_LENGTH;
    public AnagramDictionary(InputStream wordListStream) throws IOException {
        startList = new ArrayList<>();
        wordSet = new HashSet<>();
        lettersToWord = new HashMap<>();
        sizeToWords = new HashMap<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            startList.add(word);
            String sortedWord = sortLetters(word);
            wordSet.add(word);

            if(lettersToWord.containsKey(sortedWord))
            {
                lettersToWord.get(sortedWord).add(word);
            }
            else
            {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(word);
                lettersToWord.put(sortedWord, arrayList);
            }
        }
        Iterator it = lettersToWord.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair =(Map.Entry)it.next();
            ArrayList<String> anagrams = (ArrayList)pair.getValue();
            if(anagrams.size() - 1  >= MIN_NUM_ANAGRAMS)
            {
                Integer length = anagrams.get(0).length();
                for(int i = 0; i < anagrams.size(); i++)
                {
                    String aWord = anagrams.get(i);
                    if (sizeToWords.containsKey(length))
                    {
                        sizeToWords.get(length).add(aWord);
                    }
                    else
                    {
                        ArrayList<String> words = new ArrayList<>();
                        words.add(aWord);
                        sizeToWords.put(length, words);
                    }
                }

            }
        }
        Log.d("Anagram", "AnagramDictionary: ");
    }

    public boolean isGoodWord(String word, String base) {

        return wordSet.contains(word) && !word.contains(base);
    }

    public ArrayList<String> getAnagrams(String targetWord) {
        targetWord = sortLetters(targetWord);
        ArrayList<String> result = new ArrayList<String>();
        for(int i = 0; i < startList.size(); i++)
        {
            String orginalWord = startList.get(i);
            String sortedWord = startList.get(i);
            sortedWord = sortLetters(sortedWord);

            if(targetWord.length() == sortedWord.length() && sortedWord.equals(targetWord))
            {
                result.add(orginalWord);
            }
        }
        return result;
    }
    public String sortLetters(String string)
    {
        String retStr = null;
        char []character = string.toCharArray();
        Arrays.sort(character);
        retStr = new String(character);
        return retStr;
    }


    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (char i  = 'a'; i <= 'z'; i++)
        {
            String wordPlusChar = word + i;
            wordPlusChar = sortLetters(wordPlusChar);
            if(lettersToWord.containsKey(wordPlusChar))
            {
                ArrayList<String> angagram = lettersToWord.get(wordPlusChar);
                for(int j = 0; j < angagram.size(); j++)
                {
                    result.add(angagram.get(j));
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord()
    {
        if(wordLength > MAX_WORD_LENGTH)
        {
            wordLength = DEFAULT_WORD_LENGTH;
        }
        ArrayList<String> startList = sizeToWords.get(wordLength);
        if(startList == null)
        {
            wordLength = DEFAULT_WORD_LENGTH;
            startList = sizeToWords.get(wordLength);
        }
        wordLength++;
        int randNum = random.nextInt(startList.size());
        return startList.get(randNum);

        /*for(int i = 0; i < startList.size(); i++)
        {
            String word  = startList.get(randNum);
            String sortWord = sortLetters(word);
            ArrayList<String> anagrams = lettersToWord.get(sortWord);
            if(anagrams.size() >= MIN_NUM_ANAGRAMS)
            {
                wordLength++;
                return word;
            }
            if(randNum + 1 > startList.size() -1)
            {
                randNum = 0;
            }
            else {

                randNum++;

            }
        }
        wordLength++;

        return "stop";
        */
    }
}
