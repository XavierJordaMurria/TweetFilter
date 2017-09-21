package cat.jorda.tweetfilter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cat.jorda.tweetfilter.model.TweetItem;

public class MainActivity extends AppCompatActivity implements FilterTweetsManager.IFilterTweetsListener, View.OnTouchListener
{
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static long TWEET_LIFE_SPAN = 2000;

    private List<TweetItem> mTweetItemToBeRemovedList;
    private List<TweetItem> mBufferUpTweets;
    private RecyclerView mRecyclerView;
    private TweetsListAdapter mAdapter;
    private FilterTweetsManager mFilterTweetsManager;
    private String mFilterKeyWord;
    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private Object mListLock = new Object();

    private EditText mFilterET;

    private boolean isConnected = true;
    private boolean isRecyclerOnHold = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        mTweetItemToBeRemovedList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.tweets_list);
        mFilterET   = (EditText) findViewById(R.id.filter);

        mFilterET.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (hasFocus)
                {
                    mFilterET.setText("");
                    mFilterET.setTextColor(Color.BLACK);

                }
            }
        });

        mFilterET.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    mFilterKeyWord = mFilterET.getText().toString();

                    String filterText = getResources().getString(R.string.filtering_by) + " " + mFilterKeyWord;
                    Toast.makeText(getApplicationContext(), filterText, Toast.LENGTH_LONG).show();
                    findViewById(R.id.mainLayout).requestFocus();
                    mFilterET.setTextColor(getResources().getColor(R.color.darker_gray));
                    mFilterTweetsManager.start(new String[]{mFilterKeyWord});
                    mFilterET.setText(filterText);
                    hideSoftKeyboard(MainActivity.this);

                    return true;
                }
                return false;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFilterTweetsManager = new FilterTweetsManager(this);

        mRecyclerView.setOnTouchListener(this);

        registerReceiver(
                new ConnectivityChangeReceiver(),
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");

        if (mFilterKeyWord != null)
            mFilterTweetsManager.start(new String[]{mFilterKeyWord});
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                mFilterTweetsManager.stopStreaming();
            }
        });
    }

    @Override
    public void onFilteredTweets(TweetItem filteredTweet)
    {
        synchronized(mListLock)
        {
            if (mAdapter == null)
                refreshRecyclerView(filteredTweet);
            else
                mAdapter.addItemInFront(filteredTweet);

            removeTweetAfterDelay(filteredTweet);
        }
    }

    /**
     * Post a runnable after a TWEET_LIFE_SPAN to remove the old tweets from the list.
     * @param tweetToBeRemoved Tweet to be removed after the delay TWEET_LIFE_SPAN.
     */
    private void removeTweetAfterDelay(final TweetItem tweetToBeRemoved)
    {
        mMainThreadHandler.postDelayed(new Runnable()
        {
            public void run()
            {
                Log.d(TAG,"removing tweet with ID: " + tweetToBeRemoved.getId());
                synchronized(mListLock)
                {
                    if (!isConnected)
                        mTweetItemToBeRemovedList.add(tweetToBeRemoved);
                    else
                        mAdapter.removeItem(tweetToBeRemoved);
                }
            }
        }, TWEET_LIFE_SPAN);
    }

    private static void hideSoftKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Updates the RecyclerView with the new received data.
     */
    private void refreshRecyclerView(TweetItem filteredTweet)
    {
//        if (isRecyclerViewOnMovement)
//            return;

        List<TweetItem> tmpList = new ArrayList<>();
        tmpList.add(filteredTweet);
        mAdapter = new TweetsListAdapter(MainActivity.this, tmpList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isRecyclerOnHold = true;
                mRecyclerView.stopScroll();
            break;

            case MotionEvent.ACTION_MOVE:
            {
                System.out.println("action_move");
            }
            break;

            case MotionEvent.ACTION_UP:
                isRecyclerOnHold = false;
            break;
        }
        return false;
    }

    /**
     * Loops over the mTweetItemToBeRemovedList and removes the those
     * TweetItem from the recyclerViwe.
     */
    private void removePendingTweets()
    {
        for (TweetItem tw : mTweetItemToBeRemovedList)
        {
            mAdapter.removeItem(tw);
        }
    }

    private class ConnectivityChangeReceiver
            extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            debugIntent(intent, "grokkingandroid");
        }

        private void debugIntent(Intent intent, String tag)
        {
            Bundle extras = intent.getExtras();

            if (extras != null)
            {
                if (extras.getBoolean("noConnectivity"))
                    isConnected = false;
                else
                {
                    isConnected = true;
                    removePendingTweets();
                }

                Log.v(tag, "isConnected: " + isConnected);
            }
            else
                Log.v(tag, "no extras");
        }
    }
}
