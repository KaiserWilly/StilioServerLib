package network;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JD Isenhart on 10/24/2016.
 * Testing RMI creation in Java 8
 */
public class Array implements InifArray, Serializable {
    private ArrayList<Node> nodeList = new ArrayList();
    private HashMap<String, Node> shardMap = new HashMap<>();
    private Node coreNode = null;
    private String queryIP;
    private int queryPort;

    public void addNode(Node node) throws RemoteException {
        nodeList.add(node);
    }

    public void removeNode(Node n) {
        nodeList.remove(n);
    }

    public void setCoreNode(Node n) {
        coreNode = n;
    }

    public void addShardMap(Node node) throws RemoteException {
        shardMap.put(node.getShard().getRole(), node);
    }

    public HashMap<String, Node> getShardMap() throws RemoteException {
        return shardMap;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public void setQueryIP(String ip) {
        queryIP = ip;
    }

    public String getQueryIP() {
        return queryIP;
    }

    public void setQueryPort(int port) {
        queryPort = port;
    }

    public int getQueryPort() {
        return queryPort;
    }
}
