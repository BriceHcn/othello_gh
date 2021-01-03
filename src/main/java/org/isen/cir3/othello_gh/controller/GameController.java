package org.isen.cir3.othello_gh.controller;

import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.User;
import org.isen.cir3.othello_gh.form.GameForm;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.isen.cir3.othello_gh.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/game")
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
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
        model.addAttribute("opponents",getAllUserExceptCurrent());
        System.out.println(getAllUserExceptCurrent().size());
        System.out.println(users.findAll().size());
        List tailles = Arrays.asList(4, 6, 8);//todo dans un enum ou quoi
        model.addAttribute("sizes",tailles);
        return "game/create";
    }

    @PostMapping("/create")
    public String addAction(@Valid @ModelAttribute("game") GameForm form, BindingResult result, Model model, HttpServletRequest request) {

        System.out.println("veux jouer contre "+form.getOpponent());
        System.out.println("Sur un plateau de la taille "+form.getSize());
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

    @PostFilter("filterObject.pseudo == authentication.principal.pseudo")//TODO faire marcher ca
    private List<User> getAllUserExceptCurrent() {
        return users.findAll();
    }





}
