package pl.mrstudios.proxy.core.command.impl;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.annotation.Inject;
import pl.mrstudios.proxy.core.command.CommandDescription;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.language.LanguageEntry;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.event.annotations.EventHandler;
import pl.mrstudios.proxy.event.impl.InventoryClickEvent;
import pl.mrstudios.proxy.event.interfaces.Listener;

import static java.util.Optional.ofNullable;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;
import static pl.mrstudios.proxy.minecraft.inventory.builder.InventoryBuilder.inventoryBuilder;
import static pl.mrstudios.proxy.minecraft.inventory.builder.ItemBuilder.itemBuilder;
import static pl.mrstudios.proxy.util.MappingUtil.bookItemIdFor;
import static pl.mrstudios.proxy.util.ProtocolUtil.closeWindowPacket;

@Command(
        name = "language",
        aliases = {
                "lang"
        }
)
@CommandDescription(
        name = "language",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Change language on proxy."),
                @LanguageEntry(key = POLISH, value = "Zmień język na proxy."),
                @LanguageEntry(key = RUSSIAN, value = "Изменить язык на прокси.")
        }
)
public class CommandLanguage implements Listener {

    @Inject
    public CommandLanguage() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        inventoryBuilder()
                .id(-20).type(15).title("<reset>   <dark_aqua><b>ꜱᴇʟᴇᴄᴛ ʏᴏᴜʀ ʟᴀɴɢᴜᴀɢᴇ")
                .item(
                        1, itemBuilder(bookItemIdFor(user.getConnection().getMinecraftVersion()), 1)
                                .name("<reset> <b><gold>*</gold> <dark_aqua>English</dark_aqua> <gold>*</gold></b> <reset>")
                                .itemFlags((byte) 0x80)
                )
                .item(
                        2, itemBuilder(bookItemIdFor(user.getConnection().getMinecraftVersion()), 1)
                                .name("<reset> <b><gold>*</gold> <dark_aqua>Polski</dark_aqua> <gold>*</gold></b> <reset>")
                                .itemFlags((byte) 0x80)
                )
                .item(
                        3, itemBuilder(bookItemIdFor(user.getConnection().getMinecraftVersion()), 1)
                                .name("<reset> <b><gold>*</gold> <dark_aqua>Русский</dark_aqua> <gold>*</gold></b> <reset>")
                                .itemFlags((byte) 0x80)
                )
                .display(user.getConnection());

    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {

        if (event.getWindowId() != -20)
            return;

        ofNullable(
                switch (event.getClickedSlot()) {

                    case 1 -> ENGLISH;
                    case 2 -> POLISH;
                    case 3 -> RUSSIAN;
                    default -> null;

                }
        ).ifPresent((language) -> {
            event.getUser().getAccount().getSettings().language = language;
            event.getUser().sendMessage(event.getUser().getLanguage().commandLanguageChanged, language.name());
        });
        closeWindowPacket(event.getUser().getConnection(), -20);
        event.setCancelled(true);

    }

}
