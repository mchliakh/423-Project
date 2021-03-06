package udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import udp.FIFOObjectUDP.Message;
import utils.LiteLogger;

public class ObjectUDP {
        private final int BUFFER_SIZE = 256*16;

        private DatagramSocket socket;

        public ObjectUDP() {}
        public ObjectUDP(int port) {
	        try {
                this.socket = new DatagramSocket(port);
	        } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
	        }
        }

        public void send(String host, int port, Serializable obj) {
        	try {
            // marshal the object
        	LiteLogger.log("Marshalling...\n");
        	LiteLogger.log("Host=", host, "Port=", port);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(obj);

            LiteLogger.log("Marshalling complete");
            // send request
            byte[] buf = new byte[BUFFER_SIZE];
            buf = bStream.toByteArray();
            InetAddress address = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            LiteLogger.log("Send complete");
            oo.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }

//        public void sendGroup(ArrayList<String> group) throws InterruptedException {
//        try {
//        	// marshal the object
//            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//            ObjectOutput oo = new ObjectOutputStream(bStream);
//            //oo.writeObject(obj);
//            oo.writeObject(queue.take());
//
//            // send request
//            byte[] buf = new byte[BUFFER_SIZE];
//            buf = bStream.toByteArray();
//
//            for(int i=0;i<group.size(); i++){
//                InetAddress address = InetAddress.getByName(group.get(i));
//                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, SERVER_PORT);
//                socket.send(packet);
//            }
//            oo.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        }

        public Object receive() {
            Object obj = null;
            try {
                // receive request
                byte[] buf = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);                
                socket.receive(packet);
                LiteLogger.log("Packet Received - Unmarshalling");

                // unmarshall the object
                ByteArrayInputStream bStream = new ByteArrayInputStream(buf);
                ObjectInput oi = new ObjectInputStream(bStream);
                obj = oi.readObject();
                LiteLogger.log("Marshalling Complete. Obj=", obj);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return obj;
        }
        

        public Object receiveWithTimeout(int timeout) throws SocketTimeoutException {
            Object obj = null;
            try {
                // receive request
                byte[] buf = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.setSoTimeout(timeout);                
                socket.receive(packet);
                

                // unmarshall the object
                ByteArrayInputStream bStream = new ByteArrayInputStream(buf);
                ObjectInput oi = new ObjectInputStream(bStream);
                obj = oi.readObject();
            } catch (SocketTimeoutException e) { 
            	throw e;
            } catch (Exception e) { 
                e.printStackTrace();
            }            
            return obj;
        }

        public void close() {
        	socket.close();
        }
}