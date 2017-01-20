package network;

import network.shards.CoreShard;

import java.net.Inet4Address;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by JD Isenhart on 12/7/2016.
 * Testing RMI creation in Java 8
 */
public class Query {
    ArrayList<Shard> shardList;

    public Query(ArrayList<Shard> shardList) {
        shardList.add(new CoreShard());
        this.shardList = shardList;
    }

    public void startQuery(int port) {
        String ip = null;
        try {
            LocateRegistry.createRegistry(port);
            ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println("Unable to create Query Registry");
            System.exit(1);
        }
        startQueryServer(port);
        startQueryClient(port);
        System.out.println("Query Server Created!");
        System.out.println("IP Address: " + ip);
        System.out.println("Port: " + port);
        new QueryIOConsole(ip, port).run();
    }

    public void startQueryServer(int port) {
        try {
            QueryServer obj = new QueryServer(port, shardList);// Create new instance of content for RMI to use
            Registry registry = LocateRegistry.getRegistry(port);//Denote port to get registry from
            registry.bind("QueryServer", UnicastRemoteObject.exportObject(obj, 0)); //Bind stub to registry

            System.out.println("Query Server \"QueryServer\" Started!");

        } catch (Exception e) {
            System.err.println("Can't create Query: Server");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void startQueryClient(int port) {
        try {
            QueryClient obj = new QueryClient();// Create new instance of content for RMI to use
            Registry registry = LocateRegistry.getRegistry(port);//Denote port to get registry from
            registry.bind("QueryClient", UnicastRemoteObject.exportObject(obj, 0)); //Bind stub to registry

            System.out.println("Query Server \"QueryClient\" Started!");
        } catch (Exception e) {
            System.err.println("Can't create Query: Client");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
