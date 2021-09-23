package at.ac.univie.pantobot.controller;

import ai.api.AIServiceException;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import android.app.Activity;

/**
 * Class to handle text requests to the API
 */
public class APITextConnector extends APIConnector{

    private AIDataService aiDataService =null;

    /**
     * APITextConnector Constructor
     * @param parent Activity
     */
    public APITextConnector(Activity parent){
        super(parent);

        aiDataService = new AIDataService(context, config);
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     *
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param requests The parameters of the task.
     *
     * @return A result, defined by the subclass of this task.
     *
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected AIResponse doInBackground(AIRequest... requests) {
        final AIRequest request = requests[0];
        try {
            final AIResponse response = aiDataService.request(request);
            return response;
        } catch (AIServiceException e) {
        }
        return null;
    }

    /**
     * Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.
     *
     * This method won't be invoked if the task was cancelled.
     *
     * @param aiResponse The result of the operation computed by {@link #doInBackground}.
     *
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(AIResponse aiResponse) {
        if (aiResponse != null) {
            ActionHandler.whatToDoWith(aiResponse, parent);
        }
    }
}