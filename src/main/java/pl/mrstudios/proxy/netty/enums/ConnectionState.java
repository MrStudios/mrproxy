package pl.mrstudios.proxy.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum ConnectionState {

    HANDSHAKE(0),
    STATUS(1),
    LOGIN(2),
    PLAY(3);

    private final int id;

    public static ConnectionState getById(int id) {
        return stream(values())
                .filter((state) -> state.id == id)
                .findFirst()
                .orElse(HANDSHAKE);
    }

}