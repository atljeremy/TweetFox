package com.jeremyfox.TweetFox.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.jeremyfox.TweetFox.Adapters.TweetListAdapter;
import com.jeremyfox.TweetFox.Dialogs.NewTweetDialog;
import com.jeremyfox.TweetFox.Helpers.PrefsHelper;
import com.jeremyfox.TweetFox.Interfaces.NetworkCallback;
import com.jeremyfox.TweetFox.Interfaces.TweetsRequestCallback;
import com.jeremyfox.TweetFox.Interfaces.UsernameCallback;
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

        getTweets();
    }

    private void getTweets() {
        showLoadingDialog();
        twitterManager.getTweets(this, new TweetsRequestCallback() {
            @Override
            public void onSuccess(List<Status> tweets) {
                dismissDialog();
                setupListView(tweets);
                Status status = tweets.get(0);
                PrefsHelper.setPref(TweetListActivity.this, PrefsHelper.LATEST_TWEET_KEY, status.getText());
                getUsername(status);
            }

            @Override
            public void onFailure(int statusCode) {
                dismissDialog();
                Toast.makeText(TweetListActivity.this, "Error Loading Tweets", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUsername(Status status) {
        twitterManager.getUsername(this, status, new UsernameCallback() {
            @Override
            public void onSuccess(String username) {
                PrefsHelper.setPref(TweetListActivity.this, PrefsHelper.USERNAME_KEY, username);
            }

            @Override
            public void onFailure(int statusCode) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweets_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.refresh:
                Log.d("TweetListActivity", "Refresh Tweets");
                getTweets();
                break;

            case R.id.new_tweet:
                Log.d("TweetListActivity", "New Tweet");
                newTweet();
                break;

            case R.id.my_account:
                Log.d("TweetListActivity", "My Account");
                Intent intent = new Intent(this, MyAccountActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void newTweet() {
        final EditText tweetInput = new EditText(this);
        tweetInput.setHint(getString(R.string.enter_tweet_here));

        NewTweetDialog newTweetDialog = new NewTweetDialog(this, getString(R.string.new_tweet_string), tweetInput, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                showLoadingDialog();

                boolean tweetEmpty = tweetInput.getText().toString().length() == 0;
                if (tweetEmpty) {
                    Toast.makeText(TweetListActivity.this, getString(R.string.enter_tweet_error), Toast.LENGTH_LONG).show();
                } else {
                    final String tweet = tweetInput.getText().toString();
                    twitterManager.publishTweet(tweet, new NetworkCallback() {
                        @Override
                        public void onSuccess(Object json) {
                            dismissDialog();
                            Toast.makeText(TweetListActivity.this, getString(R.string.tweet_published), Toast.LENGTH_LONG).show();
                            getTweets();
                        }

                        @Override
                        public void onFailure(int statusCode) {
                            dismissDialog();
                            Toast.makeText(TweetListActivity.this, getString(R.string.tweet_publish_error), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        newTweetDialog.showDialog();
    }

    private void setupListView(List<Status> tweets) {
        TweetListAdapter adapter = (TweetListAdapter)listView.getAdapter();
        if (null == adapter) {
            listView.setAdapter(new TweetListAdapter(this, R.id.tweet, tweets));
        } else {
            adapter.tweets = tweets;
            adapter.notifyDataSetChanged();
        }

        listView.invalidate();
    }

}
