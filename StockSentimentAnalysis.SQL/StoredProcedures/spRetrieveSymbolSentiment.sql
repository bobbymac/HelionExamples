use stocksentiment;

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

/*
call spRetrieveSymbolSentiment("HPQ", 5);
*/