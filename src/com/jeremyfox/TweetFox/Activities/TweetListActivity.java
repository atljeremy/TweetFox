package com.jeremyfox.TweetFox.Activities;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import com.jeremyfox.TweetFox.Adapters.TweetListAdapter;
import com.jeremyfox.TweetFox.Interfaces.TweetsRequestCallback;
import com.jeremyfox.TweetFox.R;
import twitter4j.Status;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 6:26 PM
 */
public class TweetListActivity extends BaseActivity {

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.tweetlist);

        listView = (ListView)findViewById(R.id.listView);

        showLoadingDialog();
        twitterManager.getTweets(this, new TweetsRequestCallback() {
            @Override
            public void onSuccess(List<Status> tweets) {
                dismissDialog();
                setupListView(tweets);
            }

            @Override
            public void onFailure(int statusCode) {
                dismissDialog();
                Toast.makeText(TweetListActivity.this, "Error Loading Tweets", Toast.LENGTH_LONG);
            }
        });
    }

    private void setupListView(List<Status> tweets) {
        listView.setAdapter(new TweetListAdapter(this, R.id.tweet, tweets));
        listView.invalidate();
    }

}
