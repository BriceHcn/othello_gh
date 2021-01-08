package org.isen.cir3.othello_gh.controller;
import lombok.Getter;
import lombok.Setter;
import org.isen.cir3.othello_gh.service.AuthorityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Getter
@Setter

@Controller
public class IndexController {
    @GetMapping({"","/"})
    public String index(){



        return "redirect:/game/list";
    }
}


