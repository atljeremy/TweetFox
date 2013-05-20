package com.jeremyfox.TweetFox.Interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 10:58 AM
 */
public interface NetworkCallback {

    /**
     * On success callback.
     */
    public void onSuccess(Object json);

    /**
     * On failure callback.
     */
    public void onFailure(int statusCode);

}
