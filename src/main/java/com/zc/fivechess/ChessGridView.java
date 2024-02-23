package com.zc.fivechess;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.shape.Line;

/**
 * TODO
 * 2023-11-24
 * zhangxl
 */
public class ChessGridView extends Parent {
    public ChessGridView(int size, int cellWidth, int cellHeight) {
        Group linesGroup = new Group();
        for (int x = 0; x < size; x++) {
            var lineX = new Line(x * cellWidth, 0, x * cellWidth, (size - 1) * cellWidth);
            var lineY = new Line(0, x * cellHeight, (size - 1) * cellWidth, x * cellHeight);

            linesGroup.getChildren().add(lineX);
            linesGroup.getChildren().add(lineY);
        }

        getChildren().addAll(linesGroup);
    }

}
