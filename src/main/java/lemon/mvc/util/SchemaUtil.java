/**SchemaUtil.java*/
package lemon.mvc.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SchemaUtil：
 *
 * @author howsun(zjh@58.com)
 * @Date 2010-10-22
 * @version v0.1
 */
public class SchemaUtil {

    private static Connection conn;

    static Map DATABASE_INFO = new TreeMap();

    private static String entTypes[] = { "TABLE" };

    private static Logger logger = LoggerFactory.getLogger(SchemaUtil.class);

    static int findDbVendor(DatabaseMetaData dm) throws SQLException {
        String dbName = dm.getDatabaseProductName();

        if ("MySQL".equalsIgnoreCase(dbName)) {
            return DAO.MYSQL;
            // dm.setEscapeChar("`");
        } else if ("Microsoft SQL Server".equalsIgnoreCase(dbName))
            return DAO.MSSQL;
        else if ("Oracle".equalsIgnoreCase(dbName))
            return DAO.ORACLE;
        // else if ("DB2".equalsIgnoreCase(dbName.substring(0, 3)))
        // return (TransactionManager.DB2);
        // else if ("HSQL".equalsIgnoreCase(dbName.substring(0, 4)))
        // return TransactionManager.HSQL;
        // else if ("PostgreSQL".equalsIgnoreCase(dbName))
        // return TransactionManager.POSTGRESQL;

        return 0;
    }

    static Map getColumns(String table) throws SQLException {

        // logger.info("fetching columns for " + table);

        Map result = new TreeMap();
        DatabaseMetaData dmd = conn.getMetaData();

        ResultSet rs = dmd.getColumns(null, null, table.toUpperCase(), null);
        // NPE 4.12.2007

        logger.debug("rs=" + rs);
        if (rs != null && !rs.next())
            throw new SQLException("No columns");

        do {

            logger.trace("col=" + rs.getObject(4));
            String col = rs.getObject(4).toString().toLowerCase();
            String type = getDataType(rs.getInt(5));
            result.put(col, type);

        } while (rs.next());

        return result;

    }

    private static String getDataType(int type) {

        Integer jType = new Integer(type);
        String result = (String) DataTypeMappings.TYPE_MAPPINGS.get(jType);

        if (result == null)
            result = "Object";

        return result;

    }

    static Map getPrimaryKeys(String table) {
        Map result = new TreeMap();
        try {
            // connect();
            DatabaseMetaData dmd = conn.getMetaData();

            ResultSet rs = dmd.getPrimaryKeys(null, null, table);
            while (rs.next()) {
                String col = rs.getString(4);
                result.put(col, "Primary Key");
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }

        logger.debug("Entity Name = " + table + " Primary Key(s) = " + result);

        return result;

    }

    static Map getTables(Connection cn, String schema) throws SQLException {

        logger.debug("Entering cn=" + cn);
        conn = cn;

        Map result = new TreeMap();
        if (cn == null) {
            conn = null;
            // connect();
            cn = conn;
        }
        DatabaseMetaData dmd = cn.getMetaData();
        ResultSet rs = null;
        rs = dmd.getTables(null, schema, null, entTypes);

        // removed schema 3.22.05
        while (rs.next()) {
            // logger.info("getTables :: " + rs.getString(3));
            String tbl = rs.getString(3);
            // result.put(tbl.toLowerCase(), rs.getString(4));// table or view
            result.put(tbl, rs.getString(4));
        }

        // MS SQL Server system tables
        result.remove("syssegments");
        result.remove("sysconstraints");
        return result;
    }

}

class DataTypeMappings {

    public static java.util.Map TYPE_MAPPINGS;

    protected static boolean isNumber(Object obj) {

        return obj instanceof Integer || obj instanceof BigDecimal || obj instanceof Double || obj instanceof Long;

    }

    static {

        Map tm = new TreeMap();
        tm.put(new Integer(Types.BIT), "boolean");

        tm.put(new Integer(Types.BLOB), "java.sql.Blob");
        tm.put(new Integer(Types.CLOB), "java.sql.Clob");

        tm.put(new Integer(Types.DATE), "java.sql.Date");
        tm.put(new Integer(Types.TIME), "java.sql.Time");
        tm.put(new Integer(Types.TIMESTAMP), "java.sql.Timestamp");

        tm.put(new Integer(Types.VARCHAR), "String");
        tm.put(new Integer(Types.CHAR), "String");
        tm.put(new Integer(Types.LONGVARCHAR), "String");

        tm.put(new Integer(Types.INTEGER), "Integer");
        tm.put(new Integer(Types.TINYINT), "Integer");
        tm.put(new Integer(Types.SMALLINT), "Integer");

        tm.put(new Integer(Types.BIGINT), "long");

        tm.put(new Integer(Types.NUMERIC), "java.math.BigDecimal");
        tm.put(new Integer(Types.DECIMAL), "java.math.BigDecimal");

        tm.put(new Integer(Types.REAL), "float");

        tm.put(new Integer(Types.FLOAT), "double");
        tm.put(new Integer(Types.DOUBLE), "double");

        TYPE_MAPPINGS = tm;

    }
}

interface DAO {
    final int MSSQL = 200;
    final int MYSQL = 400;
    final int ORACLE = 100;
}