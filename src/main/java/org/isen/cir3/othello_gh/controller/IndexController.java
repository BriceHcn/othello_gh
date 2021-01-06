package org.isen.cir3.othello_gh.controller;

import lombok.Getter;
import lombok.Setter;
import org.isen.cir3.othello_gh.domain.Authority;
import org.isen.cir3.othello_gh.domain.User;
import org.isen.cir3.othello_gh.form.UserForm;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;



@Getter
@Setter

@Controller
public class IndexController {

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder PasswordEncoder;

    @GetMapping({"","/"})
    public String index(){
        //todo rajouter un truc en bdd qui creer les deux statut du site
        //Authority auth = new Authority(1,ROLE_ADMIN);
        //Authority auth = new Authority(2,ROLE_ADMIN);
        return "redirect:/game/list";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(@PathVariable(required = false)Long id,Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("user",userForm);

        if(id!= null){
            User c = users.findById(id).orElseThrow(() ->new RuntimeException("Not found"));
            userForm.setId(c.getId());
            userForm.setPseudo(c.getPseudo());
            userForm.setUsername(c.getUsername());
            userForm.setPassword(c.getPassword());
        }

        return "register";
    }

    @PostMapping("/register")
    public String addAction(@Valid @ModelAttribute("user") UserForm form, BindingResult result, Model model,HttpServletRequest request) {
        if(result.hasErrors()){
            model.addAttribute("user",form);
            return "register";
        }
        User c = new User();

        if(form.getId() != null){
            c = users.findById(form.getId()).orElseThrow(() -> new RuntimeException("Not found"));
        }

        if( (users.findByUsername(form.getUsername())!=null) || (users.findByPseudo(form.getPseudo())!=null)){
            model.addAttribute("errorUniciteLoginMail",1);
            return "register";
        }

        c.setUsername(form.getUsername()); //todo deplacer tout ca dans user service genre CreateUser(pseudo,username,password) et faire le metier la dedans
        c.setPseudo(form.getPseudo());
        c.setPassword(getPasswordEncoder().encode(form.getPassword()));
        Authority e = new Authority();
        e.setId(Integer.toUnsignedLong(2));
        c.setAuthorities(new ArrayList<Authority>());
        c.getAuthorities().add(e);
        users.save(c);
        /*
        try {//TODO connexion automatique aprés inscription
            request.login(c.getUsername(),c.getPassword());
        } catch (ServletException servletException) {
            servletException.printStackTrace();
        }
         */
        return "redirect:/login";
    }
}


