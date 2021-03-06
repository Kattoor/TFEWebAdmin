package server;

import server.models.CreateAdmin;
import server.models.CreateRoom;

public interface Server {

    boolean connect(String ip, int port, String username, String password);
    void skipMap(String roomId, String dsc, String map);
    String getRoomsInfo();
    void closeRoom(String roomId);
    void restartRoom(String roomId);
    void createRoom(CreateRoom data);
    String getMapsAndModes();
    void kickPlayer(String playerId);
    void removePlayerFromBlackList(String roomId, String playerId);
    String getAdminList();
    void createAdmin(CreateAdmin data);
    void deleteAdmin(CreateAdmin data);

    void closeConnection();
}
