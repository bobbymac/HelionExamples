use stocksentiment;

create table TweetStream
(
TweetDateTime 	DateTime,
TweetText 		nvarchar(200)
);

/*
insert into TweetStream(TweetDateTime, TweetText) values ("2014-02-07 17:21:09", "Test");

select now()

select * from TweetStream
*/