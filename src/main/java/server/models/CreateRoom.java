package server.models;

import java.util.List;

public class CreateRoom {
    private String map;
    private int matchType;
    private int gameLength;
    private int maxPlayer;
    private int maxScore;
    private int warmupTime;
    private int spawnProtectionTime;
    private int injuryTime;
    private int timeBetweenMatches;
    private int goalTakenTime;
    private int pspTakingTime;
    private String region;
    private String roomName;
    private int numOfBots;
    private boolean bSaveChatLog;
    private boolean bEnableWordCensorship;
    private List<WeaponRule> weaponRules;
    private List<String> mapRotation;
    private boolean bAllowSpectator;
    private RoomMessage roomMessage;
    private String password;

    public CreateRoom() {

    }

    public CreateRoom(String map, int matchType, String roomName, List<WeaponRule> weaponRules, List<String> mapRotation, RoomMessage roomMessage) {
        this.map = map;
        this.matchType = matchType;
        this.gameLength = 20;
        this.maxPlayer = 24;
        this.maxScore = 250;
        this.warmupTime = 30;
        this.spawnProtectionTime = 5;
        this.injuryTime = 120;
        this.timeBetweenMatches = 30;
        this.goalTakenTime = 600;
        this.pspTakingTime = 10;
        this.region = "local";
        this.roomName = roomName;
        this.numOfBots = 0;
        this.bSaveChatLog = false;
        this.bEnableWordCensorship = false;
        this.weaponRules = weaponRules;
        this.mapRotation = mapRotation;
        this.bAllowSpectator = false;
        this.roomMessage = roomMessage;
        this.password = "";
    }

    public String getMap() {
        return map;
    }

    public int getMatchType() {
        return matchType;
    }

    public int getGameLength() {
        return gameLength;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getWarmupTime() {
        return warmupTime;
    }

    public int getSpawnProtectionTime() {
        return spawnProtectionTime;
    }

    public int getInjuryTime() {
        return injuryTime;
    }

    public int getTimeBetweenMatches() {
        return timeBetweenMatches;
    }

    public int getGoalTakenTime() {
        return goalTakenTime;
    }

    public int getPspTakingTime() {
        return pspTakingTime;
    }

    public String getRegion() {
        return region;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getNumOfBots() {
        return numOfBots;
    }

    public boolean isbSaveChatLog() {
        return bSaveChatLog;
    }

    public boolean isbEnableWordCensorship() {
        return bEnableWordCensorship;
    }

    public List<WeaponRule> getWeaponRules() {
        return weaponRules;
    }

    public List<String> getMapRotation() {
        return mapRotation;
    }

    public boolean isbAllowSpectator() {
        return bAllowSpectator;
    }

    public RoomMessage getRoomMessage() {
        return roomMessage;
    }

    public String getPassword() {
        return password;
    }
}
