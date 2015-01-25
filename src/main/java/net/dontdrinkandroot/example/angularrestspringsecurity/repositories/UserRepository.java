package net.dontdrinkandroot.example.angularrestspringsecurity.repositories;

import net.dontdrinkandroot.example.angularrestspringsecurity.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByName(String name);
}
