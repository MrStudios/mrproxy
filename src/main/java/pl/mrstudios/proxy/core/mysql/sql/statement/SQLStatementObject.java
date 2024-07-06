package pl.mrstudios.proxy.core.mysql.sql.statement;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;

public record SQLStatementObject(
        @NotNull Integer position,
        @NotNull SQLType type,
        @NotNull Object object
) {}
