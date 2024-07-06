package pl.mrstudios.proxy.core.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.config.Configuration;
import pl.mrstudios.proxy.core.mysql.sql.result.SQLEntry;
import pl.mrstudios.proxy.core.mysql.sql.result.SQLResult;
import pl.mrstudios.proxy.core.mysql.sql.statement.SQLStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collection;

public class MySQL {

    /* HikariCP */
    private final HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    public MySQL(Configuration configuration) {

        /* HikariCP */
        this.hikariConfig = new HikariConfig();

        this.hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s/%s", configuration.database.host, configuration.database.name));
        this.hikariConfig.setUsername(configuration.database.user);
        this.hikariConfig.setPassword(configuration.database.password);
        configuration.database.properties.forEach(this.hikariConfig::addDataSourceProperty);

        this.hikariDataSource = new HikariDataSource(this.hikariConfig);

    }

    @SneakyThrows
    @SuppressWarnings("all")
    public @NotNull Collection<SQLResult> fetch(@NotNull SQLStatement statement) {

        Collection<SQLResult> result = new ArrayList<>();

        if (this.hikariDataSource.isClosed())
            this.hikariDataSource = new HikariDataSource(this.hikariConfig);

        try (
                Connection connection = this.hikariDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(statement.query())
        ) {

            statement.prepare(preparedStatement);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    result.add(this.proceed(resultSet));
            }

        }

        return result;

    }

    @SneakyThrows
    @SuppressWarnings("all")
    public void execute(@NotNull SQLStatement statement) {

        try (
                Connection connection = this.hikariDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(statement.query())
        ) {
            statement.prepare(preparedStatement);
            preparedStatement.execute();
        }

    }

    @SneakyThrows
    protected SQLResult proceed(@NotNull ResultSet resultSet) {

        SQLResult result = new SQLResult();
        ResultSetMetaData metaData = resultSet.getMetaData();

        for (int i = 1; i <= metaData.getColumnCount(); i++)
            result.add(new SQLEntry(metaData.getColumnName(i), Class.forName(metaData.getColumnClassName(i)), resultSet.getObject(i)));

        return result;

    }

    public @NotNull SQLStatement make(@NotNull String query) {
        return new SQLStatement(query);
    }

}
