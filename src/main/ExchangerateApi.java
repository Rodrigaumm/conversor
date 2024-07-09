package main;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ExchangerateApi {
    private static final HttpClient httpClient = HttpClient.newBuilder().build();
    public static final String BASE_CURRENCY;
    public static final String API_URL;
    private static Map<String, Double> conversionRates;
    
    static {
    	BASE_CURRENCY = System.getProperty("BASE_CURRENCY").toUpperCase();
    	API_URL = "https://v6.exchangerate-api.com/v6/"+ System.getProperty("TOKEN")  +"/latest/" + BASE_CURRENCY;

    	try {
			conversionRates = fetchRates();
		} catch (URISyntaxException | IOException | InterruptedException e) {
			conversionRates = null;
			e.printStackTrace();
		}
    }
    
    public static Map<String, Double> fetchRates() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Map<String, Double> taxasCambio = new HashMap<>();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
        for (Map.Entry<String, JsonElement> entry : conversionRates.entrySet()) {
            String codigoMoeda = entry.getKey();
            double taxaCambio = entry.getValue().getAsDouble();
            taxasCambio.put(codigoMoeda, taxaCambio);
        }
        
        return taxasCambio;
    }
    
    public static Double getConversionRate(Integer conversion) {
    	Map.Entry<String, String> conversionMap = Main.conversions.get(conversion).entrySet().iterator().next();
    	String conversionKey;
    	if (conversionMap.getKey().equals(ExchangerateApi.BASE_CURRENCY)) {
    		conversionKey = conversionMap.getValue();
    	} else {
    		conversionKey = conversionMap.getKey();
    	}
    	
    	return conversionRates.get(conversionKey);
    }
    
    
}
