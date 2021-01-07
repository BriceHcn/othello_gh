package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.User;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    @PostFilter("filterObject.username != authentication.principal.username")//ca ne marche pas donc en attendant on utilises les fonctions du bas :(
    public List<User> getAllUserExceptCurrent(UserRepository users) {
        return users.findAll();
    }

    public String getConnectedUserUsername(){
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    public List<User> getAllUserExceptCurrentTemp(UserRepository users) {
        List<User> listWithoutSaidUser=new ArrayList<User>(users.findAll());
        for(User u : users.findAll()){
            if(u.getUsername().equals(getConnectedUserUsername())){
               listWithoutSaidUser.remove(u);
            }
        }
        return listWithoutSaidUser;
    }

    public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) {
            System.out.println(("Error while login "+ e));
        }
    }
}
