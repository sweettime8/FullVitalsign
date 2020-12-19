package com.elcom.notify;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class UdpMulticastSender2 {

    public static void main(String[] args) {
        new UdpMulticastSender2().now();
    }

    public void now() {
        try {
            DatagramSocket sock = new DatagramSocket(0);
            for (int i = 0; i < 32; i++) {
                new Thread(new Sender(sock)).start();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public class Sender implements Runnable {

        private final DatagramSocket sock;

        public Sender(DatagramSocket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            try {
                int mcPort = 45566;
                String mcIPStr = "231.12.21.132";
                InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);
                byte[] msg = "Hello".getBytes();
                DatagramPacket packet = new DatagramPacket(msg, msg.length);
                packet.setAddress(mcIPAddress);
                packet.setPort(mcPort);
                for (int i = 0; i < 12; i++) {
                    try {
                        sock.send(packet);
                        System.out.println("sent!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
