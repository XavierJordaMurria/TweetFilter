package cat.jorda.tweetfilter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import cat.jorda.tweetfilter.model.TweetItem;

/**
 * Created by xj1 on 19/09/2017.
 */

public class TweetsListAdapter extends RecyclerView.Adapter<TweetsListAdapter.CustomViewHolder>
{
    private final static String TAG = TweetsListAdapter.class.getSimpleName();
    private List<TweetItem> mTweetItemList;

    public TweetsListAdapter(List<TweetItem> tweetItemList)
    {
        mTweetItemList = tweetItemList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tweet_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i)
    {
        TweetItem tweetItem = mTweetItemList.get(i);

        //Setting text view title
        customViewHolder.mTweetText.setText(tweetItem.getText());
        customViewHolder.mTweetCreationDate.setText(tweetItem.getcreationDateStr());
    }

    @Override
    public int getItemCount()
    {
        return (null != mTweetItemList ? mTweetItemList.size() : 0);
    }

    /**
     * Removes a list of items into the adapter's inner list, and notifies of a data set has been changed.
     * @param listOfTweetsToBeRemoved list to be removed.
     */
    void removeListOfTweets(List<TweetItem> listOfTweetsToBeRemoved)
    {
        Log.d(TAG,"removeListOfTweets #" + listOfTweetsToBeRemoved.size());
        mTweetItemList.removeAll(listOfTweetsToBeRemoved);
        notifyDataSetChanged();
    }

    /**
     * Removes the passed item from the inner adapter list, and notifies of a data set has been changed.
     * @param tweetsToBeRemoved Item to be removed.
     */
    void removeItem(TweetItem tweetsToBeRemoved)
    {
        mTweetItemList.remove(tweetsToBeRemoved);
        notifyDataSetChanged();
    }

    /**
     * Add a single item to the front of the adapter's inner list, and notifies of a data set has been changed.
     * @param filteredTweet item to be added.
     */
    void addItemInFront(TweetItem filteredTweet)
    {
        mTweetItemList.add(0, filteredTweet);
        notifyItemInserted(0);
    }

    /**
     * Adds a list of items into the adapter's inner list, and notifies of a data set has been changed.
     * @param listOfTweets list to be added.
     */
    void addListOfTweets(List<TweetItem> listOfTweets)
    {
        Log.d(TAG,"addListOfTweets #" + listOfTweets.size());
        mTweetItemList.addAll(listOfTweets);
        notifyDataSetChanged();
    }

    /**
     * View holder holding the UI components from the tweet_item.xml
     */
    class CustomViewHolder extends RecyclerView.ViewHolder
    {
        TextView mTweetText;
        TextView mTweetCreationDate;

        public CustomViewHolder(View view)
        {
            super(view);
            mTweetText = view.findViewById(R.id.tweet_txt);
            mTweetCreationDate = view.findViewById(R.id.tweet_creation_date);
        }
    }
}
