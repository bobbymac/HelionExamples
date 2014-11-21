create table TweetStream
(
TweetDateTime 	DateTime,
TweetText 		nvarchar(200)
);











create table StockSentimentStream
(
CreateDate	 	DateTime,
Symbol			nvarchar(10),
IsRetweet		bit,
IsPositive		bit,
IsNegative		bit
);

CREATE INDEX idx_StockSentimentStream_Symbol ON StockSentimentStream (Symbol);

CREATE INDEX idx_StockSentimentStream_CreateDate ON StockSentimentStream (CreateDate);










drop procedure if exists spInsertTweet;

delimiter $$
create procedure spInsertTweet
(
	in locTweetDateTime datetime, 
	in locTweetText nvarchar(250)
)
begin
	insert into TweetStream (TweetDateTime, TweetText) 
	values (locTweetDateTime, locTweetText);
end$$

delimiter ;







drop procedure if exists spInsertStockSentiment;

DELIMITER $$
CREATE PROCEDURE `spInsertStockSentiment`(
	in locCreateDate 		datetime, 
	in locSymbol 			nvarchar(10),
	in locIsRetweet			bit,
	in locIsPositive		bit,
	in locIsNegative		bit
)
begin
	insert into StockSentimentStream (CreateDate, Symbol, IsRetweet, IsPositive, IsNegative) 
	values (locCreateDate, locSymbol, locIsRetweet, locIsPositive, locIsNegative);
end$$
DELIMITER ;









drop procedure if exists spReportAllSymbolSummary;

delimiter $$
create procedure spReportAllSymbolSummary
(
	in locDaysToReport		int
)
begin
	select Symbol, count(Symbol) as "Total Tweets", 
	sum(IsRetweet) as "Retweets",
	sum(IsPositive)-sum(IsNegative) as "Net Sentiment"
	from StockSentimentStream
	where CreateDate > date_add(now(), INTERVAL -locDaysToReport DAY)
	group by Symbol
	order by count(Symbol) desc;
end$$

delimiter ;









drop procedure if exists spReportSymbolHistory;

delimiter $$
create procedure spReportSymbolHistory
(
	in locSymbol 			nvarchar(10),
	in locDaysToReport		int
)
begin
	if locDaysToReport=1 then
		select DATE_FORMAT(CreateDate, '%Y-%m-%d %H:00:00') AS CreateHour,
		count(Symbol) as "Count",
		sum(IsPositive)-sum(IsNegative) as "NetSentiment"
		from StockSentimentStream
		where CreateDate > date_add(now(), INTERVAL -locDaysToReport DAY)
		and Symbol=locSymbol
		and IsRetweet=0
		group by CreateHour
		order by CreateHour;
	else
		select DATE_FORMAT(CreateDate, '%Y-%m-%d 00:00:00') AS CreateHour,
		count(Symbol) as "Count",
		sum(IsPositive)-sum(IsNegative) as "NetSentiment"
		from StockSentimentStream
		where CreateDate > date_add(now(), INTERVAL -locDaysToReport DAY)
		and Symbol=locSymbol
		and IsRetweet=0
		group by CreateHour
		order by CreateHour;
	end if;
end$$

delimiter ;









drop procedure if exists spRetrieveSymbolSentiment;

delimiter $$
create procedure spRetrieveSymbolSentiment
(
	in locSymbol 			nvarchar(10),
	in locDaysToUse			int
)
begin
	select sum(IsPositive)-sum(IsNegative) as "NetSentiment"
	from StockSentimentStream
	where CreateDate > date_add(now(), INTERVAL -locDaysToUse DAY)
	and Symbol=locSymbol
	and IsRetweet=0;
end$$

delimiter ;










drop procedure if exists spRetrieveTweetStream;

delimiter $$
create procedure spRetrieveTweetStream
(
	in locStartDate		datetime
)
begin

	if locStartDate is null then
		select TweetDateTime, TweetText 
		from TweetStream
		order by TweetDateTime desc 
		limit 1;
	else
		select TweetDateTime, TweetText 
		from TweetStream 
		where TweetDateTime>=locStartDate
		order by TweetDateTime desc
		limit 20;
	end if;
end$$

delimiter ;