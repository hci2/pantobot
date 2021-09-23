package at.ac.univie.pantobot.controller;

import at.ac.univie.pantobot.model.DBAccessor;
import at.ac.univie.pantobot.model.Topic;
import at.ac.univie.pantobot.view.HomeActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TimerTask;

/**
 * Suggest Domains after 30 seconds of no input
 */
public class DomainSuggestionController extends TimerTask {
    private String domain;
    private HomeActivity homeActivity;
    /**
     * constructor
     * @param fordomain String the Domain to present
     */
    public DomainSuggestionController(String fordomain, HomeActivity homeActivity) {
        this.domain = fordomain;
        this.homeActivity = homeActivity;
    }

    @Override
    public void run() {
        DBAccessor dbAccessor = new DBAccessor(homeActivity.getApplicationContext());
        long seed = System.nanoTime();
        ArrayList<Topic> domainTopics= dbAccessor.searchDB("domain", domain);
        Collections.shuffle(domainTopics, new Random(seed));
        final ArrayList<Topic> randomTopics= domainTopics;

        homeActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    homeActivity.addMsg("I saw, you are interested in "+domain, 1);
                    Thread.sleep(500);
                    homeActivity.addMsg("Here is a topic that you might be interested in:", 1);
                    Thread.sleep(500);
                    ResultPresenter.showTopics(randomTopics, homeActivity, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
