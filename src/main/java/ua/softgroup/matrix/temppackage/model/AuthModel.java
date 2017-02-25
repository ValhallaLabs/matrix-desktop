package ua.softgroup.matrix.temppackage.model;

import java.io.Serializable;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class AuthModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public AuthModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
