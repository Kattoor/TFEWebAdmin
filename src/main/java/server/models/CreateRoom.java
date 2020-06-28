package server.models;

import java.util.List;

public class CreateRoom {
    private final String map;
    private final int matchType;
    private final int gameLength;
    private final int maxPlayer;
    private final int maxScore;
    private final int warmupTime;
    private final int spawnProtectionTime;
    private final int injuryTime;
    private final int timeBetweenMatches;
    private final int goalTakenTime;
    private final int pspTakingTime;
    private final String region;
    private final String roomName;
    private final int numOfBots;
    private final boolean bSaveChatLog;
    private final boolean bEnableWordCensorship;
    private final List<WeaponRule> weaponRules;
    private final List<String> mapRotation;
    private final boolean bAllowSpectator;
    private final RoomMessage roomMessage;
    private final String password;

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
