package pl.mrstudios.proxy.core.language.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.language.Language;

import static java.lang.String.join;
import static java.util.List.of;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.ENGLISH;

public class English extends Language {

    {

        /* General */
        this.prefix = "<blue><dark_gray>[<dark_aqua>MrProxy</dark_aqua>]</dark_gray> »</blue> <reset>";
        this.prefixShort = "<blue><dark_gray>[<dark_aqua>*</dark_aqua>]</dark_gray> »</blue> <reset>";

        /* Words */
        this.wordEnabled = "<green>ENABLED</green>";
        this.wordDisabled = "<red>DISABLED</red>";
        this.wordDisconnected = "DISCONNECTED";

        /* Plural */
        this.pluralOneDay = "day";
        this.pluralTwoDays = "days";
        this.pluralFiveDays = "days";

        this.pluralOneHour = "hour";
        this.pluralTwoHours = "hours";
        this.pluralFiveHours = "hours";

        this.pluralOneMinute = "minute";
        this.pluralTwoMinutes = "minutes";
        this.pluralFiveMinutes = "minutes";

        this.pluralOneSecond = "second";
        this.pluralTwoSeconds = "seconds";
        this.pluralFiveSeconds = "seconds";

        /* Message */
        this.welcomeMessage = join("<br>", of(
                "<reset>",
                "<reset>      <blue><b>┏╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┓</b></blue>",
                "<reset>",
                "<reset>                           <gray>Welcome on <dark_aqua><b>MrProxy</b></dark_aqua>!</gray>",
                "<reset>",
                "<reset>         <gray>Thank you for buying tool, we are appreciative,</gray>",
                "<reset>         <gray>available commands is available under <dark_aqua>,help</dark_aqua>.</gray>%s",
                "<reset>",
                "<reset>      <blue><b>┗╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┛</b></blue>",
                "<reset>"
        ));
        this.welcomeMessageExpireLine = "<br><br><reset>         <gray><red><b>WARNING!</b></red> Your account will expire in <dark_aqua>%s</dark_aqua>.</gray>";

        /* Server Status Resolver */
        this.serverStatusResolverResponse = join("<br>", of(
                "<reset>",
                this.prefixShort + "<gray>Latency: <dark_aqua>%dms</dark_aqua></gray>",
                this.prefixShort + "<gray>Version: <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%d</aqua>)</dark_gray></gray>",
                this.prefixShort + "<gray>Players: <aqua>%d</aqua><dark_gray>/</dark_gray><dark_aqua>%d</dark_aqua></gray>",
                this.prefixShort + "<gray>Description:</gray><br><dark_aqua>%s</dark_aqua>",
                "<reset>"
        ));
        this.serverStatusResolverConnectionFailed = this.prefixShort + "<gray>Unable to resolve server status, server is probably offline.";

        /* Error Messages */
        this.errorNoPermissions = this.prefix + "<gray>You don't have permissions to use this command.";
        this.errorInvalidCommandUsage = this.prefix + "<gray>Correct command usage is <dark_aqua>%s</dark_aqua>.";
        this.errorCommandNotFound = this.prefix + "<gray>Command doesn't exists, use <dark_aqua>,help</dark_aqua> to display available commands.";
        this.errorMustEnterMessage = this.prefix + "<gray>You must enter message to send.";
        this.errorMustWaitBeforeNextUsage = this.prefix + "<gray>You must wait <dark_aqua>%dms</dark_aqua> before next command usage.";
        this.errorMustBeLogged = this.prefix + "<gray>You must be logged to use this command.";
        this.errorProxyListEmpty = this.prefix + "<gray>List of <dark_aqua>%s</dark_aqua> proxies is empty.</gray>";
        this.errorProxyNoAccessToType = this.prefix + "<gray>You don't have access to <dark_aqua>%s</dark_aqua> proxy.</gray>";
        this.errorExceptionOccurredYourConnection = this.prefixShort + "<gray>Exception occurred in <dark_aqua>%s</dark_aqua> server connection.";
        this.errorYouMustBeConnected = this.prefix + "<gray>You must connected to remote server to use this command.";
        this.errorNoConnectedBots = this.prefix + "<gray>You don't have any connected bot.";
        this.errorNotInRange = this.prefix + "<gray>Argument <dark_aqua>%s</dark_aqua> is not in defined range.</gray>";

        /* Format */
        this.proxyJoinMessageFormat = this.prefix + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%s</aqua>] </dark_gray><aqua>%s</aqua> <gray>joined to proxy.";
        this.proxyQuitMessageFormat = this.prefix + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%s</aqua>] </dark_gray><aqua>%s</aqua> <gray>disconnected from proxy.";
        this.proxyChatMessageFormat = this.prefixShort + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> %s%s<reset><dark_gray>:</dark_gray> <white>%s</white>";
        this.proxyRemoteJoinMessageFormat = this.prefixShort + "<gray>Player <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> connected to <dark_aqua>%s</dark_aqua> server.</gray>";
        this.proxyRemoteServerDisconnectMessageFormat = this.prefixShort + "<gray>Player <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> disconnected from <dark_aqua>%s</dark_aqua> server.</gray>";
        this.proxyRemoteDisconnectedMessageFormat = this.prefixShort + "<gray>You have been disconnected from <dark_aqua>%s</dark_aqua> server.</gray><br><dark_aqua>%s</dark_aqua>";
        this.proxyOptionChangedMessageFormat = this.prefix + "<gray>Option <dark_aqua>%s</dark_aqua> has been changed to <dark_aqua>%s</dark_aqua>.</gray>";

        this.remoteLastPacketReceivedNotifyFormat = "<gold><b>*</b></gold> <blue>%s</blue> <dark_gray>(<aqua>%dms</aqua>)</dark_gray> <gold><b>*</b></gold>";
        this.proxyRemoteConnectingBotsMessageFormat = this.prefixShort + "<gray>Player <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> is connecting <dark_aqua>%d bots</dark_aqua> to <dark_aqua>%s</dark_aqua> server.</gray>";
        this.remoteBotChatReceivedMessageFormat = this.prefixShort + "<dark_gray>(<blue>BOT CHAT</blue>) <dark_aqua>%s</dark_aqua>: <aqua>%s</aqua></dark_gray>";
        this.remoteBotInfoBotConnectedMessageFormat = this.prefixShort + "<gray><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> connected to <dark_aqua>%s</dark_aqua> server.</gray>";
        this.remoteBotInfoBotDisconnectedMessageFormat = this.prefixShort + "<gray><hover:show_text:'%s'><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> disconnected from <dark_aqua>%s</dark_aqua> server.</hover></gray>";
        this.remoteBotInfoBotSwitchedServerMessageFormat = this.prefixShort + "<gray><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> is redirected to sub server.</gray>";

        /* Command Messages */

        /* Help */
        this.commandHelpEntry = "<reset> <click:suggest_command:',%s'><hover:show_text:'%s'><dark_aqua>,%s</dark_aqua> <dark_gray>-</dark_gray> <gray>%s</gray></hover></click>";
        this.commandHelpSectionParameters = "<br><br><reset> <gold><b>*</b></gold> <gray>Command Parameters:%s";
        this.commandHelpEntryParameter = "<br><reset>   <white><b>*</b></white> <dark_aqua>%s</dark_aqua> <dark_gray>-</dark_gray> <gray>%s</gray> <reset>";
        this.commandHelpEntryHoverNoDescription = "<reset> <dark_red><b>*</b></dark_red> <red>No description available for this command.</red> <dark_red><b>*</b></dark_red> <reset>";
        this.commandHelpHeader = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Help: <dark_gray>(<aqua>%d</aqua><gray>/</gray><dark_aqua>%d</dark_aqua>)</dark_gray>",
                "<reset>"
        ));

        /* Command Language */
        this.commandLanguageChanged = this.prefix + "<gray>Your language was set to <dark_aqua>%s</dark_aqua>.</gray>";

        /* Chat Clear */
        this.commandChatClearCleared = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Your chat has been cleaned.</gray>",
                "<reset>"
        ));

        /* Connect */
        this.commandConnectConnecting = this.prefix + "<gray>Connecting to <dark_aqua>%s</dark_aqua> server.</gray>";

        this.commandHelpFooter = join("<br>", of(
                "<reset>",
                "<reset>         <dark_gray><click:run_command:',help %d'>(<aqua>Previous</aqua>)</click></dark_gray><reset>                                      <dark_gray><click:run_command:',help %d'>(<aqua>Next</aqua>)</click></dark_gray> <reset>",
                "<reset>"
        ));

        this.commandHelpEntryHoverDescription = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Command Usage: <reset>",
                "<reset>   <white><b>*</b></white> <dark_aqua>,%s</dark_Aqua>%s <reset>",
                "<reset>"
        ));

        /* Command ConnectBot */
        this.commandConnectBotTooManyBots = this.prefix + "<gray>You have too many bots connected to remote server.</gray>";

        /* Command Broadcast */
        this.commandBroadcastMessageSent = this.prefix + "<gray>Message <dark_aqua>%s</dark_aqua> has been successfully sent by bots.</gray>";

        /* Broadcast Infinity */
        this.commandBroadcastInfinityMessageScheduled = this.prefix + "<gray>Message has been scheduled for sending.</gray>";
        this.commandBroadcastInfinityStopped = this.prefix + "<gray>Message sending has been stopped.</gray>";

        /* Add User */
        this.commandAddUserAdded = this.prefix + "<gray>User <dark_aqua>%s</dark_aqua> was added to proxy.";

        /* Detach */
        this.commandDetachDetached = this.prefix + "<gray>You have been detached from remote server.";

        /* Login */
        this.commandLoginSuccess = this.prefix + "<gray>You have been logged successfully.";
        this.commandLoginNotRegistered = this.prefix + "<gray>You are not registered, use <dark_aqua>,register <password> <password></dark_aqua> to register.";

        /* Register */
        this.commandRegisterSuccess = this.prefix + "<gray>You have been registered successfully.";
        this.commandRegisterAlreadyRegistered = this.prefix + "<gray>You are already registered, use <dark_aqua>,login <password></dark_aqua> to login.";
        this.commandRegisterPasswordsNotMatch = this.prefix + "<gray>Provided passwords are not identical.";

        /* Proxies */
        this.commandProxiesListHeader = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Available Proxies:",
                "<reset>"
        ));

        this.commandProxiesListEntry = "<br><reset> <hover:show_text:'%s'><dark_aqua>%s</dark_aqua> <dark_gray>(<gray>Amount: <aqua>%d</aqua>, Average Latency: <aqua>%dms</aqua></gray>)</dark_gray></hover>";
        this.commandProxiesListEntryHoverCountryEntry = "<br><reset>   <white><b>*</b></white> <dark_aqua>%s</dark_Aqua> <dark_gray>(%d proxies)</dark_gray> <reset>";
        this.commandProxiesListEntryHover = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Countries: <reset>%s",
                "<reset>"
        ));

        this.commandProxiesListFooter = join("<br>", of(
                "<reset>",
                this.prefixShort + "<gray>To add own proxies use <dark_aqua>,ownproxy</dark_aqua> command.",
                "<reset>"
        ));

        /* Status */
        this.commandStatusChecking = this.prefix + "<gray>Checking server status, please wait..</gray>";
        this.commandStatusResolvedHost = this.prefixShort + "<gray>Resolved server host <dark_aqua>%s:%d</dark_aqua>, connecting..</gray>";

        /* AutoRegister */
        this.commandAutoRegisterPasswordSet = this.prefix + "<gray>Automatic registration password for <dark_aqua>%s</dark_aqua> has been set to <dark_aqua>%s</dark_aqua>.</gray>";

        /* List */
        this.commandList = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Online Users: <dark_gray>(<aqua>%d</aqua>)</dark_gray></gray>%s",
                "<reset>"
        ));
        this.commandListEntry = "<br><reset> <hover:show_text:'%s'><dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_aqua>%s%s</dark_aqua></hover>";
        this.commandListEntryHover = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Session: <reset>",
                "<reset>   <white><b>*</b></white> <gray>Name: <dark_aqua>%s</dark_aqua></gray> <reset>",
                "<reset>   <white><b>*</b></white> <gray>Server: <dark_aqua>%s</dark_aqua></gray> <reset>",
                "<reset>"
        ));

        /* GameMode */
        this.commandGameModeChanged = this.prefix + "<gray>Your game mode has been changed to <dark_aqua>%s</dark_aqua>.</gray>";

        /* Disconnect */
        this.commandDisconnectDisconnectedBots = this.prefix + "<gray>You disconnected connected bots.</gray>";
        this.commandDisconnectDisconnectedGhost = this.prefix + "<gray>You disconnected ghost sessions.</gray>";

        /* Service */

        /* Reconnect */
        this.autoReconnectService = this.prefixShort + "<gray>You will be automatically reconnected in <dark_aqua>%dms</dark_aqua>.</gray>";

        /* Authorization */
        this.serviceAuthorizationMustLoginMessage = this.prefixShort + "<gray>You must login using <dark_aqua>,login <password></dark_aqua> command.";
        this.serviceAuthorizationMustRegisterMessage = this.prefixShort + "<gray>You must register using <dark_aqua>,register <password> <password></dark_aqua> command.";
        this.serviceAuthorizationTitle = "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>";
        this.serviceAuthorizationSubtitle = "<gray>You have <dark_aqua>%d %s</dark_aqua> for authorization.</gray>";

        /* Proxy */
        this.serviceProxyCheckingNotify = this.prefix + "<gray>Checking proxies, please wait.. <dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%d</aqua>]</dark_gray>";

        /* Connect */
        this.serviceConnectResolvedServerBrand = this.prefixShort + "<gray>Server Engine: <dark_aqua>%s</dark_aqua></gray>";

        /* Lag Detection */
        this.lagDetectionTitle = "<red><b>LAG!</b></red>";
        this.lagDetectionSubtitle = "<gray>Server is not responding for <dark_aqua>%dms</dark_aqua>.</gray>";

        /* TabList */
        this.tabListHeader = join("<br>", of(
                "<reset> ",
                "<reset> <reset> <reset> <blue><b>┏╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┓</b></blue> <reset> <reset> <reset>",
                "<reset>",
                "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                "<gray>Created by <dark_aqua>MrStudios Industries</dark_aqua></gray>",
                "<reset>",
                "<gray>Group: <dark_aqua>%s</dark_aqua></gray>",
                "<gray>Validity: <dark_aqua>%s</dark_aqua></gray>",
                "<reset>",
                "<gray>Connected Bots: <dark_gray><aqua>%d</aqua>/<dark_aqua>%d</dark_aqua></dark_gray></gray>",
                "<reset>"
        ));

        this.tabListFooter = join("<br>", of(
                "<reset>",
                "<gray>Last packet received:</gray>",
                "<dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%dms</aqua>)</dark_gray>",
                "<reset>",
                "<gray>Session: <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray></gray>",
                "<reset>",
                "<reset> <reset> <reset> <blue><b>┗╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┛</b></blue> <reset> <reset> <reset>",
                "<reset>"
        ));

        /* AutoRegister */
        this.serviceAutoRegisterAutomaticallyLoggedMessage = this.prefixShort + "<gray>You have been automatically logged using <dark_aqua>%s</dark_aqua> password.</gray>";
        this.serviceAutoRegisterAutomaticallyRegisteredMessage = this.prefixShort + "<gray>You have been automatically registered using <dark_aqua>%s</dark_aqua> password.</gray>";

    }

    @Override
    public @NotNull LanguageType type() {
        return ENGLISH;
    }

}
