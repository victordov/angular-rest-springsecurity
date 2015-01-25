package com.github.storytime.ars.services;

import com.github.storytime.ars.entity.NewsEntry;
import com.github.storytime.ars.repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Transactional(readOnly = true)
    public Iterable<NewsEntry> findAll() {
        return newsRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        newsRepository.delete(id);
    }

    @Transactional
    public NewsEntry save(NewsEntry newsEntry) {
        return newsRepository.save(newsEntry);
    }

    @Transactional
    public NewsEntry find(Long id) {
        return newsRepository.findOne(id);
    }
}
