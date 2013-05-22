package com.jeremyfox.TweetFox.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeremyfox.TweetFox.R;
import twitter4j.Status;
import twitter4j.User;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/19/13
 * Time: 6:45 PM
 */
public class TweetListAdapter extends ArrayAdapter<Status> {

    public List<Status> tweets;

    public TweetListAdapter(Context context, int textViewResourceId, List<Status> tweets) {
        super(context, textViewResourceId, tweets);
        this.tweets = tweets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.tweet_list_item, null);
        }

        Status status = tweets.get(position);
        User user = status.getUser();
        if (null != status && null != user) {
            ImageView profilePic = (ImageView)view.findViewById(R.id.imageView);
            TextView username = (TextView) view.findViewById(R.id.username);
            TextView tweet = (TextView) view.findViewById(R.id.tweet);
//            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Dakota-Regular.ttf");
//            title.setTypeface(typeface);
            username.setText(user.getScreenName());
            tweet.setText(status.getText());

        }
        return view;
    }

}
