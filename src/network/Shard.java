package network;

import java.io.Serializable;


/**
 * Created by JD Isenhart on 10/24/2016.
 * Testing RMI creation in Java 8
 */
public abstract class Shard implements Serializable {
    private String role = "Unassigned";

    public Shard(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public abstract void startShard(Array data, Node n);
//        try {
//            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
//        } catch (Exception e){
//            e.printStackTrace();
//            System.err.println("Unable to start shard!");
//        }
//        try {
//            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
//        switch (n.getShard().getRole()) {
//            case "Core":
//                CoreShard coreShard = new CoreShard();
//                InifCore coreStub = (InifCore) UnicastRemoteObject.exportObject(coreShard, 0);
//                registry.bind("Core", coreStub);
//                System.out.println("Client Server (InifCore) started!");
//                break;
//            case "Contract":
//                ContractShard contractShard = new ContractShard();
//                InifContract contractStub = (InifContract) UnicastRemoteObject.exportObject(contractShard, 0);
//                registry.bind("Contract", contractStub);
//                System.out.println("Client Server (InifContract) started!");
//                break;
//            case "Chat":
//                ChatShard chatShard = new ChatShard();
//                InifChat chatStub = (InifChat) UnicastRemoteObject.exportObject(chatShard, 0);
//                registry.bind("Chat", chatStub);
//                System.out.println("Client Server (InifChat) started!");
//                break;
//            case "Filing":
//                FilingShard filingShard = new FilingShard();
//                InifFiling filingStub = (InifFiling) UnicastRemoteObject.exportObject(filingShard, 0);
//                registry.bind("Filing", filingStub);
//                System.out.println("Client Server (InifFiling) started!");
//                break;
//        }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


}
