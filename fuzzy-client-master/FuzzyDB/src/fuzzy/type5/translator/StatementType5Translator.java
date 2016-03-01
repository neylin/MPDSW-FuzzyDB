/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.helpers.Error;
import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import fuzzy.common.translator.Translator;
import fuzzy.helpers.Memory;
import fuzzy.type5.operations.CreateFuzzyDomainOperation;
import fuzzy.type5.operations.RemoveFuzzyColumnsOperation;
import java.sql.SQLException;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.fuzzy.constant.CreateFuzzyConstant;
import net.sf.jsqlparser.statement.fuzzy.domain.AlterFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyType2Domain;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.statement.table.CreateTable;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

/**
 *
 * @author hector
 */
public class StatementType5Translator extends Translator implements StatementVisitor {

    public StatementType5Translator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    @Override
    public void visit(Select select) throws Exception {
        SelectTranslator translator = new SelectTranslator(connector, !this.connector.getLibraryMode());
        SelectBody selectBody = select.getSelectBody();
        selectBody.accept(translator);
    }

    @Override
    public void visit(Delete delete) throws Exception {
    }

    @Override
    public void visit(Update update) throws Exception {
        String schemaName = Helper.getSchemaName(connector);
        String tableName = update.getTable().getName();
        Expression expression;
        RowExpression fuzzyExt;

        for (int i = 0; i < update.getColumns().size(); i++) {
            Column column = (Column) update.getColumns().get(i);

            if (Memory.isFuzzyType5Column(connector, schemaName, tableName, column.getColumnName())) {
                expression = (Expression) update.getExpressions().get(i);

                if (expression instanceof RowExpression) {
                    fuzzyExt = (RowExpression) expression;
                    List<Expression> le
                            = fuzzyExt.getExpressions().getExpressions();

                    le.remove(le.size() - 1); // Remove the last boolean
                }
            }

        }
    }

    @Override
    public void visit(Insert insert) throws Exception {
        InsertTranslator insertTranslator = new InsertTranslator(connector, operations);
        insertTranslator.translate(insert);
    }

    @Override
    public void visit(Replace replace) throws Exception {
    }

    @Override
    public void visit(Drop drop) throws Exception {
        String type = drop.getType();
        if ("TABLE".equalsIgnoreCase(type)) {
            String table = drop.getName();
            operations.add(new RemoveFuzzyColumnsOperation(connector, Helper.getSchemaName(connector), table));
        } else if ("FUZZY DOMAIN".equalsIgnoreCase(type)) {
            // LOOK statement translator type 3
            // already handled
        }

    }

    @Override
    public void visit(Truncate truncate) throws Exception {
    }

    @Override
    public void visit(CreateTable createTable) throws Exception {
        CreateTableTranslator translator = new CreateTableTranslator(connector, operations);
        translator.translate(createTable);
    }

    @Override
    public void visit(AlterTable alterTable) throws Exception {
        AlterTableTranslator alterTableTranslator = new AlterTableTranslator(connector, operations);
        alterTableTranslator.translate(alterTable);
        Memory.wipeMemory();
    }

    @Override
    public void visit(CreateFuzzyDomain fuzzyDomain) throws Exception {
        // Instruccion corresponde a tipo 3
    }

    @Override
    public void visit(CreateFuzzyType2Domain fuzzyDomain) throws Exception {
        String type3DomainName = fuzzyDomain.getType().toLowerCase();

        // Instruccion corresponde a tipo 2
        if (Connector.isNativeDataType(type3DomainName)) {
            return;
        }

        String schemaName;

        try {
            schemaName = Helper.getSchemaName(connector);
        } catch (SQLException ex) {
            Logger.debug(StatementType5Translator.class.getName() + ": " + Error.getError("getSchemaT5"));
            throw new SQLException(Error.getError("getSchemaT5"));
        }

        if (schemaName == null || schemaName.equals("")) {
            Logger.debug(StatementType5Translator.class.getName() + ": " + Error.getError("getSchemaT5"));
            throw new SQLException(Error.getError("getSchemaT5"));
        }

        Integer type3DomainId = getFuzzyDomainId(schemaName, type3DomainName, "3");

        // domainName es un tipo Nativo o no esta definido como tipo 3
        if (type3DomainId == null) {
            Logger.debug(StatementType5Translator.class.getName() + ": " + Error.getError("getDomainT3"));
            throw new SQLException(Error.getError("getDomainT3"));
        }

        CreateFuzzyDomainOperation operation
                = new CreateFuzzyDomainOperation(connector,
                        fuzzyDomain.getName(),
                        type3DomainId);

        operations.add(operation);
        this.ignoreAST = true;
    }

    @Override
    public void visit(AlterFuzzyDomain alterFuzzyDomain) throws Exception {
        if (getFuzzyDomainId(connector.getSchema(), alterFuzzyDomain.getName(), "5") != null) {
            throw new SQLException(fuzzy.helpers.Error.getError("operationNotDefinedT5"));
        } else {
            throw new SQLException(fuzzy.helpers.Error.getError("fuzzyNotDefined"));
        }
    }

    @Override
    public void visit(CreateFuzzyConstant createFuzzyConstant) throws Exception {
    }
}
