package com.github.storytime.ars.repositories;

import com.github.storytime.ars.entity.NewsEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends CrudRepository<NewsEntry, Long> {
}
