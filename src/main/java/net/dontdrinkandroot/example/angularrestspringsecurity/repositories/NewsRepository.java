package net.dontdrinkandroot.example.angularrestspringsecurity.repositories;

import net.dontdrinkandroot.example.angularrestspringsecurity.entity.NewsEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends CrudRepository<NewsEntry, Long> {
}
