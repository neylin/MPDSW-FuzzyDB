package fuzzy.common.translator;

import fuzzy.common.operations.Operation;
import fuzzy.common.operations.RawSQLOperation;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import static fuzzy.helpers.Helper.getDomainType;
import fuzzy.helpers.Logger;
import fuzzy.helpers.Memory;
import fuzzy.type2.operations.DropFuzzyType2DomainOperation;
import fuzzy.type2.operations.RemoveFuzzyType2ColumnsOperation;
import fuzzy.type3.operations.DropFuzzyDomainOperation;
import fuzzy.type3.operations.RemoveFuzzyColumnsOperation;
import fuzzy.type5.operations.DropFuzzyType5DomainOperation;
import java.sql.SQLException;
import java.util.List;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import fuzzy.helpers.Error;
/**
 *
 * @author Jose Sanchez
 */
public class DropFuzzyDomainTranslator extends Translator {
    public  DropFuzzyDomainTranslator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }
    
    public void translate(Drop drop) throws Exception {
        
        Integer domainType;
        String domain = drop.getName();
        domainType = getDomainType(connector, domain);
        String schemaName = connector.getSchema();

        if(domainType == null){
            Logger.debug("Type not found");
            throw new SQLException(Error.getError("fuzzyNotDefined"));
        }else if(domainType.equals(2)){
        // TODO remove fuzzy
            Logger.debug("Starting DROP Fuzzy 2");

            /*drop.setType("TYPE IF EXISTS");
            StringBuffer sb = new StringBuffer();
            StatementDeParser sdp = new StatementDeParser(sb);
            try {
                drop.accept(sdp);
            } catch (Exception e) {
                throw new SQLException("Deparser exception: " + e.getMessage(),
                                                              "42000", 3019, e);
            }
            String sql = sb.toString();

            // If is not a type3 domain we add extra operations
            if (null == getFuzzyDomainId(connector.getSchema(), drop.getName(), "3"))
                

            operations.add(new RawSQLOperation(this.connector, sql));*/
            
            operations.add(new DropFuzzyType2DomainOperation(connector, drop.getName()));
        }else if(domainType.equals(3)){
            Logger.debug("Starting DROP Fuzzy 3");
            if(Helper.isDomainLinked(connector, schemaName,domain)){
                throw new SQLException(Error.getError("dropFuzzyDomainLinked"));
            }
            operations.add(new DropFuzzyDomainOperation(connector, drop.getName()));
        }else if(domainType.equals(5)){
            Logger.debug("Starting DROP Fuzzy 5");
            operations.add(new DropFuzzyType5DomainOperation(connector, drop.getName()));
        }
        
    }
        //
}

