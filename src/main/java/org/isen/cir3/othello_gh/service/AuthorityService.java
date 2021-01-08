package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.Authority;
import org.isen.cir3.othello_gh.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository auths;

    public void createBaseAuth(){

        Optional<Authority> authA = auths.findById(Integer.toUnsignedLong(1));
        if (authA.isEmpty()){

            Authority e = new Authority();
            e.setId(1L);
            e.setAuthority("ROLE_ADMIN");
            auths.save(e);

        }
        Optional<Authority> authU = auths.findById(Integer.toUnsignedLong(2));
        if (authU.isEmpty()){
            Authority i = new Authority();
            i.setId(2L);
            i.setAuthority("ROLE_USER");
            auths.save(i);
        }
    }
}
