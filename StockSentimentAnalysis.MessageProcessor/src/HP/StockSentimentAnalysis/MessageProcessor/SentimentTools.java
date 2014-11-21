package HP.StockSentimentAnalysis.MessageProcessor;

//import java.util.Date;

//import org.json.simple.JSONObject;

public class SentimentTools {
	
	public static boolean IsPositive(String sText){
		boolean bReturn = false;
		String sentimentPositiveTerms = System.getenv("SENTIMENT_POSITIVE_TERMS");
		String Terms[] = sentimentPositiveTerms.split(",");
		for (String term : Terms){
			int found = sText.indexOf(term.toLowerCase());
			if (found != -1){
				bReturn = true;   // only set if found.
			}
		}
		return bReturn;
	}
	public static boolean IsNegative(String sText){
		boolean bReturn = false;
		String sentimentNegativeTerms = System.getenv("SENTIMENT_NEGATIVE_TERMS").toLowerCase();
		String Terms[] = sentimentNegativeTerms.split(",");
		for (String term : Terms){
			int found = sText.indexOf(term.toLowerCase());
			if (found != -1){
				bReturn = true;   // only set if found.
			}
			//System.out.println( new Date() + " DEBUG: Found Negative Term: " + term );
		}
		return bReturn;
	}
	public static String getNetSentimentDescription(boolean bPos, boolean bNeg){
		String sReturn = "NEUTRAL";
		if ((bNeg == true) && (bPos == false)){
			sReturn = "NEGATIVE";
		}
		else if ((bNeg == false) && (bPos == true)){
			sReturn = "POSITIVE";
		}
		return sReturn;
		
	}
	public static boolean IsRetweet(String sText){
		boolean bReturn = false;
		
		boolean match = sText.startsWith("RT @");  //RT at beginning of line.
		
		if (match){
			bReturn = true;
		}
		
		return bReturn;
	}
	public static String MatchedTerms(String sText, String[] Terms){
		String sReturn = "";
		
		
		return sReturn;
	}
	

}
