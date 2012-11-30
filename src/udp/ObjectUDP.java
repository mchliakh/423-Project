package udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjectUDP {
        private final int BUFFER_SIZE = 256;
        private final int SERVER_PORT = 4445;

        // FIFO queue
        public LinkedBlockingQueue<Object> queue;

        private DatagramSocket socket;

        public ObjectUDP(int port) {
                try {
                        this.socket = new DatagramSocket(port);
                } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        public void send(String host) throws InterruptedException {
        	try {
            // marshal the object
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            //oo.writeObject(obj);
            oo.writeObject(queue.take());

            // send request
            byte[] buf = new byte[BUFFER_SIZE];
            buf = bStream.toByteArray();
            InetAddress address = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, SERVER_PORT);
            socket.send(packet);

            oo.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }

        public void sendGroup(ArrayList<String> group) throws InterruptedException {
        try {
        	// marshal the object
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            //oo.writeObject(obj);
            oo.writeObject(queue.take());

            // send request
            byte[] buf = new byte[BUFFER_SIZE];
            buf = bStream.toByteArray();

            for(int i=0;i<group.size(); i++){
                InetAddress address = InetAddress.getByName(group.get(i));
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, SERVER_PORT);
                socket.send(packet);
            }
            oo.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }

        public void receive() throws InterruptedException {
                Object obj = null;
                try {
                        // receive request
                        byte[] buf = new byte[BUFFER_SIZE];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        // unmarshall the object
                        ByteArrayInputStream bStream = new ByteArrayInputStream(buf);
                        ObjectInput oi = new ObjectInputStream(bStream);
                        obj = oi.readObject();
                } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                queue.put(obj);
        }

        public void close() {
        socket.close();
        }
}