package org.isen.cir3.othello_gh.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class GameForm {

    @NotNull
    private int opponent;

    @NotNull
    private int currentUser;

    @Min(4)
    @Max(8)
    private int Size;
}
