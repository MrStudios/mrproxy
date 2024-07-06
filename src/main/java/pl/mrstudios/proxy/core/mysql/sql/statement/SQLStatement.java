package pl.mrstudios.proxy.core.mysql.sql.statement;

import org.jetbrains.annotations.NotNull;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SQLStatement {

    private final String query;
    private final Map<Integer, SQLStatementObject> elements;

    public SQLStatement(@NotNull String query) {
        this.query = query;
        this.elements = new HashMap<>();
    }

    public SQLStatement setString(int position, @NotNull String value) {
        this.elements.put(position, new SQLStatementObject(position, JDBCType.VARCHAR, value));
        return this;
    }

    public SQLStatement setLongString(int position, @NotNull String value) {
        this.elements.put(position, new SQLStatementObject(position, JDBCType.LONGVARCHAR, value));
        return this;
    }

    public SQLStatement setInteger(int position, int value) {
        this.elements.put(position, new SQLStatementObject(position, JDBCType.INTEGER, value));
        return this;
    }

    public SQLStatement setDouble(int position, double value) {
        this.elements.put(position, new SQLStatementObject(position, JDBCType.DOUBLE, value));
        return this;
    }

    public SQLStatement setTimestamp(int position, Timestamp value) {
        this.elements.put(position, new SQLStatementObject(position, JDBCType.TIMESTAMP, value));
        return this;
    }

    public @NotNull String query() {
        return this.query;
    }

    public @NotNull PreparedStatement prepare(@NotNull PreparedStatement preparedStatement) {

        this.elements.forEach((position, object) -> {
            try {
                preparedStatement.setObject(position, object.object(), object.type());
            } catch (Exception exception) {
                throw new RuntimeException("Unable to prepare statement due to exception.", exception);
            }
        });

        return preparedStatement;

    }

}
