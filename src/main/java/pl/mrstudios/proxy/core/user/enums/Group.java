package pl.mrstudios.proxy.core.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Group {

    STAFF("<dark_gray>[<#ff0000><b>STAFF</b></#ff0000>]</dark_gray> <#ff0000>", 5, 1000),
    MODERATOR("<dark_gray>[<#195080><b>MODERATOR</b></#195080>]</dark_gray> <#195080>", 4, 500),
    SUPPORT("<dark_gray>[<#277ecd><b>SUPPORT</b></#277ecd>]</dark_gray> <#277ecd>", 3, 500),
    FRIEND("<dark_gray>[<#985db3>FRIEND</#985db3>]</dark_gray> <#985db3>", 2, 250),
    USER("<#02cccc>", 1, 200),
    NONE("", 0, 0);

    private final String prefix;
    private final int permissionLevel;
    private final int maxBots;

}
