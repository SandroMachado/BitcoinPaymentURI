package com.sandro.bitcoinpaymenturi;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sandro.bitcoinpaymenturi.model.Parameter;

/**
 * Java library to handle Bitcoin payment URI.
 * This library is based on the specification at the BIP 21. 
 * 
 * The BIT is available at: https://github.com/bitcoin/bips/blob/master/bip-0021.mediawiki
 */

public class BitcoinPaymentURI {
	
	private static final String SCHEME = "bitcoin:";
	private static final String PARAMETER_AMOUNT = "amount";
	private static final String PARAMETER_LABEL = "label";
	private static final String PARAMETER_MESSAGE = "message";
	
	private final String address;
	private final HashMap<String, Parameter> parameters;
	
	private BitcoinPaymentURI(Builder builder) {
		this.address = builder.address;
		
		parameters = new HashMap<String, Parameter>();
		
		if (builder.amount != null) {
			parameters.put(PARAMETER_AMOUNT, new Parameter(String.valueOf(builder.amount), false));
		}
		
		if (builder.label != null) {
			parameters.put(PARAMETER_LABEL, new Parameter(builder.label, false));
		}
		
		if (builder.message != null) {
			parameters.put(PARAMETER_MESSAGE, new Parameter(builder.message, false));
		}
		
		if (builder.otherParameters != null) {
			parameters.putAll(builder.otherParameters);
		}
	}
	
	/**
	 * Gets the URI Bitcoin address.
	 * 
	 * @return the URI Bitcoin address.
	 */
	
	public String getAddress() {
		return address;
	}

	/**
	 * Gets the URI amount.
	 * 
	 * @return the URI amount.
	 */
	
	public Double getAmount() {
		if (parameters.get(PARAMETER_AMOUNT) == null) {
			return null;
		}
		
		return Double.valueOf(parameters.get(PARAMETER_AMOUNT).getValue());
	}
	
	/**
	 * Gets the URI label.
	 * 
	 * @return the URI label.
	 */
	
	public String getLabel() {
		if (parameters.get(PARAMETER_LABEL) == null) {
			return null;
		}
		
		return parameters.get(PARAMETER_LABEL).getValue();
	}
	
	/**
	 * Gets the URI message.
	 * 
	 * @return the URI message.
	 */
	
	public String getMessage() {
		if (parameters.get(PARAMETER_MESSAGE) == null) {
			return null;
		}
		
		return parameters.get(PARAMETER_MESSAGE).getValue();
	}
	
	/**
	 * Gets the URI parameters.
	 *  
	 * @return the URI parameters.
	 */
	
	public HashMap<String, Parameter> getParameters() {
		HashMap<String, Parameter> filteredParameters = new HashMap<String, Parameter>(parameters);
		
		filteredParameters.remove(PARAMETER_AMOUNT);
		filteredParameters.remove(PARAMETER_LABEL);
		filteredParameters.remove(PARAMETER_MESSAGE);
	
		return filteredParameters;
	}
	
	/**
	 * Gets the URI.
	 * 
	 * @return a string with the URI. This string can be used to make a Bitcoin payment.
	 */
	
	public String getURI() {
		String queryParameters = null;
		try {
			for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
				if (queryParameters == null) {
					if (entry.getValue().isRequired()) {
						queryParameters = String.format("req-%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));

						continue;
					} 
					
					queryParameters = String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));
					
					continue;
				}
				
				if (entry.getValue().isRequired()) {
					queryParameters = String.format("%s&req-%s=%s", queryParameters, URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));
					
					continue;
				}
				
				queryParameters = String.format("%s&%s=%s", queryParameters, URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return String.format("%s%s%s", SCHEME, getAddress(), queryParameters == null ? "" : String.format("?%s", queryParameters));
	}
	
	/**
	 * Parses a string to a Bitcoin payment URI.
	 * 
	 * @param string The string to be parsed.
	 * 
	 * @return a Bitcoin payment URI if the URI is valid, or null for an invalid string.
	 */
	
	public static BitcoinPaymentURI parse(String string) {
		try {
			string = URLDecoder.decode(string,  "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null;
		}
		
		if (string == null) {
			return null;
		}
		
		if (string.isEmpty()) {
			return null;
		}
		
		if (!string.toLowerCase().startsWith(SCHEME)) {
			return null;
		}
		
		String bitcoinPaymentURIWithoutScheme = string.replaceFirst(".*:", "");
        ArrayList<String> bitcoinPaymentURIElements = new ArrayList<>(Arrays.asList(bitcoinPaymentURIWithoutScheme.split("\\?")));
        
        if (bitcoinPaymentURIElements.size() != 1 && bitcoinPaymentURIElements.size() != 2) {
        	return null;
        }
        
        if (bitcoinPaymentURIElements.get(0).length() == 0) {
        	return null;
        }
        
        if (bitcoinPaymentURIElements.size() == 1) {
        	return new Builder().address(bitcoinPaymentURIElements.get(0)).build();
        }
        
        List<String> queryParametersList = Arrays.asList(bitcoinPaymentURIElements.get(1).split("&"));
        
        if (queryParametersList.isEmpty()) {
        	return new Builder().address(bitcoinPaymentURIElements.get(0)).build();
        }
       
        HashMap<String, String> queryParametersFiltered = new HashMap<String, String>(); 
        
        for (String query : queryParametersList) {
        	String[] queryParameter = query.split("=");
        	
        	try {
            	queryParametersFiltered.put(queryParameter[0], queryParameter[1]);
        	}catch(ArrayIndexOutOfBoundsException exception) {
        		exception.printStackTrace();
        		
        		return null;
        	}
        }
        
        Builder bitcoinPaymentURIBuilder = new Builder().address(bitcoinPaymentURIElements.get(0));
        
        if (queryParametersFiltered.containsKey(PARAMETER_AMOUNT)) {
        	bitcoinPaymentURIBuilder.amount(Double.valueOf(queryParametersFiltered.get(PARAMETER_AMOUNT)));
        	
        	queryParametersFiltered.remove(PARAMETER_AMOUNT);
        }
        
        if (queryParametersFiltered.containsKey(PARAMETER_LABEL)) {
        	bitcoinPaymentURIBuilder.label(queryParametersFiltered.get(PARAMETER_LABEL));
        	
        	queryParametersFiltered.remove(PARAMETER_LABEL);
        }
        
        if (queryParametersFiltered.containsKey(PARAMETER_MESSAGE)) {
        	bitcoinPaymentURIBuilder.message(queryParametersFiltered.get(PARAMETER_MESSAGE));
        	
        	queryParametersFiltered.remove(PARAMETER_MESSAGE);
        }
        
		for (Map.Entry<String, String> entry : queryParametersFiltered.entrySet()) {
			bitcoinPaymentURIBuilder.parameter(entry.getKey(), entry.getValue());
		}
        
		return bitcoinPaymentURIBuilder.build();
	}
	
	public static class Builder{
		
		private String address;
		private Double amount;
		private String label;
		private String message;
		private HashMap<String, Parameter> otherParameters;
		
		/**
		 * Returns a builder for the Bitcoin payment URI.
		 */

		public Builder() {
		}
		
		/**
		 * Adds the address to the builder.
		 * 
		 * @param address The address.
		 * 
		 * @return the builder with the address.
		 */
		
		public Builder address(String address) {
			this.address = address;
			
			return this;
		}
		
		/**
		 * Adds the amount to the builder.
		 * 
		 * @param amount The amount.
		 * 
		 * @return the builder with the amount.
		 */
		
		public Builder amount(Double amount) {
			this.amount = amount;
			
			return this;
		}
				
		/**
		 * Adds the label to the builder.
		 * 
		 * @param label The label.
		 * 
		 * @return the builder with the label.
		 */
		
		public Builder label(String label) {
			this.label = label;
			
			return this;
		}
		
		/**
		 * Adds the message to the builder.
		 * 
		 * @param message The message.
		 * 
		 * @return the builder with the message.
		 */
		
		public Builder message(String message) {
			this.message = message;
			
			return this;
		}
		
		/**
		 * Adds a parameter to the builder.
		 * 
		 * @param key The parameter.
		 * @param value The value.
		 * 
		 * @return the builder with the parameter.
		 */
		
		public Builder parameter(String key, String value) {
			if (otherParameters == null) {
				otherParameters = new HashMap<String, Parameter>();
			}
			
			if (key.startsWith("req-")) {
				otherParameters.put(key.replace("req-", ""), new Parameter(value, true));
				
				return this;
			}
			
			otherParameters.put(key, new Parameter(value, false));
			
			return this;
		}
		
		/**
		 * Adds a required to the builder.
		 * 
		 * @param key The key.
		 * @param value The value.
		 * 
		 * @return the builder with the parameter.
		 */
		
		public Builder requiredParameter(String key, String value) {
			if (otherParameters == null) {
				otherParameters = new HashMap<String, Parameter>();
			}
			
			otherParameters.put(key, new Parameter(value, true));
			
			return this;
		}
		
		/**
		 * Builds a Bitcoin payment URI.
		 * 
		 * @return a Bitcoin payment URI.
		 */
		
		public BitcoinPaymentURI build() {
			return new BitcoinPaymentURI(this);
		}
	
	}
	
}
