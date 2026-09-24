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
                .addResourceLocations("file:" + basePath + "/")
                // Sans ce resolver, un fichier absent (disque ephemere efface au
                // redemarrage du conteneur) remonte une exception non geree -> 500
                // brut au lieu d'un 404 propre.
                .resourceChain(true)
                .addResolver(new org.springframework.web.servlet.resource.PathResourceResolver() {
                    @Override
                    protected org.springframework.core.io.Resource getResource(
                            String resourcePath,
                            org.springframework.core.io.Resource location) throws java.io.IOException {
                        org.springframework.core.io.Resource requested = location.createRelative(resourcePath);
                        return (requested.exists() && requested.isReadable()) ? requested : null;
                    }
                });
    }
}
