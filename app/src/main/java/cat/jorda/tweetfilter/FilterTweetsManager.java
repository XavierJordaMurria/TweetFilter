package cat.jorda.tweetfilter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cat.jorda.tweetfilter.model.TweetItem;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by xj1 on 19/09/2017.
 */

public class FilterTweetsManager implements StatusListener
{
    private final static String TAG = FilterTweetsManager.class.getSimpleName();
    private TwitterStream mTwitterStream;
    private IFilterTweetsListener mListener;

    final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    public interface IFilterTweetsListener
    {
        void onFilteredTweets(TweetItem filteredTweet);
    }

    public FilterTweetsManager(IFilterTweetsListener listener)
    {
        Log.d(TAG, "FilterTweetsManager constructor");

        mListener = listener;

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(Constants.CONSUMER_KEY);
        cb.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
        cb.setOAuthAccessToken(Constants.ACCESS_TOKEN);
        cb.setOAuthAccessTokenSecret(Constants.ACCESS_TOKEN_SECRET);

        mTwitterStream = new TwitterStreamFactory(cb.build()).getInstance();
    }

    public void start(String[] keywords)
    {
        FilterQuery fq = new FilterQuery();

        fq.track(keywords);

        mTwitterStream.addListener(this);
        mTwitterStream.filter(fq);
    }

    public void stopStreaming()
    {
        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                mTwitterStream.cleanUp(); // shutdown internal stream consuming thread
                mTwitterStream.shutdown(); // Shuts down internal dispatcher thread shared by all TwitterStream instances.
            }
        });

    }

    @Override
    public void onException(Exception arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatus(twitter4j.Status status) {
        Log.d(TAG, "onStatus " + status.getText());

//        collected_.add(new TweetItem(status.getId(), status.getText(), status.getCreatedAt()));
        TweetItem tweetItem = new TweetItem(status.getId(), status.getText(), status.getCreatedAt());
        dispatchToMainThread(tweetItem);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onScrubGeo(long arg0, long arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStallWarning(StallWarning warning) {
    }

    @Override
    public void onTrackLimitationNotice(int arg0) {
        // TODO Auto-generated method stub
    }

    private void dispatchToMainThread(final TweetItem filteredTweet)
    {
        if (mListener == null)
            return;

        mMainThreadHandler.post(new Runnable()
        {
            @Override
            public void run() {
                mListener.onFilteredTweets(filteredTweet);
            }
        });
    }
}
