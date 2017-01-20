package network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Created by james on 10/25/2016.
 */
public interface InifArray extends Remote {

    void setCoreNode(Node n) throws RemoteException;

    void addShardMap(Node node) throws RemoteException;

    HashMap<String, Node> getShardMap() throws RemoteException;
}
