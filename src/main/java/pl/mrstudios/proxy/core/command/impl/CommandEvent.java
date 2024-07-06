package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.netty.packet.impl.play.server.ServerGameEventPacket;

import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.core.user.enums.Group.STAFF;

@Command(name = "event")
public class CommandEvent {

    private final UserManager userManager;

    @Inject
    public CommandEvent(
            @NotNull UserManager userManager
    ) {
        this.userManager = userManager;
    }

    @Execute
    public void execute(
            @Context @HasGroup(STAFF) User user,
            @Arg("target") String target,
            @Arg("id") int event,
            @Arg("value") float value
    ) {

        ofNullable(
                this.userManager.user(target)
        ).ifPresent((targetUser) -> targetUser.getConnection().sendPacket(
                new ServerGameEventPacket(event, value)
        ));

    }

}