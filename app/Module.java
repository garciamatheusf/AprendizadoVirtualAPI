import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.inject.AbstractModule;
import play.libs.Json;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(JavaJsonCustomObjectMapper.class).asEagerSingleton();
    }

    public static class JavaJsonCustomObjectMapper {
        JavaJsonCustomObjectMapper() {
            ObjectMapper mapper = Json.newDefaultMapper();
            // enable features and customize the object mapper here ...
            mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(mapper.getTypeFactory()))
                    .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            Json.setObjectMapper(mapper);
        }

    }

}
