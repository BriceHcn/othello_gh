package org.isen.cir3.othello_gh.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime created = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Column
    private Integer winner;

    @Column
    private Integer white;

    @Column
    private Integer scoreWhite;

    @Column
    private Integer scoreBlack;

    @Column
    private Integer black;

    @Column
    private Integer currentPlayer;

    @Lob
    private CellStatus[][] board;

    public Game(){}
    public Game(Integer size) {
        board = new CellStatus[size][size];
    }
}
