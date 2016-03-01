package fuzzy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import fuzzy.database.Connector;
import fuzzy.database.Connector.ExecutionResult;
import fuzzy.helpers.Printer;
import fuzzy.helpers.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entry point for the fuzzy database Client.
 *
 * It defines the accepted command line parameters, parses them, sets up the database Connector,
 * and then enters a loop for processing user commands and queries.
 */
public class Client {

    private static class Parameters {

        @Parameter(names = {"-u", "--username"}, description = "Username")
        private String username = "fuzzy";

        @Parameter(names = {"-p", "--password"}, password = true, description = "Password")
        private String password = "fuzzy";

        @Parameter(names = {"-h", "--hostname"}, description = "PostgreSQL server hostname")
        private String host = "127.0.0.1";

        @Parameter(names = {"-d", "--database"}, description = "Database name")
        private String databaseName = "fuzzy";

        @Parameter(names = {"-s", "--schema"}, description = "Initial schema")
        private String schemaName = "public";

        @Parameter(names = {"-D", "--debug"}, description = "Debug mode")
        private boolean debug = false;

        @Parameter(names = "--help", help = true, description = "Show this manual", hidden = true)
        private boolean help;

        @Parameter
        private List<String> additionalParameters = new ArrayList<String>();

    }

    private static Parameters parameters;
    private static Connector connector;

    private Client() {
        // El único propósito de Client es proveer main(), así que no tiene sentido que
        // pueda instanciarse.
    }

    /**
     * Entry point of the fuzzy database Client.
     * 
     * It parses the command line arguments, sets up the database Connector,
     * and then enters a loop for processing user commands and queries.
     * 
     * main() detects administrative commands, such as changing the current
     * schema, and executes them. Everything else is handed over to the
     * Connector for translation and execution.
     * 
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        
        // Parse command line parameters
        parameters = new Parameters();
        JCommander jc = new JCommander(parameters);

        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            jc.usage();
            System.exit(30);
        }

        if (parameters.help) {
            jc.usage();
            System.exit(0);
        }

        // Setup connector
        try {
            connector = new Connector(
                    parameters.host,
                    parameters.username,
                    parameters.password,
                    parameters.databaseName
            );
            connector.setSchema(parameters.schemaName);
        } catch (SQLException e) {
            Printer.printSQLErrors(e);
            System.exit(20);
        }

        // Prompt loop
        Reader keybrd = new Reader();
        String userInput;

        while (true) {
            
            // Detect current selected schema and show it on the prompt.
            String catalogName = null;
            try {
                catalogName = connector.getSchema();
            } catch (SQLException e) {
                Printer.printSQLErrors(e);
            }
            if (null == catalogName) {// There was an error in connection
                System.exit(10);
            }

            if (catalogName.isEmpty()) {
                catalogName = "(none)";
            }

            userInput = keybrd.nextLine("FuzzydoDB [" + catalogName + "]> ");

            // Detect EOF, such as when you use Ctrl+D on a console.
            if (null == userInput) {
                Printer.println("");
                Printer.printlnInWhite("Bye");
                System.exit(0);
            }

            ArrayList<String> sentences = mySplit(userInput);

            // Process each sentence found in the input
            for (String sentence : sentences ) {

                sentence = sentence.trim();
                if ("".equals(sentence)) {
                    continue;
                }
                
                if (proccessAdministrationCommand(sentence)) {
                    // That method returns True if it was successful detecting
                    // and processing an admin command, so we can skip
                    // translation. It would blow up the translator anyway.
                    continue;
                }

                // connector.execute() translates and then executes.
                ExecutionResult result = null;
                try {
                    result = connector.execute(sentence);
                } catch (SQLException e) {
                    Printer.printSQLErrors(e);
                    continue;
                }
                
                // TODO: it reeks of bad design to use side effects of execute()
                // TODO: to extract the result. It'd be better if execute()
                // TODO: returned a class with the results, and use that instead.
                if (null != result) {
                    if (null != result.result) {
                        Printer.printResultSet(result.result);
                    }
                    if (result.updateCount != -1) {
                        Printer.printRowsUpdated(result.updateCount);
                    }
                }
            }
        }
    }

    /**
     * In case an administrative sentence is specified, execute it and indicate
     * (returning) that no further actions are required.
     *
     * The previous developer didn't specify WHAT was considered an administrative command.
     * From what I understand from the code, it parses and executes "USE <schema>" and
     * "quit".
     *
     * @param sentence Sentence that could be administrative
     * @return if the sentence specified is administrative
     */
    private static boolean proccessAdministrationCommand(String sentence) {
        String[] words = sentence.split(" ");
        if (words.length > 0 && words[0].equalsIgnoreCase("use")) {
            if (words.length == 1) {
                // Missing argument
                Printer.println("ERROR: USE must be followed by a database name");
            } else {
                String catalogName = words[1].replaceAll(";$", "");
                try {
                    connector.setSchema(catalogName);
                } catch (SQLException e) {
                    Printer.printSQLErrors(e);
                }
            }
            return true;
        } else if (words.length > 0 && words[0].equalsIgnoreCase("quit")) {
            Printer.printlnInWhite("Bye");
            System.exit(0);
        }
        return false;
    }
    
    private static ArrayList<String> mySplit(String input){        
        /* Regex: Text Blocks|Line Breaks and Tabs|Single Line Comments|Multiple Line Comments */
        String pattern = "('(''|[^'])*')|[\t\r\n]|(--[^\r\n]*)|(/\\*[\\w\\W]*?(?=\\*/)\\*/)";
        
        ArrayList<String> sentence_list = new ArrayList<String>();
        String match;
        StringBuilder builder = new StringBuilder();
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        int last_end = 0;
        
        // Replace comments
        while ( m.find() ) {
            match = m.group();
            
            if ( match.startsWith("'") && match.endsWith("'") ) {
                builder.append(input, last_end, m.end());
            } else {
                builder.append(input, last_end, m.start());
            }
            
            last_end = m.end();
        }
        
        builder.append(input, last_end, input.length());
        
        // Split sentences by ;
        int start = 0;
        boolean isText = false;
        for ( int i = 0; i < builder.length(); i++) {
            
            if ( builder.charAt(i) == 39 ) { // 39: '
                isText = !isText;
            }
            
            if ( !isText && builder.charAt(i) == 59 ) { // 59: ;
                sentence_list.add( builder.substring(start, i) );
                start = i + 1;
            }
        }
        
        sentence_list.add( builder.substring(start, builder.length()) );
        return sentence_list;
    }
}