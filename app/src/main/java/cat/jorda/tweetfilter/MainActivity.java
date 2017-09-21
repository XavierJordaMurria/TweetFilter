package cat.jorda.tweetfilter;

import android.app.Activity;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainContract.IMainView, View.OnTouchListener
{
    private final static String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView emptyView;

    private String mFilterKeyWord;
    private EditText mFilterET;
    private ProgressBar mProgressBar;

    private MainContract.IMainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.tweets_list);
        emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setVisibility(View.VISIBLE);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

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
                    mPresenter.setNewFilter(new String[]{mFilterKeyWord});
                    mPresenter.startTweetStreaming();
                    mFilterET.setText(filterText);
                    hideSoftKeyboard(MainActivity.this);
                    mProgressBar.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setOnTouchListener(this);

        mPresenter = new MainPresenter(this, this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        mPresenter.registerBroadcasts();
        mPresenter.startTweetStreaming();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        mPresenter.unregisterBroadcasts();
        mPresenter.stopTweetStreaming();
    }

    private static void hideSoftKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mPresenter.recyclerViewOnTouch(true);
                break;

            case MotionEvent.ACTION_UP:
                mPresenter.recyclerViewOnTouch(false);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onAdapterSet(TweetsListAdapter adapter)
    {
        emptyView.setVisibility(View.GONE);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void stopProgressBar()
    {
        mProgressBar.setVisibility(View.GONE);
    }
}
