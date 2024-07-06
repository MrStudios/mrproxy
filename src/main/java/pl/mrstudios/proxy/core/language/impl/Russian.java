package pl.mrstudios.proxy.core.language.impl;

import org.jetbrains.annotations.NotNull;
import pl.mrstudios.proxy.core.language.Language;

import static java.lang.String.join;
import static java.util.List.of;
import static pl.mrstudios.proxy.core.language.Language.LanguageType.RUSSIAN;

public class Russian extends Language {

    {

        /* General */
        this.prefix = "<blue><dark_gray>[<dark_aqua>MrProxy</dark_aqua>]</dark_gray> »</blue> <reset>";
        this.prefixShort = "<blue><dark_gray>[<dark_aqua>*</dark_aqua>]</dark_gray> »</blue> <reset>";

        /* Words */
        this.wordEnabled = "<green>включено</green>";
        this.wordDisabled = "<red>отключено</red>";
        this.wordDisconnected = "ОТДЕЛЬНО";

        /* Plural */
        this.pluralOneDay = "день";
        this.pluralTwoDays = "дней";
        this.pluralFiveDays = "дней";

        this.pluralOneHour = "час";
        this.pluralTwoHours = "часы";
        this.pluralFiveHours = "часы";

        this.pluralOneMinute = "минута";
        this.pluralTwoMinutes = "минут";
        this.pluralFiveMinutes = "минут";

        this.pluralOneSecond = "второй";
        this.pluralTwoSeconds = "секунды";
        this.pluralFiveSeconds = "секунды";

        /* Message */
        this.welcomeMessage = join("<br>", of(
                "<reset>",
                "<reset>      <blue><b>┏╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┓</b></blue>",
                "<reset>",
                "<reset>                  <gray>Добро пожаловать на <dark_aqua><b>MrProxy</b></dark_aqua>!</gray>",
                "<reset>",
                "<reset>         <gray>Благодарим вас за покупку инструмента, мы очень</gray>",
                "<reset>         <gray>признательны, доступные команды можно найти в</gray>",
                "<reset>         <gray>разделе <dark_aqua>,help</dark_aqua>.</gray>%s",
                "<reset>",
                "<reset>      <blue><b>┗╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┛</b></blue>",
                "<reset>"
        ));
        this.welcomeMessageExpireLine = "<br><br><reset>         <gray><red><b>ВНИМАНИЕ!</b></red> Срок действия вашей учетной записи истекает в <dark_aqua>%s</dark_aqua>.</gray>";

        /* Server Status Resolver */
        this.serverStatusResolverResponse = join("<br>", of(
                "<reset>",
                this.prefixShort + "<gray>Латентность: <dark_aqua>%dms</dark_aqua></gray>",
                this.prefixShort + "<gray>Версия: <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%d</aqua>)</dark_gray></gray>",
                this.prefixShort + "<gray>Игроки: <aqua>%d</aqua><dark_gray>/</dark_gray><dark_aqua>%d</dark_aqua></gray>",
                this.prefixShort + "<gray>Описание:</gray><br><dark_aqua>%s</dark_aqua>",
                "<reset>"
        ));
        this.serverStatusResolverConnectionFailed = this.prefixShort + "<gray>Невозможно определить статус сервера, вероятно, сервер находится в автономном режиме.";

        /* Error Messages */
        this.errorNoPermissions = this.prefix + "<gray>У вас нет прав на использование этой команды.";
        this.errorInvalidCommandUsage = this.prefix + "<gray>Правильно использовать команду <dark_aqua>%s</dark_aqua>.";
        this.errorCommandNotFound = this.prefix + "<gray>Команда не существует, используйте <dark_aqua>,help</dark_aqua> для отображения доступных команд.";
        this.errorMustEnterMessage = this.prefix + "<gray>Для отправки сообщения необходимо ввести.";
        this.errorMustWaitBeforeNextUsage = this.prefix + "<gray>Вы должны подождать <dark_aqua>%dms</dark_aqua> перед следующим использованием команды.";
        this.errorMustBeLogged = this.prefix + "<gray>Вы должны войти в систему, чтобы использовать эту команду.";
        this.errorProxyListEmpty = this.prefix + "<gray>Список <dark_aqua>%s</dark_aqua> прокси пуст.</gray>";
        this.errorProxyNoAccessToType = this.prefix + "<gray>У вас нет доступа к <dark_aqua>%s</dark_aqua> прокси.</gray>";
        this.errorExceptionOccurredYourConnection = this.prefixShort + "<gray>Исключение произошло в <dark_aqua>%s</dark_aqua> подключение к серверу.";
        this.errorYouMustBeConnected = this.prefix + "<gray>Вы должны быть подключены к удаленному серверу, чтобы использовать эту команду.";
        this.errorNoConnectedBots = this.prefix + "<gray>У вас нет ни одного подключенного бота.";
        this.errorNotInRange = this.prefix + "<gray>Аргумент <dark_aqua>%s</dark_aqua> не входит в определенный диапазон.</gray>";

        /* Format */
        this.proxyJoinMessageFormat = this.prefix + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%s</aqua>] </dark_gray><aqua>%s</aqua> <gray>присоединился к прокси.";
        this.proxyQuitMessageFormat = this.prefix + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%s</aqua>] </dark_gray><aqua>%s</aqua> <gray>отключен от прокси-сервера.";
        this.proxyChatMessageFormat = this.prefixShort + "<dark_gray>(<aqua>%s</aqua>)</dark_gray> %s%s<reset><dark_gray>:</dark_gray> <white>%s</white>";
        this.proxyRemoteJoinMessageFormat = this.prefixShort + "<gray>Игрок <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> соединённый с <dark_aqua>%s</dark_aqua> сервер.</gray>";
        this.proxyRemoteServerDisconnectMessageFormat = this.prefixShort + "<gray>Игрок <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> отключённый от <dark_aqua>%s</dark_aqua> сервер.</gray>";
        this.proxyRemoteDisconnectedMessageFormat = this.prefixShort + "<gray>Вы были отключены от <dark_aqua>%s</dark_aqua> сервер.</gray><br><dark_aqua>%s</dark_aqua>";
        this.proxyOptionChangedMessageFormat = this.prefix + "<gray>Вариант <dark_aqua>%s</dark_aqua> был изменен на <dark_aqua>%s</dark_aqua>.</gray>";

        this.remoteLastPacketReceivedNotifyFormat = "<gold><b>*</b></gold> <blue>%s</blue> <dark_gray>(<aqua>%dms</aqua>)</dark_gray> <gold><b>*</b></gold>";
        this.proxyRemoteConnectingBotsMessageFormat = this.prefixShort + "<gray>Игрок <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray> is connecting <dark_aqua>%d bots</dark_aqua> to <dark_aqua>%s</dark_aqua> сервер.</gray>";
        this.remoteBotChatReceivedMessageFormat = this.prefixShort + "<dark_gray>(<blue>BOT CHAT</blue>) <dark_aqua>%s</dark_aqua>: <aqua>%s</aqua></dark_gray>";
        this.remoteBotInfoBotConnectedMessageFormat = this.prefixShort + "<gray><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> соединённый с <dark_aqua>%s</dark_aqua> сервер.</gray>";
        this.remoteBotInfoBotDisconnectedMessageFormat = this.prefixShort + "<gray><hover:show_text:'%s'><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> отключённый от <dark_aqua>%s</dark_aqua> сервер.</hover></gray>";
        this.remoteBotInfoBotSwitchedServerMessageFormat = this.prefixShort + "<gray><dark_gray>(<blue>BOT INFO</blue>)</dark_gray> <dark_aqua>%s</dark_aqua> перенаправляется на подсервер.</gray>";

        /* Command Messages */

        /* Help */
        this.commandHelpEntry = "<reset> <click:suggest_command:',%s'><hover:show_text:'%s'><dark_aqua>,%s</dark_aqua> <dark_gray>-</dark_gray> <gray>%s</gray></hover></click>";
        this.commandHelpSectionParameters = "<br><br><reset> <gold><b>*</b></gold> <gray>Параметры команды:%s";
        this.commandHelpEntryParameter = "<br><reset>   <white><b>*</b></white> <dark_aqua>%s</dark_aqua> <dark_gray>-</dark_gray> <gray>%s</gray> <reset>";
        this.commandHelpEntryHoverNoDescription = "<reset> <dark_red><b>*</b></dark_red> <red>Описание для этой команды отсутствует.</red> <dark_red><b>*</b></dark_red> <reset>";
        this.commandHelpHeader = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Помощь: <dark_gray>(<aqua>%d</aqua><gray>/</gray><dark_aqua>%d</dark_aqua>)</dark_gray>",
                "<reset>"
        ));

        /* Command Language */
        this.commandLanguageChanged = this.prefix + "<gray>Ваш язык был установлен на <dark_aqua>%s</dark_aqua>.</gray>";

        /* Chat Clear */
        this.commandChatClearCleared = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Ваш чат был очищен.</gray>",
                "<reset>"
        ));

        /* Connect */
        this.commandConnectConnecting = this.prefix + "<gray>Подключение к <dark_aqua>%s</dark_aqua> сервер.</gray>";

        this.commandHelpFooter = join("<br>", of(
                "<reset>",
                "<reset>       <dark_gray><click:run_command:',help %d'>(<aqua>Предыдущий</aqua>)</click></dark_gray><reset>                                 <dark_gray><click:run_command:',help %d'>(<aqua>Следующий</aqua>)</click></dark_gray> <reset>",
                "<reset>"
        ));

        this.commandHelpEntryHoverDescription = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Использование команд: <reset>",
                "<reset>   <white><b>*</b></white> <dark_aqua>,%s</dark_aqua>%s <reset>",
                "<reset>"
        ));

        /* Command ConnectBot */
        this.commandConnectBotTooManyBots = this.prefix + "<gray>У вас слишком много ботов, подключенных к удаленному серверу.</gray>";

        /* Command Broadcast */
        this.commandBroadcastMessageSent = this.prefix + "<gray>Сообщение <dark_aqua>%s</dark_aqua> был успешно отправлен ботами.</gray>";

        /* Broadcast Infinity */
        this.commandBroadcastInfinityMessageScheduled = this.prefix + "<gray>Сообщение было настроено на отправку ботами.</gray>";
        this.commandBroadcastInfinityStopped = this.prefix + "<gray>Сообщение было остановлено.</gray>";

        /* Add User */
        this.commandAddUserAdded = this.prefix + "<gray>Пользователь <dark_aqua>%s</dark_aqua> был добавлен к прокси.";

        /* Login */
        this.commandLoginSuccess = this.prefix + "<gray>Вы успешно вошли в систему.";
        this.commandLoginNotRegistered = this.prefix + "<gray>Вы не зарегистрированы, используйте <dark_aqua>,register <пароль> <пароль></dark_aqua> зарегистрироваться.";

        /* Register */
        this.commandRegisterSuccess = this.prefix + "<gray>Вы успешно зарегистрированы.";
        this.commandRegisterAlreadyRegistered = this.prefix + "<gray>Вы уже зарегистрированы, используйте <dark_aqua>,login <пароль></dark_aqua> войти в систему.";
        this.commandRegisterPasswordsNotMatch = this.prefix + "<gray>Предоставленные пароли не являются идентичными.";

        /* Proxies */
        this.commandProxiesListHeader = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Доступные прокси-серверы:",
                "<reset>"
        ));

        this.commandProxiesListEntry = "<br><reset> <hover:show_text:'%s'><dark_aqua>%s</dark_aqua> <dark_gray>(<gray>Сумма: <aqua>%d</aqua>, Средняя задержка: <aqua>%dms</aqua></gray>)</dark_gray></hover>";
        this.commandProxiesListEntryHoverCountryEntry = "<br><reset>   <white><b>*</b></white> <dark_aqua>%s</dark_Aqua> <dark_gray>(%d прокси)</dark_gray> <reset>";
        this.commandProxiesListEntryHover = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Страны: <reset>%s",
                "<reset>"
        ));

        this.commandProxiesListFooter = join("<br>", of(
                "<reset>",
                this.prefixShort + "<gray>Чтобы добавить собственные прокси, используйте <dark_aqua>,ownproxy</dark_aqua> команда.",
                "<reset>"
        ));

        /* Status */
        this.commandStatusChecking = this.prefix + "<gray>Проверяем состояние сервера, пожалуйста, подождите...</gray>";
        this.commandStatusResolvedHost = this.prefixShort + "<gray>Решенный хост сервера <dark_aqua>%s:%d</dark_aqua>, подключение..</gray>";

        /* AutoRegister */
        this.commandAutoRegisterPasswordSet = this.prefix + "<gray>Автоматический пароль регистрации для <dark_aqua>%s</dark_aqua> был установлен на <dark_aqua>%s</dark_aqua>.</gray>";

        /* List */
        this.commandList = join("<br>", of(
                "<reset>",
                this.prefix + "<gray>Онлайн пользователи: <dark_gray>(<aqua>%d</aqua>)</dark_gray></gray>%s",
                "<reset>"
        ));
        this.commandListEntry = "<br><reset> <hover:show_text:'%s'><dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_aqua>%s%s</dark_aqua></hover>";
        this.commandListEntryHover = join("<br>", of(
                "<reset>",
                "<reset> <gold><b>*</b></gold> <gray>Сессия: <reset>",
                "<reset>   <white><b>*</b></white> <gray>Имя: <dark_aqua>%s</dark_aqua></gray> <reset>",
                "<reset>   <white><b>*</b></white> <gray>Сервер: <dark_aqua>%s</dark_aqua></gray> <reset>",
                "<reset>"
        ));

        /* GameMode */
        this.commandGameModeChanged = this.prefix + "<gray>Ваш режим игры был изменен на <dark_aqua>%s</dark_aqua>.</gray>";

        /* Disconnect */
        this.commandDisconnectDisconnectedBots = this.prefix + "<gray>Вы отключили подключенных ботов.</gray>";
        this.commandDisconnectDisconnectedGhost = this.prefix + "<gray>Вы отключились от прокси-сервера.</gray>";

        /* Detach */
        this.commandDetachDetached = this.prefix + "<gray>Вы отключились от удаленного сервера.</gray>";

        /* Service */

        /* Reconnect */
        this.autoReconnectService = this.prefixShort + "<gray>Вы будете автоматически переподключены в <dark_aqua>%dms</dark_aqua>.</gray>";

        /* Authorization */
        this.serviceAuthorizationMustLoginMessage = this.prefixShort + "<gray>Вы должны войти в систему, используя <dark_aqua>,login <пароль></dark_aqua> команда.";
        this.serviceAuthorizationMustRegisterMessage = this.prefixShort + "<gray>Вы должны зарегистрироваться, используя <dark_aqua>,register <пароль> <пароль></dark_aqua> команда.";
        this.serviceAuthorizationTitle = "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>";
        this.serviceAuthorizationSubtitle = "<gray>У вас есть <dark_aqua>%d %s</dark_aqua> для авторизации.</gray>";

        /* Proxy */
        this.serviceProxyCheckingNotify = this.prefix + "<gray>Проверка прокси-серверов, пожалуйста, подождите... <dark_gray>(<aqua>%s</aqua>)</dark_gray> <dark_gray>[<aqua>%d</aqua>]</dark_gray>";

        /* Connect */
        this.serviceConnectResolvedServerBrand = this.prefixShort + "<gray>Серверный двигатель: <dark_aqua>%s</dark_aqua></gray>";

        /* Lag Detection */
        this.lagDetectionTitle = "<red><b>LAG!</b></red>";
        this.lagDetectionSubtitle = "<gray>Сервер не отвечает для <dark_aqua>%dms</dark_aqua>.</gray>";

        /* TabList */
        this.tabListHeader = join("<br>", of(
                "<reset> ",
                "<reset> <reset> <reset> <blue><b>┏╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┓</b></blue> <reset> <reset> <reset>",
                "<reset>",
                "<gold>★</gold> <b><dark_aqua>MrProxy</dark_aqua></b> <gold>★</gold>",
                "<gray>Создано <dark_aqua>MrStudios Industries</dark_aqua></gray>",
                "<reset>",
                "<gray>Группа: <dark_aqua>%s</dark_aqua></gray>",
                "<gray>Валидность: <dark_aqua>%s</dark_aqua></gray>",
                "<reset>",
                "<gray>Подключенные боты: <dark_gray><aqua>%d</aqua>/<dark_aqua>%d</dark_aqua></dark_gray></gray>",
                "<reset>"
        ));

        this.tabListFooter = join("<br>", of(
                "<reset>",
                "<gray>Последний полученный пакет:</gray>",
                "<dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%dms</aqua>)</dark_gray>",
                "<reset>",
                "<gray>Сессия: <dark_aqua>%s</dark_aqua> <dark_gray>(<aqua>%s</aqua>)</dark_gray></gray>",
                "<reset>",
                "<reset> <reset> <reset> <blue><b>┗╋━━━━━━━━━━━━━━━━━━━━◥◣◆◢◤━━━━━━━━━━━━━━━━━━━━╋┛</b></blue> <reset> <reset> <reset>",
                "<reset>"
        ));

        /* AutoRegister */
        this.serviceAutoRegisterAutomaticallyLoggedMessage = this.prefixShort + "<gray>Вы автоматически вошли в систему, используя <dark_aqua>%s</dark_aqua> пароль.</gray>";
        this.serviceAutoRegisterAutomaticallyRegisteredMessage = this.prefixShort + "<gray>Вы были автоматически зарегистрированы с помощью <dark_aqua>%s</dark_aqua> пароль.</gray>";

    }

    @Override
    public @NotNull LanguageType type() {
        return RUSSIAN;
    }

}
