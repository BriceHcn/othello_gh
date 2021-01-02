package org.isen.cir3.othello_gh.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "authority")
public class Authority implements GrantedAuthority {
    @Id
    @Column
    @GeneratedValue
    private Long id;

    private String authority;
}
