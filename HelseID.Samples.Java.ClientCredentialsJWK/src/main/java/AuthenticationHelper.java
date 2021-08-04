import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class AuthenticationHelper {

	private final static String HELSEID_METADATA_LINK = "https://helseid-sts.utvikling.nhn.no/.well-known/openid-configuration";
	private final static String CLIENT_ID = "sample-m2m-app";

	private JSONObject helseIdMetadata;
	private HttpClient client = HttpClient.newHttpClient();

	public AuthenticationHelper() {
		retrieveAuthenticationServerMetadata();
	}

	public String getAccessToken() throws Exception {
		// create params for the token request
		var values = new HashMap<String, String>();
		values.put("grant_type", "client_credentials");
		values.put("scope", "norsk-helsenett:java-sample-api/read");
		values.put("client_assertion", createClientAssertionToken());
		values.put("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
		var params = getParamsString(values);
		var body = HttpRequest.BodyPublishers.ofString(params);

		// generate and send token request to HelseID
		HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(helseIdMetadata.getString("token_endpoint")))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.POST(body)
			.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// retrive the access token from response
		var responseBody = new JSONObject(response.body());
		return responseBody.getString("access_token");
	}

	private String createClientAssertionToken() throws Exception {
		var now = new Date();
		var expires = new Date(now.getTime() + 60 * 1000);
		var jti = UUID.randomUUID().toString();

		var claims = new JWTClaimsSet.Builder()
			.issuer(CLIENT_ID)
			.subject(CLIENT_ID)
			.notBeforeTime(now)
			.issueTime(now)
			.expirationTime(expires)
			.jwtID(jti)
			.audience(helseIdMetadata.getString("token_endpoint"))
			.build();

		var jwk = readJWKFromFile();
		var signer = new RSASSASigner(jwk.toRSAKey());

		var jwt = new SignedJWT(
			new JWSHeader.Builder(JWSAlgorithm.PS256).keyID(jwk.getKeyID()).build(),
			claims
		);

		jwt.sign(signer);

		return jwt.serialize();
	}

	private void retrieveAuthenticationServerMetadata() {
		HttpClient client = HttpClient.newHttpClient();
		try {
			var request = HttpRequest.newBuilder()
			.uri(new URI(HELSEID_METADATA_LINK))
			.GET()
			.build();
			var response = client.send(request, HttpResponse.BodyHandlers.ofString());
			helseIdMetadata = new JSONObject(response.body());
		} catch (Exception e) {
			System.out.println("failed to retrive meta data from " + HELSEID_METADATA_LINK);
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
		var result = new StringBuilder();
		for (var entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}
		String resultString = result.toString();
		return resultString.length() > 0
			? resultString.substring(0, resultString.length() - 1)
			: resultString;
	}

	// DO NOT store private keys in source code!
	// This is bad practice
	private JWK readJWKFromFile() throws Exception {
		try {
			File myObj = new File("jwk_private_key.json");
			Scanner myReader = new Scanner(myObj);
			var jsonString = "";
			while (myReader.hasNextLine()) {
				jsonString += myReader.nextLine();
			}
			myReader.close();
			return JWK.parse(jsonString);
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
			return null;
		}
	}
}
