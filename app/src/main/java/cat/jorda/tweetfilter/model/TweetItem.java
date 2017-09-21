package cat.jorda.tweetfilter.model;

import java.util.Date;

/**
 * Created by xj1 on 19/09/2017.
 */

public class TweetItem
{
    private long id_;
    private String text_;
    private Date creationDate_;

    public TweetItem(long id, String text, Date creationDate)
    {
        id_ = id;
        text_   = text;
        creationDate_   = creationDate;
    }

    public String getText()
    {
        return text_;
    }

    public void setText(String text)
    {
        text_ = text;
    }

    public Date getcreationDate()
    {
        return creationDate_;
    }

    public String getcreationDateStr()
    {
        return creationDate_.toString();
    }

    public long getId()
    {
        return id_;
    }

    public void setId(long id)
    {
        id_ = id;
    }

    public void setDate(Date creationDate)
    {
        creationDate_ = creationDate;
    }
}