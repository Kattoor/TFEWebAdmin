package server.models;

import java.util.ArrayList;
import java.util.List;

public class RoomMessage {
    private final int frequency;
    private final boolean bInOrder;
    private final boolean bInChat;
    private final List<String> messages;

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
