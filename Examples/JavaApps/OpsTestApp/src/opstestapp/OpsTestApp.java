/*
 * OpsTestApp.java
 */

package opstestapp;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class OpsTestApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new OpsTestView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of OpsTestApp
     */
    public static OpsTestApp getApplication() {
        return Application.getInstance(OpsTestApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
/// Needed on Windows 7, to get correct netmas for IP4
///        System.setProperty("java.net.preferIPv4Stack","true");  ///TEST
        launch(OpsTestApp.class, args);
    }
}
