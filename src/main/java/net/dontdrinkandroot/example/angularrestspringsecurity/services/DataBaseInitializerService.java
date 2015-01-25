package net.dontdrinkandroot.example.angularrestspringsecurity.services;

import net.dontdrinkandroot.example.angularrestspringsecurity.entity.NewsEntry;
import net.dontdrinkandroot.example.angularrestspringsecurity.entity.User;
import net.dontdrinkandroot.example.angularrestspringsecurity.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;


public class DataBaseInitializerService {

    @Autowired
    private NewsService newsService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void initDataBase() {
        User userUser = new User("user", this.passwordEncoder.encode("user"));
        userUser.addRole("user");
        this.userRepository.save(userUser);

        User adminUser = new User("admin", this.passwordEncoder.encode("admin"));
        adminUser.addRole("user");
        adminUser.addRole("admin");
        this.userRepository.save(adminUser);

        long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24;
        for (int i = 0; i < 10; i++) {
            NewsEntry newsEntry = new NewsEntry();
            newsEntry.setContent("This is example content " + i);
            newsEntry.setDate(new Date(timestamp));
            this.newsService.save(newsEntry);
            timestamp += 1000 * 60 * 60;
        }
    }

}