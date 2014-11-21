package HP.StockSentimentAnalysis.DataCollection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
//import java.util.Map;
import java.util.Properties;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.ITweet;
import winterwell.jtwitter.TwitterStream;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import org.json.simple.JSONObject;

public class DataCollection {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException,
			InterruptedException, KeyManagementException,
			NoSuchAlgorithmException, URISyntaxException {

		try
		{
			// grab config from environment
			String apikey = System.getenv("TWAPIKEY");
			String apisecret = System.getenv("TWAPISECRET");
			String appkey = System.getenv("TWTOKEN");
			String appsecret = System.getenv("TWTOKENSECRET");
			String qname = System.getenv("RABBITMQ_QUEUE");
			String qurl_external = System.getenv("EXTERNAL_RABBITMQ_URL");			//Specified in manifest.yml in place of ALS container specific
			String qurl = System.getenv("RABBITMQ_URL");			//This is an ALS container specific URL - not something set in manifest.yml
			String trackterms = System.getenv("TRACK_TERMS");
			String sProxyEnabled = System.getenv("PROXY_ENABLED").trim();
			
			if (qurl != null || qurl_external !=null) 
			{
				System.out.println(new Date() + "INFO:  Initializing Stock Sentiment Data Collection Engine v1.0.20141113");
				
				//Set up rabbit
				ConnectionFactory factory = new ConnectionFactory();
				
				if (qurl_external!=null)
				{
					System.out.println("Using external message queue:  " + qurl_external);
					factory.setUri(qurl_external);					
				}
				else if (qurl!=null)
				{
					System.out.println("Using ALS attached message queue:  " + qurl);
					factory.setUri(qurl);					
				}				

				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				channel.queueDeclare(qname, true, false, false, null);

				if (sProxyEnabled.equals("1"))
				{
					System.out.println("Proxy is ENABLED.");
					//Setting proxy - //http://web-proxy.uswest.hpcloud.net:8080
					Properties systemSettings = System.getProperties();
					systemSettings.put("http.proxyHost", System.getenv("APP_HTTP_PROXY_HOST").trim());
					systemSettings.put("http.proxyPort", System.getenv("APP_HTTP_PROXY_PORT").trim());
					systemSettings.put("https.proxyHost", System.getenv("APP_HTTPS_PROXY_HOST").trim());
					systemSettings.put("https.proxyPort", System.getenv("APP_HTTPS_PROXY_PORT").trim());
					
					/*systemSettings.put("http.proxyHost", "http://web-proxy.uswest.hpcloud.net");
					systemSettings.put("http.proxyPort", "8080");
					systemSettings.put("https.proxyHost", "http://web-proxy.uswest.hpcloud.net");
					systemSettings.put("https.proxyPort", "8080");*/
					
					System.setProperties(systemSettings);
				}
				else
				{
					System.out.println("Proxy is DISABLED.");					
				}

				/*System.out.println("************************** ENVIRONMENT VARIABLES **************************");
				Map<String, String> env = System.getenv();
				for (String envName : env.keySet()) 
				{
				    System.out.format("%s=%s%n", envName, env.get(envName));
				}
				System.out.println("***************************************************************************");*/

				OAuthSignpostClient oauth = new OAuthSignpostClient(apikey, apisecret, appkey, appsecret);

				Twitter twitter = new Twitter("stocksentiment", oauth);
				System.out.println(new Date()
						+ " INFO:  Connected to Twitter via JTwitter");

				TwitterStream ts = new TwitterStream(twitter);
				
				String arrTerms[] = trackterms.split(",");
				List<String> terms = Arrays.asList(arrTerms);
				
				//Add $ symbol to from of each symbol if needed
				for( int i = 0; i < terms.size(); i++)
				{
				    String term = terms.get(i);
				    
				    if (term.indexOf("$")<0)
				    {
				    	term="$" + term;
				    	terms.set(i, term);
				    }			    
				}
				
				for( int i = 0; i < terms.size(); i++)
				{
				    String term = terms.get(i);
				    
				    System.out.println(new Date() + " INFO:  Tracking term: " + term);		    
				}		
				

				ts.setTrackKeywords(terms);
				ts.setAutoReconnect(true);

				ts.connect();

				//Counter to detect if how many loops pass with no tweets - could indicate a problem with the connection
				int nNoTweetLoops=0;
				int nResetAfter_sec=300;	//Reset the connection after this many seconds
				int nPauseTime_ms=1000;
				
				while (ts.isAlive()) {
					
					//Pause to wait for tweets
					Thread.sleep(nPauseTime_ms);
					List<ITweet> tweets = ts.popTweets();
					int numtweets = tweets.size();
					
					if (numtweets>0)
					{
						for (int msgRead = 0; msgRead < numtweets; msgRead++) {
							ITweet tweet = tweets.get(msgRead);

							JSONObject json = new JSONObject();

							//System.out.println(tweet.getCreatedAt() + " INFO: Displatching tweet to queue. " );
							long epoch = tweet.getCreatedAt().getTime();
							json.put("time", epoch);
							json.put("user", tweet.getUser().toString());
							json.put("text", tweet.getDisplayText().toString());

							System.out.println(tweet.getCreatedAt() + " TWEET: " + tweet.getUser().toString() + " - " + tweet.getDisplayText().toString());
							
							// publish the tweet to a queue.
							channel.basicPublish("", qname, null, json.toJSONString().getBytes());
						}
						
						nNoTweetLoops=0;
					}
					else
					{
						nNoTweetLoops++;				
					}

					if (nNoTweetLoops>((nResetAfter_sec*1000)/nPauseTime_ms))
					{
						//Reset the connection
						System.out.println("WARNING:  Forcing a connection reset.");
						ts.close();
						ts = new TwitterStream(twitter);
						ts.setTrackKeywords(terms);
						ts.setAutoReconnect(true);

						ts.connect();
						
						nNoTweetLoops=0;
					}
				}
				
				ts.close();
			} 
			else 
			{
				System.out.println(new Date() + " ERR:  Invalid Rabbit Queue URL (check config)");
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
