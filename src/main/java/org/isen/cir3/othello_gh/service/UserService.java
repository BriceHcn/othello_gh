package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.User;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

public class UserService {

    @PostFilter("filterObject.username != authentication.principal.username")//todo ca ne marche pas 
    public List<User> getAllUserExceptCurrent(UserRepository users) {
        return users.findAll();
    }
}
