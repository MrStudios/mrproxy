package pl.mrstudios.proxy.core.service.impl.proxy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.mrstudios.proxy.core.user.enums.Group;

import static pl.mrstudios.proxy.core.user.enums.Group.STAFF;
import static pl.mrstudios.proxy.core.user.enums.Group.USER;

@Getter
@AllArgsConstructor
public enum ProxyType {

    HTTP(USER, true),
    SOCKS(USER, true),
    OWN(USER, false),
    DEDICATED(USER, true),
    RESIDENTIAL(USER, true),
    NONE(STAFF, false);

    private final Group group;
    private final boolean visible;

}
