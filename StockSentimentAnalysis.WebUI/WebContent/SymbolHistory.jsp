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
       	
       	String apiURL="api/SymbolHistory?d=" + nDays + "&s=" + sSymbol;
       	
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
    //-->
    </script>
</head>
<body>
	<form id="form1" onsubmit="return formSubmit();">
    <div id="wrap">
        <div id="header"><a href="http://www.hp.com"><img src="resources/images/hp-logo.gif" alt="HP Logo" id="hplogo" border="0"></a><span id="title">Stock Tweet Monitor</span><span id="subtitle">HP Helion Development Platform Demo</span></div>
        <div style="padding-bottom:10px; padding-top: 5px;">
            <a href="index.jsp?d=7" class="MenuBarButton" style="width:150px;">All Symbol Summary</a>&nbsp;
            <span class="MenuBarButton_Selected" style="width:150px;">Symbol History</span>&nbsp;
            <a href="#" onclick="openWindow('TweetStream.jsp');" class="MenuBarButton" style="width:200px;">Open Realtime Tweet Stream</a>
            <span class="MenuBarPlaceholder" style="width:263px;">&nbsp;</span>       
        </div>
        <div style="padding-left:70px; padding-top:30px;">
            <div>
                <b>Enter Stock Symbol:</b>&nbsp;<input type="text" value="<%= sSymbol%>" id="txtSymbol" name="txtSymbol">
                 <select id="ddTimeframe" name="ddTimeframe" onchange="return btnSubmit_OnClick();">
				  <option value="1" <%=sIsSelected_1 %>>Last Day</option>
				  <option value="7" <%=sIsSelected_7 %>>Last Week</option>
				  <option value="30" <%=sIsSelected_30 %>>Last Month</option>
				</select> 
                <input type="button" onclick="return btnSubmit_OnClick();" value="Lookup">
                &nbsp;<a href="#" onclick="openQuoteWindow();">View Stock Details>></a>              
            </div>            
            <div style="padding-top: 10px;">
                <b>Current Sentiment For <%= sSymbol%>:</b>&nbsp;NEUTRAL                  
            </div>
            <div style="padding-top:20px;">
            <!-- 650x300 -->
                <div class="chart-wrapper" style="width: 650px; height: 400px">
			         <kendo:chart name="chartTotalTweets">
			             <kendo:chart-title text="Total Tweets" />
			             <kendo:chart-legend visible="false" />
			             <kendo:dataSource>
			                 <kendo:dataSource-transport read="<%= apiURL %>">
			                 </kendo:dataSource-transport>
			             </kendo:dataSource>
			             <kendo:chart-series>
			                <kendo:chart-seriesItem type="column" field="Count" name="Count" color="#F05332"/>
			             </kendo:chart-series>
			             <kendo:chart-categoryAxis>
			                <kendo:chart-categoryAxisItem field="CreateHour">
			                    <kendo:chart-categoryAxisItem-labels rotation="-45" step="3" />
			                    <kendo:chart-categoryAxisItem-majorGridLines visible="false"/>		                  
			                </kendo:chart-categoryAxisItem>
			             </kendo:chart-categoryAxis>
			             <kendo:chart-tooltip visible="true" template="#= category #: #= value # Tweets" />
			         </kendo:chart>
			     </div>
            </div>        
            <div style="padding-top:20px;">
                <div class="chart-wrapper" style="width: 650px; height: 400px">
			         <kendo:chart name="chartNetSentiment">
			             <kendo:chart-title text="Net Sentiment" />
			              <kendo:chart-legend visible="false" />
			             <kendo:dataSource>
			                 <kendo:dataSource-transport read="<%= apiURL %>">
			                 </kendo:dataSource-transport>
			             </kendo:dataSource>
			             <kendo:chart-series>
			                <kendo:chart-seriesItem type="column" field="NetSentiment" name="NetSentiment" color="#008B2B"/>
			             </kendo:chart-series>
			             <kendo:chart-categoryAxis>
			                <kendo:chart-categoryAxisItem field="CreateHour">
			                    <kendo:chart-categoryAxisItem-labels rotation="-45" step="3"/>
			                    <kendo:chart-categoryAxisItem-majorGridLines visible="false"/>
			                </kendo:chart-categoryAxisItem>
			             </kendo:chart-categoryAxis>
			             <kendo:chart-tooltip visible="true" template="#= category #: #= value # Net Sentiment" />
			         </kendo:chart>
			     </div>
            </div> 
            <div style="padding-top:30px; padding-left:30px;">
                <div style="padding-left:270px; padding-bottom:10px; font-size:9pt;">Stock Chart</div>
                <!-- TradingView Widget BEGIN -->
                <script type="text/javascript" src="https://s3.amazonaws.com/tradingview/tv.js"></script>
                <script type="text/javascript">
                    new TradingView.widget({
                        "width": 600,
                        "height": 200,
                        "symbol": "<%= sSymbol %>",
                        "interval": "D",
                        "timezone": "exchange",
                        "theme": "White",
                        "toolbar_bg": "#f4f7f9",
                        "allow_symbol_change": false,
                        "hideideas": true
                    });
                </script>
                <!-- TradingView Widget END -->
            </div>          
        </div>
        <div id="footer"></div>
    </div>
    </form>
</body>
</html>