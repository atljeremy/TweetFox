package com.jeremyfox.TweetFox.Interfaces;

import twitter4j.Status;
import twitter4j.auth.RequestToken;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 7:21 PM
 */
public interface TweetsRequestCallback {

    /**
     * On success callback.
     */
    public void onSuccess(List<Status> tweets);

    /**
     * On failure callback.
     */
    public void onFailure(int statusCode);

}
