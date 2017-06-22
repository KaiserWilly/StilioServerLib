package network;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created 10/24/2016
 * Software Development - Team 2063-1
 * Colorado TSA Conference - Feb 2017
 *
 * Purpose: Node is the basic building block of
 * the framework. It represents one available machine
 * on which server code can execute on. They are
 * assigned roles (Shards)
 */
public class Node implements InifNode, InifNodeServer, Serializable {
    private Array arrayData; //Parent Array
    private Shard shard; //Role
    private String queryIP, nodeIP; //OSI Level 3 Addresses
    private int nodePort = 1180, qport = 1180; //Port Addresses
    transient private Timer timer = new Timer();

    public Node() {
    }

    public Node(String queryIP, int qPort) {
        try {
            this.nodeIP = Inet4Address.getLocalHost().getHostAddress();
            this.queryIP = queryIP;
            this.qport = qPort;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        createRegistry();
        startAdminServer();
        registerWithQuery(queryIP, qPort);
    }

    public Node(String nodeIP, int port, Shard shard) {
        this.nodeIP = nodeIP;
        this.nodePort = port;
        this.shard = shard;
    }

    private void createRegistry() { //Create local RMI Registry
        try {
            LocateRegistry.createRegistry(nodePort);
        } catch (RemoteException e) { //Recursively call method until open port is found
            if (nodePort > 1200) {
                System.err.println("Unable to bind to a port!");
                System.exit(0);
            }
            nodePort++;
            createRegistry();
        }
    }

    private void startAdminServer() { //Start Administrative RMI server
        try {
            Node obj = new Node();// Create new instance of content for RMI to use
            InifNodeServer stub = (InifNodeServer) UnicastRemoteObject.exportObject(obj, 0); //create stub
            Registry registry = LocateRegistry.getRegistry(nodePort);//Denote nodePort to get registry from
            registry.bind("AdminServer", stub); //Bind stub to registry
            System.out.println("Admin Server (InifNodeServer) Ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        try {
            Node obj = new Node();// Create new instance of content for RMI to use
            InifNode stub = (InifNode) UnicastRemoteObject.exportObject(obj, 0); //create stub
            Registry registry = LocateRegistry.getRegistry(nodePort);//Denote nodePort to get registry from
            registry.bind("AdminNode", stub); //Bind stub to registry
            System.out.println("Admin Server (InifNode) Ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private void registerWithQuery(String queryIP, int port) { //Register with remote Query
        try {
            Registry registry = LocateRegistry.getRegistry(queryIP, port); //IP Address of RMI Server, nodePort of RMIRegistry
            InifQueryServer stub = (InifQueryServer) registry.lookup("QueryServer"); //Name of RMI Server in registry
            stub.registerNode(Inet4Address.getLocalHost().getHostAddress(), nodePort);
            System.out.println("Successfully Registered with QueryServer! Port: " + nodePort);
            System.out.println();
        } catch (Exception e) {
            System.err.println("Can't connect to QueryServer Server!");
            System.err.println("IP Address: " + queryIP + "  Port: " + port);
            System.err.println("Terminating Node");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void startService() throws RemoteException { //Start local Shard service
        verifyNodePort();
        System.out.println("Current Port: " + nodePort);
        System.out.println("Service Started!");
        System.out.print("Core IP: " + arrayData.getShardMap().get("Core").getNodeIP());
        System.out.println(" Port: " + arrayData.getShardMap().get("Core").getNodePort());
        System.out.println("Role of this server: " + shard.getRole());
        System.out.println();
        shard.startShard(arrayData, this);
        if (!shard.getRole().equals("Core")) { //Check-in with Core
            startCoreCheck();
        }


    }

    public void unassignNode(String reason) throws RemoteException { //Remove Node from Array
        timer.cancel();
        verifyNodePort();

        System.err.println("Node Unassigned! Reason: " + reason);
        System.out.println("Current Port: " + nodePort);
        System.out.println("Query IP:"+queryIP+" Port:"+qport);
        registerWithQuery(queryIP,qport);
        arrayData = null;
        shard = null;

    }

    @Override
    public void terminateNode(String reason) throws RemoteException { //End Node Thread
        //Run Shard Cleanup methods
        System.err.println("Node to Terminate: " + reason);
        System.exit(1);
    }

    public void setShard(Shard shard) {
        this.shard = shard;
    }

    public Shard getShard() {
        try {
            return shard;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getNodeIP() throws RemoteException {
        return nodeIP;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setArrayData(Array data) {
        this.arrayData = data;
    }


    private void startCoreCheck() {
        System.out.println("Core Integrity Check Started!");
        try {
            timer.schedule(timerTask(), 7000, 4000); //Task, delay, update speed
        } catch (IllegalStateException e) {
            System.out.println("Resetting Timer!");
            timer = new Timer();
            startCoreCheck();
        }
    }

    private TimerTask timerTask() { //Timer for core check
        return new TimerTask() {

            @Override
            public void run() {
                try {
                    Registry registry = LocateRegistry.getRegistry(arrayData.getShardMap().get("Core").getNodeIP(), arrayData.getShardMap().get("Core").getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    registry.lookup("AdminServer");
                } catch (Exception e) {
                    System.out.println("Core Timed Out!");
                    try {
//                        timer.cancel();
                        reportQryErr();
                        unassignNode("Core timeout!");

                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
    }

    private void verifyNodePort() {
        try {
            int certPort = arrayData.getShardMap().get(this.shard.getRole()).getNodePort();
            if (nodePort != certPort) {
                nodePort = certPort;
                System.out.println("Node Port Reset! Port: " + certPort);
            } else {
                System.out.println("Node Port Verified!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Node Port Verification Failed!");
        }

    }

    private void reportQryErr() { // Report failure of Core Node to Query Server
        try {
            Node core = arrayData.getShardMap().get("Core");
            Registry queryRegistry = LocateRegistry.getRegistry(arrayData.getQueryIP(), arrayData.getQueryPort()); //IP Address of RMI Server, port of RMIRegistry
            InifQueryServer queryStub = (InifQueryServer) queryRegistry.lookup("QueryServer");
            queryStub.queryErrState("Reported Core Timeout! \n " +
                    "Core IP:" + core.getNodeIP() + " Port:" + core.getNodePort() +
                    "\n Reporting Node IP:" + Inet4Address.getLocalHost().getHostAddress() + " Port:" + getNodePort());

        } catch (Exception e) {
            System.err.println("Unable to inform QueryServer of Core Timeout!");
        }
    }

    public boolean ping() throws RemoteException {
        return true;
    }
}
