package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.common.PageResponse;
import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.domain.News;
import cm.vogt.digitalcampus.dto.content.NewsRequest;
import cm.vogt.digitalcampus.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final AuditLogService auditLogService;

    public PageResponse<News> listPublished(String category, Pageable pageable) {
        return listPublished(category, pageable, "fr");
    }

    public PageResponse<News> listPublished(String category, Pageable pageable, String lang) {
        var page = (category == null || category.isBlank())
                ? newsRepository.findByStatusOrderByPublishedAtDesc(PublicationStatus.PUBLISHED, pageable)
                : newsRepository.findByStatusAndCategoryOrderByPublishedAtDesc(PublicationStatus.PUBLISHED, category, pageable);
        page.getContent().forEach(n -> applyLang(n, lang));
        return PageResponse.from(page);
    }

    public News getPublishedBySlug(String slug) {
        return getPublishedBySlug(slug, "fr");
    }

    public News getPublishedBySlug(String slug, String lang) {
        News news = newsRepository.findBySlugAndStatus(slug, PublicationStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Article introuvable : " + slug));
        return applyLang(news, lang);
    }

    /** Ne persiste jamais : mutation en memoire du seul objet renvoye au client (aucune methode ici n'est @Transactional). */
    private News applyLang(News n, String lang) {
        if ("en".equalsIgnoreCase(lang)) {
            if (n.getTitleEn() != null && !n.getTitleEn().isBlank()) n.setTitle(n.getTitleEn());
            if (n.getExcerptEn() != null && !n.getExcerptEn().isBlank()) n.setExcerpt(n.getExcerptEn());
            if (n.getContentEn() != null && !n.getContentEn().isBlank()) n.setContent(n.getContentEn());
        }
        return n;
    }

    /** Utilise par le VOGT ADMIN — tous les articles, quel que soit leur statut. */
    public java.util.List<News> listAllForAdmin() {
        return newsRepository.findAll();
    }

    @Transactional
    public News create(NewsRequest request) {
        News news = new News();
        apply(news, request);
        news = newsRepository.save(news);
        auditLogService.log("CREATE_NEWS", "News", news.getId().toString(), "Article cree : " + news.getTitle());
        return news;
    }

    @Transactional
    public News update(UUID id, NewsRequest request) {
        News news = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("Article introuvable."));
        apply(news, request);
        news = newsRepository.save(news);
        auditLogService.log("UPDATE_NEWS", "News", news.getId().toString(), "Article modifie : " + news.getTitle());
        return news;
    }

    @Transactional
    public void publish(UUID id) {
        News news = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("Article introuvable."));
        news.setStatus(PublicationStatus.PUBLISHED);
        news.setPublishedAt(Instant.now());
        newsRepository.save(news);
        auditLogService.log("PUBLISH_NEWS", "News", news.getId().toString(), "Article publie : " + news.getTitle());
    }

    @Transactional
    public void schedule(UUID id, Instant publishAt) {
        News news = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("Article introuvable."));
        news.setStatus(PublicationStatus.SCHEDULED);
        news.setPublishedAt(publishAt);
        newsRepository.save(news);
    }

    @Transactional
    public void archive(UUID id) {
        News news = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("Article introuvable."));
        news.setStatus(PublicationStatus.ARCHIVED);
        newsRepository.save(news);
        auditLogService.log("ARCHIVE_NEWS", "News", news.getId().toString(), "Article archive : " + news.getTitle());
    }

    private void apply(News news, NewsRequest request) {
        news.setSlug(request.getSlug());
        news.setTitle(request.getTitle());
        news.setCategory(request.getCategory());
        news.setCoverImageUrl(request.getCoverImageUrl());
        news.setExcerpt(request.getExcerpt());
        news.setContent(request.getContent());
        news.setAuthor(request.getAuthor());
        news.setSeoTitle(request.getSeoTitle());
        news.setMetaDescription(request.getMetaDescription());
        news.setTitleEn(request.getTitleEn());
        news.setExcerptEn(request.getExcerptEn());
        news.setContentEn(request.getContentEn());
    }
}
