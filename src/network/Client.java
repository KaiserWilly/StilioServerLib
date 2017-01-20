package network;

import java.io.Serializable;

/**
 * Created by JD Isenhart on 12/7/2016.
 * Testing RMI creation in Java 8
 */
public class Client implements Serializable {
    private Array server;
    private String playerName, clientIP, coreIP;
    private int clientPort, corePort;

    public Client(String playerName, String clientIP, int clientPort) {
        this.playerName = playerName;
        this.clientIP = clientIP;
        this.clientPort = clientPort;
    }

    public void setServer(Array server) {
        this.server = server;
        try {
            setCore(server.getShardMap().get("Core").getNodeIP(), server.getShardMap().get("Core").getNodePort());
        } catch (Exception e) {
            System.out.println("Unable to set Core in Client");
        }
    }

    public void setCore(String ip, int port) {
        coreIP = ip;
        corePort = port;
    }

    public String getCoreIP() {
        return coreIP;
    }

    public int getCorePort() {
        return corePort;
    }

    public String getClientIP() {
        return clientIP;
    }

    public int getClientPort() {
        return clientPort;
    }
}
