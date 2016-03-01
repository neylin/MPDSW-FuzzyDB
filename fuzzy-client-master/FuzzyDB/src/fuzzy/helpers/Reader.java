/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.helpers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;

/**
 *
 * @author bishma-stornelli
 */
public class Reader {
    
    protected ConsoleReader console;
    
    public Reader() {
        try {
            console = new ConsoleReader(System.in, new OutputStreamWriter(AnsiConsole.out));
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String nextLine() {
        try {
            return console.readLine();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return null;        
    }

    public String nextLine(String prompt) {
        try {
            return console.readLine(prompt);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
