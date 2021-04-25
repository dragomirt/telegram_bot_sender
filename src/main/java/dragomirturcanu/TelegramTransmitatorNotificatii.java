package main.java.dragomirturcanu;

import jakarta.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.*;

public class TelegramTransmitatorNotificatii {
    private String CHAT_ID = "";
    private String TOKEN = "";

    public void setCHAT_ID(String chat_id) {
        this.CHAT_ID = chat_id;
    }

    public void setTOKEN(String token) {
        this.TOKEN = token;
    }

    public void transmite(String titlu, String mesaj) throws IOException, InterruptedException {

        if (titlu.isEmpty() || mesaj.isEmpty()) {
            return;
        }

        String message = titlu + "\n\n" + mesaj;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id", this.CHAT_ID)
                .queryParam("text", message);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + this.TOKEN))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }

    public ArrayList<HashMap> getChats() throws IOException, InterruptedException {
        if (this.TOKEN.isEmpty()) {
            return null;
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/getUpdates");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + this.TOKEN))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

        JSONObject parsedResponse = new JSONObject(response.body());
        System.out.println(parsedResponse.get("ok"));

        if (parsedResponse.isEmpty()) {
            return null;
        }

        if (!parsedResponse.has("result")) {
            return null;
        }

        JSONArray results = parsedResponse.getJSONArray("result");
//        System.out.println(results.toString());
        if (results.isEmpty()) {
            return null;
        }

        ArrayList<HashMap> messages = new ArrayList<HashMap>();

        for (int i=0; i < results.length(); i++) {
            JSONObject updateInstance = results.getJSONObject(i);
            if(!updateInstance.has("message")) {
                continue;
            }

            JSONObject from = updateInstance.getJSONObject("message").getJSONObject("from");
            JSONObject chat = updateInstance.getJSONObject("message").getJSONObject("chat");

            if (!from.has("first_name")) {
                continue;
            }

            HashMap dataObject = new HashMap();
            dataObject.put("chat_id", chat.getBigInteger("id"));
//            dataObject.put("username", from.getString("username"));
            dataObject.put("first_name", from.getString("first_name"));

            messages.add(dataObject);
        }

        System.out.println(messages);
        return messages;
    }
}
