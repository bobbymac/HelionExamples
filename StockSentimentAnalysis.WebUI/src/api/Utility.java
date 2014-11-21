package api;

import java.sql.DriverManager;
import java.sql.SQLException;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

public final class Utility {

	public static Object getMySQLConnection() throws SQLException 
	{
		//Sample External URL:  jdbc:mysql://15.125.35.31:3306/stocksentiment?user=admin&password=ALS1234!
		
		String vcap_services = System.getenv("VCAP_SERVICES");
		String dburl_external = System.getenv("EXTERNAL_DATABASE_URL");				//To use a database not attached to ALS cluster, set this env variable
		
		if(dburl_external!=null && dburl_external.length()>0)
		{
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
	
}
