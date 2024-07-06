package pl.mrstudios.proxy.core;

import com.mysql.cj.jdbc.Driver;
import dev.rollczi.litecommands.LiteCommandsFactory;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.annotations.command.Command;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.mrstudios.commons.inject.Injector;
import pl.mrstudios.commons.reflection.Reflections;
import pl.mrstudios.proxy.core.account.AccountManager;
import pl.mrstudios.proxy.core.command.CommandManager;
import pl.mrstudios.proxy.core.command.handler.InvalidCommandUsageHandler;
import pl.mrstudios.proxy.core.command.handler.ValidatorResultHandler;
import pl.mrstudios.proxy.core.command.platform.ProxyPlatform;
import pl.mrstudios.proxy.core.command.platform.annotations.Cooldown;
import pl.mrstudios.proxy.core.command.platform.annotations.HasGroup;
import pl.mrstudios.proxy.core.command.platform.annotations.Range;
import pl.mrstudios.proxy.core.command.platform.validator.CooldownValidator;
import pl.mrstudios.proxy.core.command.platform.validator.HasGroupValidator;
import pl.mrstudios.proxy.core.command.platform.validator.RangeValidator;
import pl.mrstudios.proxy.core.config.Configuration;
import pl.mrstudios.proxy.core.config.ConfigurationFactory;
import pl.mrstudios.proxy.core.mysql.MySQL;
import pl.mrstudios.proxy.core.service.Service;
import pl.mrstudios.proxy.core.user.User;
import pl.mrstudios.proxy.core.user.UserManager;
import pl.mrstudios.proxy.event.EventManager;
import pl.mrstudios.proxy.event.interfaces.Listener;
import pl.mrstudios.proxy.logger.Logger;
import pl.mrstudios.proxy.netty.packet.PacketRegistry;
import pl.mrstudios.proxy.netty.server.NettyServer;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import static dev.rollczi.litecommands.schematic.SchematicFormat.angleBrackets;
import static java.lang.Runtime.getRuntime;
import static java.nio.file.Path.of;
import static java.sql.DriverManager.registerDriver;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.kyori.adventure.text.minimessage.MiniMessage.builder;

@SuppressWarnings("all")
public class Application {

    private MySQL mySQL;
    private Logger logger;
    private Injector injector;

    /* Server */
    private NettyServer nettyServer;

    /* Kyori */
    private MiniMessage miniMessage;

    /* Configuration */
    private Configuration configuration;
    private ConfigurationFactory configurationFactory;

    /* Registry */
    private PacketRegistry packetRegistry;

    /* Managers */
    private UserManager userManager;
    private EventManager eventManager;
    private AccountManager accountManager;
    private CommandManager commandManager;

    /* Executors */
    protected final ScheduledExecutorService executorService = newScheduledThreadPool(getRuntime().availableProcessors());

    public Application() {

        /* Initialize */
        this.logger = new Logger();
        this.logger.info("Loading application, please wait..");

        /* Configuration */
        this.configurationFactory = new ConfigurationFactory(of("./"));
        this.configuration = this.configurationFactory.produce(Configuration.class, "config.yml");

        /* MySQL */
        this.mySQL = new MySQL(this.configuration);

        /* Initialize Registry */
        this.packetRegistry = new PacketRegistry();

        /* Initialize Managers */
        this.userManager = new UserManager();
        this.eventManager = new EventManager();
        this.accountManager = new AccountManager(this.mySQL);
        this.commandManager = new CommandManager();

        /* Kyori */
        this.miniMessage = builder()
                .build();

        /* Injector */
        this.injector = new Injector()

                /* Kyori */
                .register(MiniMessage.class, this.miniMessage)

                /* General */
                .register(MySQL.class, this.mySQL)
                .register(Logger.class, this.logger)
                .register(Configuration.class, this.configuration)
                .register(ConfigurationFactory.class, this.configurationFactory)

                /* Registry */
                .register(PacketRegistry.class, this.packetRegistry)

                /* Managers */
                .register(UserManager.class, this.userManager)
                .register(EventManager.class, this.eventManager)
                .register(CommandManager.class, this.commandManager)
                .register(AccountManager.class, this.accountManager);

        this.injector.register(Injector.class, this.injector);

        /* Server */
        this.nettyServer = this.injector.inject(NettyServer.class);

        /* Post Injector Registry */
        this.injector.register(NettyServer.class, this.nettyServer);

        /* Initialize Services */
        new Reflections<Service>("pl.mrstudios.proxy")
                .getClassesImplementing(Service.class)
                .stream().map(this.injector::inject).filter(Objects::nonNull)
                .peek(this.injector::register).peek((service) -> this.eventManager.register(service))
                .filter((service) -> service.repeatDelay().toMillis() > 0).forEach(
                        (service) -> this.executorService.scheduleAtFixedRate(service, 0, service.repeatDelay().toMillis(), MILLISECONDS)
                );

        /* Initialize Listeners */
        new Reflections<Listener>("pl.mrstudios.proxy")
                .getClassesImplementing(Listener.class)
                .stream().filter((clazz) -> clazz != Service.class)
                .filter((clazz) -> !clazz.isAnnotationPresent(Command.class))
                .map(this.injector::inject).filter(Objects::nonNull)
                .forEach(this.eventManager::register);

        /* Lite Commands */
        LiteCommandsFactory.builder(User.class, new ProxyPlatform(this.commandManager))

                /* Validators */
                .annotations((annotation) -> annotation.validator(Integer.class, Range.class, new RangeValidator()))
                .annotations((annotation) -> annotation.validator(User.class, HasGroup.class, new HasGroupValidator()))
                .annotations((annotation) -> annotation.validator(User.class, Cooldown.class, new CooldownValidator()))

                /* Handlers */
                .invalidUsage(this.injector.inject(InvalidCommandUsageHandler.class))
                .result(String.class, this.injector.inject(ValidatorResultHandler.class))

                /* Commands */
                .commands(LiteCommandsAnnotations.of(
                        new Reflections<Object>("pl.mrstudios.proxy")
                                .getClassesAnnotatedWith(Command.class)
                                .stream().map(this.injector::inject)
                                .filter(Objects::nonNull)
                                .peek((command) -> { if (command instanceof Listener) this.eventManager.register((Listener) command); })
                                .toArray(Object[]::new)
                ))

                /* Schematic */
                .schematicGenerator(angleBrackets())

                /* Build */
                .build();

    }


    public static @NotNull String VERSION = "2.0.0";
    public static @NotNull String CODE_NAME = "Epyc";

    {
        try {
            registerDriver(new Driver());
        } catch (Exception exception) {
            throw new RuntimeException("Unable to register 'com.mysql.cj.jdbc.Driver' driver due to exception.", exception);
        }
    }

}
