package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.model.Page;
import com.hendisantika.ecommerce.springbootecommerce.repository.PageRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cms/pages")
public class CmsController {

    private final PageRepository pageRepository;

    public CmsController(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @GetMapping
    public ResponseEntity<List<Page>> getAllPages() {
        return ResponseEntity.ok(pageRepository.findAll());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Page> getPageBySlug(@PathVariable String slug) {
        return pageRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Page> getPageById(@PathVariable Long id) {
        return pageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Page> createPage(@Valid @RequestBody Page page) {
        Page savedPage = pageRepository.save(page);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Page> updatePage(@PathVariable Long id, @Valid @RequestBody Page page) {
        return pageRepository.findById(id)
                .map(existing -> {
                    page.setId(id);
                    page.setCreatedAt(existing.getCreatedAt());
                    page.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(pageRepository.save(page));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        return pageRepository.findById(id)
                .map(page -> {
                    pageRepository.delete(page);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
