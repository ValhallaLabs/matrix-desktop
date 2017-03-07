package ua.softgroup.matrix.desktop.utils;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Vadim on 10.02.2017.
 */
public class SocketProvider {
    private static String hostName; //= "192.168.11.84";
    private static int portNumber;// = 6666;

    public static Socket openNewConnection() throws IOException {
        return new Socket(hostName, portNumber);
    }

    public static String getHostName() {
        return hostName;
    }

    public static void setHostName(String hostName) {
        SocketProvider.hostName = hostName;
    }

    public static int getPortNumber() {
        return portNumber;
    }

    public static void setPortNumber(int portNumber) {
        SocketProvider.portNumber = portNumber;
    }
}
