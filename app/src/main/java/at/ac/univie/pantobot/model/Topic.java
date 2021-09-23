package at.ac.univie.pantobot.model;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Topic implements Serializable{

    private String id;
    private String topic;
    private double influence;
    private double sentiment_pos;
    private String type;
    private String domain;
    private String platform;
    private String region;
    private String language;
    private int timestamp;
    private double forecast;
    private ArrayList<Related> related_topics=new ArrayList<>();
    private ArrayList<String> subTopic = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();

    public static class Related implements Serializable {
        private String topic;
        private int weight;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public double getInfluence() {
        return influence;
    }

    public void setInfluence(double influence) {
        this.influence = influence;
    }

    public double getSentiment_pos() {
        return sentiment_pos;
    }

    public void setSentiment_pos(double sentiment_pos) {
        this.sentiment_pos = sentiment_pos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public double getForecast() {
        return forecast;
    }

    public void setForecast(double forecast) {
        this.forecast = forecast;
    }

    public ArrayList<Related> getRelated_topics() {
        return related_topics;
    }

    public void setRelated_topics(ArrayList<Related> related_topics) {
        this.related_topics = related_topics;
    }

    public ArrayList<String> getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(ArrayList<String> subTopic) {
        this.subTopic = subTopic;
    }

    public ArrayList<String> getUrl() {
        return this.url;
    }

    public void setUrl(ArrayList<String> url) {
        this.url = url;
    }

    public String toString(){
        String temp ="";
        for(Field field:this.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                temp+=field.getName()+"="+field.get(this)+";";
            }catch (IllegalAccessException e){
                System.out.println(e.getMessage());
            }

        }
        return temp;
    }

    public Topic addTopic(String id, String topic, String domain, String language, ArrayList<String> url) {
        Topic newTopic = new Topic();
        newTopic.setId(id);
        newTopic.setTopic(topic);
        newTopic.setDomain(domain);
        newTopic.setLanguage(language);
        newTopic.setUrl(url);

        return newTopic;
    }
}
