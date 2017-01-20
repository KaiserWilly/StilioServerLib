package network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by JD Isenhart on 12/7/2016.
 * Testing RMI creation in Java 8
 */
public interface InifQueryClient extends Remote {
    Array assignToArray(Client c) throws RemoteException;
    void openArray(Array data) throws RemoteException;
    void closeArray(Array data) throws RemoteException;
}
