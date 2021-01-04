package org.isen.cir3.othello_gh.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class GameForm {

    //@NotNull
    //@NotEmpty
    //@NotBlank
    //@Size(min = 2, max = 20)
    private Integer opponent;

    private Integer currentUser;

    @Min(4)
    @Max(8)
    private int Size;
}
