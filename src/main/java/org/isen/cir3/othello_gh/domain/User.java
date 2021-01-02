package org.isen.cir3.othello_gh.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {
    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column(unique = true, length = 100,nullable = false)
    private String pseudo;

    @Column(unique = true,length = 100,nullable = false)
    private String username;

    @Column(length = 100,nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Authority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
