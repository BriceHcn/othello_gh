package org.isen.cir3.othello_gh.controller;

import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.exception.InvalidMoveException;
import org.isen.cir3.othello_gh.form.GameForm;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.isen.cir3.othello_gh.service.GameService;
import org.isen.cir3.othello_gh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private GameRepository games;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("opponents",userService.getAllUserExceptCurrentTemp(users));
        List tailles = Arrays.asList(8, 6, 4);
        model.addAttribute("sizes",tailles);
        return "game/create";
    }

    @PostMapping("/create")
    public String addAction(@Valid @ModelAttribute("game") GameForm form, BindingResult result, Model model, HttpServletRequest request) {
        if(result.hasErrors()){
            model.addAttribute("game",form);
            return "game/create";
        }
        if(form == null){
            return "game/create";
        }
        Game game = games.save(gameService.create(form.getSize(),form.getCurrentUser(), form.getOpponent()));
        return "redirect:/game/"+game.getId();
    }



    @GetMapping("/list")
    public String list(Model model){
        model.addAttribute("games",gameService.findGameForCurrentUser());
        model.addAttribute("users",users);
        return"game/list";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/list/all")
    public String listAll(Model model){
        model.addAttribute("games",games.findAll());
        model.addAttribute("users",users);
        return"game/list";
    }

    @GetMapping("/{id}")
    public String game(@PathVariable Long id, Model model,RedirectAttributes attribs) {
        Game game = games.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("game", game);
        model.addAttribute("boardSize",game.getBoard().length);
        model.addAttribute("users",users);
        return "game/play";
    }


    @GetMapping("/play/{id}/{col}/{row}")
    public String play(@PathVariable Long id, @PathVariable int col, @PathVariable int row,Model model, RedirectAttributes attribs) {
        Game game = games.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(!gameService.canIPlay(userService.getConnectedUserUsername(), game)){
            attribs.addFlashAttribute("message", "C'est pas ton tour");
            return "redirect:/game/" + id;
        }
        if(!gameService.isCaseEmpty(game, col, row)){
            attribs.addFlashAttribute("message", "Cette case est deja prise");
            return "redirect:/game/" + id;
        }

        try {
            games.save(gameService.play(game, col, row));
        } catch (InvalidMoveException e) {
            attribs.addFlashAttribute("message", "tu ne peux pas jouer dans cette case");
        }
        return "redirect:/game/" + id;
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        games.deleteById(id);
        return"redirect:/game/list";
    }





}
