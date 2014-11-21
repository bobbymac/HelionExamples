use stocksentiment;

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

/*
call spRetrieveTweetStream(null);
call spRetrieveTweetStream('2014-02-27 16:00:36');

call spRetrieveTweetStream(0, 0);

call spRetrieveTweetStream('2014-02-26 17:34:30');
*/


/*
use socialmonitor;
select * from TweetStream limit 100;

create procedure spRetrieveTweetStream
(
	in locStartDate		datetime, 
	in locEndDate		datetime
)
begin

	if locStartDate is null then
		select locStartDate=now();
	end if;

	if locEndDate is null then
		select locEndDate=now();
	end if;

	select TweetDateTime, TweetText 
	from TweetStream 
	where TweetDateTime>locStartDate
	and TweetDateTime<=locEndDate
	order by TweetDateTime asc;
end$$

delimiter ;
*/

