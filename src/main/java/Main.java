import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(Main::persistPlayerCount,
                        0, 1, TimeUnit.MINUTES);

        new web.backend.Server();
    }

    private static void persistPlayerCount() {
        final Path path = Paths.get("./playercount");

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1?appid=1148810"))
                .build();

        try {
            String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            String playerCount = body.split("player_count\":")[1].split(",")[0];

            Files.write(path,
                    Collections.singletonList(System.currentTimeMillis() + ";" + playerCount),
                    StandardCharsets.UTF_8,
                    Files.exists(path)
                            ? StandardOpenOption.APPEND
                            : StandardOpenOption.CREATE);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
