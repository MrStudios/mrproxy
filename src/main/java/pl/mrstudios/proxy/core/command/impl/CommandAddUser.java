package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.account.Account;
import pl.mrstudios.proxy.core.account.AccountManager;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.core.user.enums.Group;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.lang.String.join;
import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.core.account.Account.create;
import static pl.mrstudios.proxy.core.user.enums.Group.STAFF;

@Command(name = "adduser")
public class CommandAddUser {

    private final MiniMessage miniMessage;
    private final UserManager userManager;
    private final AccountManager accountManager;

    @Inject
    public CommandAddUser(
            @NotNull MiniMessage miniMessage,
            @NotNull UserManager userManager,
            @NotNull AccountManager accountManager
    ) {
        this.miniMessage = miniMessage;
        this.userManager = userManager;
        this.accountManager = accountManager;
    }

    @Execute
    public void defaultCommand(
            @Context @HasGroup(STAFF) User sender,
            @Arg("user") String name,
            @Arg("group") Group group,
            @Arg("validity") Duration validity
    ) {

        Optional<User> targetUser = ofNullable(this.userManager.user(name));
        Optional<Account> targetAccount = ofNullable(this.accountManager.fetch(name));

        targetUser.ifPresentOrElse(
                (user) -> {
                    user.getAccount().setGroup(group);
                    user.getAccount().setExpires(user.getAccount().getExpires().plus(validity));
                    user.disconnect(
                            this.miniMessage.deserialize(join("<br>", List.of(
                                    "<reset>",
                                    "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                                    "<dark_gray>Proxy Disconnected</dark_gray>",
                                    "<reset>",
                                    "<dark_aqua>Your account was updated, please rejoin.</dark_aqua>",
                                    "<reset>"
                            )))
                    );
                },
                () -> targetAccount.ifPresentOrElse(
                        (account) -> {
                            account.setGroup(group);
                            account.setExpires(account.getExpires().plus(validity));
                            this.accountManager.save(account);
                        },
                        () -> this.accountManager.create(create(
                                name, group, validity
                        ))
                )
        );

        sender.sendMessage(sender.getLanguage().commandAddUserAdded, name);

    }

}
