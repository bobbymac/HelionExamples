<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
		int nDays=1;

		if (request.getParameter("d")!=null)
		{
			nDays=Integer.parseInt(request.getParameter("d"));
		}

       	String sSymbol=request.getParameter("s");
       
       	if (sSymbol==null)
       	{
       		sSymbol="HPQ";            		
       	}
       	
       	sSymbol=sSymbol.toUpperCase();
       	
       	String apiURL="api/SymbolSummary?d=" + nDays;
       	
       	String sIsSelected_1="";
       	if (nDays==1){sIsSelected_1="SELECTED";} 
       	
       	String sIsSelected_7="";
       	if (nDays==7){sIsSelected_7="SELECTED";} 
       	
       	String sIsSelected_30="";
       	if (nDays==30){sIsSelected_30="SELECTED";} 
	%>           
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
    function GetURL()
    {
        var txtSymbol = document.getElementById("txtSymbol");
        var ddTimeframe = document.getElementById("ddTimeframe");        
        
        var sURL="SymbolHistory.jsp?d=" + ddTimeframe.value + "&s=" + txtSymbol.value;
        return sURL;   
    }

    function btnSubmit_OnClick() {
        document.location=GetURL();
        return false;
    }

    function openWindow(window_src) {
        window.open(window_src, 'RTTweetStream', config = 'height=500,width=1000,toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, directories=no, status=no');
    }

    function openQuoteWindow() {
    	var txtSymbol = document.getElementById("txtSymbol");
        var sURL = "http://finance.yahoo.com/q?ql=1&s=" + txtSymbol.value;
        window.open(sURL, 'Quote', config = 'height=700,width=1100,toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, directories=no, status=no');
    }
    
    function formSubmit()
    {
    	btnSubmit_OnClick();
    	return false;
    }
          
	$(document).ready(function()
	{
	    var refreshId = setInterval( function()
	    { 
	    	var grid = $("#gridSymbolSummary").data("kendoGrid");
	    	grid.dataSource.read();
	    	grid.dataSource.refresh();
	    }, 15000);
	});
    //-->
    </script>
    
</head>
<body>
    <form id="form1">
    <div id="wrap">
        <div id="header"><a href="http://www.hp.com"><img src="resources/images/hp-logo.gif" alt="HP Logo" id="hplogo" border="0"></a><span id="title">Stock Tweet Monitor</span><span id="subtitle">HP Helion Development Platform Demo</span></div>
        <div style="padding-bottom:10px; padding-top: 5px;">
            <span class="MenuBarButton_Selected" style="width:150px;">All Symbol Summary</span>&nbsp;
            <a href="SymbolHistory.jsp" class="MenuBarButton" style="width:150px;">Symbol History</a>&nbsp;
            <a href="#" onclick="openWindow('TweetStream.jsp');" class="MenuBarButton" style="width:200px;">Open Realtime Tweet Stream</a>
            <span class="MenuBarPlaceholder" style="width:263px;">&nbsp;</span>       
        </div>
        <div style="padding-left:70px; padding-top:30px;">        
            <div style="padding-bottom:30px;">
                <b>Enter Stock Symbol:</b>&nbsp;<input type="text" value="<%= sSymbol%>" id="txtSymbol" name="txtSymbol">
                 <select id="ddTimeframe" name="ddTimeframe" onchange="return btnSubmit_OnClick();">
				  <option value="1" <%=sIsSelected_1 %>>Last Day</option>
				  <option value="7" <%=sIsSelected_7 %>>Last Week</option>
				  <option value="30" <%=sIsSelected_30 %>>Last Month</option>
				</select> 
                <input type="button" onclick="return btnSubmit_OnClick();" value="Lookup">
                &nbsp;<a href="#" onclick="openQuoteWindow();">View Stock Details>></a> 
            </div>
            <div style="text-align:right; padding-bottom:10px; width:630px;">
            	<a href="index.jsp?d=1">Last Day</a>&nbsp;|&nbsp;
            	<a href="index.jsp?d=7">Last Week</a>&nbsp;|&nbsp;
            	<a href="index.jsp?d=30">Last Month</a>
            </div>
        </div>
		<div style="padding-left:60px; width:650px;">
			<kendo:grid name="gridSymbolSummary" pageable="false" height="400" sortable="false" filterable="false" groupable="false" scrollable="true">
				<kendo:grid-columns>
					<kendo:grid-column title="Symbol" field="Symbol" width="50" encoded="false"/>
					<kendo:grid-column title="Total Tweets" field="TotalTweets" width="50"/>
					<kendo:grid-column title="Net Sentiment" field="NetSentiment" width="50"/>
					<kendo:grid-column title="Retweets" field="Retweets" width="50"/>
				</kendo:grid-columns>
				<kendo:dataSource>
					<kendo:dataSource-transport read="<%= apiURL %>">
					</kendo:dataSource-transport>
				 </kendo:dataSource>	
			</kendo:grid>
		</div>
        <div id="footer"></div>
    </div>
    </form>
</body>
</html>