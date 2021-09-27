package at.ac.univie.pantobot.controller;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

/**
 * Class to handle all API Calls
 */
public abstract class APIConnector extends AsyncTask<AIRequest, Void, AIResponse> {
    protected AIConfiguration config;
    protected final String access_token ="TODO:INSERT_ACCESS_TOKEN";
    protected Context context;
    protected Activity parent;

    /**
     * Constructor for APIConnectort
     * @param parent Activity
     */
    protected APIConnector(Activity parent){
        config  = new AIConfiguration(access_token,AIConfiguration.SupportedLanguages.English,AIConfiguration.RecognitionEngine.System);
        this.parent = parent;
        this.context = parent.getApplicationContext();
    }
}
