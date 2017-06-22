package network;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 11/17/2016
 * Software Development - Team 2063-1
 * Colorado TSA Conference - Feb 2017
 *
 * Purpose: QueryServer is the implementation
 * of the server responsible for handling
 * incoming Server connections. It controls if
 * Arrays can accept clients, and directs clients
 * to valid Arrays. it also manages inter-Array
 * communication, and checks the health of each
 * Array.
 */
public class QueryServer implements InifQueryServer {
    private ArrayList<Node> nodeList = new ArrayList<>();
    private ArrayList<Array> arrayList = new ArrayList<>();
    private final ArrayList<Shard> SHARDS;
    private final int QUERYPORT;

    QueryServer(int port, ArrayList<Shard> shards) {
        this.QUERYPORT = port;
        this.SHARDS = shards;
    }

    public void registerNode(String ip, int port) throws RemoteException { //Register new Node
        Node nNode = new Node(ip, port, null);
        boolean update = false;

        for (int i = 0; i < nodeList.size() && !update; i++) {
            Node n = nodeList.get(i);
            if (n.getNodeIP().equals(ip) && n.getNodePort() == port) {
                nodeList.set(i, nNode);
                System.out.println("Reconnected Node! IP: " + ip + " Port: " + port);
                update = true;
            }
        }
        if (!update) {
            nodeList.add(new Node(ip, port, null));
            System.out.println("New Node! IP: " + ip + " Port: " + port);
        }


        if (nodeList.size() >= SHARDS.size()) {
            System.out.println("Creating new Array!");
            new ArrayCreate().run();
        }
    }

    @Override
    public void removeArray(Array a) throws RemoteException { //Remove Array from references
        arrayList.remove(a);
        try {
            Registry registry = LocateRegistry.getRegistry(Inet4Address.getLocalHost().getHostAddress(), QUERYPORT); //IP Address of RMI Server, port of RMIRegistry
            InifQueryClient stub = (InifQueryClient) registry.lookup("QueryClient");
            stub.closeArray(a);
        } catch (Exception e) {
            System.out.println("Can't open Array to clients!");
        }

        System.err.println("Array Dissolved!");
    }

    public void queryErrState(String statement) throws RemoteException { //Print out Query Error message
        System.err.println(statement);
    }

    private class ArrayCreate extends Thread {
        //Concurrent thread that creates a new Array and dispatches it from Query Server
        Array data = new Array();
        public void run() {
            List<Node> aList = new ArrayList<>();
            Node n = null;
            try {
                data.setQueryIP(Inet4Address.getLocalHost().getHostAddress());
                data.setQueryPort(1180);
                for (int i = 0; i < SHARDS.size(); i++) { //One Node per Shard
                    n = nodeList.get(i);
                    aList.add(n);
                    n.setShard(SHARDS.get(i));
                    data.addShardMap(n);
                    Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    registry.lookup("AdminServer"); //Verify Node is active
                    data.addNode(n); //Add node to Array
                }
            } catch (Exception e) {
                System.err.println("Unable to create new Array! (Ping)");
                nodeList.remove(nodeList.indexOf(n)); //Remove Bad node from NodeList
                System.out.println("Returned good Nodes to List!");
                return;
            }
            nodeList.removeAll(aList); //Remove nodes in new array from Query

            for (Node o : data.getNodeList()) {  //Transcribe data to Nodes
                startServices startNodes = new startServices(data, o);
                startNodes.run();
            }

            arrayList.add(data);
            try {
                Registry registry = LocateRegistry.getRegistry(Inet4Address.getLocalHost().getHostAddress(), QUERYPORT); //IP Address of RMI Server, port of RMIRegistry
                InifQueryClient stub = (InifQueryClient) registry.lookup("QueryClient");
                stub.openArray(data);
            } catch (Exception e) {
                System.out.println("Can't open Array to clients!");
            }
        }

        class startServices extends Thread { //Thread that starts each Node's services concurrently
            Array data;
            Node n;

            startServices(Array data, Node n) {
                this.data = data;
                this.n = n;
            }

            public void run() {
                try {
                    Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    InifNode stub = (InifNode) registry.lookup("AdminNode"); //Name of RMI Server in registry
                    stub.setArrayData(data);
                    stub.setShard(n.getShard());
                    stub.startService();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void printUnassignedNodes() {
        try {
            System.out.println("Nodes Unassigned: " + nodeList.size());
            for (Node n : nodeList) {
                System.out.println("Node IP: " + n.getNodeIP() + " Port: " + n.getNodePort());
            }
        } catch (Exception e) {
            System.out.println("Can't print Unassigned Nodes!");
        }
    }

    public ArrayList<Shard> getShardList() throws RemoteException {
        return SHARDS;
    }

    public ArrayList<Array> getArrayList() throws RemoteException {
        return null;
    }

    public ArrayList<Node> getUnassignedNodes() throws RemoteException {
        return null;
    }

    public void stopQuery(String altQryIP, int altQryPrt) throws RemoteException {

    }

    public void stopQuery(String reason) throws RemoteException {
//        System.err.println("QueryServer Server Terminated! Reason: " + reason);
        ArrayList<Array> aList = arrayList;
        for (Array a : aList) {
            ArrayList<Node> nList = a.getNodeList();
            for (Node n : nList) {
                try {
                    Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    InifNode stub = (InifNode) registry.lookup("AdminNode");
                    try {
                        stub.terminateNode(reason);
                    } catch (Exception e) {
                        System.err.println("Node Terminated: IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
                    }
                } catch (Exception e) {
                    System.out.println("\nCan't Contact Node!(Array)\n IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
                }
            }
        }
        for (Node n : nodeList) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                InifNode stub = (InifNode) registry.lookup("AdminNode");
                try {
                    stub.terminateNode(reason);
                } catch (Exception e) {
                    System.err.println("Node Terminated: IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
                }
            } catch (Exception e) {
                System.out.println("\nCan't Contact Node! (nodeList)\n IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
            }
        }
        System.exit(1);
    }

}



