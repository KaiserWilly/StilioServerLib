package network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public interface InifQueryServer extends Remote {
    void registerNode(String ip, int port) throws RemoteException;

    void removeArray(Array a) throws RemoteException;

    void queryErrState(String statement) throws RemoteException;

    void printUnassignedNodes() throws RemoteException;

    ArrayList<Shard> getShardList() throws RemoteException;

    ArrayList<Array> getArrayList() throws RemoteException;

    ArrayList<Node> getUnassignedNodes() throws RemoteException;

    void stopQuery(String altQryIP, int altQryPrt) throws RemoteException;

    void stopQuery(String reason) throws RemoteException;
}
