package at.ac.univie.pantobot.controller;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.view.menu.ActionMenuItemView;
import at.ac.univie.pantobot.R;
import at.ac.univie.pantobot.model.Topic;

import java.util.ArrayList;
import java.util.Random;

/**
 * Automatically add a random topic and notifies the user at random time interval.
 */
public class NewTopicController {
    private Activity homeActivity;
    private Topic newTopic;
    private ArrayList<Topic> topicsChoice;
    private Handler handler = new Handler();

    public NewTopicController(Activity activity) {
        homeActivity = activity;
        topicsChoice = new ArrayList<>();

        Topic topic = new Topic();
        ArrayList<String> url = new ArrayList<>();

        url.add("https://www.google.com/");
        topic = topic.addTopic("000", "Google", "Internet", "en", url);
        topicsChoice.add(topic);

        url = new ArrayList<>();
        url.add("https://www.youtube.com/");
        topic = topic.addTopic("001", "Youtube", "Internet", "en", url);
        topicsChoice.add(topic);

        url = new ArrayList<>();
        url.add("https://twitter.com/");
        topic = topic.addTopic("002", "Twitter", "Social Media", "en", url);
        topicsChoice.add(topic);

        url = new ArrayList<>();
        url.add("https://www.instagram.com/");
        topic = topic.addTopic("003", "Instagram", "Social Media", "en", url);
        topicsChoice.add(topic);

        url = new ArrayList<>();
        url.add("https://www.facebook.com/");
        topic = topic.addTopic("004", "Facebook", "Social Media", "en", url);
        topicsChoice.add(topic);

        handler.post(runnableCode);
    }

    public Topic getNewTopic() {
        return newTopic;
    }


    /**
     * executes addNewTopic at random time interval
     */
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            int ran = randInt(1, 5);
            handler.postDelayed(runnableCode, ran*30000); //random number * 60 sec
            activateNotificationIcon();
            addNewTopic();
        }
    };


    /**
     * turns notification bell icon to active
     */
    public void activateNotificationIcon() {
        ActionMenuItemView actionMenuItemView = (ActionMenuItemView) homeActivity.findViewById(R.id.action_notification);

        if(actionMenuItemView != null) {
            actionMenuItemView.getItemData().setIcon(R.drawable.ic_notifications_active);
        }
    }


    /**
     * randomly adds a new topic
     */
    public void addNewTopic() {
        newTopic = new Topic();
        newTopic = topicsChoice.get(randInt(1, topicsChoice.size())-1);
    }


    public String toString(Topic topic) {
        String out = "";

        if (newTopic != null) {
            ArrayList<String> urlList = topic.getUrl();
            String linksMsg = ResultPresenter.getLinksOfOneTopic(urlList, topic.getTopic());

            out = "New topic in " + topic.getDomain()
                            + " domain was added to our archive. Topic: "
                            + topic.getTopic() + "." + linksMsg;
            newTopic = null;
        }
        return out;
    }


    /**
     * randomizes number in between a given range
     * @param min minimum number that can be pick
     * @param max maximum number that can be pick
     * @return a randomized number
     */
    private static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
