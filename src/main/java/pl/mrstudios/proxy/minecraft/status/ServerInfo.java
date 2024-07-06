package pl.mrstudios.proxy.minecraft.status;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

@Getter @Setter
@AllArgsConstructor
public class ServerInfo {

    private Object description;
    private String favicon;

    private PlayerInfo players;
    private VersionInfo version;

    private boolean enforcesSecureChat;
    private boolean previewsChat;

    public ServerInfo() {}

    public ServerInfo(Component component, String favicon, PlayerInfo players, VersionInfo version) {
        this.description = gson.fromJson(gson().serialize(component), JsonObject.class);
        this.favicon = favicon;
        this.players = players;
        this.version = version;
        this.enforcesSecureChat = false;
        this.previewsChat = false;
    }

    public ServerInfo(Component component, String favicon, PlayerInfo players, VersionInfo version, boolean enforcesSecureChat, boolean previewsChat) {
        this(component, favicon, players, version);
        this.enforcesSecureChat = enforcesSecureChat;
        this.previewsChat = previewsChat;
    }

    public @NotNull Component getDescription() {
        return gson().deserialize(gson.toJson(this.description));
    }

    public void setDescription(@NotNull Component component) {
        this.description = gson.fromJson(gson().serialize(component), JsonObject.class);
    }

    public @NotNull ServerInfo duplicate() {

        ServerInfo serverInfo = new ServerInfo();

        serverInfo.description = this.description;
        serverInfo.favicon = this.favicon;
        serverInfo.players = this.players;
        serverInfo.version = this.version;

        serverInfo.enforcesSecureChat = this.enforcesSecureChat;
        serverInfo.previewsChat = this.previewsChat;

        return serverInfo;

    }

    protected static final Gson gson = new Gson();

}
