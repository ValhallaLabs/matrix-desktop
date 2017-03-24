package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class LinuxActiveWindowListener extends ActiveWindowListener {

    @Override
    protected String getProcessTitle() {
        return "hey!";
    }

}
