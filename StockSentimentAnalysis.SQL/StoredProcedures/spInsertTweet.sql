use stocksentiment;

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

/*
call InsertTweet(now(), "Test");

*/