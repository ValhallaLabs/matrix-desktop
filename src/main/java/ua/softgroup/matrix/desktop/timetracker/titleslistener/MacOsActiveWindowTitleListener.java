package ua.softgroup.matrix.desktop.timetracker.titleslistener;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class MacOsActiveWindowTitleListener extends  ActiveWindowTitleListener {
    private final String script = "tell application \"System Events\"\n" +
            "\tname of application processes whose frontmost is tru\n" +
            "end";

    @Override
    protected String getProcessTitle() {
        ScriptEngine appleScript = new ScriptEngineManager().getEngineByName("AppleScript");
        try {
            return (String) appleScript.eval(script);
        } catch (ScriptException e) {
            logger.debug("Title wasn't received successfully:", e);
            return "No title" ;
        }
    }

    public static void main(String[] args) {
        MacOsActiveWindowTitleListener macOsActiveWindowTitleListener = new MacOsActiveWindowTitleListener();
        macOsActiveWindowTitleListener.turnOn();
    }
}
