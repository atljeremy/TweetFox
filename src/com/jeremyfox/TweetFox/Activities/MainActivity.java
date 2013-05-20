package com.jeremyfox.TweetFox.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.jeremyfox.TweetFox.Interfaces.RegisterCallback;
import com.jeremyfox.TweetFox.Interfaces.RequestTokenCallback;
import com.jeremyfox.TweetFox.R;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class MainActivity extends BaseActivity {


    RegisterCallback registerCallback = new RegisterCallback() {
        /**
         * On success callback.
         */
        @Override
        public void onSuccess(AccessToken accessToken) {
            dismissDialog();
            Toast.makeText(MainActivity.this, "Successfully Authenticated With Twitter", Toast.LENGTH_LONG).show();
            twitterManager.storeAccessToken(MainActivity.this, accessToken);
            Intent intent = new Intent(MainActivity.this, TweetListActivity.class);
            startActivity(intent);
        }

        /**
         * On failure callback.
         */
        @Override
        public void onFailure(int statusCode) {
            dismissDialog();
            Toast.makeText(MainActivity.this, "An Error Occurred In Twitter Authentication", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (!twitterManager.hasExistingAccessToken(this)) {
            showLoadingDialog();

            twitterManager.getOAuthRequestToken(MainActivity.this, new RequestTokenCallback() {
                @Override
                public void onSuccess(final RequestToken requestToken) {
                    dismissDialog();
                    WebView webView = new WebView(MainActivity.this);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(requestToken.getAuthorizationURL());
                    webView.setWebViewClient(new WebViewClient() {

                        public void onPageFinished(WebView view, String url) {
                            configurePINView(url, requestToken);
                        }
                    });

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    addContentView(webView, new ViewGroup.LayoutParams(layoutParams));
                }

                @Override
                public void onFailure(int statusCode) {

                }
            });
        } else {
            Intent intent = new Intent(this, TweetListActivity.class);
            startActivity(intent);
        }
    }

    private void configurePINView(String url, final RequestToken requestToken) {
        if (url.equals("https://api.twitter.com/oauth/authorize")) {

            RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);

            RelativeLayout.LayoutParams relativeLayoutParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            LinearLayout layout = new LinearLayout(this);
            layout.setBackgroundColor(Color.BLACK);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.setBaselineAligned(true);
            layout.setWeightSum(10);

            ViewGroup.LayoutParams layoutParams;

            final EditText editText = new EditText(this);
            editText.setHint("Enter PIN Here");
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 7);
            editText.setLayoutParams(layoutParams);

            Button button = new Button(this);
            button.setText("Confirm");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = editText.getText().toString();
                    if (null != pin && pin.length() > 0) {
                        twitterManager.registerUser(MainActivity.this, requestToken, pin, registerCallback);
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter pin before confirming", Toast.LENGTH_LONG);
                    }

                }
            });
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 3);
            button.setLayoutParams(layoutParams);

            layout.addView(editText, layoutParams);
            layout.addView(button, layoutParams);
            relativeLayout.addView(layout, relativeLayoutParams);

            addContentView(relativeLayout, relativeLayoutParams);

            final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
            relativeLayout.startAnimation(animationFadeIn);
        }
    }
}
