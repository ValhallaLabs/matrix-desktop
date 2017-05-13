package ua.softgroup.matrix.desktop.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class UTF8Control extends ResourceBundle.Control {

    public ResourceBundle newBundle( Locale locale, ClassLoader loader)
    {
        String bundleName = toBundleName("Locale", locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = loader.getResourceAsStream(resourceName);;
        if (stream != null) {
            try {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return bundle;
    }
}
