package pro.wordsuncross;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * Created by AbdulfataiAbdulhafiz on 7/22/2018.
 */

public class WordGen {
    private ArrayList<String> mDict = new ArrayList<>();
    private ArrayList<String> mCurrentDict = new ArrayList<>();
    private ArrayList<String> mFilterDict = new ArrayList<>();
    private Context mContext;
    private BufferedReader scan;
    private String temp;
    private int count;
    private boolean filtered;
    public WordGen(Context c){
        this.mContext = c;
        filtered = false;
    }
    public ArrayList<String> getWord(String str) throws Exception{
        mCurrentDict.clear();
        filtered = false;
        try{
            scan = new BufferedReader(new InputStreamReader(mContext.getAssets().open("words_alpha")));
            temp = scan.readLine();
            while(temp != null){
                if(checkWord(temp, str)){
                    mCurrentDict.add(temp);
                }
                temp = scan.readLine();
            }
            scan.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return mCurrentDict;
    }
    public ArrayList<String> getWordWithSize(int i){
        filtered = true;
        count = 0;
        mFilterDict.clear();
        while (count<mCurrentDict.size()) {
            String temp = mCurrentDict.get(count++);
            if(temp.length() == i){
                mFilterDict.add(temp);
            }
        }
        return mFilterDict;
    }
    public ArrayList<String> getWordWithChars(String str){
        filtered = true;
        count = 0;
        mFilterDict.clear();
        while (count<mCurrentDict.size()) {
            String temp = mCurrentDict.get(count++);
            if(temp.indexOf(str) > 0){
                mFilterDict.add(temp);
                System.out.println(temp);
            }
        }
        return mFilterDict;
    }
    public ArrayList<String> getWordWithCharsStart(String str){
        filtered = true;
        count = 0;
        mFilterDict.clear();
        while (count<mCurrentDict.size()) {
            String temp = mCurrentDict.get(count++);
            if(temp.indexOf(str) == 0){
                mFilterDict.add(temp);
                System.out.println(temp);
            }
        }
        return mFilterDict;
    }
    public void setCurrentDictDict(ArrayList<String> arrayList){
        this.mCurrentDict = arrayList;
    }

    public ArrayList<String> getCurrentDictionary(){
        return mCurrentDict;
    }
    public void setFilteredDict(ArrayList<String> arrayList){
        this.mFilterDict = arrayList;
    }
    public ArrayList<String> getFilterDictionary(){
        return mFilterDict;
    }
    public void setFiltered(boolean f){
        this.filtered = f;
    }
    public boolean getFiltered(){
        return filtered;
    }
    public int getSize(){
        return mCurrentDict.size();
    }

    public boolean checkWord(String dictWord, String word){
        int checker = 0;
        String tempWord = word;
        for (int i = 0; i<dictWord.length();i++ ) {
            if(tempWord.indexOf(dictWord.charAt(i)) < 0){
                checker--;
            }else{
                checker++;
                tempWord = tempWord.substring(0, tempWord.indexOf(dictWord.charAt(i)))+" " + tempWord.substring(tempWord.indexOf(dictWord.charAt(i))+1);
            }
        }
        if(checker == dictWord.length()){
            return true;
        }else{
            return false;
        }
    }
     public String getWordAt(int c){
         if(filtered){
             return mFilterDict.get(c);
         }
         return mCurrentDict.get(c);
     }

}
