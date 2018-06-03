# AI Trader
The AI trader starts in the year 1988 trading on the dax. It starts with an equialent of 1000 dax points.
Each day the AI decides (bdased on the history of the dax since 1988) whether to invest all money in the dax or to cash in all dax shares.
Cash money stayes the same, but invested money increases proptrional with the dax.

## The best strategy
Write a strategy, which successfully trades on the last 30 years of the dax.

## So far
###  Only invest when the dax increased for the last 3 days
Not so good!

###  Only invest when the dax increased yesterday
almost as good as keeping all money on the dax the whole time.

### ReverseStrategy: Only invest when the dax decreased yesterday
Not much better the keeping all the money in cash.
