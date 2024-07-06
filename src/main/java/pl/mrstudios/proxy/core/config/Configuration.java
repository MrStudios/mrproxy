package pl.mrstudios.proxy.core.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Names;
import pl.mrstudios.proxy.core.service.impl.proxy.enums.ProxyType;
import pl.mrstudios.proxy.minecraft.entity.Location;

import java.net.Proxy;
import java.util.List;
import java.util.Map;

import static eu.okaeri.configs.annotation.NameModifier.TO_LOWER_CASE;
import static eu.okaeri.configs.annotation.NameStrategy.HYPHEN_CASE;

@Names(strategy = HYPHEN_CASE, modifier = TO_LOWER_CASE)
public class Configuration extends OkaeriConfig {

    public General general = new General();
    public Server server = new Server();
    public Display display = new Display();
    public Database database = new Database();

    public static class General extends OkaeriConfig {

        public Map<ProxyType, Map<Proxy.Type, List<String>>> proxyGrabberUrls = Map.of(
                ProxyType.HTTP, Map.of(
                        Proxy.Type.HTTP, List.of(
                                "https://api.openproxylist.xyz/http.txt",
                                "https://www.proxy-list.download/api/v1/get?type=http",
                                "https://api.proxyscrape.com/?request=displayproxies&proxytype=http&country=all"
                        )
                ),
                ProxyType.SOCKS, Map.of(
                        Proxy.Type.SOCKS, List.of(
                                "https://api.openproxylist.xyz/socks5.txt",
                                "https://www.proxy-list.download/api/v1/get?type=socks5",
                                "https://api.proxyscrape.com/?request=displayproxies&proxytype=socks5&country=all"
                        )
                )
        );

        public Location crateLocation = new Location(
                0, 62, 19,
                0, 0
        );

    }

    public static class Server extends OkaeriConfig {
        public String host = "0.0.0.0";
        public int port = 25565;
        public boolean behindHaProxy = false;
    }

    public static class Display extends OkaeriConfig {

        public List<String> descriptionLegacy = List.of(
                "<reset>                   <gold>✯ <dark_aqua>MrProxy</dark_aqua> <dark_gray>(1.16+)</dark_gray> <gold>✯",
                "<reset>   <dark_red><b>✖</b></dark_red> <red>Your version is not compatible with proxy. <dark_red><b>✖</b></dark_red>"
        );

        public List<String> descriptionModern = List.of(
                "<reset>                   <#02cccc><b>ᴍʀᴘʀᴏxʏ</b></#02cccc> <dark_gray>(1.16 - 1.20)</dark_gray>",
                "<reset>   <#02aaaa>ᴀᴅᴠᴀɴᴄᴇᴅ ᴛᴏᴏʟ ꜰᴏʀ ᴛᴇꜱᴛɪɴɢ ᴍɪɴᴇᴄʀᴀꜰᴛ ꜱᴇʀᴠᴇʀꜱ.</#02aaaa>"
        );

        public String version = "&8v%s &6★ &8« &3%d &7ONLINE &8»";
        public List<String> hover = List.of(
                "&r",
                "&r   &9&l┏╋━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━╋┓   &r",
                "&r",
                "&r                  &7Welcome on &3&lMrProxy&7!",
                "&r",
                "&r       &3MrProxy &7is advanced tool for testing",
                "&r       &7minecraft servers against &3crashers&7,",
                "&r       &3bots &7and &3exploits&7.",
                "&r",
                "&r   &9&l┗╋━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━╋┛   &r",
                "&r"
        );

    }

    public static class Database extends OkaeriConfig {
        public String host = "localhost:3306";
        public String user = "admin";
        public String password = "ENTER_PASSWORD_HERE";
        public String name = "mrproxy";
        public Map<String, Object> properties = Map.of(
                "maximumPoolSize", 10,
                "minimumIdle", 5,
                "connectionTimeout", 5000,
                "useSSL", false
        );

    }

}
