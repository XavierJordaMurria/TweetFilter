package cat.jorda.tweetfilter;

import cat.jorda.tweetfilter.model.TweetItem;

/**
 * Created by xj1 on 21/09/2017.
 *
 * The interface that exposes the functionalities of The MainView and MainPresenter
 */
interface MainContract
{
    interface IMainView
    {
        void onAdapterSet(TweetsListAdapter adapter);
    }

    interface IMainPresenter
    {
        void setNewFilter(String[] filterKeyWords);
        void startTweetStreaming();
        void stopTweetStreaming();
        void recyclerViewOnTouch(boolean isBeingTouched);
        void unregisterBroadcasts();
        void registerBroadcasts();
    }
}