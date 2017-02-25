package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class MacOsActiveWindowListener extends ActiveWindowListener {

    MacOsActiveWindowListener(long projectId) {
        super(projectId);
    }
    @Override
    protected String getProcessTitle() {
       return "Unable to get title on Mac";
    }
}