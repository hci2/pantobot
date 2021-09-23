package at.ac.univie.pantobot.controller;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Class to manage the speech output
 */

public class TextToSpeechManager {

    private TextToSpeech mTts = null;
    private boolean isLoaded = false;

    /**
     * initializes the manager
     * @param context Context
     */
    public void init(Context context) {
        try {
            mTts = new TextToSpeech(context, onInitListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the listener for output
     */
    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(Locale.ENGLISH);
                isLoaded = true;

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "This Language is not supported");
                }
            } else {
                Log.e("error", "Initialization Failed!");
            }
        }
    };


    /**
     * Exits the speech output
     */
    public void shutDown() {
        mTts.shutdown();
    }

    /**
     * Adds a text to the speech output list
     * @param text String text to say
     */
    public void addQueue(String text) {
        if (isLoaded)
            mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            Log.e("error", "TTS Not Initialized");
    }


}