/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.helpers;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author bishma-stornelli
 */
public class Logger {
    
    private static final java.util.logging.Logger LOGGER = 
            getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public static void severe(String s) {
        LOGGER.log(SEVERE, s);
    }

    public static void severe(String s, Throwable e) {
        LOGGER.log(SEVERE, s, e);
    }
    
    public static void notice(String s) {
        Printer.printlnInGreen(s);
    }
    
    public static void info(String s) {
        LOGGER.info(s);
    }

    public static void debug(String string) {
        //Printer.printInRed("DEBUG: ");
        //Printer.printlnInRed(string);
    }
    
    public static void logQuery(String string) {
        Printer.printlnInRed("SQL: "+string);
    }
}
