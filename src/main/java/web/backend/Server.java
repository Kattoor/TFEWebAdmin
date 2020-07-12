package web.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import server.ServerImpl;
import server.models.CreateAdmin;
import server.models.CreateRoom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class Server extends NanoHTTPD {

    private final Map<String, ServerCredentials> authentication = new HashMap<>();

    public Server() throws IOException {
        super(80);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Running!");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "";
        Map<String, String> parameters = session.getParms();

        System.out.println("Requested URL: " + session.getUri());

        Response response = route(session, parameters);
        if (response != null)
            return response;

        try {
            String uri = session.getUri().equals("/") ? "/index.html" : session.getUri();

            Path path = Paths.get("./dist/" + uri);

            System.out.println(path.toString());

            if (session.getUri().endsWith(".ico")) {
                byte[] bytes = Files.readAllBytes(path);
                return newFixedLengthResponse(Response.Status.OK, "image/x-icon", new ByteArrayInputStream(bytes), bytes.length);
            } else if (session.getUri().endsWith(".png")) {
                byte[] bytes = Files.readAllBytes(path);
                return newFixedLengthResponse(Response.Status.OK, "image/png", new ByteArrayInputStream(bytes), bytes.length);
            } else {
                if (Files.exists(path))
                    msg += Files.readString(path);
                else
                    System.out.println("Couldn't find " + session.getUri());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mimeType = "text/html";
        if (session.getUri().endsWith(".js"))
            mimeType = "text/javascript";
        else if (session.getUri().equals("/App"))
            mimeType = "text/javascript";
        else if (session.getUri().endsWith(".css"))
            mimeType = "text/css";
        return newFixedLengthResponse(Response.Status.OK, mimeType, msg);
    }

    private Response route(IHTTPSession session, Map<String, String> parameters) {
        if (session.getMethod() == Method.OPTIONS) {
            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, content-length, token");
            return response;
        }

        if (session.getUri().contains("/api/verifytoken")) {
            String token = parameters.get("token");

            Response response = newFixedLengthResponse(Response.Status.OK, "text", authentication.containsKey(token) ? "valid" : "invalid");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        int port = 1113;
        if (session.getUri().contains("/api/canlogin")) {
            String ip = parameters.get("ip");
            String username = parameters.get("username");
            String password = parameters.get("password");
            System.out.println(" > Attempting to log in with ip [" + ip + "], username[" + username + "], password [" + password + "]");

            ServerImpl si = new ServerImpl();
            boolean success = si.connect(ip, port, username, password);
            si.closeConnection();

            Response response;

            if (success) {
                System.out.println(" > Successfully logged in");
                String token = generateAuthenticationKey();
                authentication.put(token, new ServerCredentials(ip, username, password));
                response = newFixedLengthResponse(Response.Status.OK, "text", token);
                response.addHeader("Access-Control-Allow-Headers", "token");
            } else {
                System.out.println(" > Failed to log in");
                response = newFixedLengthResponse(Response.Status.OK, "text", "false");
            }

            response.addHeader("Access-Control-Allow-Origin", "*");

            return response;
        }

        if (session.getUri().contains("/api/getplayercounts")) {
            final Path path = Paths.get("./playercount");
            List<String> lastLines = new ArrayList<>();
            if (Files.exists(path)) {
                try {
                    List<String> lines = Files.readAllLines(path);
                    lastLines = lines.stream()
                            .skip(lines.size() <= 25 ? 0 : lines.size() - 25)
                            .map(line -> {
                                String[] parts = line.split(";");
                                return "{\"time\":\"" + parts[0] + "\",\"count\":\"" + parts[1] + "\"}";
                            })
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "application/json", "[" + String.join(",", lastLines) + "]");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, content-length, token");
            return response;
        }

        if (session.getMethod() == Method.POST && session.getUri().contains("/api/createroom")) {
            try {
                String token = session.getHeaders().get("token");
                ServerCredentials creds = token != null ? authentication.get(token) : null;
                if (creds != null) {
                    int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                    String in = new String(session.getInputStream().readNBytes(contentLength));

                    CreateRoom cr = new Gson().fromJson(in, CreateRoom.class);

                    ServerImpl si = new ServerImpl();

                    si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                    si.createRoom(cr);
                    si.closeConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, content-length, token");
            return response;
        }

        if (session.getUri().contains("/api/serverlist/allplayercount")) {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1?appid=1148810"))
                    .build();

            String body;

            try {
                body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                body = "error";
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", body);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/removefromblacklist")) {
            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                String roomId = parameters.get("roomId");
                String playerId = parameters.get("playerId");

                System.out.println("Removing player [" + playerId + "] from blacklist for room [" + roomId + "]");

                ServerImpl si = new ServerImpl();
                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                si.removePlayerFromBlackList(roomId, playerId);
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/kickplayer")) {
            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                String playerId = parameters.get("playerId");

                System.out.println("Kicking player " + playerId);

                ServerImpl si = new ServerImpl();
                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                si.kickPlayer(playerId);
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/getrooms")) {
            String roomsInfo = "{}";

            String token = session.getHeaders().get("token");
            System.out.println(" > Attempting to fetch rooms for token [" + token + "]");

            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                System.out.println(" > Token resolves to user [" + creds.getUsername() + "]");

                ServerImpl si = new ServerImpl();

                System.out.println(" > Connecting to server");
                boolean successfullyConnected = si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                if (successfullyConnected) {
                    System.out.println(" > Successfully connected");
                    System.out.println(" > Fetching room info");

                    String jsonRoomsInfo = si.getRoomsInfo();

                    JsonObject obj = new Gson().fromJson(jsonRoomsInfo, JsonObject.class);
                    JsonArray rooms = obj.getAsJsonArray("rooms");
                    rooms.forEach(room -> {
                        JsonObject roomObj = room.getAsJsonObject();
                        String dsc = roomObj.get("dsc").getAsString();
                        if (dsc.startsWith("192.168.")) {
                            roomObj.remove("dsc");
                            roomObj.addProperty("dsc", creds.getIp());
                        }
                    });

                    roomsInfo = new Gson().toJson(obj);

                    System.out.println(" > Fetched room info");
                    System.out.println(roomsInfo);
                } else {
                    System.out.println(" > Failed to connect");
                }
                si.closeConnection();
            } else
                System.out.println("No user found for token");


            Response response = newFixedLengthResponse(Response.Status.OK, "application/json", roomsInfo);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/mapsandmodes")) {
            String roomsInfo = "";

            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                ServerImpl si = new ServerImpl();
                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                roomsInfo = si.getMapsAndModes();
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "application/json", roomsInfo);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/closeroom")) {
            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                ServerImpl si = new ServerImpl();
                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                String roomId = parameters.get("id");
                si.closeRoom(roomId);
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/restartroom")) {
            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                ServerImpl si = new ServerImpl();
                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                String roomId = parameters.get("id");
                si.restartRoom(roomId);
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/changemap")) {
            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                ServerImpl si = new ServerImpl();
                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());

                String roomId = parameters.get("roomId");
                String dsc = parameters.get("dsc");
                String map = parameters.get("map");

                si.skipMap(roomId, dsc, map);
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "application/json", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/getadminlist")) {
            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            String adminList = "";

            if (creds != null) {
                ServerImpl si = new ServerImpl();
                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());

                adminList = si.getAdminList();
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "application/json", adminList);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getMethod() == Method.POST && session.getUri().contains("/api/createadmin")) {
            try {
                String token = session.getHeaders().get("token");
                ServerCredentials creds = token != null ? authentication.get(token) : null;
                if (creds != null) {
                    int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                    String in = new String(session.getInputStream().readNBytes(contentLength));

                    CreateAdmin cr = new Gson().fromJson(in, CreateAdmin.class);

                    ServerImpl si = new ServerImpl();

                    si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                    si.createAdmin(cr);
                    si.closeConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, content-length, token");
            return response;
        }

        if (session.getMethod() == Method.POST && session.getUri().contains("/api/creatediscordaccount")) {
            String responseText = "";

            try {
                String token = session.getHeaders().get("token");
                ServerCredentials creds = token != null ? authentication.get(token) : null;
                if (creds != null) {
                    int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                    String in = new String(session.getInputStream().readNBytes(contentLength));

                    CreateAdmin cr = new Gson().fromJson(in, CreateAdmin.class);
                    String password = generateAuthenticationKey();
                    cr.setPassword(password);
                    responseText = password;

                    ServerImpl si = new ServerImpl();

                    si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                    si.createAdmin(cr);

                    final Path path = Paths.get("discord-credentials");
                    Files.write(path,
                            Collections.singletonList(cr.getUserName() + ";" + cr.getPassword() + ";" + creds.getIp()),
                            StandardCharsets.UTF_8,
                            Files.exists(path)
                                    ? StandardOpenOption.APPEND
                                    : StandardOpenOption.CREATE);

                    si.closeConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
                responseText = "";
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", responseText);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, content-length, token");
            return response;
        }

        if (session.getMethod() == Method.POST && session.getUri().contains("/api/deleteadmin")) {
            try {
                String token = session.getHeaders().get("token");
                ServerCredentials creds = token != null ? authentication.get(token) : null;
                if (creds != null) {
                    int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                    String in = new String(session.getInputStream().readNBytes(contentLength));

                    CreateAdmin cr = new Gson().fromJson(in, CreateAdmin.class);
                    ServerImpl si = new ServerImpl();

                    si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                    si.deleteAdmin(cr);
                    si.closeConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, content-length, token");
            return response;
        }

        return null;
    }

    private String generateAuthenticationKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return new BigInteger(1, bytes).toString();
    }
}
