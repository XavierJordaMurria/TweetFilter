package cat.jorda.tweetfilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cat.jorda.tweetfilter.model.TweetItem;

/**
 * Created by xj1 on 21/09/2017.
 */

public class MainPresenter implements MainContract.IMainPresenter, FilterTweetsManager.IFilterTweetsListener
{
    private final static long TWEET_LIFE_SPAN = 2000;
    private final static String TAG = MainPresenter.class.getSimpleName();

    private Context mContext;
    private MainContract.IMainView mMainView;
    private FilterTweetsManager mFilterTweetsManager;
    private TweetsListAdapter mAdapter;
    private Object mListLock = new Object();
    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private String[] mFilterKeyWords;

    private List<TweetItem> mTweetItemToBeRemovedList = new ArrayList<>();
    private List<TweetItem> mBufferUpTweets  = new ArrayList<>();
    private boolean mHoldingRecyclerView = false;
    private boolean mIsConnected = true;

    private ConnectivityChangeReceiver mConnectivityChangeReceiver;

    public MainPresenter(Context context, MainContract.IMainView mainView)
    {
        mContext = context;
        mMainView = mainView;
        mFilterTweetsManager = new FilterTweetsManager(this);
    }

    @Override
    public void setNewFilter(String[] filterKeyWords)
    {
        mFilterKeyWords = filterKeyWords;
    }

    @Override
    public void startTweetStreaming()
    {
        if (mFilterKeyWords != null)
            mFilterTweetsManager.start(mFilterKeyWords);
    }

    @Override
    public void stopTweetStreaming()
    {
        mFilterTweetsManager.stopStreaming();
    }

    @Override
    public void recyclerViewOnTouch(boolean isBeingTouched)
    {
        mHoldingRecyclerView = isBeingTouched;

        if (!mHoldingRecyclerView && mAdapter != null)
        {
            mAdapter.addListOfTweets(mBufferUpTweets);
            removePendingTweets();
        }
    }

    @Override
    public void registerBroadcasts()
    {
        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mContext.registerReceiver(mConnectivityChangeReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void unregisterBroadcasts()
    {
        mContext.unregisterReceiver(mConnectivityChangeReceiver);
    }

    @Override
    public void onFilteredTweets(TweetItem filteredTweet)
    {
        synchronized(mListLock)
        {
            if (mAdapter == null)
                refreshRecyclerView(filteredTweet);
            else if (mHoldingRecyclerView)
                mBufferUpTweets.add(filteredTweet);
            else
                mAdapter.addItemInFront(filteredTweet);
        }

        removeTweetAfterDelay(filteredTweet);
    }
    /**
     * Updates the RecyclerView with the new received data.
     */
    private void refreshRecyclerView(TweetItem filteredTweet)
    {
        List<TweetItem> tmpList = new ArrayList<>();
        tmpList.add(filteredTweet);
        mAdapter = new TweetsListAdapter(tmpList);
        mMainView.onAdapterSet(mAdapter);
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
                synchronized(mListLock)
                {
                    if (!mIsConnected || mHoldingRecyclerView)
                        mTweetItemToBeRemovedList.add(tweetToBeRemoved);
                    else
                    {
                        Log.d(TAG,"removing tweet with ID: " + tweetToBeRemoved.getId());
                        mAdapter.removeItem(tweetToBeRemoved);
                    }
                }
            }
        }, TWEET_LIFE_SPAN);
    }

    /**
     * BroadCastReceiver set to receive information from the ConnectivityManager.CONNECTIVITY_ACTION
     */
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
                boolean noConnectivity = extras.getBoolean("noConnectivity");
                if (noConnectivity)
                    mIsConnected = false;
                else
                {
                    mIsConnected = true;
                    removePendingTweets();
                }

                Log.v(tag, "isConnected: " + !noConnectivity);
            }
            else
                Log.v(tag, "no extras");
        }
    }

    /**
     * Loops over the mTweetItemToBeRemovedList and removes the those
     * TweetItem from the recyclerViwe.
     */
    private void removePendingTweets()
    {
        if (mAdapter != null)
            mAdapter.removeListOfTweets(mTweetItemToBeRemovedList);
    }
}
