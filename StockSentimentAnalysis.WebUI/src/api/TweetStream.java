package api;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import sun.misc.Regexp;

/**
 * Servlet implementation class SymbolHistory
 */
@WebServlet("/api/TweetStream")
public class TweetStream extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TweetStream() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection con = null;
		ResultSet rs = null;
		Statement stmt = null;
		
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			
			Cookie[] cookies = request.getCookies();

			String sStart = null;
			for(Cookie cookie : cookies)
			{
			    if("start".equals(cookie.getName()))
			    {
			    	sStart = cookie.getValue();
			    }
			}
			
			if (sStart=="")
			{
				sStart=null;
			}
			
			//String url = "jdbc:mysql://15.125.35.31:3306/stocksentiment?user=admin&password=ALS1234!";
			String sSQL=null;
			//con = DriverManager.getConnection(url);
			con=(Connection)api.Utility.getMySQLConnection();
			stmt = con.createStatement();
			
			if (sStart !=null)
			{
				sSQL="call spRetrieveTweetStream('" + sStart + "')";
			}
			else
			{
				sSQL="call spRetrieveTweetStream(null)";
			}			
			
			//sSQL="call spRetrieveTweetStream('2014-10-11 10:04:02.0')";
			rs = stmt.executeQuery(sSQL);
						
			//Return results
			response.setContentType("application/json");
			response.getWriter().write(toJSON(rs));
			
			//Set cookie to the last date retrieved
			try{
				rs.last();
				Cookie cookie = new Cookie("start", rs.getTimestamp("TweetDateTime").toString());
				//Cookie cookie = new Cookie("start", sStart);
				cookie.setMaxAge(60*10);	//Set cookie to expire in 10 minutes - if data is not refreshed by then, then start over
				response.addCookie(cookie);
			}
			catch (Exception e2)
			{
				/*//No results set timestamp to now
				java.util.Date now= new java.util.Date();				
				Cookie cookie = new Cookie("start", new Timestamp(now.getTime()).toString());
				cookie.setMaxAge(60*10);	//Set cookie to expire in 10 minutes - if data is not refreshed by then, then start over
				response.addCookie(cookie);*/
			}
			
			
			rs.close();	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	private String toJSON(ResultSet rs) throws SQLException, IOException 
	{
		List<dataStructures.Tweet> listData=new ArrayList<dataStructures.Tweet>();
	    
	    while(rs.next() ) 
	    {
	    	dataStructures.Tweet dp=new dataStructures.Tweet();
	    	dp.TweetDateTime=rs.getTimestamp("TweetDateTime").toString();
	    	dp.TweetText=ConvertUrlsToLinks(rs.getString("TweetText"));	//Grid does not support embedded links?
	    	//dp.TweetText=rs.getString("TweetText");
	    	
	    	//Strip off the ms
	    	if (dp.TweetDateTime.endsWith(".0")) 
	    	{
	    		dp.TweetDateTime = dp.TweetDateTime.substring(0, dp.TweetDateTime.length() - 2);
    		}
	    	
			listData.add(dp);
	    }
	    
	    Gson gson=new Gson();	    

	    return gson.toJson(listData);
	}
	
	private String ConvertUrlsToLinks(String msg)
    {
        return msg.replaceAll("((www\\.|(http|https|ftp|news|file)+\\:\\/\\/)[&#95;.a-z0-9-]+\\.[a-z0-9\\/&#95;:@=.+?,##%&~-]*[^.|\\'|\\# |!|\\(|?|,| |>|<|;|\\)])", "<a href=\"$1\" title=\"Click to open in a new window or tab\" target=\"&#95;blank\">$1</a>");
    }
}

