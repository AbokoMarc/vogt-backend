package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.Partner;
import cm.vogt.digitalcampus.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/partners")
@RequiredArgsConstructor
public class PublicPartnerController {

    private final PartnerRepository partnerRepository;

    @Value("${app.public-base-url:}")
    private String publicBaseUrl;

    @GetMapping
    public ApiResponse<List<Partner>> list() {
        List<Partner> partners = partnerRepository.findAll().stream()
                .filter(Partner::isActive)
                .peek(this::normalizeLogoUrl)
                .toList();

        return ApiResponse.ok(partners);
    }

    /**
     * Les anciens partenaires peuvent avoir une URL relative en base de donnees
     * (ex. /files/partners/xxx.jpg), alors que les nouveaux peuvent deja avoir
     * une URL absolue. On normalise la reponse publique pour que Netlify ne
     * cherche jamais les fichiers sur son propre domaine.
     */
    private void normalizeLogoUrl(Partner partner) {
        String logoUrl = partner.getLogoUrl();

        if (logoUrl == null || logoUrl.isBlank() || isAbsoluteUrl(logoUrl)) {
            return;
        }

        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            return;
        }

        String base = publicBaseUrl.replaceAll("/$", "");
        String path = logoUrl.startsWith("/") ? logoUrl : "/" + logoUrl;
        partner.setLogoUrl(base + path);
    }

    private boolean isAbsoluteUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
