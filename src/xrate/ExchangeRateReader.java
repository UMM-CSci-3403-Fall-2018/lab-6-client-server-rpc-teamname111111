package xrate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Provide access to basic currency exchange rate services.
 * 
 * @author PUT YOUR TEAM NAME HERE
 */
public class ExchangeRateReader {

    /**
     * Construct an exchange rate reader using the given base URL. All requests
     * will then be relative to that URL. If, for example, your source is Xavier
     * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
     * for specific days will be constructed from that URL by appending the
     * year, month, and day; the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL
     *            the base URL for requests
     */
	private String url;
	private String accessKey;
	
    public ExchangeRateReader(String baseURL) {
        //Checks if an accessKey exists.
        //If no access key exists, it tells the user to check
        //access_keys.properties and exits
        try {
            readAccessKeys();
        } catch (IOException e){
            System.out.println("Could not receive accessKey, check if your access_keys.properties is in the correct location.");
            System.exit(1);
        }
        //Sets the url to be baseURL
    	url = baseURL;
    }
    
    public static String replaceSingle(int x){
    	String y = Integer.toString(x);
    	if(y.length() == 1){
    		y = "0" + y;
    	}
    	return y;
    }

	private void readAccessKeys() throws IOException {
		Properties properties = new Properties();
		FileInputStream in = null;
		try {
			// Don't change this filename unless you know what you're doing.
			// It's crucial that we don't commit the file that contains the
			// (private) access keys. This file is listed in `.gitignore` so
			// it's safe to put keys there as we won't accidentally commit them.
			in = new FileInputStream("etc/access_keys.properties");
		} catch (FileNotFoundException e) {
			/*
			 * If this error gets generated, make sure that you have the desired
			 * properties file in your project's `etc` directory. You may need
			 * to rename the file ending in `.sample` by removing that suffix.
			 */
			System.err.println("Couldn't open etc/access_keys.properties; have you renamed the sample file?");
			throw(e);
		}
		properties.load(in);
		// This assumes we're using Fixer.io and that the desired access key is
		// in the properties file in the key labelled `fixer_io`.
		accessKey = properties.getProperty("fixer_io");
	}

    /**
     * Get the exchange rate for the specified currency against the base
     * currency (the Euro) on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     */
    public float getExchangeRate(String currencyCode, int year, int month, int day) throws IOException {
        //Creates the url for the webpage
		String urlString = url + year + "-" + replaceSingle(month) + "-" + replaceSingle(day) + "?access_key=" + accessKey;
		//Creates a url
		URL url = new URL(urlString);
		//Opens an input stream on the url
		InputStream inputStream = url.openStream();
		//Creates a BufferedReader on inputstream
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		//Creates a JsonObject containing the json of the webpage
		JsonObject fullobj = new JsonParser().parse(reader).getAsJsonObject();
		//Creates a JsonObject of the rates in fullobj
		JsonObject rates =  fullobj.get("rates").getAsJsonObject();
		//Gets the exchange rate of the currencycode as a float from rates
		float exchangerate =  rates.get(currencyCode).getAsFloat();
		//Returns the exchange rate
		return exchangerate;
    }

    /**
     * Get the exchange rate of the first specified currency against the second
     * on the specified date.
     * 
     * @param fromCurrency
     *            the currency code we're exchanging *from*
     * @param toCurrency
     *            the currency code we're exchanging *to*
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     */
    public float getExchangeRate(
            String fromCurrency, String toCurrency,
            int year, int month, int day) throws IOException {
        //Creates the url for the webpage
        String urlString = url + year + "-" + replaceSingle(month) + "-" + replaceSingle(day) + "?access_key=" + accessKey;
        //Creates a url
        URL url = new URL(urlString);
        //Opens an input stream on the url
        InputStream inputStream = url.openStream();
        //Creates a BufferedReader on inputstream
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        //Creates a JsonObject containing the json of the webpage
        JsonObject fullobj = new JsonParser().parse(reader).getAsJsonObject();
        //Creates a JsonObject of the rates in fullobj
        JsonObject rates =  fullobj.get("rates").getAsJsonObject();
        //Gets the exchange rate of the currencycode as a float from rates
		float exchangerate =  rates.get(fromCurrency).getAsFloat() / rates.get(toCurrency).getAsFloat();
		//Returns the exchange rate
		return exchangerate;
    }
}