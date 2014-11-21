package HP.StockSentimentAnalysis.MessageProcessor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
//import java.util.Locale;



import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;



//import com.mysql.CallableStatement;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import java.sql.DriverManager;
import java.sql.SQLException;

//import java.text.SimpleDateFormat;

public class MessageProcessor {

	public static void main(String[] args) throws IOException,
			ShutdownSignalException, ConsumerCancelledException,
			InterruptedException, KeyManagementException,
			NoSuchAlgorithmException, URISyntaxException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		// parameters
		boolean autoAck = false;

		// retrieve environment for message queue
		String rabbiturl_calculated = null;
		String qurl_external = System.getenv("EXTERNAL_RABBITMQ_URL");			//Specified in manifest.yml in place of ALS container specific
		String qurl = System.getenv("RABBITMQ_URL");							//This is an ALS container specific URL - not something set in manifest.yml	
		String rabbitqueue = System.getenv("RABBITMQ_QUEUE");
		String tracks = System.getenv("TRACK_TERMS");

		if (qurl_external!=null)
		{
			System.out.println("Using external message queue:  " + qurl_external);
			rabbiturl_calculated=qurl_external;					
		}
		else if (qurl!=null)
		{
			System.out.println("Using ALS attached message queue:  " + qurl);
			rabbiturl_calculated=qurl;			
		}	

		// validate the URL has data (object will parse/validate)
		if ((rabbiturl_calculated != null) && (tracks != null)) 
		{
			// split up the tracking terms here, comma separated
			String terms[] = tracks.split(",");
			
			//Add $ symbol to from of each symbol if needed
			for( int i = 0; i < terms.length; i++)
			{
			    String term = terms[i];
			    
			    if (term.indexOf("$")<0)
			    {
			    	term="$" + term;
			    	terms[i]= term;
			    }			    
			}
			
			for( int i = 0; i < terms.length; i++)
			{
			    String term = terms[i];
			    
			    System.out.println(" INFO:  Tracking term: " + term);		    
			}

			// configure the mysql connection from the url string
			// load up the MySQL driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// WARNING: jdbc url format is different than CF:
			// jdbc:mysql://host:port/db?user=user&password=password
			// connect to the database. This will exception if the DB is down
			java.sql.Connection dbconn;
			dbconn = null;
			// configure the rabbit connection from the URL string
			ConnectionFactory mqfactory = null;
			// connect to the queue
			Connection mqconnection = null;
			Channel mqchannel = null;
			// set up consumer
			QueueingConsumer mqconsumer = null;

			// create an uninitialized channel

			while (dbconn == null) 
			{
				try 
				{
					dbconn=(java.sql.Connection)getMySQLConnection();
					
					if (dbconn==null)
					{
						System.out.println("ERROR:  Unable to connect to database");
					}
					
					
					if (mqfactory == null) {
						// only connect to rabbit if not connected already - handle sql retries which happen a lot.
						mqfactory = new ConnectionFactory();
						mqfactory.setUri(rabbiturl_calculated);
						mqconnection = mqfactory.newConnection();
						mqchannel = mqconnection.createChannel();
						mqchannel.queueDeclare(rabbitqueue, true, false, false,
								null);
						mqconsumer = new QueueingConsumer(mqchannel);
					}

					// get handles to our stored procedures here for speed
					if (dbconn != null) {
						java.sql.CallableStatement insertTweet;
						java.sql.CallableStatement insertSentiment;
						insertTweet = dbconn
								.prepareCall("{call spInsertTweet(?, ?)}");
						insertSentiment = dbconn
								.prepareCall("{call spInsertStockSentiment(?, ?, ?, ?, ?)}");

						System.out.println(" INFO: Waiting for messages. To exit press CTRL+C");

						mqchannel
								.basicConsume(rabbitqueue, autoAck, mqconsumer);

						while (true) {
							QueueingConsumer.Delivery delivery = mqconsumer.nextDelivery();
							
							//Grab the message body
							String message = new String(delivery.getBody());

							JSONParser parser = new JSONParser();
							try {
								Object obj = parser.parse(message.trim());
								JSONObject json = (JSONObject) obj;
								Date time;
								// System.out.println(" INFO: " +
								// json.toString());
								Long lTime = (Long) json.get("time");
								time = new Date(lTime);
								java.sql.Timestamp tweettime = new java.sql.Timestamp(
										lTime);
								String user = (String) json.get("user");
								String text = (String) json.get("text");

//								System.out.println(" INFO: Received @" + user
//										+ " sent at " + time + " " + text);
//								long start = System.nanoTime();
								

								boolean bNeg = SentimentTools.IsNegative(text);
								boolean bPos = SentimentTools.IsPositive(text);
								boolean bRT = SentimentTools.IsRetweet(text);

								/*
								 * System.out.println(" INFO: Assessment Negative: "
								 * + bNeg + " Positive: " + bPos + " RT: " + bRT
								 * + " Sentiment: " +
								 * SentimentTools.getNetSentimentDescription
								 * (bPos, bNeg));
								 */

								System.out.println("INFO: Begin spInsertStockSentiment:  Received @" + user
										+ " sent at " + time + " " + text);
								long start = System.nanoTime();

								for (String term : terms) {
									String textlower = text.toLowerCase();
									if (textlower.contains(term.toLowerCase())) {
										insertSentiment.setTimestamp(1, tweettime);
										insertSentiment.setString(2, term.replace("$", ""));
										insertSentiment.setBoolean(3, bRT);
										insertSentiment.setBoolean(4, bPos);
										insertSentiment.setBoolean(5, bNeg);
										insertSentiment.execute();
									}
								}
								long stop = System.nanoTime();
								long elapsed = (stop-start)/1000000;
								
								//AIRLIFT:
								System.out.println("INFO:  spInsertStockSentiment Complete:  " + elapsed + "ms , " + user + " - " + time);
								
								start = System.nanoTime();  // time next insert
								

								// store the tweet as well for giggles
								insertTweet.setTimestamp(1, tweettime);
								try {
									insertTweet.setString(2, text);
									insertTweet.execute();
								} catch (SQLException sqex) {
									System.out
											.println("WARN: Tweet insert failed"
													+ sqex.toString());
								}

								stop = System.nanoTime();
								elapsed = (stop-start)/1000000;
								
								//AIRLIFT:  
								System.out.println("INFO:  spInsertTweet Complete:  " + elapsed + "ms , " + user + " - " + time);
								
								// ack the message
								mqchannel.basicAck(delivery.getEnvelope()
										.getDeliveryTag(), false);


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				} catch (SQLException e) {
					System.out
							.println(" ERR: Cannot connect to database or database error caught.  Reconnecting in 10 seconds. "
									+ e.toString());

					dbconn = null; // start with a new one next time around.
					Thread.sleep(10000);

				}// end sql catch
			}// end retry while

		} 
		else 
		{
			System.out
					.println(" ERR:  Invalid/Missing RABBITMQ_URL and/or DATABASE_URL and/or TRACK_TERMS in environment.  (Check Config)");
		}

	} // end parameter validation
	
	private static Object getMySQLConnection() throws SQLException 
	{
		//Sample External URL:  jdbc:mysql://15.125.35.31:3306/stocksentiment?user=admin&password=ALS1234!
			
		String vcap_services = System.getenv("VCAP_SERVICES");
		String dburl_external = System.getenv("EXTERNAL_DATABASE_URL");				//To use a database not attached to ALS cluster, set this env variable
		
		if(dburl_external!=null && dburl_external.length()>0)
		{
			System.out.println("Using External Database: " + dburl_external);
			return DriverManager.getConnection(dburl_external);
		}
		else
		{
			if (vcap_services != null && vcap_services.length()>0) 
			{
				try
				{
					JsonRootNode root = new JdomParser().parse(vcap_services);
					JsonNode mysqlNode = root.getNode("mysql");
					JsonNode credentials = mysqlNode.getNode(0).getNode("credentials");
					String dbname = credentials.getStringValue("name");
					String hostname = credentials.getStringValue("hostname");
					String user = credentials.getStringValue("user");
					String password = credentials.getStringValue("password");
					String port = credentials.getNumberValue("port");
					String dbUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname;
					Class.forName("com.mysql.jdbc.Driver");
					
					System.out.println("Using ALS Attached Database: " + dbUrl);
					return DriverManager.getConnection(dbUrl, user, password);
				}
				catch (Exception e)
				{
					throw new SQLException(e);
				}
			}
		}		
		
		return null;
	}
} // end class def
