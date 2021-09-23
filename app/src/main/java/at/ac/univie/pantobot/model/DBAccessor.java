package at.ac.univie.pantobot.model;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Retrieves information from assets/data.json and Stores it into Topic model class
 *
 * Note: URL can be null (all topics with language:en have no url),
 * forecast_pos marked -111 represents NaN in original data
 */

public class DBAccessor {

    private Context context;
    private String jsonString = null;
    private JSONArray jsonArr = null;

    public DBAccessor(Context context) {
        this.context = context;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");

            jsonArr = new JSONArray(jsonString);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search through JSONString, that was obtained in Constructor
     *
     * @param search_field   Selected Field, such as topic, domain, platform, related_topic, ...
     * @param search_content Specify text to search for, for instance APPLE MUSIC(topic), energy(domain) and so on.
     * @return ArrayList of topics, those content matched the search input.
     */
    public ArrayList<Topic> searchDB(String search_field, String search_content) {
        search_content = search_content.replace("\"", "");
        JSONObject jsonObj = null;
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<Topic> data = new ArrayList<>();
        System.out.println("Searching for "+search_content+" in "+search_field+" started");
        try {

            for (int i = 0; i < jsonArr.length(); i++) {
                jsonObj = jsonArr.getJSONObject(i);

                JSONArray related_arr = jsonObj.getJSONArray("related_topic");
                JSONArray subtopic_arr = jsonObj.getJSONArray("subTopic");


                if (search_field.equalsIgnoreCase("related_topic")) {
                    for (int j = 0; j < related_arr.length(); j++) {
                        JSONObject related_inside = related_arr.getJSONObject(j);
                        if (search_content.equalsIgnoreCase(related_inside.getString("topic"))) {
                            data.add(retrieveTopicData(i));
                        }
                    }
                }

                if (search_field.equalsIgnoreCase("subTopic")) {
                    for (int j = 0; j < subtopic_arr.length(); j++) {
                        if (search_content.equalsIgnoreCase(subtopic_arr.get(0).toString())) {
                            data.add(retrieveTopicData(i));
                        }
                    }
                }

                if (search_content.equalsIgnoreCase(jsonObj.getString(search_field))) {
                    data.add(retrieveTopicData(i));
                }
            }

            System.out.println("data size " + data.size());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }


    /**
     * Retrieve all information on a specific json object
     *
     * @param jsonArrIndex index of that specific object
     * @return A Topic object with all information, retrieved from the File/Database
     */
    private Topic retrieveTopicData(int jsonArrIndex) {
        Topic topic = null;

        try {
            JSONObject jsonObj = jsonArr.getJSONObject(jsonArrIndex);

            JSONArray related_arr = jsonObj.getJSONArray("related_topic");
            JSONArray subtopic_arr = jsonObj.getJSONArray("subTopic");
            JSONArray url_arr = null;
            if (jsonObj.has("url")) {
                url_arr = jsonObj.getJSONArray("url");
            }

            topic = new Topic();

            topic.setId(jsonObj.getString("id"));
            topic.setTopic(jsonObj.getString("topic"));
            topic.setInfluence(jsonObj.getDouble("influence"));
            topic.setSentiment_pos(jsonObj.getDouble("sentiment_pos"));
            topic.setType(jsonObj.getString("type"));
            topic.setDomain(jsonObj.getString("domain"));
            topic.setPlatform(jsonObj.getString("platform"));
            topic.setRegion(jsonObj.getString("region"));
            topic.setLanguage(jsonObj.getString("language"));
            topic.setTimestamp(jsonObj.getInt("timestamp"));
            topic.setForecast(jsonObj.getDouble("forecast"));

            ArrayList<Topic.Related> relatedList = new ArrayList<>();
            for (int j = 0; j < related_arr.length(); j++) {
                JSONObject related_inside = related_arr.getJSONObject(j);
                Topic.Related related = new Topic.Related();
                related.setTopic(related_inside.getString("topic"));
                related.setWeight(related_inside.getInt("weight"));
                relatedList.add(related);
            }
            topic.setRelated_topics(relatedList);

            ArrayList<String> subTopicList = new ArrayList<>();
            for (int j = 0; j < subtopic_arr.length(); j++) {
                subTopicList.add(subtopic_arr.getString(j));
            }
            topic.setSubTopic(subTopicList);

            ArrayList<String> urlList = new ArrayList<>();
            if (url_arr != null) {
                for (int j = 0; j < url_arr.length(); j++) {
                    try{
                        urlList.add(url_arr.getString(j));
                    }catch (NullPointerException e){
                        System.out.println(e.getMessage());
                    }

                }
            }
            topic.setUrl(urlList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return topic;
    }

}
