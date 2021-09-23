package at.ac.univie.pantobot.controller;

import at.ac.univie.pantobot.view.HomeActivity;

import java.util.Timer;

/**
 * The Timer
 */
public class SuggestionTimer extends Timer {
    private static SuggestionTimer ourInstance = new SuggestionTimer(null, null);

    /**
     * get Instance for Singelton
     * @return SuggestionTimer instance
     */
    public static SuggestionTimer getInstance() {
        return ourInstance;
    }

    /**
     * constructor
     * @param fordomain the domain to present
     */
    private SuggestionTimer(String fordomain, HomeActivity homeActivity) {
        super();
        schedule(new DomainSuggestionController(fordomain, homeActivity), 20000);
    }

    /**
     * resets the timer
     * @param fordomain the domain to present
     */
    public void reschedule(String fordomain, HomeActivity homeActivity){
        this.cancel();
        this.ourInstance = new SuggestionTimer(fordomain, homeActivity);
    }

}
