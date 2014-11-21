package api;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SymbolHistory
 */
@WebServlet("/api/SymbolSummary")
public class SymbolSummary extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private int nDays=1;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SymbolSummary() {
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
			
			if (request.getParameter("d")!=null)
			{
				nDays=Integer.parseInt(request.getParameter("d"));
			}
			else
			{
				nDays=7;				
			}
			
			if ((nDays>0 && nDays<1000) )
			{
				//String url = "jdbc:mysql://15.125.35.31:3306/stocksentiment?user=admin&password=ALS1234!";
				//con = DriverManager.getConnection(url);
				con=(Connection)api.Utility.getMySQLConnection();
				stmt = con.createStatement();
				rs = stmt.executeQuery("call spReportAllSymbolSummary(" + nDays + ")");
							
				//Return results
				response.setContentType("application/json");
				response.getWriter().write(toJSON(rs));
				
				rs.close();
			}		
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
		List<dataStructures.SymbolSummaryDataPoint> listData=new ArrayList<dataStructures.SymbolSummaryDataPoint>();
	    
	    while(rs.next() ) 
	    {
	    	dataStructures.SymbolSummaryDataPoint dp=new dataStructures.SymbolSummaryDataPoint();
	    	//dp.Symbol=rs.getString("Symbol");
	    	
	    	dp.Symbol = "<a href='SymbolHistory.jsp?d=" + nDays + "&s=" + rs.getString("Symbol") + "'>" + rs.getString("Symbol") +"</a>";
	    	
	    	dp.TotalTweets=rs.getInt("Total Tweets");
	    	dp.Retweets=rs.getInt("Retweets");
	    	dp.NetSentiment=rs.getInt("Net Sentiment");
	    	
			listData.add(dp);
	    }
	    
	    Gson gson=new Gson();	    

	    return gson.toJson(listData);
	}
}

