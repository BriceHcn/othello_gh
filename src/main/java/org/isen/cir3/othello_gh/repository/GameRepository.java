package org.isen.cir3.othello_gh.repository;

import org.isen.cir3.othello_gh.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}
