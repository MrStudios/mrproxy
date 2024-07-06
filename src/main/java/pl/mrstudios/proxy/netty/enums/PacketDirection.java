package pl.mrstudios.proxy.netty.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum PacketDirection {
    CLIENT(), SERVER()
}
