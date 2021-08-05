import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {

	public static final String RESOURCE_URI = "http://localhost:8080/api/private";

	public static void main(String[] args) {
		try {
			var auth = new AuthenticationHelper();
			var client = HttpClient.newHttpClient();
			var request = HttpRequest.newBuilder()
				.uri(new URI(RESOURCE_URI))
				.header("Authorization", "Bearer " + auth.getAccessToken())
				.GET()
				.build();
			try {
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				System.out.println("Response");
				System.out.println("status: " + response.statusCode());
				System.out.println("body: " + response.body());
			} catch (ConnectException e) {
				System.out.println("Could not connect to API at " + RESOURCE_URI + ". Please check if API is running");
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
