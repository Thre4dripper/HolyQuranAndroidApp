package com.example.holyquran.Utils;

import android.util.Log;
import android.widget.Toast;

import com.example.holyquran.Activities.ResultActivity;
import com.example.holyquran.Quran;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkRequest {
    private static final String TAG = "NetworkRequest";

    public static List<Quran> getVersesListByChapter(String JSON){
        List<Quran> list=new ArrayList<>();


        StringBuilder verseTranslation,arabic;
        try {
            JSONObject rootObject=new JSONObject(JSON);

            ResultActivity.TOTAL_RECORDS =rootObject.getJSONObject("pagination").getInt("total_pages");
            ResultActivity.CURRENT_PAGE=rootObject.getJSONObject("pagination").getInt("current_page");

            JSONArray verses=rootObject.getJSONArray("verses");
            for(int i=0;i<verses.length();i++){
                verseTranslation=new StringBuilder();
                arabic=new StringBuilder();
                JSONObject currentVerse=verses.getJSONObject(i);
                JSONArray words=currentVerse.getJSONArray("words");

                verseTranslation.append(currentVerse.getJSONArray("translations").getJSONObject(0).getString("text"));
                for(int j=0;j<words.length()-1;j++){
                    if(!words.getJSONObject(j).getJSONObject("translation").getString("text").equals("null")) {
                        arabic.append(words.getJSONObject(j).getJSONObject("transliteration").getString("text"));
                        arabic.append(" ");
                    }
                }

                String language=currentVerse.getJSONArray("translations").getJSONObject(0).getString("resource_id");

                if(language.equals("122") && verseTranslation.toString().contains("<")){
                    language=fixHindiTranslation(verseTranslation.toString());
                    list.add(new Quran(arabic.toString(),language,null,null,0));
                }
                else
                list.add(new Quran(arabic.toString(),verseTranslation.toString(),null,null,0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String fixHindiTranslation(String translation){
        return translation.substring(0,translation.indexOf("<"))+translation.substring(translation.lastIndexOf(">")+1);
    }

    public static List<Quran> getChaptersFromJSON(String JSON){
        List<Quran> list=new ArrayList<>();

        try {
            JSONObject rootObject=new JSONObject(JSON);

            JSONArray chaptersArray=rootObject.getJSONArray("chapters");

            int verseCount;
            for(int i=0;i<chaptersArray.length();i++)
            {
                verseCount=chaptersArray.getJSONObject(i).getInt("verses_count");
                list.add(new Quran(null,null,chaptersArray.getJSONObject(i).getString("name_complex")+
                        "\t("+chaptersArray.getJSONObject(i).getJSONObject("translated_name").getString("name")+")",null,verseCount));
            }

            //System.out.println(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Quran> getSpecificVerseFromJSON(String JSON){
        List<Quran> list=new ArrayList<>();

        StringBuilder verseTranslation,arabic;
        try {
            JSONObject rootObject=new JSONObject(JSON);
            JSONObject verse=rootObject.getJSONObject("verse");

            ResultActivity.TOTAL_RECORDS =1;
            ResultActivity.CURRENT_PAGE=1;

            verseTranslation=new StringBuilder();
            arabic=new StringBuilder();
            verseTranslation.append(verse.getJSONArray("translations").getJSONObject(0).getString("text"));

            JSONArray words=verse.getJSONArray("words");

            for(int i=0;i<words.length();i++){
                arabic.append(words.getJSONObject(i).getJSONObject("transliteration").getString("text"));
                arabic.append(" ");
            }

            String language=verse.getJSONArray("translations").getJSONObject(0).getString("resource_id");

            if(language.equals("122") && verseTranslation.toString().contains("<")){
                language=fixHindiTranslation(verseTranslation.toString());
                list.add(new Quran(arabic.toString(),language,null,null,0));
            }
            else
                list.add(new Quran(arabic.toString(),verseTranslation.toString(),null,null,0));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

}
