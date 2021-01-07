package org.isen.cir3.othello_gh.controller;

import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.exception.InvalidMoveException;
import org.isen.cir3.othello_gh.form.GameForm;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.isen.cir3.othello_gh.service.GameService;
import org.isen.cir3.othello_gh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public String addAction(@Valid @ModelAttribute("game") GameForm form, BindingResult result, Model model) {
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
        Optional<Game> game = games.findById(id);
        //si cette partie existe
        if(game.isPresent()){
            model.addAttribute("game", game.get());
            model.addAttribute("boardSize",game.get().getBoard().length);
            model.addAttribute("users",users);
            return "game/play";
            //sinon redirection vers la liste des parties
        }else{
            attribs.addFlashAttribute("message", "Cette partie n'existe pas (ou plus) 😢");
            return "redirect:/game/list";
        }
    }


    @GetMapping("/play/{id}/{col}/{row}")
    public String play(@PathVariable Long id, @PathVariable int col, @PathVariable int row, RedirectAttributes attribs) {
        Optional<Game> game = games.findById(id);
        //si la partie n'existe pas: redirection vers la liste des parties
        if(game.isEmpty()){
            attribs.addFlashAttribute("message", "Ton adversaire à supprimé cette partie (sans doute un mauvais perdant) 😢");
            return "redirect:/game/list";
        }

        //si c'est mon tour
        if(!gameService.canIPlay(userService.getConnectedUserUsername(), game.get())){
            attribs.addFlashAttribute("message", "C'est pas ton tour");
            return "redirect:/game/" + id;
        }
        //que la case ou je joue est vide
        if(!gameService.isCaseEmpty(game.get(), col, row)){
            attribs.addFlashAttribute("message", "Cette case est deja prise");
            return "redirect:/game/" + id;
        }
        //et que mon moove est valide selon les regles du jeu
        try {
            games.save(gameService.play(game.get(), col, row));
        } catch (InvalidMoveException e) {
            attribs.addFlashAttribute("message", "tu ne peux pas jouer dans cette case");
        }
        return "redirect:/game/" + id;
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,RedirectAttributes attribs){
        Optional<Game> game = games.findById(id);
        if(game.isPresent()){
            games.deleteById(id);
            return"redirect:/game/list";
        }else{
            attribs.addFlashAttribute("message", "Cette partie à deja été supprimée 😢");
            return "redirect:/game/list";
        }


    }





}
