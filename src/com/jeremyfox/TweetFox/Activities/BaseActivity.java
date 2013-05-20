package com.jeremyfox.TweetFox.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.jeremyfox.TweetFox.Helpers.PrefsHelper;
import com.jeremyfox.TweetFox.Interfaces.NetworkCallback;
import com.jeremyfox.TweetFox.Interfaces.RegisterCallback;
import com.jeremyfox.TweetFox.Interfaces.RequestTokenCallback;
import com.jeremyfox.TweetFox.Managers.TwitterManager;
import com.jeremyfox.TweetFox.R;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 9:41 AM
 */
public class BaseActivity extends Activity {

    private Twitter twitter;
    public TwitterManager twitterManager;
    public ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        twitterManager = TwitterManager.getInstance(this);
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public Twitter getTwitter() {
        return this.twitter;
    }

    /**
     * Shows the loading spinner dialog
     * @return ProgressDialog the progress dialog that will be displayed while loading notes from the API
     */
    public void showLoadingDialog() {
        if (null == this.dialog) this.dialog = new ProgressDialog(this);
        this.dialog.setMessage("Loading...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    public void dismissDialog() {
        if (null != this.dialog) this.dialog.dismiss();
    }

}
