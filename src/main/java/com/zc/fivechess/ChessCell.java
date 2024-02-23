package com.zc.fivechess;

import com.almasb.fxgl.core.collection.grid.Cell;

/**
 * TODO
 * 2023-11-24
 * zhangxl
 */
public class ChessCell extends Cell {
    private EcEntityType type;
    private int score = 0;
    private int defendS = 0;//防守分
    private int sort = 0;

    public ChessCell(int x, int y) {
        super(x, y);
        this.type = EcEntityType.EMPTY;
    }

    public ChessCell(int x, int y, EcEntityType type) {
        super(x, y);
        this.type = type;
    }


    public String toString() {
        return "Cell(" + getX() + "," + getY() + "," + getType() + ")";
    }


    public EcEntityType getType() {
        return type;
    }

    public void setType(EcEntityType type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void initScore() {
        this.score = 0;
        this.defendS = 0;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getDefendS() {
        return defendS;
    }

    public void setDefendS(int defendS) {
        this.defendS = defendS;
    }
}
