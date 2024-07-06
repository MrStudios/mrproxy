package pl.mrstudios.proxy.core.account;

public class AccountSqlRepository {

    public static String createTable =
            """
            CREATE TABLE IF NOT EXISTS `users` (
                `id` INT NOT NULL AUTO_INCREMENT,
                `name` VARCHAR(16) NOT NULL,
                `group` ENUM('STAFF','MODERATOR','SUPPORT','FRIEND','USER') NOT NULL,
                `expires` TIMESTAMP NOT NULL,
                `data` TEXT NOT NULL,
                `settings` TEXT NOT NULL,
                PRIMARY KEY (`id`)
            );
            """;

    public static String selectAccount =
            """
            SELECT * FROM `users` WHERE `name` = ?;
            """;

    public static String insertAccount =
            """
            INSERT INTO `users` (`name`, `group`, `expires`, `data`, `settings`) VALUES (?, ?, ?, ?, ?);
            """;

    public static String updateAccount =
            """
            UPDATE `users` SET `group` = ?, `expires` = ?, `data` = ?, `settings` = ? WHERE `name` = ?;
            """;

}
