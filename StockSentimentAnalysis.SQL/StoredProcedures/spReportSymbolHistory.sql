use stocksentiment;

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

/*
use socialmonitor;
call spReportSymbolHistory("HPQ", 1);
*/
