package fuzzy.type3.translator;

import fuzzy.common.translator.Translator;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.type3.operations.AlterFuzzyDomainOperation;
import fuzzy.type3.operations.CreateFuzzyDomainFromColumnOperation;
import fuzzy.type3.operations.CreateFuzzyDomainOperation;
import fuzzy.common.operations.Operation;
import fuzzy.common.translator.DropFuzzyDomainTranslator;
import fuzzy.type3.operations.CreateFuzzyDomainFromSelectOperation;
import fuzzy.type3.operations.RemoveFuzzyColumnsOperation;
import java.sql.SQLException;
import java.util.List;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Relation;
import net.sf.jsqlparser.expression.Similarity;
import net.sf.jsqlparser.expression.StringValue;
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

public class StatementTranslator extends Translator implements StatementVisitor {

    public StatementTranslator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    //Este el visit de create table, se debe hacer algo parecido con el de select 
    @Override
    public void visit(CreateTable createTable) throws Exception {
        CreateTableTranslator createTableTranslator = new CreateTableTranslator(connector, operations);
        createTableTranslator.translate(createTable);
    }

    @Override
    public void visit(AlterTable alterTable) throws Exception {
        AlterTableTranslator alterTableTranslator = new AlterTableTranslator(connector, operations);
        alterTableTranslator.translate(alterTable);
    }

    //PlainSelect o Select
    @Override
    public void visit(Select select) throws Exception {
        SelectTranslator selectTranslator = new SelectTranslator(connector);
        SelectBody selectBody = select.getSelectBody();
        // This handles both: PlainSelect and Union
        selectBody.accept(selectTranslator);
    }

    @Override
    public void visit(CreateFuzzyDomain createFuzzyDomain) throws Exception {

        // Sintaxis alterna 1:
        if (createFuzzyDomain.isFromColumn()) {

            String domainName = createFuzzyDomain.getName();
            String schemaName = createFuzzyDomain.getFromColumn().getTable().getSchemaName();
            String tableName = createFuzzyDomain.getFromColumn().getTable().getName();
            String columnName = createFuzzyDomain.getFromColumn().getColumnName();

            if (schemaName == null) {
                schemaName = connector.getSchema();
            }

            CreateFuzzyDomainFromColumnOperation o;
            o = new CreateFuzzyDomainFromColumnOperation(connector,
                    domainName,
                    schemaName,
                    tableName,
                    columnName);
            operations.add(o);

            this.ignoreAST = true;

            return;
        }

        // Sintaxis alterna 2:
        if (createFuzzyDomain.isFromSelect()) {
            String domainName = createFuzzyDomain.getName();
            Select selectStatement = createFuzzyDomain.getSelect();

            CreateFuzzyDomainFromSelectOperation o;
            o = new CreateFuzzyDomainFromSelectOperation(connector,
                    domainName,
                    selectStatement);
            operations.add(o);

            this.ignoreAST = true;
            return;
        }

        String name = createFuzzyDomain.getName();
        CreateFuzzyDomainOperation cfdo = new CreateFuzzyDomainOperation(connector, name);

        // get labels
        List<Expression> labels = createFuzzyDomain.getValues().getExpressions();
        for (Expression label : labels) {
            if (label instanceof StringValue) {
                cfdo.addLabel(((StringValue) label).getValue());
            } else {
                // TODO it should rise an SQLException defined in Translator.SOMETHING and present in tests and Github wiki
                throw new UnsupportedOperationException("Only string labels for now: " + label.toString());
            }
        }

        // get similarities
        List<Similarity> similarities = createFuzzyDomain.getSimilarities().getExpressions();
        for (Similarity similarity : similarities) {
            if (!(similarity.getLabel1() instanceof StringValue)) {
                // TODO it should rise an SQLException defined in Translator.SOMETHING and present in tests and Github wiki
                throw new UnsupportedOperationException("Only string labels"
                        + " for now: " + similarity.getLabel1().toString());
            }
            if (!(similarity.getLabel2() instanceof StringValue)) {
                // TODO it should rise an SQLException defined in Translator.SOMETHING and present in tests and Github wiki
                throw new UnsupportedOperationException("Only string labels"
                        + " for now: " + similarity.getLabel2().toString());
            }
            cfdo.addSimilarity(
                    ((StringValue) similarity.getLabel1()).getValue(),
                    ((StringValue) similarity.getLabel2()).getValue(),
                    ((DoubleValue) similarity.getValue()).getValue());
        }

        // calculate changed needed to be made on database
        cfdo.calculate();
        operations.add(cfdo);

        // Mark this statement to be ignored by the translation execution.
        // This means this statement, when deparsed, won't make sense for the
        // RDBMS.
        this.ignoreAST = true;
    }

    @Override
    public void visit(AlterFuzzyDomain alterFuzzyDomain) throws Exception {
        String domainName = alterFuzzyDomain.getName();

        if (Connector.isNativeDataType(domainName)) {
            throw new SQLException(fuzzy.helpers.Error.getError("notFuzzyDomain"));
        }

        if (getFuzzyDomainId(connector.getSchema(), alterFuzzyDomain.getName(), "3") == null) {
            // Dominio no es tipo 3 (Solo tipo3 puede alterarse)
            // Si el dominio no existe se indicara en:
            // StatementType5Translator.visit(AlterFuzzyDomain alterFuzzyDomain)

            return;
        }

        if (Helper.isDomainLinked(connector, connector.getSchema(), domainName)) {
            throw new SQLException(fuzzy.helpers.Error.getError("alterFuzzyDomainLinked"));
        }

        AlterFuzzyDomainOperation afdo = new AlterFuzzyDomainOperation(connector, domainName);

        // add labels
        if (alterFuzzyDomain.getAddValues() != null) {
            List<Expression> addLabels = alterFuzzyDomain.getAddValues()
                    .getExpressions();
            for (Expression label : addLabels) {
                if (label instanceof StringValue) {
                    afdo.addLabel(((StringValue) label).getValue());
                } else {
                    throw new UnsupportedOperationException("Only string labels for now: " + label.toString());
                }
            }
        }

        // add similarities
        if (alterFuzzyDomain.getAddSimilarity() != null) {
            List<Similarity> addSimilarities = alterFuzzyDomain.getAddSimilarity()
                    .getExpressions();
            for (Similarity similarity : addSimilarities) {
                if (!(similarity.getLabel1() instanceof StringValue)) {
                    throw new UnsupportedOperationException("Only string labels"
                            + " for now: " + similarity.getLabel1().toString());
                }
                if (!(similarity.getLabel2() instanceof StringValue)) {
                    throw new UnsupportedOperationException("Only string labels"
                            + " for now: " + similarity.getLabel2().toString());
                }
                afdo.addSimilarity(
                        ((StringValue) similarity.getLabel1()).getValue(),
                        ((StringValue) similarity.getLabel2()).getValue(),
                        ((DoubleValue) similarity.getValue()).getValue());
            }
        }

        // drop labels
        if (alterFuzzyDomain.getDropValues() != null) {
            List<Expression> dropLabels = alterFuzzyDomain.getDropValues()
                    .getExpressions();
            for (Expression label : dropLabels) {
                if (label instanceof StringValue) {
                    afdo.dropLabel(((StringValue) label).getValue());
                } else {
                    throw new UnsupportedOperationException("Only string labels for now: " + label.toString());
                }
            }
        }

        // drop similarities
        if (alterFuzzyDomain.getDropSimilarity() != null) {
            List<Similarity> dropSimilarities = alterFuzzyDomain.getDropSimilarity()
                    .getExpressions();
            for (Relation similarity : dropSimilarities) {
                if (!(similarity.getLabel1() instanceof StringValue)) {
                    throw new UnsupportedOperationException("Only string labels"
                            + " for now: " + similarity.getLabel1().toString());
                }
                if (!(similarity.getLabel2() instanceof StringValue)) {
                    throw new UnsupportedOperationException("Only string labels"
                            + " for now: " + similarity.getLabel2().toString());
                }
                afdo.dropSimilarity(
                        ((StringValue) similarity.getLabel1()).getValue(),
                        ((StringValue) similarity.getLabel2()).getValue());
            }
        }

        // calculate changed needed to be made on database
        afdo.calculate();
        operations.add(afdo);

        // Mark this statement to be ignored by the translation execution.
        // This means this statement, when deparsed, won't make sense for the
        // RDBMS.
        this.ignoreAST = true;
    }

    @Override
    public void visit(Delete delete) throws Exception {
        SelectTranslator selectTranslator = new SelectTranslator(connector);
//		selectTranslator.setBuffer(buffer);
//        ExpressionTranslator expressionTranslator = new ExpressionTranslator(selectTranslator);
//        selectTranslator.setExpressionVisitor(expressionTranslator);
//        DeleteTranslator deleteTranslator = new DeleteTranslator(expressionTranslator);
//        deleteTranslator.translate(delete);
    }

    @Override
    public void visit(Drop drop) throws Exception {
        // TODO Auto-generated method stub
        String type = drop.getType();
        if ("TABLE".equalsIgnoreCase(type)) {
            String table = drop.getName();
            operations.add(new RemoveFuzzyColumnsOperation(connector, Helper.getSchemaName(connector), table));
        } else if ("FUZZY DOMAIN".equalsIgnoreCase(type)) {
            DropFuzzyDomainTranslator t = new DropFuzzyDomainTranslator(connector, operations);
            t.translate(drop);
            this.ignoreAST = true;

        }
    }

    @Override
    public void visit(Insert insert) throws Exception {
        InsertTranslator insertTranslator = new InsertTranslator(connector);
        insertTranslator.translate(insert);
    }

    @Override
    public void visit(Replace replace) throws Exception {
        SelectTranslator selectTranslator = new SelectTranslator(connector);
//		selectTranslator.setBuffer(buffer);
//        ExpressionTranslator expressionTranslator = new ExpressionTranslator(selectTranslator);
//        selectTranslator.setExpressionVisitor(expressionTranslator);
//        ReplaceTranslator replaceTranslator = new ReplaceTranslator(expressionTranslator, selectTranslator);
//        replaceTranslator.translate(replace);
    }

    @Override
    public void visit(Truncate truncate) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(Update update) throws Exception {
//        SelectTranslator selectTranslator = new SelectTranslator(connector);
//		selectTranslator.setBuffer(buffer);
//        ExpressionTranslator expressionTranslator = new ExpressionTranslator(selectTranslator);
//        UpdateTranslator updateTranslator = new UpdateTranslator(expressionTranslator);
//        selectTranslator.setExpressionVisitor(expressionTranslator);
//        updateTranslator.translate(update);

    }

    @Override
    public void visit(CreateFuzzyType2Domain fuzzyDomain) throws Exception {
    }

    @Override
    public void visit(CreateFuzzyConstant createFuzzyConstant) throws Exception {
    }
}
