package com.elcom.usersystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Admin
 */
public class UdpMulticastSender {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        int mcPort = 1025;
        String mcIPStr = "224.0.0.1";
        DatagramSocket udpSocket = new DatagramSocket();

        InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);
        byte[] msg = "Hello".getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        packet.setAddress(mcIPAddress);
        packet.setPort(mcPort);
        try {
            udpSocket.send(packet);
            System.out.println("Sent a  multicast message.");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if( udpSocket.isConnected() )
                udpSocket.close();
            System.out.println("Exiting application");
        }
    }
    
    private static String printException(Exception ex) {
        return ex.getCause()!=null ? ex.getCause().toString() : ex.toString();
    }
}
