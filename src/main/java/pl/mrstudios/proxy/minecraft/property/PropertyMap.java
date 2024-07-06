package pl.mrstudios.proxy.minecraft.property;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

import static com.google.common.collect.LinkedHashMultimap.create;

public class PropertyMap extends ForwardingMultimap<String, Property> {

    private final Multimap<String, Property> properties = create();

    public void add(@NotNull Property property) {
        this.put(property.name(), property);
    }

    public static class Serializer implements JsonSerializer<PropertyMap>, JsonDeserializer<PropertyMap> {

        public PropertyMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            PropertyMap result = new PropertyMap();

            if (json instanceof JsonObject jsonObjectFirst)
                for (Map.Entry<String, JsonElement> entry : jsonObjectFirst.entrySet())
                    if (entry.getValue() instanceof JsonArray jsonArrayFirst)
                        for (JsonElement element : jsonArrayFirst)
                            result.put(entry.getKey(), new Property(entry.getKey(), element.getAsString(), null));

            if (json instanceof JsonArray jsonArray)
                for (JsonElement element : jsonArray)
                    if (element instanceof JsonObject jsonObjectSecond) {

                        String name = jsonObjectSecond.getAsJsonPrimitive("name").getAsString();
                        String value = jsonObjectSecond.getAsJsonPrimitive("value").getAsString();

                        if (jsonObjectSecond.has("signature"))
                            result.put(name, new Property(name, value, jsonObjectSecond.getAsJsonPrimitive("signature").getAsString()));

                        if (jsonObjectSecond.has("signature"))
                            continue;

                        result.put(name, new Property(name, value, null));

                    }

            return result;

        }


        public JsonElement serialize(PropertyMap source, Type typeOfSource, JsonSerializationContext context) {

            JsonArray result = new JsonArray();
            for (Property property : source.values()) {

                JsonObject object = new JsonObject();

                object.addProperty("name", property.name());
                object.addProperty("value", property.value());

                if (property.signature() != null)
                    object.addProperty("signature", property.signature());

                result.add(object);

            }

            return result;

        }

    }

    @Override
    protected @NotNull Multimap<String, Property> delegate() {
        return this.properties;
    }

}
