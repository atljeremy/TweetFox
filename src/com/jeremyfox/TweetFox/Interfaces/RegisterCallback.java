package com.jeremyfox.TweetFox.Interfaces;

import twitter4j.auth.AccessToken;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 11:29 AM
 */
public interface RegisterCallback {

    /**
     * On success callback.
     */
    public void onSuccess(AccessToken accessToken);

    /**
     * On failure callback.
     */
    public void onFailure(int statusCode);
}
