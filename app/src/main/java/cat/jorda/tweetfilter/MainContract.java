package cat.jorda.tweetfilter;
/**
 * Created by xj1 on 21/09/2017.
 *
 * The interface that exposes the functionalities of The MainView and MainPresenter
 */
interface MainContract
{
    interface IMainView
    {
        /**
         * Callback call for when the adapter is being set.
         * @param adapter adapter set up.
         */
        void onAdapterSet(TweetsListAdapter adapter);
    }

    interface IMainPresenter
    {
        /**
         * Set a new arrays of key words that will be used for the Twitter API for the streaming filter.
         * @param filterKeyWords String[] containing the keyworkds to be used by the filter().
         */
        void setNewFilter(String[] filterKeyWords);
        void startTweetStreaming();
        void stopTweetStreaming();

        /**
         * Informs the presenter of the Touch events over the RecyclerView.
         * @param isBeingTouched onTouchDown should be true and set it to false onTouchUp.
         */
        void recyclerViewOnTouch(boolean isBeingTouched);
        void unregisterBroadcasts();
        void registerBroadcasts();
    }
}