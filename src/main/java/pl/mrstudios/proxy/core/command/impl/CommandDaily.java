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
import pl.mrstudios.proxy.minecraft.inventory.builder.InventoryBuilder;

import static java.util.Arrays.stream;
import static pl.mrstudios.proxy.core.Constants.DEFAULT_GROUP_DELAY;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.*;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;
import static pl.mrstudios.proxy.minecraft.inventory.builder.InventoryBuilder.inventoryBuilder;
import static pl.mrstudios.proxy.minecraft.inventory.builder.ItemBuilder.itemBuilder;
import static pl.mrstudios.proxy.util.MappingUtil.*;

@Command(
        name = "daily",
        aliases = {
                "rewards",
                "dailyrewards"
        }
)
@CommandDescription(
        name = "daily",
        description = {
                @LanguageEntry(key = ENGLISH, value = "Claim daily rewards."),
                @LanguageEntry(key = POLISH, value = "Odbierz dzienne nagrody."),
                @LanguageEntry(key = RUSSIAN, value = "Получить ежедневные награды.")
        }
)
public class CommandDaily implements Listener {

    @Inject
    public CommandDaily() {}

    @Execute
    public void execute(
            @Context @HasGroup(USER) @Cooldown(DEFAULT_GROUP_DELAY) User user
    ) {

        InventoryBuilder inventoryBuilder = inventoryBuilder()
                .id(-22).type(5).title("<reset>          <dark_aqua><b>ᴅᴀɪʟʏ ʀᴇᴡᴀʀᴅꜱ")
                .fill(
                        itemBuilder(blackGlassItemIdFor(user.getConnection().getMinecraftVersion()), 1)
                                .name("<reset>").itemFlags((byte) 0x80)
                );

        for (int i = 0; i < DAILY_REWARD_SLOTS.length; i++)
            inventoryBuilder.item(
                    DAILY_REWARD_SLOTS[i], itemBuilder(mineCartTNTItemIdFor(user.getConnection().getMinecraftVersion()), i + 1)
                            .name("<reset>")
                            .lore(
                                    "<reset>           <dark_red><b>*</b></dark_red> <red>Day #" + (i + 1) + "</red> <dark_red><b>*</b></dark_red>   <reset>",
                                    "<reset>",
                                    "<reset> <gold>»</gold> <gray>Coins: <dark_aqua>" + ((int) ((i + 1) * ((i > 14) ? 4.75 : 2.5))) + " ⛃</dark_aqua></gray> <reset>",
                                    "<reset> <gold>»</gold> <gray>Crates: <dark_aqua>" + (((i / 5) > 0) ? (i / 5) : 1) + "</dark_aqua></gray> <reset>",
                                    "<reset>",
                                    "<reset> <red>You can't claim this reward.</red> <reset>",
                                    "<reset>"
                            )
                            .itemFlags((byte) 0x80)
            );

        inventoryBuilder.item(
                DAILY_REWARD_SLOTS[0], itemBuilder(mineCartChestItemIdFor(user.getConnection().getMinecraftVersion()), 1)
                        .name("<reset>")
                        .lore(
                                "<reset>       <dark_green><b>*</b></dark_green> <green>Day #1</green> <dark_green><b>*</b></dark_green>   <reset>",
                                "<reset>",
                                "<reset> <gold>»</gold> <gray>Coins: <dark_aqua>3 ⛃</dark_aqua></gray> <reset>",
                                "<reset> <gold>»</gold> <gray>Crates: <dark_aqua>1</dark_aqua></gray> <reset>",
                                "<reset>",
                                "<reset> <green>Click to claim reward.</green> <reset>",
                                "<reset>"
                        )
                        .itemFlags((byte) 0x80)
        );

        inventoryBuilder.display(user.getConnection());

    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {

        if (event.getWindowId() != -22)
            return;

        if (stream(DAILY_REWARD_SLOTS).anyMatch((i) -> i == event.getClickedSlot()))
            event.getUser().sendMessage(event.getUser().getLanguage().prefix + "<gray>You are not permitted to do that action, please try again later.");

        event.setCancelled(true);

    }

    protected static final int[] DAILY_REWARD_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

}
