package server;

import com.google.gson.Gson;
import server.models.CreateAdmin;
import server.models.CreateRoom;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

class Resultt {
    Packet packet;
    byte[] previouslyRead;

    public Resultt(Packet packet, byte[] previouslyRead) {
        this.packet = packet;
        this.previouslyRead = previouslyRead;
    }
}

public class ServerImpl implements Server {

    private Socket socket;

    private Resultt receivePacket(byte[] previouslyRead) throws IOException {
        if (previouslyRead.length > 0) {
            String previouslyReadStr = new String(previouslyRead, StandardCharsets.US_ASCII);
            if (previouslyReadStr.contains("@end@")) {
                String json = previouslyReadStr.split(Packet.endDataString)[0];
                return new Resultt(new Packet(json.getBytes(StandardCharsets.US_ASCII)), new byte[] {});
            }
        }

        System.out.println(" > > > receive packet called");
        byte[] buffer = new byte[1024*10];
        BufferedInputStream is = new BufferedInputStream(socket.getInputStream());
        int bytesRead;

        String result = "";

        is.mark(1024*10);
        while (!result.contains("@end@")) {
            is.reset();
            bytesRead = is.read(buffer);
            if (bytesRead != -1) {
                byte[] messageInBytes = new byte[bytesRead + previouslyRead.length];
                System.arraycopy(previouslyRead, 0, messageInBytes, 0, previouslyRead.length);
                System.arraycopy(buffer, 0, messageInBytes, previouslyRead.length, messageInBytes.length);

                result = new String(messageInBytes, StandardCharsets.US_ASCII);
            } else
                throw new RuntimeException("Should not be happening");
        }
        
        String json = result.split(Packet.endDataString)[0];
        System.out.println(json);
        
        if (json.length() + 5 != result.length()) {
            byte[] b = new byte[result.length() - (json.length() + 5)];
            System.arraycopy(buffer, json.length() + 5, b, 0, b.length);
            System.out.println("JASPER REST: ");
            System.out.println(new String(b, StandardCharsets.US_ASCII));
            return new Resultt(new Packet(json.getBytes(StandardCharsets.US_ASCII)), b);
        } else {
            return new Resultt(new Packet(json.getBytes(StandardCharsets.US_ASCII)), new byte[] {});   
        }
    }

    @Override
    public boolean connect(String ip, int port, String username, String password) {
        System.out.println(" > > connecting");
        try {
            this.socket = new Socket(ip, port);
            System.out.println(" > > socket ok ; " + ip);
            receivePacket(new byte[] {});
            System.out.println(" > > received packet ; " + ip);
            Packet logInPacket = new Packet(0, "{\"secret\": \"4bf3fd6a0c4f4ac570903654c28fb2bb\",\"userName\": \"" + username + "\", \"password\":\"" + password + "\"}");
            logInPacket.send(socket);
            System.out.println(" > > sent packet ; " + ip);
            Resultt resultt = receivePacket(new byte[] {});
            System.out.println(" > > received packet ; " + ip);
            try {
                //System.out.println("JASPER JASPER JASPER JASPER " + new S);
                receivePacket(resultt.previouslyRead);
                System.out.println(" > > received packet ; " + ip);
            } catch (RuntimeException e) {
                System.out.println("Only received one packet after sending log in packet ; " + ip);
            }
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
            receivePacket(new byte[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getRoomsInfo() {
        try {
            new Packet(7, "").send(socket);
            return receivePacket(new byte[] {}).packet.getData();
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
            receivePacket(new byte[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restartRoom(String roomId) {
        try {
            String json = "{\"room\": \"" + roomId + "\"}";
            new Packet(2, json).send(socket);
            receivePacket(new byte[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createRoom(CreateRoom data) {
        try {
            new Packet(5, new Gson().toJson(data)).send(socket);
            receivePacket(new byte[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMapsAndModes() {
        try {
            new Packet(8, "").send(socket);
            return receivePacket(new byte[] {}).packet.getData();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void kickPlayer(String playerId) {
        try {
            new Packet(1, "{\"playerID\": \"" + playerId + "\"}").send(socket);
            receivePacket(new byte[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlayerFromBlackList(String roomId, String playerId) {
        try {
            new Packet(20, "{\"room\": \"" + roomId + "\", \"pid\": \"" + playerId + "\"}").send(socket);
            receivePacket(new byte[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAdminList() {
        try {
            new Packet(21, "").send(socket);
            return receivePacket(new byte[] {}).packet.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void createAdmin(CreateAdmin data) {
        try {
            new Packet(22, "{\"editMode\": 0, \"data\": " + new Gson().toJson(data) + "}").send(socket);
            receivePacket(new byte[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAdmin(CreateAdmin data) {
        try {
            new Packet(22, "{\"editMode\": 2, \"data\": " + new Gson().toJson(data) + "}").send(socket);
            receivePacket(new byte[] {});
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
