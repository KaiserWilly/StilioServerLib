package network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public interface InifNode extends Remote {

    void setShard(Shard shard) throws RemoteException;

    Shard getShard() throws RemoteException;

    void setArrayData(Array data) throws RemoteException;

    void startService() throws RemoteException;

    void unassignNode(String reason) throws RemoteException;

    void terminateNode(String reason) throws RemoteException;
}
