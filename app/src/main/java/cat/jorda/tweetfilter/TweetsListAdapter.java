package cat.jorda.tweetfilter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cat.jorda.tweetfilter.model.TweetItem;

/**
 * Created by xj1 on 19/09/2017.
 */

public class TweetsListAdapter extends RecyclerView.Adapter<TweetsListAdapter.CustomViewHolder>
{
    private List<TweetItem> mTweetItemList;
    private Context mContext;

    public TweetsListAdapter(Context context, List<TweetItem> tweetItemList)
    {
        mTweetItemList = tweetItemList;
        mContext = context;
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

    public void reloadList(List<TweetItem> filteredTweetList)
    {
        mTweetItemList.addAll(filteredTweetList);
        notifyDataSetChanged();
    }

    public void removeListOfTweets(List<TweetItem> listOfTweetsToBeRemoved)
    {
        mTweetItemList.removeAll(listOfTweetsToBeRemoved);
        notifyDataSetChanged();
    }

    public void removeItem(TweetItem tweetsToBeRemoved)
    {
        mTweetItemList.remove(tweetsToBeRemoved);
        notifyDataSetChanged();
    }

    public void addItemInFront(TweetItem filteredTweet)
    {
        mTweetItemList.add(0, filteredTweet);
        notifyItemInserted(0);
    }

    public void addListOfTweets(List<TweetItem> listOfTweets)
    {
        mTweetItemList.addAll(listOfTweets);
        notifyDataSetChanged();
    }

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
