package network;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JD Isenhart on 12/7/2016.
 * Testing RMI creation in Java 8
 */
public class QueryClient implements InifQueryClient {
    List<Array> arrayList = new ArrayList<>();
    HashMap<Array, List<Client>> balance = new HashMap<>();

    public Array assignToArray(Client c) throws RemoteException {
        List<Integer> counts = new ArrayList<>();
        for (Array a : arrayList) {
            counts.add(balance.get(a).size());
        }
        int min = Integer.MAX_VALUE;
        for (int i : counts) {
            if (min > i) min = i;
        }
        Array server = arrayList.get(counts.indexOf(min));
        List<Client> playerList = balance.get(server);
        playerList.add(c);
        balance.replace(server, playerList);
        System.out.println("Client Connected (" + c.getClientIP() + ")! Directed to (" + server.getShardMap().get("Core").getNodeIP() + ")");
        return server;
    }

    public void openArray(Array data) {
        arrayList.add(data);
        balance.put(data, new ArrayList<>());
        try {
            System.out.println("Array Opened to Clients! (" + data.getShardMap().get("Core").getNodeIP() + ")");
        } catch (RemoteException e) {
            System.out.println("Array Opened to Clients!");
        }
    }

    public void closeArray(Array data) {
        arrayList.remove(data);
        System.out.println("Array Closed to Clients!");
    }
}
