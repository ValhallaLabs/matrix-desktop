package ua.softgroup.matrix.desktop.spykit.listeners.titleslistener;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class MacOsActiveWindowListener extends ActiveWindowListener {
    @Override
    protected String getProcessTitle() {
       return "Unable to get title on Mac";
    }
}
