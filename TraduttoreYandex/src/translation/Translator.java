package translation;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Translator {
	
	private final String USER_AGENT = "Mozilla/5.0";
	private final String APIkey = "trnsl.1.1.20170122T150435Z.ed844a36d05d3a6e.26c95fea3481d32c8c126719769e577e750d1d43";
	private final String url = "https://translate.yandex.net/api/v1.5/tr/translate?";
	public Translator()
	{
	}
	public String translatePOST(String text, String from, String to, String format) throws IOException, ParserConfigurationException, SAXException
	{
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		//request parameters
		String urlParameters = "key="+APIkey+"&text="+text+"&lang="+from+"-"+to+"&format="+format;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream input =  new ByteArrayInputStream(response.toString().getBytes("UTF-8"));
		Document doc = builder.parse(input);
		String content = doc.getElementsByTagName("text").item(0).getTextContent();
		//print result
		return content;
	}
}
