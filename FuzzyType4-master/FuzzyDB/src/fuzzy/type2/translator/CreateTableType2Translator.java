package fuzzy.type2.translator;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.type2.operations.AddFuzzyType2ColumnOperation;
import fuzzy.common.operations.Operation;
import fuzzy.type3.translator.Translator;
import java.sql.SQLException;
import java.util.Iterator;

import java.util.List;
import net.sf.jsqlparser.statement.table.ColumnDefinition;
import net.sf.jsqlparser.statement.table.CreateTable;

/*
        Ver qué columnas son de difusas tipo 2.
        Agregarlas a la tabla de columnas difusas.
        Cambiarle el tipo al tipo difuso que le toca según el dominio.
        Agregarle los CHECKs del dominio difuso, si los tiene
 */
public class CreateTableType2Translator extends Translator {

    public CreateTableType2Translator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    public void translate(CreateTable createTable)
        throws SQLException {
        String schemaName = Helper.getSchemaName(connector, createTable.getTable());
        String tableName = createTable.getTable().getName();
        if (createTable.getColumnDefinitions() != null) {
            for (Iterator iter = createTable.getColumnDefinitions().iterator(); iter.hasNext();) {
                ColumnDefinition columnDefinition = (ColumnDefinition) iter.next();
                String columnName = columnDefinition.getColumnName();
                String dataType = columnDefinition.getColDataType().getDataType();
                Integer domainId = null;
                if ((domainId = getFuzzyType2DomainId(schemaName, dataType)) != null) {
                    // Agregar la columna al catálogo, y encolar consultas para agregar
                    // restricciones de integridad al tipo difuso.
                    // Se deben agregar las restricciones aparte porque JSqlParser
                    // no soporta colocar constraints CHECK a la tabla, así
                    // que no es posible modificar el AST para agregarlo.
                    operations.add(new AddFuzzyType2ColumnOperation(connector, schemaName, tableName, columnName, domainId));
                }
            }
        }
    }

}
