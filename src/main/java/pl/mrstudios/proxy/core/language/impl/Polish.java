package pl.mrstudios.proxy.core.language.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.language.Language;

import static java.lang.String.join;
import static java.util.List.of;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.POLISH;

public class Polish extends Language {

    {

        /* General */
        this.prefix = "<blue><dark_gray>[<dark_aqua>MrProxy</dark_aqua>]</dark_gray> »</blue> <reset>";
        this.prefixShort = "<blue><dark_gray>[<dark_aqua>*</dark_aqua>]</dark_gray> »</blue> <reset>";

        /* Words */
        this.wordEnabled = "<green>WŁĄCZONE</green>";
        this.wordDisabled = "<red>WYŁĄCZONE</red>";
        this.wordDisconnected = "ROZŁĄCZONO";

        /* Plural */
        this.pluralOneDay = "dzień";
        this.pluralTwoDays = "dni";
        this.pluralFiveDays = "dni";

        this.pluralOneHour = "godzina";
        this.pluralTwoHours = "godziny";
        this.pluralFiveHours = "godzin";

        this.pluralOneMinute = "minuta";
        this.pluralTwoMinutes = "minuty";
        this.pluralFiveMinutes = "minut";

        this.pluralOneSecond = "sekunda";
        this.pluralTwoSeconds = "sekundy";
        this.pluralFiveSeconds = "sekund";

        /* Message */
        this.welcomeMessage = join("<br>", of(
                "<reset>",
                "<reset>      <blue><b>┏╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┓</b></blue>",
                "<reset>",
                "<reset>                             <gray>Witaj na <dark_aqua><b>MrProxy</b></dark_aqua>!</gray>",
                "<reset>",
                "<reset>         <gray>Dziękujemy za zakup narzędzia, jesteśmy bardzo</gray>",
                "<reset>         <gray>wdzięczni za twoje wsparcie, przydatne komendy</gray>",
                "<reset>         <gray>znajdziesz używając <dark_aqua>,help</dark_aqua>.</gray>%s",
                "<reset>",
                "<reset>      <blue><b>┗╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┛</b></blue>",
                "<reset>"
        ));
        this.welcomeMessageExpireLine = "<br><br><reset>         <gray><red><b>UWAGA!</b></red> Twój dostęp wygaśnie za <dark_aqua>%s</dark_aqua>.</gray>";

        /* Server Status Resolver */
        this.serverStatusResolverResponse = join("<br>", of(
                "<reset>",
                this.prefixShort + "<gray>Opóźnienie: <dark_aqua>%dms</dark_aqua></gray>",
                this.prefixShort + "<gray>Wersja: <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%d</aqua>)</dark_gray></gray>",
                this.prefixShort + "<gray>Graczy: <aqua>%d</aqua><dark_gray>/</dark_gray><dark_aqua>%d</dark_aqua></gray>",
                this.prefixShort + "<gray>Opis:</gray><br><dark_aqua>%s</dark_aqua>",
                "<reset>"
        ));
        this.serverStatusResolverConnectionFailed = this.prefixShort + "<gray>Nie można pobrać statusu serwera, prawdopodobnie jest niedostępny.";

        /* Error Messages */
        this.errorNoPermissions = this.prefix + "<gray>Nie posiadasz uprawnień do tej komendy..";
        this.errorInvalidCommandUsage = this.prefix + "<gray>Poprawne użycie komendy <dark_aqua>%s</dark_aqua>.";
        this.errorCommandNotFound = this.prefix + "<gray>Komenda nie została znaleziona, użyj <dark_aqua>,help</dark_aqua> aby wyświetlić dostępne komendy.";
        this.errorMustEnterMessage = this.prefix + "<gray>Musisz podać wiadomość którą chcesz wysłać.";
        this.errorMustWaitBeforeNextUsage = this.prefix + "<gray>Musisz odczekać <dark_aqua>%dms</dark_aqua> przed następnym użyciem.";
        this.errorMustBeLogged = this.prefix + "<gray>Musisz być zalogowany aby to zrobić.";
        this.errorProxyListEmpty = this.prefix + "<gray>Lista <dark_aqua>%s</dark_aqua> jest pusta.</gray>";
        this.errorProxyNoAccessToType = this.prefix + "<gray>Nie posiadasz dostępu do tej listy proxy.</gray>";
        this.errorExceptionOccurredYourConnection = this.prefixShort + "<gray>Wystąpił problem w połączeniu z serwerem <dark_aqua>%s</dark_aqua>.";
        this.errorYouMustBeConnected = this.prefix + "<gray>Musisz być połączony z serwerem aby użyć tej komendy.";
        this.errorNoConnectedBots = this.prefix + "<gray>Nie posiadasz połączonych botów.";
        this.errorNotInRange = this.prefix + "<gray>Parametr <dark_aqua>%s</dark_aqua> nie znajduje się w ustalonym przedziale.</gray>";

        /* Format */
        this.proxyJoinMessageFormat = this.prefix + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%s</aqua>] </dark_gray><aqua>%s</aqua> <gray>dołączył do proxy.";
        this.proxyQuitMessageFormat = this.prefix + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%s</aqua>] </dark_gray><aqua>%s</aqua> <gray>wyszedł z proxy.";
        this.proxyChatMessageFormat = this.prefixShort + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> %s%s<reset><dark_gray>:</dark_gray> <white>%s</white>";
        this.proxyRemoteJoinMessageFormat = this.prefixShort + "<gray>Gracz <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> połączył się z serwerem <dark_aqua>%s</dark_aqua>.</gray>";
        this.proxyRemoteServerDisconnectMessageFormat = this.prefixShort + "<gray>Gracz <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> odłączył się od serwera <dark_aqua>%s</dark_aqua>.</gray>";
        this.proxyRemoteDisconnectedMessageFormat = this.prefixShort + "<gray>Zostałeś odłączony z serwera <dark_aqua>%s</dark_aqua>.</gray><br><dark_aqua>%s</dark_aqua>";
        this.proxyOptionChangedMessageFormat = this.prefix + "<gray>Ustawienie <dark_aqua>%s</dark_aqua> zostało ustawione na <dark_aqua>%s</dark_aqua>.</gray>";

        this.remoteLastPacketReceivedNotifyFormat = "<gold><b>*</b></gold> <blue>%s</blue> <dark_gray>(<aqua>%dms</aqua>)</dark_gray> <gold><b>*</b></gold>";
        this.proxyRemoteConnectingBotsMessageFormat = this.prefixShort + "<gray>Gracz <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> łączy <dark_aqua>%d botów</dark_aqua> do serwera <dark_aqua>%s</dark_aqua>.</gray>";
        this.remoteBotChatReceivedMessageFormat = this.prefixShort + "<dark_gray>(<blue>BOT CHAT</blue>) <dark_aqua>%s</dark_aqua>: <aqua>%s</aqua></dark_gray>";
        this.remoteBotInfoBotConnectedMessageFormat = this.prefixShort + "<gray><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> połączył się z serwerem <dark_aqua>%s</dark_aqua>.</gray>";
        this.remoteBotInfoBotDisconnectedMessageFormat = this.prefixShort + "<gray><hover:show_text:'%s'><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> odłączył się z serwera <dark_aqua>%s</dark_aqua>.</hover></gray>";
        this.remoteBotInfoBotSwitchedServerMessageFormat = this.prefixShort + "<gray><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> został przekierowany na podserwer.</gray>";

        /* Command Messages */

        /* Help */
        this.commandHelpEntry = "<reset> <click:suggest_command:',%s'><hover:show_text:'%s'><dark_aqua>,%s</dark_aqua> <dark_gray>-</dark_gray> <gray>%s</gray></hover></click>";
        this.commandHelpSectionParameters = "<br><br><reset> <gold><b>*</b></gold> <gray>Parametry:%s";
        this.commandHelpEntryParameter = "<br><reset>   <white><b>*</b></white> <dark_aqua>%s</dark_aqua> <dark_gray>-</dark_gray> <gray>%s</gray> <reset>";
        this.commandHelpEntryHoverNoDescription = "<reset> <dark_red><b>*</b></dark_red> <red>Brak dostępnego opisu dla tej komendy.</red> <dark_red><b>*</b></dark_red> <reset>";
        this.commandHelpHeader = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Pomoc: <dark_gray>(<aqua>%d</aqua><gray>/</gray><dark_aqua>%d</dark_aqua>)</dark_gray>",
                "<reset>"
        ));

        /* Command Language */
        this.commandLanguageChanged = this.prefix + "<gray>Twój język został ustawiony na <dark_aqua>%s</dark_aqua>.</gray>";

        /* Chat Clear */
        this.commandChatClearCleared = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Twój czat został wyczyszczony.</gray>",
                "<reset>"
        ));

        /* Connect */
        this.commandConnectConnecting = this.prefix + "<gray>Łączenie z serwerem <dark_aqua>%s</dark_aqua>.</gray>";

        this.commandHelpFooter = join("<br>", of(
                "<reset>",
                "<reset>         <dark_gray><click:run_command:',help %d'>(<aqua>Wstecz</aqua>)</click></dark_gray><reset>                                       <dark_gray><click:run_command:',help %d'>(<aqua>Dalej</aqua>)</click></dark_gray> <reset>",
                "<reset>"
        ));

        this.commandHelpEntryHoverDescription = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Użycie: <reset>",
                "<reset>   <white><b>*</b></white> <dark_aqua>,%s</dark_Aqua>%s <reset>",
                "<reset>"
        ));

        /* Command ConnectBot */
        this.commandConnectBotTooManyBots = this.prefix + "<gray>Posiadasz zbyt dużo połączonych botów.</gray>";

        /* Command Broadcast */
        this.commandBroadcastMessageSent = this.prefix + "<gray>Wiadomość <dark_aqua>%s</dark_aqua> została wysłana przez boty.</gray>";

        /* Broadcast Infinity */
        this.commandBroadcastInfinityMessageScheduled = this.prefix + "<gray>Wiadomość została ustawiona do wysyłania przez boty.</gray>";
        this.commandBroadcastInfinityStopped = this.prefix + "<gray>Wysyłanie wiadomości przez boty zostało zatrzymane.</gray>";

        /* Add User */
        this.commandAddUserAdded = this.prefix + "<gray>Gracz <dark_aqua>%s</dark_aqua> został dodany do proxy.";

        /* Login */
        this.commandLoginSuccess = this.prefix + "<gray>Zostałeś pomyślnie zalogowany.";
        this.commandLoginNotRegistered = this.prefix + "<gray>Nie jesteś zarejestrowany, użyj <dark_aqua>,register <hasło> <hasło></dark_aqua> aby się zarejestrować.";

        /* Register */
        this.commandRegisterSuccess = this.prefix + "<gray>Zostałeś zarejestrowany prawidłowo..";
        this.commandRegisterAlreadyRegistered = this.prefix + "<gray>Jesteś już zalogowany, użyj <dark_aqua>,login <hasło></dark_aqua> aby się zalogować.";
        this.commandRegisterPasswordsNotMatch = this.prefix + "<gray>Podane hasła nie są identyczne.";

        /* Proxies */
        this.commandProxiesListHeader = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Dostępne adresy proxy:",
                "<reset>"
        ));

        this.commandProxiesListEntry = "<br><reset> <hover:show_text:'%s'><dark_aqua>%s</dark_aqua> <dark_gray>(<gray>Ilość: <aqua>%d</aqua>, Średnie Opóźnienie: <aqua>%dms</aqua></gray>)</dark_gray></hover>";
        this.commandProxiesListEntryHoverCountryEntry = "<br><reset>   <white><b>*</b></white> <dark_aqua>%s</dark_Aqua> <dark_gray>(%d adresów)</dark_gray> <reset>";
        this.commandProxiesListEntryHover = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Kraje: <reset>%s",
                "<reset>"
        ));

        this.commandProxiesListFooter = join("<br>", of(
                "<reset>",
                this.prefixShort + "<gray>Aby dodać własne adresy użyj komendy <dark_aqua>,ownproxy</dark_aqua>.",
                "<reset>"
        ));

        /* Status */
        this.commandStatusChecking = this.prefix + "<gray>Sprawdzanie statusu serwera, prosze czekać..</gray>";
        this.commandStatusResolvedHost = this.prefixShort + "<gray>Ustalono adres serwera <dark_aqua>%s:%d</dark_aqua>, łączenie..</gray>";

        /* AutoRegister */
        this.commandAutoRegisterPasswordSet = this.prefix + "<gray>Hasło automatycznej rejestracji dla <dark_aqua>%s</dark_aqua> zostało ustawione na <dark_aqua>%s</dark_aqua>.</gray>";

        /* List */
        this.commandList = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Użytkownicy: <dark_gray>(<aqua>%d</aqua>)</dark_gray></gray>%s",
                "<reset>"
        ));
        this.commandListEntry = "<br><reset> <hover:show_text:'%s'><dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_aqua>%s%s</dark_aqua></hover>";
        this.commandListEntryHover = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Sesja: <reset>",
                "<reset>   <white><b>*</b></white> <gray>Nick: <dark_aqua>%s</dark_aqua></gray> <reset>",
                "<reset>   <white><b>*</b></white> <gray>Serwer: <dark_aqua>%s</dark_aqua></gray> <reset>",
                "<reset>"
        ));

        /* GameMode */
        this.commandGameModeChanged = this.prefix + "<gray>Twój tryb gry został ustawiony na <dark_aqua>%s</dark_aqua>.</gray>";

        /* Detach */
        this.commandDetachDetached = this.prefix + "<gray>Zostawiono sesję na serwerze zdalnym.</gray>";

        /* Disconnect */
        this.commandDisconnectDisconnectedBots = this.prefix + "<gray>Rozłączyłeś połączone boty.</gray>";
        this.commandDisconnectDisconnectedGhost = this.prefix + "<gray>Rozłączyłeś połączone sesje.</gray>";

        /* Service */

        /* Reconnect */
        this.autoReconnectService = this.prefixShort + "<gray>Ponowne połączenie z serwerem nastąpi za <dark_aqua>%dms</dark_aqua>.</gray>";

        /* Authorization */
        this.serviceAuthorizationMustLoginMessage = this.prefixShort + "<gray>Musisz się zalogować używając <dark_aqua>,login <hasło></dark_aqua>.";
        this.serviceAuthorizationMustRegisterMessage = this.prefixShort + "<gray>Musisz się zarejestrować używając <dark_aqua>,register <hasło> <hasło></dark_aqua>.";
        this.serviceAuthorizationTitle = "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>";
        this.serviceAuthorizationSubtitle = "<gray>Zostało tobie <dark_aqua>%d %s</dark_aqua> na autoryzacje.</gray>";

        /* Proxy */
        this.serviceProxyCheckingNotify = this.prefix + "<gray>Sprawdzanie adresów proxy, prosze czekać.. <dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%d</aqua>]</dark_gray>";

        /* Connect */
        this.serviceConnectResolvedServerBrand = this.prefixShort + "<gray>Silnik Serwera: <dark_aqua>%s</dark_aqua></gray>";

        /* Lag Detection */
        this.lagDetectionTitle = "<red><b>LAG!</b></red>";
        this.lagDetectionSubtitle = "<gray>Serwer nie odpowiada od <dark_aqua>%dms</dark_aqua>.</gray>";

        /* TabList */
        this.tabListHeader = join("<br>", of(
                "<reset> ",
                "<reset> <reset> <reset> <blue><b>┏╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┓</b></blue> <reset> <reset> <reset>",
                "<reset>",
                "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                "<gray>Stworzone przez <dark_aqua>MrStudios Industries</dark_aqua></gray>",
                "<reset>",
                "<gray>Ranga: <dark_aqua>%s</dark_aqua></gray>",
                "<gray>Wygasa: <dark_aqua>%s</dark_aqua></gray>",
                "<reset>",
                "<gray>Połączone Boty: <dark_gray><aqua>%d</aqua>/<dark_aqua>%d</dark_aqua></dark_gray></gray>",
                "<reset>"
        ));

        this.tabListFooter = join("<br>", of(
                "<reset>",
                "<gray>Ostatni otrzymany pakiet:</gray>",
                "<dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%dms</aqua>)</dark_gray>",
                "<reset>",
                "<gray>Sesja: <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray></gray>",
                "<reset>",
                "<reset> <reset> <reset> <blue><b>┗╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┛</b></blue> <reset> <reset> <reset>",
                "<reset>"
        ));

        /* AutoRegister */
        this.serviceAutoRegisterAutomaticallyLoggedMessage = this.prefixShort + "<gray>Zostałeś automatycznie zalogowany używając hasła <dark_aqua>%s</dark_aqua>.</gray>";
        this.serviceAutoRegisterAutomaticallyRegisteredMessage = this.prefixShort + "<gray>Zostałeś automatycznie zarejestrowany używając hasła <dark_aqua>%s</dark_aqua>.</gray>";

    }

    @Override
    public @NotNull LanguageType type() {
        return POLISH;
    }

}
