package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.repository.NewsRepository;
import cm.vogt.digitalcampus.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sitemap genere dynamiquement a partir du contenu reellement publie —
 * contrairement au sitemap.xml statique du frontend qui ne liste que les
 * pages fixes, celui-ci reflete les formations et articles publies a
 * l'instant T. A pointer depuis robots.txt en production :
 * Sitemap: https://votre-api/api/v1/public/sitemap.xml
 */
@RestController
@RequiredArgsConstructor
public class SitemapController {

    private final ProgramRepository programRepository;
    private final NewsRepository newsRepository;

    private static final String SITE_URL = "https://VOTRE-DOMAINE";

    @GetMapping(value = "/api/v1/public/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        xml.append(url(SITE_URL + "/index.html", "1.0"));

        programRepository.findByStatusOrderByDisplayOrderAsc(PublicationStatus.PUBLISHED)
                .forEach(p -> xml.append(url(SITE_URL + "/formation.html?slug=" + p.getSlug(), "0.8")));

        newsRepository.findByStatusOrderByPublishedAtDesc(PublicationStatus.PUBLISHED, PageRequest.of(0, 200))
                .forEach(n -> xml.append(url(SITE_URL + "/actualite.html?slug=" + n.getSlug(), "0.6")));

        xml.append("</urlset>\n");
        return xml.toString();
    }

    private String url(String loc, String priority) {
        return "  <url><loc>" + loc.replace("&", "&amp;") + "</loc><priority>" + priority + "</priority></url>\n";
    }
}
