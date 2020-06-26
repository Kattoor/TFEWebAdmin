package web.backend;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import server.ServerImpl;
import server.models.CreateRoom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

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

        int port = 1113;

        if (session.getUri().contains("/api/verifytoken")) {
            String token = parameters.get("token");

            Response response = newFixedLengthResponse(Response.Status.OK, "text", authentication.containsKey(token) ? "valid" : "invalid");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getUri().contains("/api/canlogin")) {
            String ip = parameters.get("ip");
            String username = parameters.get("username");
            String password = parameters.get("password");

            ServerImpl si = new ServerImpl();
            boolean success = si.connect(ip, port, username, password);
            si.closeConnection();

            String token = generateAuthenticationKey();
            if (success)
                authentication.put(token, new ServerCredentials(ip, username, password));

            Response response = newFixedLengthResponse(Response.Status.OK, "text", success ? token : "false");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "token");
            return response;
        }

        if (session.getMethod() == Method.OPTIONS) {
            Response response = newFixedLengthResponse(Response.Status.OK, "text", "");
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

        if (session.getUri().contains("/api/getrooms")) {

            String roomsInfo = "{}";

            String token = session.getHeaders().get("token");
            ServerCredentials creds = token != null ? authentication.get(token) : null;

            if (creds != null) {
                ServerImpl si = new ServerImpl();

                si.connect(creds.getIp(), port, creds.getUsername(), creds.getPassword());
                roomsInfo = si.getRoomsInfo();
                si.closeConnection();
            }

            Response response = newFixedLengthResponse(Response.Status.OK, "application/json", String.valueOf(roomsInfo));
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

        try {
            String uri = session.getUri().equals("/") ? "/index.html" : session.getUri();

            Path path = Paths.get("src/main/java/web/frontend/app/dist/" + uri);
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

    private String generateAuthenticationKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return new BigInteger(1, bytes).toString();
    }

}
