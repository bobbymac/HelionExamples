use stocksentiment;

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

/*
call spReportAllSymbolSummary(30);


select * from StockSentimentStream where Symbol='GOOG';
*/
