package com.jeremyfox.TweetFox.Managers;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;
import com.jeremyfox.TweetFox.Helpers.PrefsHelper;
import com.jeremyfox.TweetFox.Interfaces.*;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 10:33 AM
 */
public class TwitterManager {

    private static TwitterManager tmInstance = null;
    private Twitter twitter;
    private String token;
    private String tokenSecret;
    public static final String TOKEN_KEY = "token";
    public static final String TOKEN_SECRET_KEY = "tokenSecret";
    private static final String CONSUMER_KEY = "upTJdg8N7WEdjCmwPF6w";
    private static final String CONSUMER_SECRET = "py5eDmnyGlpSucn8xu9xL4hO5Kye8h3sUD8etjcs";

    public static TwitterManager getInstance(Context context) {
        if (null == tmInstance) {
            tmInstance = new TwitterManager(context);
        }

        return tmInstance;
    }

    private TwitterManager(Context context) {
        // Set twitter iVar
        Twitter tw = TwitterFactory.getSingleton();
        tw.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        setTwitter(tw);

        // Set token iVar if exists
        setToken(PrefsHelper.getPref(context, TOKEN_KEY));

        // Set tokenSecret iVar if exists
        setTokenSecret(PrefsHelper.getPref(context, TOKEN_SECRET_KEY));
    }

    public void getOAuthRequestToken(Context context, RequestTokenCallback callback) {
        Twitter tw = getTwitter();
        if (null != tw && NetworkManager.isConnected(context)) {
            new RequestTokenAsyncTask().execute(tw, callback);
        } else {
            callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }

    public void registerUser(Context context, RequestToken requestToken, String pin, RegisterCallback callback) {
        Twitter tw = getTwitter();
        if (null != tw && NetworkManager.isConnected(context)) {
            new RegisterAsyncTask().execute(tw, callback, requestToken, pin);
        } else {
            callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }

    public void storeAccessToken(Context context, AccessToken accessToken){
        setToken(accessToken.getToken());
        setTokenSecret(accessToken.getTokenSecret());
        PrefsHelper.setPref(context, TOKEN_KEY, token);
        PrefsHelper.setPref(context, TOKEN_SECRET_KEY, tokenSecret);
    }

    public boolean hasExistingAccessToken(Context context) {
        token = PrefsHelper.getPref(context, TOKEN_KEY);
        tokenSecret = PrefsHelper.getPref(context, TOKEN_SECRET_KEY);
        boolean retVal;
        if (null != token && token.length() > 0 && null != tokenSecret && tokenSecret.length() > 0) {
            retVal = true;
        } else {
            retVal = false;
        }

        return retVal;
    }

    public void getTweets(Context context, TweetsRequestCallback callback) {
        Twitter tw = getSignedTwitter();
        if (NetworkManager.isConnected(context)) {
            new TweetsAsyncTask().execute(tw, callback, Integer.valueOf(20));
        } else {
            callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }

    public void getLatestTweet(Context context, TweetsRequestCallback callback) {
        Twitter tw = getSignedTwitter();
        if (NetworkManager.isConnected(context)) {
            new TweetsAsyncTask().execute(tw, callback, Integer.valueOf(1));
        } else {
            callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }

    public void getUsername(Context context, Status status, UsernameCallback callback) {
        Twitter tw = getSignedTwitter();
        if (NetworkManager.isConnected(context)) {
            new UsernameAsyncTask().execute(tw, status, callback);
        } else {
            callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }

    public void publishTweet(String tweet, NetworkCallback callback) {
        if (null != tweet && tweet.length() > 0) {
            Twitter tw = getSignedTwitter();
            new PublishTweetAsynTask().execute(tw, tweet, callback);
        } else {
            callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }

    public Twitter getSignedTwitter() {
        Twitter tw = getTwitter();
        tw.setOAuthAccessToken(new AccessToken(token, tokenSecret));
        return tw;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
}

class PublishTweetAsynTask extends AsyncTask<Object, Integer, JSONObject> {

    private NetworkCallback callback;

    @Override
    protected JSONObject doInBackground(Object... params) {
        Twitter tw = (Twitter)params[0];
        String tweet = (String)params[1];
        this.callback = (NetworkCallback)params[2];

        JSONObject jsonObject = null;
        try {
            twitter4j.Status status = tw.updateStatus(tweet);
            if (null != status) {
                jsonObject = new JSONObject("{\"posted\": 1}");
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (null != jsonObject) {
            this.callback.onSuccess(jsonObject);
        } else {
            this.callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }
}

class UsernameAsyncTask extends AsyncTask<Object, Integer, String> {

    private UsernameCallback callback;

    @Override
    protected String doInBackground(Object... params) {
        Twitter tw = (Twitter)params[0];
        twitter4j.Status status = (twitter4j.Status)params[1];
        this.callback = (UsernameCallback)params[2];
        String username = null;
        try {
            if (null != status) {
                username = status.getUser().getScreenName();
            } else if(null != tw) {
                username = tw.getScreenName();
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return username;
    }

    @Override
    protected void onPostExecute(String username) {
        if (null != username) {
            this.callback.onSuccess(username);
        } else {
            this.callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }
}

class TweetsAsyncTask extends AsyncTask<Object, Integer, List<twitter4j.Status>> {

    private TweetsRequestCallback callback;

    @Override
    protected List<twitter4j.Status> doInBackground(Object... params) {
        Twitter tw = (Twitter)params[0];
        this.callback = (TweetsRequestCallback)params[1];
        Integer count = (Integer)params[2];
        List<twitter4j.Status> tweets = null;
        if (null != tw && null != this.callback) {
            try {
                tweets = tw.getHomeTimeline(new Paging(1, count.intValue()));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        return tweets;
    }

    @Override
    protected void onPostExecute(List<twitter4j.Status> tweets) {
        if (null != tweets && tweets.size() > 0) {
            this.callback.onSuccess(tweets);
        } else {
            this.callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }
}

class RequestTokenAsyncTask extends AsyncTask<Object, Integer, RequestToken> {

    private RequestTokenCallback callback;

    @Override
    protected RequestToken doInBackground(Object... params) {
        Twitter tw = (Twitter)params[0];
        this.callback = (RequestTokenCallback)params[1];
        RequestToken requestToken = null;
        if (null != tw && null != this.callback) {
            try {
                requestToken = tw.getOAuthRequestToken();
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }

        return requestToken;
    }

    @Override
    protected void onPostExecute(RequestToken requestToken) {
        if (null != requestToken) {
            this.callback.onSuccess(requestToken);
        } else {
            this.callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }
}

class RegisterAsyncTask extends AsyncTask<Object, Integer, AccessToken> {

    private RegisterCallback callback;

    @Override
    protected AccessToken doInBackground(Object... params) {
        AccessToken accessToken = null;
        Twitter tw = (Twitter)params[0];
        this.callback = (RegisterCallback)params[1];
        RequestToken requestToken = (RequestToken)params[2];
        String pin = (String)params[3];
        if (null != tw && null != requestToken && null != pin && null != this.callback) {
            while (null == accessToken) {
                try {
                    if (pin.length() > 0) {
                        accessToken = tw.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = tw.getOAuthAccessToken();
                    }
                } catch (TwitterException te) {
                    if (401 == te.getStatusCode()) {
                        Log.d("TwitterManager", "Unable to get the access token.");
                    } else {
                        te.printStackTrace();
                    }
                }
            }
        }

        return accessToken;
    }

    @Override
    protected void onPostExecute(AccessToken accessToken) {
        if (null != accessToken) {
            this.callback.onSuccess(accessToken);
        } else {
            this.callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
    }
}
