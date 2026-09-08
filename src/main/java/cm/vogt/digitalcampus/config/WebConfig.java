package cm.vogt.digitalcampus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Sert les fichiers du stockage local en dev. En production, ce mapping disparait
 *  au profit d'URLs directes vers le bucket S3-compatible. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.local-path:./storage}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + basePath + "/");
    }
}
