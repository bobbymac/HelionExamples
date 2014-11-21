use stocksentiment;

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


