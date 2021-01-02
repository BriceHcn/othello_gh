package org.isen.cir3.othello_gh.repository;

import org.isen.cir3.othello_gh.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public User findByUsername(String username);

    public User findByPseudo(String pseudo);
}

