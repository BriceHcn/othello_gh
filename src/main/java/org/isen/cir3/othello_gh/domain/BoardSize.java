package org.isen.cir3.othello_gh.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BoardSize {

    private List<Integer> sizes = new ArrayList();

    public BoardSize(){
        this.sizes.add(8);
        this.sizes.add(6);
        this.sizes.add(4);
    }
}
