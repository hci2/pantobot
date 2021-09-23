package at.ac.univie.pantobot.controller;

import ai.api.model.AIResponse;
import android.util.Log;
import android.widget.Toast;
import at.ac.univie.pantobot.model.DBAccessor;
import at.ac.univie.pantobot.model.Topic;
import at.ac.univie.pantobot.view.HomeActivity;
import com.google.gson.JsonElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class ResultPresenter {

    public static HomeActivity homeActivity;
    public static ArrayList<Topic> lastResult;

    public static ArrayList<Integer> domainCount = new ArrayList<Integer>();
    public static ArrayList<String> domainNames = new ArrayList<String>();

    /**
     * Handler for the FilterTopics action
     * @param aiResponse AIResponse the Response we received
     * @param homeActivity HomeActivity the HomeActivity
     */
    static void handleReturnFilterTopics(AIResponse aiResponse, HomeActivity homeActivity) {
        ArrayList<Topic> filteredResults = new ArrayList<Topic>();
        String filter = "!!!NOTHINGFOUND!!!";
        String type = "";
        if(lastResult==null){
            homeActivity.addMsg("Nothing to filter", 1);
            return;
        }
        try{
            type =aiResponse.getResult().getParameters().get("filtertype").toString().replace("\"", "");
        } catch (Exception e){
            homeActivity.addMsg("An unexpected Error occured", 1);
            homeActivity.addMsg(e.getMessage(), 1);
        }
        switch (type){
            case "keyword":{
                try{
                    filter =aiResponse.getResult().getParameters().get("keyword").toString().replace("\"", "");
                } catch (Exception e){
                    filter =aiResponse.getResult().getParameters().get("any").toString().replace("\"", "");
                }
                break;
            }
            case "year":{
                filter ="timestamp="+aiResponse.getResult().getParameters().get("year").toString().replace("\"", "")+";";
                break;
            }
            case "region":{
                filter ="region="+aiResponse.getResult().getParameters().get("region").toString().replace("\"", "")+";";
                break;
            }
            case "language":{
                filter ="language="+aiResponse.getResult().getParameters().get("language").toString().replace("\"", "")+";";
                break;
            }
            case "domain":{
                filter ="domain="+aiResponse.getResult().getParameters().get("domain").toString().replace("\"", "")+";";
                break;
            }
            case "platform":{
                filter ="platform="+aiResponse.getResult().getParameters().get("platform").toString().replace("\"", "")+";";
                break;
            }
        }

        for (Topic t : lastResult) {
            if(t.toString().contains(filter)){
                filteredResults.add(t);
            }
        }
        lastResult = filteredResults;
        homeActivity.addMsg("Filtered down to " + filteredResults.size() + " topics", 1);
        if(filteredResults.size()>0)
            showTopicsTopic(filteredResults, homeActivity);
    }

    /**
     * Handler for the returnListDomain action
     * @param aiResponse AIResponse the Response we received
     * @param homeActivity HomeActivity the HomeActivity
     */
    static void handleReturnListDomain(AIResponse aiResponse, HomeActivity homeActivity) {
        String domain="";
        try{
            //check if there are params and extract them to find entity domain and its value
            final HashMap<String, JsonElement> params = aiResponse.getResult().getParameters();
            if (params != null && !params.isEmpty()) {
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    //Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if(entry.getKey().equals("domain")){
                        domain=entry.getValue().getAsString();
                    }
                }
            } else{
                throw new Exception();
            }

        } catch (Exception e){
            Log.e(ActionHandler.class.getName(),e.toString());

            String excuse="I am sorry, I cannot understand the searched domain. Please try again.";
            Toast.makeText(homeActivity.getApplicationContext(), excuse, Toast.LENGTH_LONG).show();
            homeActivity.getTtsManager().addQueue(excuse);
        }

        // Create a instance of DBAccessor
        DBAccessor dbAccessor = new DBAccessor(homeActivity.getApplicationContext());

        //Search in the database for the domain of the searched input
        ArrayList<Topic> domainTopics= dbAccessor.searchDB("domain", domain);
        lastResult = domainTopics;
        addToDomain(domain);
        //Choose how to return the Arraylist of topics depending on the size
        if(domainTopics.size()>=1){
            //get details for the topics and their links
            showTopicsDomain(domainTopics,homeActivity);
        }else if(domainTopics.size()==0){
            homeActivity.addMsg("Unfortunately I have not found any topics for this domain", 1);
            homeActivity.getTtsManager().addQueue("Unfortunately I have not found any topics for this domain");
        }else{
            homeActivity.addMsg("Here is some more information about "+domain+":", 1);
            homeActivity.getTtsManager().addQueue("Here is some more information about "+domain+":");
            for(String url:domainTopics.get(0).getUrl()){
                homeActivity.addMsg(url, 1);
                homeActivity.getTtsManager().addQueue(url);
            }
        }
    }


    /**
     * Handler for the returnListTopic action
     * @param aiResponse AIResponse the Response we received
     * @param homeActivity HomeActivity the HomeActivity
     */
    static void handleReturnListTopic(AIResponse aiResponse, HomeActivity homeActivity) {
        String topic;
        try{
            topic = aiResponse.getResult().getParameters().get("topic").toString();
        } catch (Exception e){
            topic = aiResponse.getResult().getParameters().get("any").toString();
        }

        DBAccessor dbAccessor = new DBAccessor(homeActivity.getApplicationContext());
        ArrayList<Topic> topics = dbAccessor.searchDB("topic", topic);
        lastResult = topics;

        if(topics.size()>3){
            homeActivity.addMsg(topics.size()+" Topics found", 1);
            homeActivity.addMsg("Can you be more specific?", 1);
        }else if(topics.size()==0){
            homeActivity.addMsg("Unfortunately I have not found any Topics", 1);
        }else{ // 1 to 3 topics found
            for (Topic t : topics) {
                addToDomain(t.getDomain());
            }
            homeActivity.addMsg("Here is some more information about "+topic+":", 1);
            showTopicsTopic(topics, homeActivity);
        }
    }


    /**
     * Handler for the Related Topics action (when user asks what topics are related to TOPIC)
     * @param aiResponse AIResponse the Response we received
     * @param homeActivity HomeActivity the HomeActivity
     */
    static void handleReturnRelatedTopics(AIResponse aiResponse, HomeActivity homeActivity) {
        String topic;

        try{
            topic =aiResponse.getResult().getParameters().get("topic").toString();
        } catch (Exception e){
            topic =aiResponse.getResult().getParameters().get("any").toString();
        }

        Log.i("related", topic);

        homeActivity.addMsg(getRelated(topic), 1);
    }


    /**
     * Adds a counter to the given domain or creates the entry in the domain if it does not exist
     * @param domain The Domain to increase
     */
    private static void addToDomain(String domain) {
        if (domainNames.contains(domain)) {
            domainCount.set(domainNames.indexOf(domain), domainCount.get(domainNames.indexOf(domain))+1);
        }else{
            domainNames.add(domain);
            domainCount.add(1);
        }
        int domainindex = findMostImportantDomain();
        SuggestionTimer.getInstance().reschedule(domainNames.get(domainindex), homeActivity);
    }

    /**
     * returns the index of the most important domain for a user
     * @return int
     */
    private static int findMostImportantDomain() {
        int mostImportant = 0;
        int mostImportantIndex = 0;
        int runner = 0;
        for (int domainImportance : domainCount) {
            if(domainImportance>mostImportant){
                mostImportant=domainImportance;
                mostImportantIndex=runner;
            }
            runner++;
        }
        return mostImportantIndex;
    }



    /**
     * Get (max) 3 related topics, which are related to the given topic
     * @param topic Specific topic to search for, what topics are related to this topic
     * @return output string, containing related topic(s)
     */
    private static String getRelated(String topic) {
        ArrayList<Topic> topicList;
        ArrayList<Topic.Related> relatedList = new ArrayList<>();
        String out = "";

        DBAccessor dbAccessor = new DBAccessor(homeActivity.getApplicationContext());
        topicList = dbAccessor.searchDB("topic", topic);

        if(topicList.size()==1){
            relatedList.addAll(topicList.get(0).getRelated_topics());
        } else if(topicList.size() > 1){
            for (int i = 0; i < topicList.size(); i++) {
                relatedList.addAll(topicList.get(i).getRelated_topics());
            }
        } else if(topicList.size()==0) {
            out += "Unfortunately I have not found any topic";
            return out;
        }

        ArrayList<Topic.Related> sortedRelate = sortArrayListOfTopics(relatedList,"weight");

        int size = sortedRelate.size();

        if (size==1) {
            out += "a related topic is ";
            out += sortedRelate.get(sortedRelate.size()-1).getTopic();
        } else if (size==2) {
            out += "two related topics are ";
            out += sortedRelate.get(sortedRelate.size()-1).getTopic();
            out += " and ";
            out += sortedRelate.get(sortedRelate.size()-2).getTopic();
        } else if (size>=3) {
            out += "three most related topics are ";
            out += sortedRelate.get(sortedRelate.size()-1).getTopic();
            out += ", ";
            out += sortedRelate.get(sortedRelate.size()-2).getTopic();
            out += " and ";
            out += sortedRelate.get(sortedRelate.size()-3).getTopic();
        }

        return out;
    }


    /**
     *  Sorts the array ascending for the parameter paramToSort
     * @param arrayToSort The arrayList to be sorted
     * @param paramToSort For which category it is sorted
     * @return ascending sorted arrayList
     */
    private static <T> ArrayList<T> sortArrayListOfTopics(Collection<T> arrayToSort, String paramToSort){
        ArrayList<T> resultArray=new ArrayList<>();
        resultArray.addAll(arrayToSort);
        //Sort the arraylist ascending for the influence value
        if(paramToSort.equals("influence")){
            Collections.sort(resultArray, (Comparator<? super T>) new Comparator<Topic>() {

                public int compare(Topic o1, Topic o2) {
                    return Double.compare(o1.getInfluence(), o2.getInfluence());
                }
            });
        } else if(paramToSort.equals("weight")){
            Collections.sort(resultArray, (Comparator<? super T>) new Comparator<Topic.Related>() {


                public int compare(Topic.Related o1, Topic.Related o2) {
                    return Integer.compare(o1.getWeight(), o2.getWeight());
                }
            });
        }
        return resultArray;
    }


    /**
     * Return a String containing all the links for one topic
     * @param urls the array to search the links
     * @return return the String containing the urls of the one topic
     */
    public static String getLinksOfOneTopic(ArrayList<String> urls, String topic){
        String linksMsg="";
        if(urls.size()!=0){
            linksMsg+="\nFor more details see the link(s): ";
            for(int i=0;i<urls.size();i++){
                try {
                    URL url = new URL(urls.get(i));
                    linksMsg+="\n"+ "<a href=" + url + ">" + url.getHost() + " </a> <br>";
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }else{ // no urls available
            linksMsg+="<br>No link available<br>";
            linksMsg+=getRelated(topic);
        }
        return linksMsg;
    }


    /**
     * retrieves topic-links for a specific domain
     * @param topicArrayList the arrayList containing the topics
     * @param homeActivity the homeacivity to know where to addMsg()
     */
    public static void showTopicsDomain(ArrayList<Topic> topicArrayList, HomeActivity homeActivity) {
        topicArrayList=sortArrayListOfTopics(topicArrayList,"influence");

        homeActivity.addMsg("I found "+topicArrayList.size()+" topic(s) for you.", 1);

        int size = topicArrayList.size();
        String linksMsg="";
        if(size==1){
            homeActivity.addMsg("The most influential topic in the domain "+topicArrayList.get(topicArrayList.size()-1).getDomain()+" is: \n",1);
        } else { // more than 1 topics
            homeActivity.addMsg("The most influential topics in the domain "+topicArrayList.get(topicArrayList.size()-1).getDomain()+" are: \n",1);
        }
        linksMsg += showTopics(topicArrayList, homeActivity, size);

    }


    /**
     * retrieves links for specific topics
     * @param topicArrayList the arrayList containing the topics
     * @param homeActivity the homeacivity to know where to addMsg()
     */
    private static void showTopicsTopic(ArrayList<Topic> topicArrayList, HomeActivity homeActivity) {
        topicArrayList=sortArrayListOfTopics(topicArrayList,"influence");

        int size = topicArrayList.size();
        String linksMsg="";

        linksMsg += showTopics(topicArrayList, homeActivity, size);
    }


    /**
     * addMsg for topics depending on the topicArrayList.size()
     * @param topicArrayList the arrayList containing the topics
     * @param homeActivity the homeacivity to know where to addMsg()
     */
    public static String showTopics(ArrayList<Topic> topicArrayList, HomeActivity homeActivity, int size){ // String domain

        String linksMsg;
        if(size==1){

            //Extract the links of the highest influenced topic
            linksMsg=getLinksOfOneTopic(topicArrayList.get(size-1).getUrl(), topicArrayList.get(size-1).getTopic());
            homeActivity.addMsg("\n"
                    +topicArrayList.get(size-1).getTopic() +", influence: " +topicArrayList.get(size-1).getInfluence()
                    +linksMsg, 1);

        }else if(size==2){

            linksMsg=getLinksOfOneTopic(topicArrayList.get(size-1).getUrl(), topicArrayList.get(size-1).getTopic());
            homeActivity.addMsg("\n"
                    +topicArrayList.get(size-1).getTopic() +", influence: " +topicArrayList.get(size-1).getInfluence()
                    +linksMsg, 1);

            linksMsg=getLinksOfOneTopic(topicArrayList.get(size-2).getUrl(), topicArrayList.get(size-2).getTopic());
            homeActivity.addMsg("\n"
                    +topicArrayList.get(size-2).getTopic() +", influence: " +topicArrayList.get(size-2).getInfluence()
                    +linksMsg, 1);

        }else { // Greater than 2
            showChartImage(topicArrayList, homeActivity);

            linksMsg = getLinksOfOneTopic(topicArrayList.get(size - 1).getUrl(), topicArrayList.get(size - 1).getTopic());
            homeActivity.addMsg("\n"
                    + topicArrayList.get(size - 1).getTopic() + ", influence: " + topicArrayList.get(size - 1).getInfluence()
                    + linksMsg, 1);

            linksMsg = getLinksOfOneTopic(topicArrayList.get(size - 2).getUrl(), topicArrayList.get(size - 2).getTopic());
            homeActivity.addMsg("\n"
                    + topicArrayList.get(size - 2).getTopic() + ", influence: " + topicArrayList.get(size - 2).getInfluence()
                    + linksMsg, 1);

            linksMsg = getLinksOfOneTopic(topicArrayList.get(size - 3).getUrl(), topicArrayList.get(size - 3).getTopic());
            homeActivity.addMsg("\n"
                    + topicArrayList.get(size - 3).getTopic() + ", influence: " + topicArrayList.get(size - 3).getInfluence()
                    + linksMsg, 1);

        }

        return linksMsg;
    }

    /**
     * This method is used to show the clickable image for creating the chart activity
     */
    private static void showChartImage(ArrayList<Topic> topicArrayList, HomeActivity homeActivity){
        homeActivity.setTopicArrayList(topicArrayList);
        homeActivity.addMsg("", 2);
    }
}
