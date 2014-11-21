use stocksentiment;

/*
drop table StockSentimentStream;
*/

create table StockSentimentStream
(
CreateDate	 	DateTime,
Symbol			nvarchar(10),
IsRetweet		bit,
IsPositive		bit,
IsNegative		bit
);

/*
-- select * from StockSentimentStream

select Symbol, count(Symbol) as "Total", sum(IsRetweet), sum(IsPositive), sum(IsNegative) from StockSentimentStream group by Symbol
*/

CREATE INDEX idx_StockSentimentStream_Symbol ON StockSentimentStream (Symbol);

CREATE INDEX idx_StockSentimentStream_CreateDate ON StockSentimentStream (CreateDate);