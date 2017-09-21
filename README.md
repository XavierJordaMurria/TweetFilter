
# TweetsFilter
This will stream filtered tweets from Twitter using its API hbc-twitter4j.
## Need to add your own Customer_(Key/Secret) Token_(Key/Secret) to make the Twitter API work.
## Technical specifications
* Using MVP architecture.
* Stoping updating the RecyclerView when the user is touching it. So it improves the readibility.
* Get Twitter’s public feed using its streaming API: https://stream.twitter.com/1.1/statuses/filter.json
* Search for a word provided by the user. That should produce a flow of data big enough for our purposes.
* Once collected, the tweets will be included in a RecyclerView.
* Every tweet has a lifespan, meaning that after that time, they will have to be removed from the list.
* Make this lifespan easy to tune in code.
* If the connection is interrupted or the app is run offline, the tweets that were alive during the previous execution will be shown until a connection is established.
* Use stock Android UI elements.
* If you use some 3rd party library, explain why.
    * I have chosen the hbc-twitter4j library supported by Twitter because:
    * It runs in its own Bacground Thread.
    * It handles mandtains and signs the HTTPS request.
    * It supports all the Twitter's REST API calling a very intuitive methods.
    * hbc is good at network reconnecting and network error handling in the background. This gives you a stable real time stream  especially with a bad network.

