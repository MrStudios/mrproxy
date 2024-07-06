package pl.mrstudios.proxy.core.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

import static eu.okaeri.configs.ConfigManager.create;

public record ConfigurationFactory(@NotNull Path path) {

    public <CONFIG extends OkaeriConfig> CONFIG produce(@NotNull Class<CONFIG> clazz, @NotNull String file) {
        return this.produce(clazz, new File(this.path.toFile(), file));
    }

    public <CONFIG extends OkaeriConfig> CONFIG produce(@NotNull Class<CONFIG> clazz, @NotNull File file) {
        return create(clazz, (initializer) ->
                initializer.withConfigurer(new YamlSnakeYamlConfigurer())
                        .withBindFile(file)
                        .saveDefaults()
                        .load(true)
        );
    }


}
