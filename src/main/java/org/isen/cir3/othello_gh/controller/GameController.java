package org.isen.cir3.othello_gh.controller;

import org.isen.cir3.othello_gh.domain.BoardSize;
import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.User;
import org.isen.cir3.othello_gh.form.GameForm;
import org.isen.cir3.othello_gh.form.UserForm;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.isen.cir3.othello_gh.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameController {
    @Autowired
    private UserRepository users;


    //private BoardSize sizes;

    @Autowired
    private GameRepository repository;

    @Autowired
    private GameService service;

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("opponents",users.findAll());//TODO enlever l'utilisateur connecté de la liste
        List couleurs = Arrays.asList(4, 6, 8);//todo dans un enum ou quoi
        model.addAttribute("sizes",couleurs);
        return "game/create";
    }

    @PostMapping("/create")
    public String addAction(@Valid @ModelAttribute("game") GameForm form, BindingResult result, Model model, HttpServletRequest request) {

        System.out.println("veux jouer contre "+form.getOpponent());
        System.out.println("Sur un plateau de la taille 4"+form.getSize());
        //TODO
        //Game game = repository.save(service.create());
        return "index";
    }


    @GetMapping("/game/{id}")
    public String game(@PathVariable Long id, Model model) {
        Game game = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("game", game);

        return "game";
    }
}
