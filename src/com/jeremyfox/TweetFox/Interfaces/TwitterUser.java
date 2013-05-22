package com.jeremyfox.TweetFox.Interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/20/13
 * Time: 11:09 PM
 */
public interface TwitterUser {

    public String getUsername();
    public String getName();
    public String getDescription();
    public int getFollowers();
    public int getFollowing();

    public void setUsername(String username);
    public void setName(String name);
    public void setDescription(String description);
    public void setFollowers(int followers);
    public void setFollowing(int following);

}
