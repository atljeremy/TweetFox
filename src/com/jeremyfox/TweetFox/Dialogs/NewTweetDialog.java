package com.jeremyfox.TweetFox.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.jeremyfox.TweetFox.R;

/**
 * Created with IntelliJ IDEA.
 * User: jeremy
 * Date: 5/21/13
 * Time: 9:20 PM
 */
public class NewTweetDialog {

    private AlertDialog dialog;
    private EditText tweetInput;

    public NewTweetDialog(Activity activity, String dialogTitle, EditText tweetInput, DialogInterface.OnClickListener saveListener){
        this.tweetInput = tweetInput;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(dialogTitle)
                .setPositiveButton(activity.getString(R.string.save), saveListener)
                .setNegativeButton(activity.getString(R.string.cancel), null);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(builder.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(lp);
        linearLayout.addView(this.tweetInput, lp);

        builder.setView(linearLayout);

        this.dialog = builder.create();
    }

    public void showDialog() {
        if (null != this.dialog) {
            this.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            this.dialog.show();
        }
    }

}
