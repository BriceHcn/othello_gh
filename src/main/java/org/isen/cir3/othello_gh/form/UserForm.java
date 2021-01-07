package org.isen.cir3.othello_gh.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class UserForm {
    private Long Id;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 20)
    private String pseudo;

    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    private String username;

    @NotNull
    @NotEmpty
    @Size(min = 4, max = 15)
    private String password;

}
