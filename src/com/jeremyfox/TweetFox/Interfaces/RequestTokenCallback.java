package com.jeremyfox.TweetFox.Interfaces;

import twitter4j.auth.RequestToken;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 11:53 AM
 */
public interface RequestTokenCallback {

    /**
     * On success callback.
     */
    public void onSuccess(RequestToken requestToken);

    /**
     * On failure callback.
     */
    public void onFailure(int statusCode);

}
