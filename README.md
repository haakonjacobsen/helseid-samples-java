# HelseID Samples in Java
HelseID is a national authentication service for the health sector in Norway. These samples are targeted at technical personnel such as application architects and developers and demonstrates how to implement HelseID in Java applications. The samples consists of 2 applications.

## [Machine to machine](helseid.samples.java.m2m-app)
The m2m app uses the client credentials flow (OAuth 2.0) to get an access token and uses the access token retrieve a resource from the [API](HelseID.Samples.Java.API).

## [API](HelseID.Samples.Java.API)
The API provides simple mock data protected with HelseID. The API provides three endpoints:
1. `../api/public` - A public endpoint accessible by anyone.
2. `../api/private` - A private encpoint protected with HelseID access token.
3. `../api/private-scoped` - A private encpoint protected with HelseID access token with the scope `norsk-helsenett:java-sample-api/read`.


For more information about HelseID checkout https://nhn.no/helseid/ (Norwegian) and https://dokumentasjon.helseid.no/
