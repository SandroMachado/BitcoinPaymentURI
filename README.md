# BitcoinPaymentURI 
[![Release](https://jitpack.io/v/SandroMachado/BitcoinPaymentURI.svg)](https://jitpack.io/#SandroMachado/BitcoinPaymentURI)

BitcoinPaymentURI is an open source library to handle the Bitcoin payment URI based on the [BIT 21](https://github.com/bitcoin/bips/blob/master/bip-0021.mediawiki). The purpose of this library is to provide a simplier way to the developers to integrate in their applications support for this URI Scheme  to easily make payments.

# Gradle Dependency

## Repository

First, add the following to your app's `build.gradle` file:

```Gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

Them include the openalpr-android dependency:

```gradle
dependencies {

    // ... other dependencies here.    	
    compile 'com.github.SandroMachado:BitcoinPaymentURI:1.0.0'
}
```

# Usage

## Code

Parse the URI `bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?amount=50&label=Luke-Jr&message=Donation%20for%20project%20xyz`.

```Java
BitcoinPaymentURI bitcoinPaymentURI = BitcoinPaymentURI.parse("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?amount=50&label=Luke-Jr&message=Donation%20for%20project%20xyz");

bitcoinPaymentURI.getAddress(); \\175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W
bitcoinPaymentURI.getAmount(); \\50
bitcoinPaymentURI.getLabel(); \\ "Luke-Jr"
bitcoinPaymentURI.getMessage(); \\ "Donation for project xyz"
bitcoinPaymentURI.getParameters().size(); \\0
```

Generate the following URI `bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?message=Donation%20for%20project%20xyz&amount=50.0&req-fiz=biz&foo=bar&label=Luke-Jr`

```Java
BitcoinPaymentURI bitcoinPaymentURI = new BitcoinPaymentURI.Builder()
	.address("175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W")
	.amount(50.0)
	.label("Luke-Jr")
	.message("Donation for project xyz")
	.parameter("foo", "bar")
	.requiredParameter("fiz", "biz")
	.build();
```