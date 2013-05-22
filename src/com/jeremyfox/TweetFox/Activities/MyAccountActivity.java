package com.jeremyfox.TweetFox.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.jeremyfox.TweetFox.CustomClasses.TwitterUserAccount;
import com.jeremyfox.TweetFox.Interfaces.UserInfoCallback;
import com.jeremyfox.TweetFox.Managers.NetworkManager;
import com.jeremyfox.TweetFox.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/20/13
 * Time: 10:14 PM
 */
public class MyAccountActivity extends BaseActivity {

    private TwitterUserAccount user;
    private String username;
    private String name;
    private int followers;
    private int following;
    private String description;
    private TextView usernameTV;
    private TextView nameTV;
    private TextView followersTV;
    private TextView followingTV;
    private TextView descTV;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.myaccount);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        usernameTV  = (TextView) findViewById(R.id.account_useranme);
        nameTV      = (TextView) findViewById(R.id.account_name);
        followersTV = (TextView) findViewById(R.id.account_followers);
        followingTV = (TextView) findViewById(R.id.account_following);
        descTV      = (TextView) findViewById(R.id.account_description);

        Twitter twitter = twitterManager.getSignedTwitter();

        showLoadingDialog();
        new UserInfoAsyncTask().execute(twitter, new UserInfoCallback() {
            @Override
            public void onSuccess(TwitterUserAccount user) {
                dismissDialog();
                configureViews(user);
            }

            @Override
            public void onFailure(int statusCode) {
                dismissDialog();
                Toast.makeText(MyAccountActivity.this, "An error occurred in retreiving user info", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureViews(TwitterUserAccount user) {
        this.user   = user;
        username    = user.getUsername();
        name        = user.getName();
        description = user.getDescription();
        followers   = user.getFollowers();
        following   = user.getFollowing();

        if (null != username && null != usernameTV) {
            usernameTV.setText(username);
        }

        if (null != name && null != nameTV) {
            nameTV.setText(name);
        }

        if (null != followersTV) {
            followersTV.setText(String.valueOf(followers));
        }

        if (null != followingTV) {
            followingTV.setText(String.valueOf(following));
        }

        if (null != description && null != descTV) {
            descTV.setText(description);
        }
    }
}

class UserInfoAsyncTask extends AsyncTask<Object, Integer, TwitterUserAccount> {

    private UserInfoCallback callback;

    @Override
    protected TwitterUserAccount doInBackground(Object... params) {
        Twitter twitter = (Twitter)params[0];
        this.callback = (UserInfoCallback)params[1];
        TwitterUserAccount twitterUserAccount = null;
        if (null != twitter && null != this.callback) {
            try {
                User user = twitter.showUser(twitter.getScreenName());
                if (null != user) {
                    twitterUserAccount = new TwitterUserAccount();
                    twitterUserAccount.username = user.getScreenName();
                    twitterUserAccount.name = user.getName();
                    twitterUserAccount.description = user.getDescription();
                    twitterUserAccount.followers = user.getFollowersCount();
                    twitterUserAccount.following = user.getFriendsCount();
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return twitterUserAccount;
    }

    @Override
    protected void onPostExecute(TwitterUserAccount user) {
        if (null != user) {
            this.callback.onSuccess(user);
        } else {
            this.callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }
}