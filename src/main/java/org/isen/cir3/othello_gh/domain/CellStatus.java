package org.isen.cir3.othello_gh.domain;

public enum CellStatus {
    EMPTY(""), B("B"), W("W");

    public final String string;

    private CellStatus(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
