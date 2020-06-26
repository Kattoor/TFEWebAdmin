package server.models;

import java.util.ArrayList;
import java.util.List;

public class RoomMessage {
    private int frequency;
    private boolean bInOrder;
    private boolean bInChat;
    private List<String> messages;

    public RoomMessage() {
        frequency = 20;
        bInOrder = true;
        bInChat = true;
        messages = new ArrayList<>();
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean isbInOrder() {
        return bInOrder;
    }

    public boolean isbInChat() {
        return bInChat;
    }

    public List<String> getMessages() {
        return messages;
    }
}
