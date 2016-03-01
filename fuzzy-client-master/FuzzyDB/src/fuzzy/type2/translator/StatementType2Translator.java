package fuzzy.type2.translator;

import fuzzy.database.Connector;
import fuzzy.common.operations.Operation;
import fuzzy.common.operations.RawSQLOperation;
import fuzzy.common.translator.Translator;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Memory;
import fuzzy.type2.operations.CreateFuzzyType2DomainOperation;
import fuzzy.type2.operations.DropFuzzyType2DomainOperation;
import fuzzy.type2.operations.RemoveFuzzyType2ColumnsOperation;
import fuzzy.type2.operations.CreateFuzzyType2ConstantOperation;
import fuzzy.type2.operations.DropFuzzyType2ConstantOperation;
import fuzzy.type2.operations.ReplaceFuzzyType2ConstantOperation;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.fuzzy.domain.AlterFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.constant.CreateFuzzyConstant;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyType2Domain;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.statement.table.CreateTable;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

public class StatementType2Translator extends Translator implements StatementVisitor {

    public StatementType2Translator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    @Override
    public void visit(CreateTable createTable) throws Exception {
        CreateTableType2Translator translator = new CreateTableType2Translator(connector, operations);
        translator.translate(createTable);
    }

    @Override
    public void visit(AlterTable alterTable) throws Exception {
        AlterTableType2Translator alterTableTranslator = new AlterTableType2Translator(connector, operations);
        alterTableTranslator.translate(alterTable);
        Memory.wipeMemory();
    }

    @Override
    public void visit(Select select) throws Exception {
        SelectType2Translator translator = new SelectType2Translator(connector, !this.connector.getLibraryMode());
        SelectBody selectBody = select.getSelectBody();
        selectBody.accept(translator);
        connector.setRestoreState(true);
    }

    @Override
    public void visit(CreateFuzzyDomain createFuzzyDomain) throws Exception {
        // Nada, el otro translator es encargado de traducir esto.
        Memory.wipeMemory();
    }

    @Override
    public void visit(AlterFuzzyDomain alterFuzzyDomain) throws Exception {
        // Nada, el otro translator es encargado de traducir esto.
        Memory.wipeMemory();
    }

    @Override
    public void visit(CreateFuzzyType2Domain fuzzyDomain) throws Exception {
        String name = fuzzyDomain.getName();
        String type = fuzzyDomain.getType();

        // Instruccion corresponde a una de tipo 5
        if (!Connector.isNativeDataType(type)) {
            return;
        }

        CreateFuzzyType2DomainOperation op = new CreateFuzzyType2DomainOperation(connector, name, type);
        String lower_bound = fuzzyDomain.getLowerBound();
        String upper_bound = fuzzyDomain.getUpperBound();
        op.setBounds(lower_bound, upper_bound);
        operations.add(op);

        // Mark this statement to be ignored by the translation execution.
        // This means this statement, when deparsed, won't make sense for the
        // RDBMS.
        this.ignoreAST = true;
        Memory.wipeMemory();
    }

    @Override
    public void visit(Delete delete) throws Exception {
    }

    @Override
    public void visit(Drop drop) throws Exception {
        String type = drop.getType();
        if ("TABLE".equalsIgnoreCase(type)) {
            String table = drop.getName();
            operations.add(new RemoveFuzzyType2ColumnsOperation(connector, Helper.getSchemaName(connector), table));
        } else if ("FUZZY DOMAIN".equalsIgnoreCase(type)) {
            // Already Handled in DropFuzzyDomainTranslator
        } else if ("FUZZY CONSTANT".equalsIgnoreCase(type)) {
            DropFuzzyType2ConstantOperation op = new DropFuzzyType2ConstantOperation(
                    connector, drop.getParameters().get(0).toString(), drop.getName());
            operations.add(op);
            this.ignoreAST = true;
        }
        Memory.wipeMemory();
    }

    @Override
    public void visit(Insert insert) throws Exception {
        FuzzyType2ExpTranslator translator = new FuzzyType2ExpTranslator(connector);
        ReplaceFuzzyType2ConstantOperation replaceFuzzyType2ConstantOperation
                = new ReplaceFuzzyType2ConstantOperation(
                        connector, insert.getTable(), insert.getColumns(),
                        ((ExpressionList) insert.getItemsList()).getExpressions());
        replaceFuzzyType2ConstantOperation.execute();
        insert.getItemsList().accept(translator);
    }

    @Override
    public void visit(Replace replace) throws Exception {
    }

    @Override
    public void visit(Truncate truncate) throws Exception {
    }

    @Override
    public void visit(Update update) throws Exception {
        FuzzyType2ExpTranslator translator = new FuzzyType2ExpTranslator(connector);
        ReplaceFuzzyType2ConstantOperation replaceFuzzyType2ConstantOperation
                = new ReplaceFuzzyType2ConstantOperation(connector, update.getTable(),
                        update.getColumns(), update.getExpressions());
        replaceFuzzyType2ConstantOperation.execute();
        List<Expression> new_exps = new ArrayList<Expression>();
        for (Expression exp : (List<Expression>) update.getExpressions()) {
            translator.setReplacement(null);
            exp.accept(translator);
            Expression replacement = translator.getReplacement();
            new_exps.add(null != replacement ? replacement : exp);
        }
        update.setExpressions(new_exps);
        if (null != update.getWhere()) {
            translator.setReplacement(null);
            update.getWhere().accept(translator);
            if (null != translator.getReplacement()) {
                update.setWhere(translator.getReplacement());
            }
        }
    }

    @Override
    public void visit(CreateFuzzyConstant createFuzzyConstant) throws Exception {
        /* Checks if the constant was already defined or the domain does not exist. */
        CreateFuzzyType2ConstantOperation op = new CreateFuzzyType2ConstantOperation(connector,
                createFuzzyConstant.getName(), createFuzzyConstant.getDomain(),
                false, null);
        operations.add(op);
        /* Execute. */
        Expression expression = (Expression) ((ExpressionList) createFuzzyConstant.getItemsList()).
                getExpressions().iterator().next();
        String expressionType = expression.getExpressionType();
        FuzzyType2ExpTranslator translator = new FuzzyType2ExpTranslator(connector);
        createFuzzyConstant.getItemsList().accept(translator);
        /* Inserts the name of the schema where the constant is.*/
        op = new CreateFuzzyType2ConstantOperation(connector, createFuzzyConstant.getName(),
                createFuzzyConstant.getDomain(), true, expression);
        operations.add(op);
    }

}
