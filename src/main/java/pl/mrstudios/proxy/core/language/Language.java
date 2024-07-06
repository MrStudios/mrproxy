package pl.mrstudios.proxy.core.language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.language.impl.English;
import pl.mrstudios.proxy.core.language.impl.Polish;
import pl.mrstudios.proxy.core.language.impl.Russian;

public abstract class Language {

    /* General */
    public String prefix;
    public String prefixShort;

    /* Words */
    public String wordEnabled;
    public String wordDisabled;
    public String wordDisconnected;

    /* Plural */
    public String pluralOneDay;
    public String pluralTwoDays;
    public String pluralFiveDays;

    public String pluralOneHour;
    public String pluralTwoHours;
    public String pluralFiveHours;

    public String pluralOneMinute;
    public String pluralTwoMinutes;
    public String pluralFiveMinutes;

    public String pluralOneSecond;
    public String pluralTwoSeconds;
    public String pluralFiveSeconds;

    /* Elements */
    public String welcomeMessage;
    public String welcomeMessageExpireLine;

    /* Server Status Resolver */
    public String serverStatusResolverResponse;
    public String serverStatusResolverConnectionFailed;

    /* Error */
    public String errorNoPermissions;
    public String errorInvalidCommandUsage;
    public String errorCommandNotFound;
    public String errorMustEnterMessage;
    public String errorMustWaitBeforeNextUsage;
    public String errorMustBeLogged;
    public String errorProxyListEmpty;
    public String errorProxyNoAccessToType;
    public String errorExceptionOccurredYourConnection;
    public String errorYouMustBeConnected;
    public String errorNoConnectedBots;
    public String errorNotInRange;

    /* Format */
    public String proxyJoinMessageFormat;
    public String proxyQuitMessageFormat;
    public String proxyChatMessageFormat;
    public String proxyRemoteJoinMessageFormat;
    public String proxyRemoteServerDisconnectMessageFormat;
    public String proxyRemoteDisconnectedMessageFormat;
    public String proxyOptionChangedMessageFormat;
    public String proxyRemoteConnectingBotsMessageFormat;

    public String remoteLastPacketReceivedNotifyFormat;
    public String remoteBotChatReceivedMessageFormat;
    public String remoteBotInfoBotConnectedMessageFormat;
    public String remoteBotInfoBotDisconnectedMessageFormat;
    public String remoteBotInfoBotSwitchedServerMessageFormat;


    /* Command Messages */
    public String commandHelpEntry;
    public String commandHelpHeader;
    public String commandHelpFooter;
    public String commandHelpEntryParameter;
    public String commandHelpSectionParameters;
    public String commandHelpEntryHoverDescription;
    public String commandHelpEntryHoverNoDescription;

    /* Language Changed */
    public String commandLanguageChanged;

    /* Chat Clear */
    public String commandChatClearCleared;

    /* Connect */
    public String commandConnectConnecting;

    /* Disconnect */
    public String commandDisconnectDisconnectedBots;
    public String commandDisconnectDisconnectedGhost;

    /* Detach */
    public String commandDetachDetached;

    /* Add User */
    public String commandAddUserAdded;

    /* Authorization */
    public String commandLoginSuccess;
    public String commandLoginNotRegistered;

    public String commandRegisterSuccess;
    public String commandRegisterAlreadyRegistered;
    public String commandRegisterPasswordsNotMatch;

    /* Proxies */
    public String commandProxiesListHeader;
    public String commandProxiesListEntry;
    public String commandProxiesListFooter;
    public String commandProxiesListEntryHover;
    public String commandProxiesListEntryHoverCountryEntry;

    /* AutoRegister */
    public String commandAutoRegisterPasswordSet;

    /* Status */
    public String commandStatusChecking;
    public String commandStatusResolvedHost;

    /* ConnectBot */
    public String commandConnectBotTooManyBots;

    /* Broadcast */
    public String commandBroadcastMessageSent;

    /* Broadcast Infinity */
    public String commandBroadcastInfinityMessageScheduled;
    public String commandBroadcastInfinityStopped;

    /* List */
    public String commandList;
    public String commandListEntry;
    public String commandListEntryHover;

    /* GameMode */
    public String commandGameModeChanged;

    /* Service */

    /* Reconnect */
    public String autoReconnectService;

    /* Authorization */
    public String serviceAuthorizationMustLoginMessage;
    public String serviceAuthorizationMustRegisterMessage;
    public String serviceAuthorizationTitle;
    public String serviceAuthorizationSubtitle;

    /* Proxy */
    public String serviceProxyCheckingNotify;

    /* TabList */
    public String tabListHeader;
    public String tabListFooter;

    /* Lag Detection */
    public String lagDetectionTitle;
    public String lagDetectionSubtitle;

    /* AutoRegister */
    public String serviceAutoRegisterAutomaticallyLoggedMessage;
    public String serviceAutoRegisterAutomaticallyRegisteredMessage;

    /* Connect */
    public String serviceConnectResolvedServerBrand;

    public abstract @NotNull LanguageType type();

    @Getter
    @AllArgsConstructor
    public enum LanguageType {

        ENGLISH(new English()),
        POLISH(new Polish()),
        RUSSIAN(new Russian());

        private final Language language;

    }

}
