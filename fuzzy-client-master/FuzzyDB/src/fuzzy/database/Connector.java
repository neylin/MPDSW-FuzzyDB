package fuzzy.database;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.parser.TokenMgrError;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import fuzzy.helpers.Logger;
import fuzzy.helpers.Printer;
import fuzzy.common.operations.Operation;
import fuzzy.type3.translator.StatementTranslator;
import fuzzy.type2.translator.StatementType2Translator;
import fuzzy.type5.translator.StatementType5Translator;
import java.util.logging.Level;

public class Connector {

    /**
     * MySQL primitive data types, used to avoid querying for a type on
     * suspicion that could be fuzzy.
     *
     * TODO: This was inherited from the previous developer. It has to be
     * updated with Postgres' types, or better yet, query the database and
     * populate this list at setup time.
     */
    private static final String[] DATA_TYPES = {"TINYINT", "BOOLEAN", "SMALLINT",
        "MEDIUMINT", "INT​", "BIGINT", "INTEGER", "DECIMAL", "DEC, NUMERIC, FIXED​",
        "FLOAT", "DOUBLE", "DOUBLE PRECISION", "BIT", "CHAR", "VARCHAR", "BINARY",
        "CHAR BYTE", "VARBINARY", "TINYBLOB", "BLOB", "BLOB DATA TYPE", "MEDIUMBLOB",
        "LONGBLOB", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT", "ENUM", "SET", "DATE",
        "TIME", "DATETIME", "TIMESTAMP", "YEAR", "POINT", "LINESTRING", "POLYGON",
        "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", "GEOMETRYCOLLECTION", "GEOMETRY"};

    /**
     * Check if @dataType is a native data type of the RDBMS.
     *
     * @param dataType the data type to be tested.
     * @return true if it's a native data type of the RDBMS.
     */
    public static boolean isNativeDataType(String dataType) {
        return Arrays.asList(DATA_TYPES).contains(dataType.toUpperCase());
    }

    // Driver module used by java.sql
    private static final String driver = "org.postgresql.Driver";

    // Driver protocol used to connect to the database
    private static final String driverProtocol = "jdbc:postgresql";

    private Connection connection;
    private ResultSet resultSet;
    private Integer updateCount;
    private String schema = "";
    private boolean libraryMode = false;
    private boolean restoreState = false;

    public boolean isRestoreState() {
        return restoreState;
    }

    public void setRestoreState(boolean restoreState) {
        this.restoreState = restoreState;
    }

    /**
     * Creates a Connector with default parameters, namely:
     * <ul>
     * <li>host: 127.0.0.1 </li>
     * <li>username: fuzzy</li>
     * <li>password: fuzzy</li>
     * <li>databaseName: fuzzy</li>
     * </ul>
     *
     * DEPRECATED: Its only purpose is to avoid breaking existing unittests.
     * Once the tests are updated to specify explicitly the Connector
     * parameters, this constructor can be removed.
     *
     * @throws SQLException
     */
    public Connector() throws SQLException {
        this("127.0.0.1", "fuzzy", "fuzzy", "fuzzy");
        // TODO: Log a deprecation warning.
    }

    /**
     * Creates a Connector with the parameters that will be used to connect to
     * the RDBMS.
     *
     * <p>
     * The initial schema will be internally set as "" (the empty string), and
     * the RDBMS will use whatever default it uses. Use setSchema() to override
     * this.</p>
     *
     * @param host the hostname, of the form name[:port]. The name might be an
     * IP address.
     * @param username the username as registere din the RDBMS.
     * @param password the password of the user.
     * @param databaseName the name of the database.
     * @throws SQLException
     */
    public Connector(String host, String username, String password, String databaseName)
            throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            Logger.severe(null, e);
        }
        String url = driverProtocol + "://" + host + "/" + databaseName;
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Return the current default schema.
     *
     * @return the current selected schema.
     * @throws SQLException
     */
    public String getSchema() throws SQLException {
        return this.schema;
    }

    /**
     * Attempt to change the default schema.
     *
     * @param schemaName Newer schema name
     * @throws java.sql.SQLException
     */
    public void setSchema(String schemaName) throws SQLException {
        // Set the schema, and test if it was really set
        // If not, revert the schema and throw an exception.
        this.executeRaw("SET search_path TO " + schemaName);
        ExecutionResult r = this.executeRaw("SELECT current_schema()");
        r.result.next();
        if (r.result.getObject(1) == null) {
            if (!this.schema.equals("")) {
                // Revert to old schema
                this.executeRaw("SET search_path TO " + this.schema);
            }
            throw new SQLException("Invalid schema");
        } else {
            this.schema = schemaName;
        }
    }

    public void setLibraryMode(boolean mode) {
        this.libraryMode = mode;
    }

    public boolean getLibraryMode() {
        return this.libraryMode;
    }

    // Methods to execute raw queries, that is, without translation
    // These are essentially wrappers for the corresponding Connection methods.
    public class ExecutionResult {

        public ResultSet result;
        public Integer updateCount;

        public ExecutionResult(ResultSet r, Integer u) {
            this.result = r;
            this.updateCount = u;
        }
    }

    /**
     * Executes the given query directly in the RDBMS, without translation. The
     * results can be later retrieved with getResultSet() and getUpdatecount().
     *
     * @param sql the query to execute.
     * @return ExecutionResult instance with the JDBC result set.
     * @throws SQLException
     */
    public ExecutionResult executeRaw(String sql) throws SQLException {
        Logger.logQuery(sql);
        Statement s = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        s.execute(sql);
        restoreState();
        return new ExecutionResult(s.getResultSet(), s.getUpdateCount());
    }

    /**
     * Executes database restore given a type2 ordering execution
     *
     * @throws SQLException
     */
    public void restoreState() throws SQLException {
        if (this.restoreState) {
            Statement s = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String restoreSugenoOrdering = "UPDATE information_schema_fuzzy.current_orderings2 SET ordering = 3;";
            s.execute(restoreSugenoOrdering);
            this.restoreState = false;
        }
    }

    /**
     * Shortcut method equivalent to executeRaw(), but directly returns the
     * ResultSet.
     *
     * @param sql the query to execute.
     * @return ResultSet query result.
     * @throws SQLException
     */
    public ResultSet executeRawQuery(String sql) throws SQLException {
        Logger.logQuery(sql);
        this.resultSet = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
        this.updateCount = -1;
        return this.resultSet;
    }

    /**
     * Shortcut method equivalent to executeRaw(), but directly returns the
     * number of affected rows.
     *
     * @param sql the query to execute.
     * @return the number of updated rows or null if there was a problem during
     * the execution of the query.
     * @throws SQLException
     */
    public Integer executeRawUpdate(String sql) throws SQLException {
        Logger.logQuery(sql);
        this.updateCount = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeUpdate(sql);
        this.resultSet = null;
        return this.updateCount;
    }

    /**
     * Executes an INSERT statement directly in the RDBMS, without translation.
     * Then it returns the generated primary key of the first row added.
     *
     * <p>
     * Note: I inherited this from the previous developer. I don't know why this
     * method would be defined like this. It didn't even have a javadoc when I
     * found it.<p/>
     *
     * @param sql the query to execute.
     * @return the generated key of the first row inserted.
     * @throws SQLException
     */
    public Integer executeRawInsert(String sql) throws SQLException {
        Logger.logQuery(sql);
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("No primary key obtained", "42000", 3019);
        }
    }

    /**
     * Represents the result of a successful fuzzy SQL translation.
     *
     * <p>
     * A translation consists of the translated SQL statement and a list of
     * additional operations that must be executed together with the translated
     * query.</p>
     *
     * <p>
     * An Operation is an object that executes one or more additional SQL
     * statements. It is important that the final executer of the query and
     * operations wrap it all within a transaction to avoid any Operation
     * failing and leaving the fuzzy representation in an incosistent state.</p>
     *
     */
    public class TranslationResult {

        public String sql;
        public List<Operation> operations;

        TranslationResult(String sql, List<Operation> operations) {
            this.sql = sql;
            this.operations = operations;
        }
    }

    /**
     * Translates the given SQL statement.
     *
     * <p>
     * The process might involve querying the fuzzy representation in the RDBMS.
     * It also requires the database to have been preloaded with the fuzzy
     * representation schemas and functions.</p>
     *
     * @param sql the statement to translate.
     * @return a TranslationResult object, which contains everything necessary
     * to execute que translation query and any changes to the fuzzy schemas.
     * @throws SQLException
     */
    public TranslationResult translate(String sql) throws SQLException {

        // PARSER
        CCJSqlParserManager pa = new CCJSqlParserManager();

        net.sf.jsqlparser.statement.Statement s = null;
        try {
            s = pa.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            // Seriously, I don't even know where to begin to clean this mess
            // Thanks previous developer!'

            Throwable c = e.getCause();
            if (c instanceof TokenMgrError) {
                throw new SQLException("JsqlParser.TokenMgrError: " + c.getMessage(), "42000", 3012, c);
            } else if (c instanceof Error) {
                throw new SQLException("JsqlParser.Error: " + c.getMessage(), "42000", 3013, c);
            } else if (c instanceof IOException) {
                throw new SQLException("JsqlParser.IOException: " + c.getMessage(), "42000", 3014, c);
            } else if (c instanceof RuntimeException) {
                Throwable d = c.getCause();
                if (d instanceof UnsupportedEncodingException) {
                    throw new SQLException("JsqlParser.UnsupportedEncodingException: " + c.getMessage(), "42000", 3015, c);
                } else {
                    throw new SQLException("Unknown JsqlParser runtime exception: " + c.getMessage(), "42000", 3016, c);
                }
            } else if (c instanceof ParseException) {
                ParseException d = (ParseException) c;
                int line = d.currentToken.beginLine,
                        column = d.currentToken.beginColumn;
                line = 0 == line ? 0 : line - 1;
                column = 0 == column ? 0 : column - 1;
                String rest = sql.split("\\r?\\n|\\r")[line]
                        .substring(column);
                throw new SQLException(
                        "You have an error in your fuzzy SQL syntax; check the manual that corresponds to your FuzzyDB server version for the right syntax to use near '" + rest + "' at line " + (line + 1) + " (JSP)",
                        "42000",
                        1064, c);
            } else {
                throw new SQLException("Unknown JsqlParser exception: " + c.getMessage(), "42000", 3017, c);
            }
        }

        /*
         * Note for future refactors:
         * StatementType2Translator relies on the fact that StatementTranslator
         * will expand any * in a SELECT into the corresponding columns.
         * It was necessary to do it that way to avoid breaking the code I
         * inherited from the previous developer.
         *
         * A better way would be to refactor that (and perhaps other analysis
         * and general AST decorating) into a preprocessing pass.
         */
        // TRANSLATOR
        List<Operation> operations = new ArrayList<Operation>();
        StatementTranslator st = new StatementTranslator(this, operations);
        try {
            s.accept(st);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Translator exception: " + e.getMessage(),
                    "42000", 3018, e);
        }

        // Fuzzy Type 2 extensions translator
        StatementType2Translator st2 = new StatementType2Translator(this, operations);
        try {
            s.accept(st2);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Type 2 Translator exception: " + e.getMessage(), "42000", 3119, e);
        }

        // Fuzzy Type 5 extensions translator
        StatementType5Translator st5 = new StatementType5Translator(this, operations);
        try {
            s.accept(st5);
        } catch (Exception e) {
            throw new SQLException("Type 5 Translator exception: " + e.getMessage(), "42000", 3119, e);
        }

        // The statement translators turn a flag whenever they encounter a
        // statement whose translation consists entirely of Operations, and
        // that the statement itself must be disregarded because the RDBMS
        // will not understand it.
        // This is the first phase of refactoring, so yeah, this is still
        // pretty terrible. But previously this was a huge ig statement with
        // many instanceof expressions, so this is better at least.
        String res = null;
        if (!st.getIgnoreAST() && !st2.getIgnoreAST() && !st5.getIgnoreAST()) {
            StringBuffer sb = new StringBuffer();
            StatementDeParser sdp = new StatementDeParser(sb);
            try {
                s.accept(sdp);
            } catch (Exception e) {
                throw new SQLException("Deparser exception: " + e.getMessage(),
                        "42000", 3019, e);
            }
            res = sb.toString();
        }

        return new TranslationResult(res, operations);
    }

    /**
     * Translates the fuzzy SQL statement and then executes it. The results can
     * be retrieved with getResultSet() and getUpdateCount().
     *
     * @param sql the fuzzy SQL to be translated and executed.
     * @throws SQLException
     */
    public ExecutionResult execute(String sql) throws SQLException {
        Logger.debug("Executing: " + sql);

        TranslationResult translateResult = translate(sql);

        // Turn on transactions so the translated result and operations
        // all execute as a single atomic operation.
        this.connection.setAutoCommit(false);
        Savepoint sp = this.connection.setSavepoint();
        ExecutionResult result = null;

        try {
            if (null != translateResult.sql) {
                result = executeRaw(translateResult.sql);
            }
            for (Operation o : translateResult.operations) {
                o.execute();
            }
            this.connection.commit();
        } catch (SQLException ex) {
            this.connection.rollback(sp);
            throw ex;
        }

        // Disable transactions so the next translation process can query
        // the database without overhead.
        this.connection.setAutoCommit(true);
        return result;
    }

    /**
     * Returns the result of the last query executed by the Connector.
     *
     * @return ResultSet the result of the last query executed by this
     * Connector. It might be null.
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Returns the number of affected rows during the last query executed by the
     * Connector.
     *
     * @return Integer number of affected rows.
     */
    public Integer getUpdateCount() {
        return updateCount;
    }
}
