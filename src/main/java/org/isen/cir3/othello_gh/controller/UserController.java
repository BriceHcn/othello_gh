package org.isen.cir3.othello_gh.controller;

import lombok.Getter;
import lombok.Setter;
import org.isen.cir3.othello_gh.domain.Authority;
import org.isen.cir3.othello_gh.domain.User;
import org.isen.cir3.othello_gh.form.UserForm;
import org.isen.cir3.othello_gh.repository.AuthorityRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.isen.cir3.othello_gh.service.AuthorityService;
import org.isen.cir3.othello_gh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;


@Getter
@Setter

@Controller
public class UserController {
    @Autowired
    private UserRepository users;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityRepository authorities;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder;

    @Autowired
    private AuthorityService authService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(@PathVariable(required = false)Long id, Model model) {
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
    public String addAction(@Valid @ModelAttribute("user") UserForm form, BindingResult result, Model model, HttpServletRequest request) {
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


        authService.createBaseAuth();

        c.setUsername(form.getUsername());
        c.setPseudo(form.getPseudo());
        c.setPassword(getPasswordEncoder().encode(form.getPassword()));
        Authority e = new Authority();
        e.setId(Integer.toUnsignedLong(2));
        e.setAuthority("ROLE_USER");
        c.setAuthorities(new ArrayList<Authority>());
        c.getAuthorities().add(e);
        users.save(c);

        //autologin
        userService.authWithHttpServletRequest(request, c.getUsername(),form.getPassword());
        return "redirect:/";
    }

    @GetMapping("/become/admin")
    public String becomeAdmin(Model model, RedirectAttributes attribs){
        User user =users.findByUsername(userService.getConnectedUserUsername());
        Authority e = new Authority();
        e.setId(Integer.toUnsignedLong(1));
        e.setAuthority("ROLE_ADMIN");
        user.getAuthorities().add(e);
        users.save(user);
        attribs.addFlashAttribute("message", "tu est maintenant administrateur du site \uD83D\uDE0E ,le changement sera effectif a ta prochaine connexion (Attention, de grands pouvoirs impliquent de grandes responsabilités) ");
        return "redirect:/game/list";
    }
}
