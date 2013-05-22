package com.jeremyfox.TweetFox.Interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/21/13
 * Time: 7:08 PM
 */
public interface UsernameCallback {

    /**
     * On success callback.
     */
    public void onSuccess(String username);

    /**
     * On failure callback.
     */
    public void onFailure(int statusCode);

}
