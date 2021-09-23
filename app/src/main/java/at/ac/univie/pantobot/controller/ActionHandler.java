package at.ac.univie.pantobot.controller;

import ai.api.model.AIResponse;
import ai.api.model.Result;
import android.app.Activity;
import at.ac.univie.pantobot.view.HomeActivity;

/**
 * Class responsible for handling the API.ai results
 *
 * Useful links:
 * http://stackoverflow.com/questions/5178092/sorting-a-list-of-points-with-java
 * http://stackoverflow.com/questions/13007778/access-values-of-hashmap
 * http://stackoverflow.com/questions/17068506/how-can-you-use-generics-to-make-universal-methods-in-java
 * http://stackoverflow.com/questions/2734270/how-do-i-make-links-in-a-textview-clickable
 *
 */
public class ActionHandler {

    private static HomeActivity homeActivity;
    /**
     * Decides what to to with an incoming API response
     * @param aiResponse AIResponse the Response we received
     * @param parent Activity the HomeActivity
     */
    public static void whatToDoWith(AIResponse aiResponse, Activity parent){
        Result result = aiResponse.getResult();
        homeActivity = (HomeActivity) parent;
        ResultPresenter.homeActivity=homeActivity;
        if(result.getAction().startsWith("smalltalk")){
            homeActivity.addMsg(result.getFulfillment().getSpeech(), 1);
            return;
        }
        if(result.getAction().startsWith("returnListTopic")){
            ResultPresenter.handleReturnListTopic(aiResponse, homeActivity);
            return;
        }
        if(result.getAction().startsWith("returnListDomain")){
            ResultPresenter.handleReturnListDomain(aiResponse, homeActivity);
            return;
        }
        if(result.getAction().startsWith("filterTopics")){
            ResultPresenter.handleReturnFilterTopics(aiResponse, homeActivity);
            return;
        }
        if(result.getAction().startsWith("relatedTopics")){
            ResultPresenter.handleReturnRelatedTopics(aiResponse, homeActivity);
            return;
        }

        homeActivity.addMsg("Unfortunately I did not understand you", 1);
    }


}
