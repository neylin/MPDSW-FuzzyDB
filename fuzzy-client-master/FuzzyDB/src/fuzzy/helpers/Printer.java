/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 *
 * @author bishma-stornelli
 */
public class Printer {

    private static boolean ansiConsoleNotInstalled = true;

    public static void printResultSet(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();
            rs.last();
            int rows = rs.getRow();
            int[] columnDisplaySizes = new int[columns];
            String[][] rowsContent = new String[rows + 1][columns];

            for (int i = 0; i < columns; ++i) {
                rowsContent[0][i] = metaData.getColumnLabel(i + 1);
                columnDisplaySizes[i] = rowsContent[0][i].length();
            }

            int row = 1;
            rs.beforeFirst();
            while (rs.next()) {
                for (int i = 0; i < columns; ++i) {
                    Object v = rs.getObject(i + 1);
                    rowsContent[row][i] = v != null ? v.toString() : "NULL";
                    columnDisplaySizes[i] = Math.max(columnDisplaySizes[i], rowsContent[row][i].length());
                }
                ++row;
            }
            printSeparatorLine(columnDisplaySizes);

            printRow(columnDisplaySizes, rowsContent[0]);

            printSeparatorLine(columnDisplaySizes);

            for (row = 1; row <= rows; ++row) {
                printRow(columnDisplaySizes, rowsContent[row]);
            }
            printSeparatorLine(columnDisplaySizes);

            printlnInWhite(rows
                    + " row" + (rows == 1 ? "" : "s")
                    + " in set");

        } catch (SQLException ex) {
            Printer.printSQLErrors(ex);
        }
    }

    public static void printRowsUpdated(int rowsUpdated) {
        printlnInWhite("Query OK, "
                + rowsUpdated + " row"
                + (rowsUpdated == 1 ? "" : "s")
                + " affected"
        );
    }

    protected static void printSeparatorLine(int[] columnDisplaySizes) {
        for (int i = 0; i < columnDisplaySizes.length; ++i) {
            print("+-");
            for (int j = 0; j < columnDisplaySizes[i]; ++j) {
                print("-");
            }
            if (i + 1 < columnDisplaySizes.length) {
                print("-");
            } else {
                println("-+");
            }
        }
    }

    protected static void printRow(int[] columnDisplaySizes, String[] content) {
        for (int i = 0; i < columnDisplaySizes.length; ++i) {
            print("| ");
            print(content[i]);
            for (int j = 0; j < columnDisplaySizes[i] - content[i].length(); ++j) {
                print(" ");
            }
            if (i + 1 < columnDisplaySizes.length) {
                print(" ");
            } else {
                println(" |");
            }
        }
    }

    public static void print(String string) {
        p(string);
    }

    public static void println(String string) {
        p(string + "\n");
    }

    public static void printInWhite(String string) {
        p((new Ansi()).bold().fg(Ansi.Color.WHITE).a(string).reset().toString());
    }

    public static void printlnInWhite(String string) {
        p((new Ansi()).bold().fg(Ansi.Color.WHITE).a(string + "\n").reset().toString());
    }

    public static void printInBlue(String string) {
        p((new Ansi()).bold().fg(Ansi.Color.BLUE).a(string).reset().toString());
    }

    public static void printlnInBlue(String string) {
        p((new Ansi()).bold().fg(Ansi.Color.BLUE).a(string + "\n").reset().toString());
    }

    private static void p(String string) {
        if (ansiConsoleNotInstalled) {
            AnsiConsole.systemInstall();
            ansiConsoleNotInstalled = true;
        }
        AnsiConsole.out.print(string);
        AnsiConsole.out.flush();
    }

    /**
     * Print SQL errors to standard input in MariaDB format
     *
     * @param ex Exception raised
     */
    public static void printSQLErrors(SQLException ex) {
        do {
            println("");
            println("ERROR "
                    + ex.getErrorCode()
                    + " (" + ex.getSQLState() + "): "
                    + ex.getMessage());
            println("");
//            Throwable t = ex;
//            printStackToDebug(t);
//            while (t.getCause() != null) {
//                t = t.getCause();
//                printStackToDebug(t);
//            }
        } while ((ex = ex.getNextException()) != null);
    }

    protected static void printStackToDebug(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String[] lines = sw.toString().split("\n");
        for (String line : lines) {
            Logger.debug(line);
        }
    }

    public static void printlnInRed(String string) {
        p((new Ansi()).bold().fg(Ansi.Color.RED).a(string + "\n").reset().toString());
    }

    public static void printlnInGreen(String string) {
        p((new Ansi()).bold().fg(Ansi.Color.GREEN).a(string + "\n").reset().toString());
    }

    static void printInRed(String string) {
        p((new Ansi()).bold().fg(Ansi.Color.GREEN).a(string).reset().toString());
    }

}
