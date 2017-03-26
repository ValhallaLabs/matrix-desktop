package ua.softgroup.matrix.desktop.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class SocketProvider {
    private static String hostName;
    private static String portNumber;

    public static Socket openNewConnection() throws IOException, NumberFormatException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(hostName, Integer.parseInt(portNumber)), 5000);
        return socket;
    }

    static String getHostName() {
        return hostName;
    }

    static void setHostName(String hostName) {
        SocketProvider.hostName = hostName;
    }

    static String getPortNumber() {
        return portNumber;
    }

    static void setPortNumber(String portNumber) {
        SocketProvider.portNumber = portNumber;
    }
}
