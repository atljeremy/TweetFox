package com.jeremyfox.TweetFox.Interfaces;

import com.jeremyfox.TweetFox.CustomClasses.TwitterUserAccount;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/20/13
 * Time: 11:20 PM
 */
public interface UserInfoCallback {

    /**
     * On success callback.
     */
    public void onSuccess(TwitterUserAccount user);

    /**
     * On failure callback.
     */
    public void onFailure(int statusCode);
}
