package com.zc.fivechess;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * TODO
 * 2023-10-16
 * zhangxl
 */
public class FcFactory implements EntityFactory {
    @Spawns("background")
    public Entity background(SpawnData data) {
        ChessGridView chessGridView = new ChessGridView(Constant.cell_size, Constant.cell_w, Constant.cell_w);
        chessGridView.setTranslateX(Constant.left);
        chessGridView.setTranslateY(Constant.top);

        Circle circle_sz = new Circle(Constant.cell_w / 5, Color.rgb(0, 0, 0, .5));
        circle_sz.setTranslateX(Constant.left + Constant.cell_w * 3);
        circle_sz.setTranslateY(Constant.top + Constant.cell_w * 3);

        Circle circle_sy = new Circle(Constant.cell_w / 5, Color.rgb(0, 0, 0, .5));
        circle_sy.setTranslateX(Constant.left + (Constant.cell_size - 3) * Constant.cell_w);
        circle_sy.setTranslateY(Constant.top + Constant.cell_w * 3);

        Circle circle_xz = new Circle(Constant.cell_w / 5, Color.rgb(0, 0, 0, .5));
        circle_xz.setTranslateX(Constant.left + Constant.cell_w * 3);
        circle_xz.setTranslateY(Constant.top + (Constant.cell_size - 3) * Constant.cell_w);

        Circle circle_xy = new Circle(Constant.cell_w / 5, Color.rgb(0, 0, 0, .5));
        circle_xy.setTranslateX(Constant.left + (Constant.cell_size - 3) * Constant.cell_w);
        circle_xy.setTranslateY(Constant.top + (Constant.cell_size - 3) * Constant.cell_w);

        Circle circle_z = new Circle(Constant.cell_w / 5, Color.rgb(0, 0, 0, .5));
        circle_z.setTranslateX(Constant.left + (Constant.cell_size - 1) / 2 * Constant.cell_w);
        circle_z.setTranslateY(Constant.top + (Constant.cell_size - 1) / 2 * Constant.cell_w);

        return entityBuilder(data)
                .view(new Rectangle(getAppWidth(), getAppHeight(), Color.rgb(162, 162, 162, .5)))
                .view(chessGridView)
                .view(circle_sz)
                .view(circle_sy)
                .view(circle_xz)
                .view(circle_xy)
                .view(circle_z)
                .zIndex(-100)
                .build();
    }

    //棋盘点位
    @Spawns("cell")
    public Entity cell(SpawnData data) {
        Rectangle cent = new Rectangle(getAppWidth() - Constant.left * 2,
                getAppHeight() - Constant.top - Constant.bottom,
                Color.rgb(222, 185, 130, .5));

        cent.setTranslateX(Constant.left);
        cent.setTranslateY(Constant.top);

        cent.addEventHandler(MouseEvent.MOUSE_MOVED, e -> FXGL.<FcApplication>getAppCast().mouse_moved(e));
        cent.addEventHandler(MouseEvent.MOUSE_EXITED, e -> FXGL.<FcApplication>getAppCast().mouse_exited());
        cent.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> FXGL.<FcApplication>getAppCast().mouse_clicked(e));

        Entity et = entityBuilder(data)
                .view(cent)
                .zIndex(10)
                .build();

        return et;
    }

    //棋子实体
    @Spawns("piece")
    public Entity piece(SpawnData data) {
        Circle circle = new Circle(Constant.cell_w / 3, FXGL.<FcApplication>getAppCast().getColor());

        return entityBuilder(data)
                .type(FXGL.<FcApplication>getAppCast().getType())
                .view(circle)
                .zIndex(100)
                .neverUpdated()
                .build();
    }

    //鼠标悬浮展示实体
    @Spawns("piece_h")
    public Entity piece_h(SpawnData data) {
        Circle circle = new Circle(Constant.cell_w / 3, FXGL.<FcApplication>getAppCast().getColor());

        return entityBuilder(data)
                .type(EcEntityType.P_H)
                .view(circle)
                .opacity(.8)
                .neverUpdated()
                .build();
    }
}
