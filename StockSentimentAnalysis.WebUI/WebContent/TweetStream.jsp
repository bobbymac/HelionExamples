<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Stock Tweet Monitor - Symbol Tweet History</title>
    <link href="resources/styles/default.css" rel="stylesheet" type="text/css" />
    <link href="resources/styles/kendo.common.min.css" rel="stylesheet" type="text/css" />
	<link href="resources/styles/kendo.blueopal.min.css" rel="stylesheet" type="text/css" />
	<link href="resources/styles/kendo.dataviz.min.css" rel="stylesheet" type="text/css" />
	<link href="resources/styles/kendo.dataviz.default.min.css" rel="stylesheet" type="text/css" />
	<script src="resources/js/jquery.min.js"></script>
	<script src="resources/js/kendo.web.min.js"></script>
	<script src="resources/js/kendo.dataviz.min.js"></script>
    <script language="javascript" type="text/javascript">
    //<!--        
	$(document).ready(function()
	{
	    var refreshId = setInterval( function()
	    { 
	    	var grid = $("#gridTweets").data("kendoGrid");
	    	grid.dataSource.read();
	    	grid.dataSource.refresh();
	    }, 5000);
	});
    //-->
    </script>
    <style type="text/css">
	#gridTweets .k-loading-image {
	  /*Hide loading spinner on grid refresh*/
	  background-image: none!important;
	}
	#gridTweets .k-loading-mask {
	  /*Hide fadein() animation on grid refresh*/
	  display:none!important;
	  visibility:hidden!important;
	  background-color: transparent!important;
	  opacity: 0.0!important;
	  height: 0px!important;
	  overflow: hidden!important;
	}
	</style>
</head>
<body>
	<form id="form1">
    <div>
    	<kendo:grid name="gridTweets" pageable="false" height="430px" sortable="false" filterable="false" groupable="false">
	    	<kendo:grid-scrollable/>
	        <kendo:grid-columns>
	            <kendo:grid-column title="Date/Time" field="TweetDateTime" width="70px" filterable="false" />
	            <kendo:grid-column title="Tweet Text" field="TweetText" width="400px" encoded="false" />
	        </kendo:grid-columns>
	        <kendo:dataSource>
                <kendo:dataSource-transport read="api/TweetStream">
                </kendo:dataSource-transport>
            </kendo:dataSource>

	    </kendo:grid>
    </div>
    </form>
</body>
</html>