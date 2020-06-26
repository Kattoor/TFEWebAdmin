package server;

import com.google.gson.Gson;
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
        int bytesRead = -1;
        int counter = 0;

        while (bytesRead == -1 && counter++ < 3) {
            bytesRead = is.read(buffer);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (bytesRead != -1) {
            byte[] messageInBytes = new byte[bytesRead];
            System.arraycopy(buffer, 0, messageInBytes, 0, messageInBytes.length);

            final String message = new String(messageInBytes, StandardCharsets.US_ASCII);
            String json = message.split(Packet.endDataString)[0];
            return new Packet(json.getBytes(StandardCharsets.US_ASCII));
        } else
            throw new RuntimeException("Should not be happening");
    }

    private void printPacket(String prefix, Packet packet) {
        System.out.println(prefix + (packet.getData().isEmpty() ? "Empty packet" : packet.getData()));
    }

    @Override
    public boolean connect(String ip, int port, String username, String password) {
        try {
            System.out.println("inside connect 1");
            this.socket = new Socket(ip, port);

            /*
            Receive packet 0 (FromMS_SendCurrentState)
            Send packet 0 (secret, userName, password)
            Receive packet 5 (FromMS_AcceptConnection)
            Receive packet 4 (FromMS_SyncData)
            */
            System.out.println("inside connect 2");
            Packet packet = receivePacket();
            System.out.println("inside connect 3");
            printPacket("Received: ", packet);

            Packet logInPacket = new Packet(0, "{\"secret\": \"4bf3fd6a0c4f4ac570903654c28fb2bb\",\"userName\": \"" + username + "\", \"password\":\"" + password + "\"}");
            logInPacket.send(socket);
            System.out.println("inside connect 4");
            printPacket("Sent: ", logInPacket);

            packet = receivePacket();
            System.out.println("inside connect 5");
            printPacket("Received: ", packet);

            packet = receivePacket();
            System.out.println("inside connect 6");
            printPacket("Received: ", packet);

            return true;
        } catch (Exception e) {
            System.out.println("inside connect ?");
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

            Packet received = receivePacket();

            // System.out.println(received.getData());
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

            Packet received = receivePacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restartRoom(String roomId) {
        try {
            String json = "{\"room\": \"" + roomId + "\"}";
            new Packet(2, json).send(socket);

            Packet received = receivePacket();
            // System.out.println(received.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createRoom(CreateRoom data) {
        try {
            new Packet(5, new Gson().toJson(data)).send(socket);
            Packet received = receivePacket();
            // System.out.println(received.getData());
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
    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
