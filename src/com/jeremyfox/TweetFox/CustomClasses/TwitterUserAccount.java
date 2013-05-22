package com.jeremyfox.TweetFox.CustomClasses;

import com.jeremyfox.TweetFox.Interfaces.TwitterUser;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/20/13
 * Time: 11:14 PM
 */
public class TwitterUserAccount implements TwitterUser {

    public String username    = null;
    public String name        = null;
    public int followers      = 0;
    public int following      = 0;
    public String description = null;

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getFollowers() {
        return this.followers;
    }

    @Override
    public int getFollowing() {
        return this.following;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setFollowers(int followers) {
        this.followers = followers;
    }

    @Override
    public void setFollowing(int following) {
        this.following = following;
    }
}
