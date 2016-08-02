package com.sandro.bitcoinpaymenturi.test;
import org.junit.Test;

import com.sandro.bitcoinpaymenturi.BitcoinPaymentURI;

import static org.junit.Assert.*;

public class BitcoinPaymentURITest {

    @Test
    public void testParseForAddressMethod() {
    	BitcoinPaymentURI bitcoinPaymentURI = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");

    	assertEquals(bitcoinPaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertNull(bitcoinPaymentURI.getAmount());
    	assertNull(bitcoinPaymentURI.getLabel());
    	assertNull(bitcoinPaymentURI.getMessage());
    	assertEquals(bitcoinPaymentURI.getParameters().size(), 0);
    }

    @Test
    public void testParseForAddressWithNameMethod() {
    	BitcoinPaymentURI bitcoinPaymentURI = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?label=Luke-Jr");

    	assertEquals(bitcoinPaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertNull(bitcoinPaymentURI.getAmount());
    	assertEquals(bitcoinPaymentURI.getLabel(), "Luke-Jr");
    	assertNull(bitcoinPaymentURI.getMessage());
    	assertEquals(bitcoinPaymentURI.getParameters().size(), 0);
    }

    @Test
    public void testParseForAddressWithAmountAndNameMethod() {
    	BitcoinPaymentURI bitcoinPaymentURI = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?amount=20.3&label=Luke-Jr");

    	assertEquals(bitcoinPaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertEquals(bitcoinPaymentURI.getAmount(), 20,3);
    	assertEquals(bitcoinPaymentURI.getLabel(), "Luke-Jr");
    	assertNull(bitcoinPaymentURI.getMessage());
    	assertEquals(bitcoinPaymentURI.getParameters().size(), 0);
    }

    @Test
    public void testParseForAddressWithAmountAndNameAndMessageAndRequiredParameterMethod() {
    	BitcoinPaymentURI bitcoinPaymentURI = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?amount=50&label=Luke-Jr&message=Donation%20for%20project%20xyz");

    	assertEquals(bitcoinPaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertEquals(bitcoinPaymentURI.getAmount(), Double.valueOf(50));
    	assertEquals(bitcoinPaymentURI.getLabel(), "Luke-Jr");
    	assertEquals(bitcoinPaymentURI.getMessage(), "Donation for project xyz");
    	assertEquals(bitcoinPaymentURI.getParameters().size(), 0);
    }

    @Test
    public void testParseForAddressWithParametersMethod() {
		BitcoinPaymentURI bitcoinPaymentURI = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?somethingyoudontunderstand=50&somethingelseyoudontget=999&r=https%3A%2F%2Ffoo.com%2Fi%2F7BpFbVsnh5PUisfh&req-app=appname");

    	assertEquals(bitcoinPaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertNull(bitcoinPaymentURI.getAmount());
    	assertNull(bitcoinPaymentURI.getLabel());
    	assertNull(bitcoinPaymentURI.getMessage());
        assertEquals(bitcoinPaymentURI.getParameters().size(), 4);
    	assertEquals(bitcoinPaymentURI.getParameters().get("somethingyoudontunderstand").getValue(), "50");
    	assertEquals(bitcoinPaymentURI.getParameters().get("somethingelseyoudontget").getValue(), "999");
        assertEquals(bitcoinPaymentURI.getParameters().get("r").getValue(), "https://foo.com/i/7BpFbVsnh5PUisfh");
    	assertEquals(bitcoinPaymentURI.getParameters().get("app").getValue(), "appname");
    	assertTrue(bitcoinPaymentURI.getParameters().get("app").isRequired());
    }

    @Test
    public void testParseForInvalidAddressesMethod() {
    	BitcoinPaymentURI bitcoinPaymentURI1 = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?somethingyoudontunderstand=50&somethingelseyoudontget");
    	BitcoinPaymentURI bitcoinPaymentURI2 = BitcoinPaymentURI.parse("bitcoinX:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?somethingyoudontunderstand=50");
    	BitcoinPaymentURI bitcoinPaymentURI3 = BitcoinPaymentURI.parse("bitcoin175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?somethingyoudontunderstand=50");
    	BitcoinPaymentURI bitcoinPaymentURI4 = BitcoinPaymentURI.parse("bitcoin:?somethingyoudontunderstand=50");
    	BitcoinPaymentURI bitcoinPaymentURI5 = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?label");

    	assertNull(bitcoinPaymentURI1);
    	assertNull(bitcoinPaymentURI2);
    	assertNull(bitcoinPaymentURI3);
    	assertNull(bitcoinPaymentURI4);
    	assertNull(bitcoinPaymentURI5);
    }

    @Test
    public void testBuilder() {
    	BitcoinPaymentURI bitcoinPaymentURI = new BitcoinPaymentURI.Builder()
    		.address("175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W")
    		.amount(50.0)
    		.label("Luke-Jr")
    		.message("Donation for project xyz")
    		.parameter("foo", "bar")
    		.requiredParameter("fiz", "biz")
    		.build();

    	assertEquals(bitcoinPaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertEquals(bitcoinPaymentURI.getAmount(), Double.valueOf(50));
    	assertEquals(bitcoinPaymentURI.getLabel(), "Luke-Jr");
    	assertEquals(bitcoinPaymentURI.getMessage(), "Donation for project xyz");
    	assertEquals(bitcoinPaymentURI.getParameters().size(), 2);
    	assertEquals(bitcoinPaymentURI.getParameters().get("foo").getValue(), "bar");
    	assertFalse(bitcoinPaymentURI.getParameters().get("foo").isRequired());
    	assertEquals(bitcoinPaymentURI.getParameters().get("fiz").getValue(), "biz");
    	assertTrue(bitcoinPaymentURI.getParameters().get("fiz").isRequired());
    	assertEquals(bitcoinPaymentURI.getURI(), "bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?message=Donation%20for%20project%20xyz&amount=50.0&req-fiz=biz&foo=bar&label=Luke-Jr");
    }

}
