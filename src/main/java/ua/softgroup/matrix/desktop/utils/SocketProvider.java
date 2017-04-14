package ua.softgroup.matrix.desktop.utils;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class SocketProvider {
    private static final String PASSPHRASE = "make_matrix_great_again";

    private static SocketProvider instance;

    private KeyStore clientKeyStore;

    private KeyStore serverKeyStore;

    private SSLContext sslContext;

    private static String hostName;

    private static String portNumber;

    public static SocketProvider getInstance() throws GeneralSecurityException, IOException {
        if (instance == null) {
            instance = new SocketProvider();
        }
        return instance;
    }

    private SocketProvider () throws GeneralSecurityException, IOException {
        setupKeyStores();
        setupSSLContext();
    }

    private void setupKeyStores() throws GeneralSecurityException, IOException {
        serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(SocketProvider.class.getClassLoader().getResourceAsStream("server.public"),
                "public".toCharArray());
        clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(SocketProvider.class.getClassLoader().getResourceAsStream("client.private"),
                PASSPHRASE.toCharArray());
    }

    private void setupSSLContext() throws GeneralSecurityException, IOException {
        sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init(getKeyManagers(), getTrustManagers(), getSecureRandom());
    }

    private KeyManager[] getKeyManagers() throws GeneralSecurityException, IOException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(clientKeyStore, PASSPHRASE.toCharArray());
        return kmf.getKeyManagers();
    }

    private TrustManager[] getTrustManagers() throws KeyStoreException, NoSuchAlgorithmException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(serverKeyStore);
        return tmf.getTrustManagers();
    }

    private SecureRandom getSecureRandom() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();
        return secureRandom;
    }

    public SSLSocket openNewConnection() throws IOException, NumberFormatException {
        return (SSLSocket) sslContext
                .getSocketFactory()
                .createSocket(hostName, Integer.parseInt(portNumber));
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
