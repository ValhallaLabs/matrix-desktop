package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class LinuxActiveWindowListener extends ActiveWindowListener {

    @Override
    protected String getProcessTitle() throws ActiveWindowListenerFactory.XdotoolException {
        try {
            Process p = Runtime.getRuntime().exec("./xdotool getwindowfocus getwindowname");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String title = br.readLine();
            p.waitFor();
            p.destroy();
            return title;
        } catch (InterruptedException | IOException e) {
//            return "Unable to get window, something went wrong";
            throw new ActiveWindowListenerFactory.XdotoolException();
        }
    }


}
