package com.jeremyfox.TweetFox.Managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.jeremyfox.TweetFox.Helpers.PrefsHelper;
import com.jeremyfox.TweetFox.Interfaces.RegisterCallback;
import com.jeremyfox.TweetFox.Interfaces.RequestTokenCallback;
import com.jeremyfox.TweetFox.Interfaces.TweetsRequestCallback;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
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
        Twitter tw = getTwitter();
        tw.setOAuthAccessToken(new AccessToken(token, tokenSecret));
        if (NetworkManager.isConnected(context)) {
            new TweetsAsyncTask().execute(tw, callback);
        } else {
            callback.onFailure(NetworkManager.FAILURE_UNKNOWN_STATUS);
        }
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

class TweetsAsyncTask extends AsyncTask<Object, Integer, List<twitter4j.Status>> {

    private TweetsRequestCallback callback;

    @Override
    protected List<twitter4j.Status> doInBackground(Object... params) {
        Twitter tw = (Twitter)params[0];
        this.callback = (TweetsRequestCallback)params[1];
        List<twitter4j.Status> tweets = null;
        if (null != tw && null != this.callback) {
            try {
                tweets = tw.getHomeTimeline(new Paging(1, 20));
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
