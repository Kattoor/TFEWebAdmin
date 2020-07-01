package server;

import com.google.gson.Gson;
import server.models.CreateAdmin;
import server.models.CreateRoom;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerImpl implements Server {

    private Socket socket;

    private Packet receivePacket() throws IOException {
        byte[] buffer = new byte[socket.getSendBufferSize()];
        InputStream is = socket.getInputStream();
        int bytesRead;

        bytesRead = is.read(buffer);

        if (bytesRead != -1) {
            byte[] messageInBytes = new byte[bytesRead];
            System.arraycopy(buffer, 0, messageInBytes, 0, messageInBytes.length);

            final String message = new String(messageInBytes, StandardCharsets.US_ASCII);
            String json = message.split(Packet.endDataString)[0];
            return new Packet(json.getBytes(StandardCharsets.US_ASCII));
        } else
            throw new RuntimeException("Should not be happening");
    }

    @Override
    public boolean connect(String ip, int port, String username, String password) {
        try {
            this.socket = new Socket(ip, port);
            receivePacket();
            Packet logInPacket = new Packet(0, "{\"secret\": \"4bf3fd6a0c4f4ac570903654c28fb2bb\",\"userName\": \"" + username + "\", \"password\":\"" + password + "\"}");
            logInPacket.send(socket);
            receivePacket();
            receivePacket();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void skipMap(String roomId, String dsc, String map) {
        try {
            String json = "{\"room\": \"" + roomId + "\",\"dsc\": \"" + dsc + "\",\"map\": \"" + map + "\"}";
            Packet switchMapPacket = new Packet(18, json);
            switchMapPacket.send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getRoomsInfo() {
        try {
            new Packet(7, "").send(socket);
            Packet received = receivePacket();
            return received.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void closeRoom(String roomId) {
        try {
            String json = "{\"room\": \"" + roomId + "\"}";
            new Packet(9, json).send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restartRoom(String roomId) {
        try {
            String json = "{\"room\": \"" + roomId + "\"}";
            new Packet(2, json).send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createRoom(CreateRoom data) {
        try {
            new Packet(5, new Gson().toJson(data)).send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMapsAndModes() {
        try {
            new Packet(8, "").send(socket);
            Packet received = receivePacket();
            return received.getData();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void kickPlayer(String playerId) {
        try {
            new Packet(1, "{\"playerID\": \"" + playerId + "\"}").send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlayerFromBlackList(String roomId, String playerId) {
        try {
            new Packet(20, "{\"room\": \"" + roomId + "\", \"pid\": \"" + playerId + "\"}").send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAdminList() {
        try {
            new Packet(21, "").send(socket);
            Packet received = receivePacket();
            return received.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void createAdmin(CreateAdmin data) {
        try {
            new Packet(22, "{\"editMode\": 0, \"data\": " + new Gson().toJson(data) + "}").send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAdmin(CreateAdmin data) {
        try {
            new Packet(22, "{\"editMode\": 2, \"data\": " + new Gson().toJson(data) + "}").send(socket);
            receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
