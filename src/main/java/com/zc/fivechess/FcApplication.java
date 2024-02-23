package com.zc.fivechess;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.zc.fivechess.Constant.AttackMap;
import static com.zc.fivechess.Constant.DefendMap;

/**
 * 五子棋
 * 2023-11-16
 * zhangxl
 */
public class FcApplication extends GameApplication {
    public static void main(String[] args) {
        launch(args);
    }

    int top = Constant.top;
    int left = Constant.left;
    int right = Constant.right;
    int bottom = Constant.bottom;
    int cell_size = Constant.cell_size;
    int cell_w = Constant.cell_w;

    ChessCell[][] cells = new ChessCell[cell_size][cell_size];

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("zc五子棋");
        gameSettings.setVersion("");
        gameSettings.setWidth(left + (cell_size - 1) * cell_w + right);
        gameSettings.setHeight(top + (cell_size - 1) * cell_w + bottom);
        gameSettings.setAppIcon("zc.png");
    }

    @Override
    public void initGame() {
        getGameWorld().addEntityFactory(new FcFactory());

        spawn("background");
        spawn("cell");

        for (int ic = 0; ic < cells.length; ic++) {
            ChessCell[] cell = cells[ic];
            for (int i = 0; i < cell.length; i++) {
                cell[i] = new ChessCell(ic, i);
            }
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        set("isOver", false);

        vars.put("wb", true);

        set("nowAiIsRun", false);// AI 正在下
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.ENTER, () -> {
            if (getb("isOver")) {
                set("isOver", false);
                getGameController().startNewGame();
            }
        });
    }

    /**
     * 初始化UI界面
     */
    @Override
    protected void initUI() {
        // 创建一个矩形对象，表示上一次落子的位置
        Rectangle lastP = new Rectangle(Constant.cell_w / 3 * 2 + 3, Constant.cell_w / 3 * 2 + 3);
        lastP.setFill(Color.rgb(255, 255, 255, .0));
        lastP.setStroke(Color.rgb(255, 0, 0, .8));
        lastP.setLayoutX(-100);
        lastP.setLayoutY(-100);

        // 创建一个文本对象，用于显示当前落子的颜色
        Text text = new Text();
        text.setFill(Color.BLACK);
        text.setFont(new Font(16));
        text.setLayoutX(getAppWidth() / 2.0 - 50);
        text.setLayoutY(20);
        text.setOpacity(.6);

        text.setText("当前落子：" + getColorStr());

        set("dqlz", text);
        set("lastP", lastP);

        addUINode(text);
        addUINode(lastP);
    }


    /**
     * 鼠标移动事件处理函数
     *
     * @param e 鼠标事件对象
     */
    public void mouse_moved(MouseEvent e) {
        if (getb("isOver")) return;
        // 如果现在是AI运行状态，则不执行任何操作
        if (getb("nowAiIsRun")) return;

        // 获取鼠标位置
        Point2D xy = getXY(e);
        int x = (int) xy.getX();
        int y = (int) xy.getY();

        // 获取鼠标位置上的棋子类型
        EcEntityType type = cells[x][y].getType();

        // 鼠标位置上的点位为空
        if (type == EcEntityType.EMPTY) {
            // 获取游戏世界中是否已经存在 鼠标悬浮展示实体
            Optional<Entity> optional = getGameWorld().getSingletonOptional(EcEntityType.P_H);

            // 鼠标坐标转棋盘坐标
            Point2D pxy = new Point2D(xy.getX() * cell_w + left, xy.getY() * cell_w + top);

            // 如果已经存在鼠标悬浮展示实体，则将其位置设置为当前鼠标位置
            if (optional.isPresent()) {
                optional.get().setPosition(pxy);
            } else {
                spawn("piece_h", pxy);
            }
        } else {
            mouse_exited();
        }
    }

    /**
     * 鼠标离开事件处理方法
     */
    public void mouse_exited() {
        if (getb("isOver")) return;
        // 如果现在正在运行AI，则直接返回
        if (getb("nowAiIsRun")) return;

        // 鼠标悬浮展示实体
        Optional<Entity> optional = getGameWorld().getSingletonOptional(EcEntityType.P_H);
        // 如果实体存在，则将其从世界中移除
        if (optional.isPresent()) {
            getGameWorld().getSingleton(EcEntityType.P_H).removeFromWorld();
        }
    }

    /**
     * 鼠标点击事件
     *
     * @param e 鼠标事件对象
     */
    public void mouse_clicked(MouseEvent e) {
        if (getb("isOver")) return;
        if (getb("nowAiIsRun")) return;

        if (e.getButton() == MouseButton.PRIMARY) {
            Point2D xy = getXY(e);
            int x = (int) xy.getX();
            int y = (int) xy.getY();

            EcEntityType type = cells[x][y].getType();

            if (type == EcEntityType.EMPTY) {
                mouse_exited();

                downChess(cells[x][y]);

                if (!getb("isOver")) {
                    long l = System.currentTimeMillis();
                    //System.out.println("================================");
                    aiRun();
                    //System.out.println("AI耗时：" + (System.currentTimeMillis() - l) + " ms");
                }
            }
        }
    }

    /**
     * 下棋函数
     *
     * @param cell 棋子对象
     */
    void downChess(ChessCell cell) {
        if (getb("isOver")) return;

        // 设置棋子类型
        cell.setType(getType());

        // 计算棋子在屏幕上的位置
        Point2D pxy = new Point2D(cell.getX() * cell_w + left, cell.getY() * cell_w + top);

        // 在屏幕上生成棋子
        spawn("piece", new SpawnData(pxy).put("cell", cell));

        // 检查游戏是否结束
        checkIsOver(cell);

        // 更新上一次落子位置标识
        Rectangle lastP = geto("lastP");
        lastP.setLayoutX(pxy.getX() - lastP.getWidth() / 2);
        lastP.setLayoutY(pxy.getY() - lastP.getHeight() / 2);

        // 如果游戏未结束，则交换黑白棋子状态
        if (!getb("isOver")) {
            set("wb", !getb("wb"));
            Text dqlz = geto("dqlz");
            dqlz.setText("当前落子：" + getColorStr());
        }
    }

    // 当前落子类型
    public EcEntityType getType() {
        EcEntityType wbt = EcEntityType.B;

        if (getb("wb")) {
            wbt = EcEntityType.W;
        }
        return wbt;
    }

    String getTypeStr(ChessCell cell) {
        switch (cell.getType()) {
            case EMPTY -> {
                return "_";
            }
            case W -> {
                return "0";
            }
            case B -> {
                return "1";
            }
        }

        return "_";
    }

    // 当前落子颜色
    public Color getColor() {
        Color color = Color.BLACK;

        if (getb("wb")) {
            color = Color.WHITE;
        }
        return color;
    }

    // 当前落子颜色
    public String getColorStr() {
        String str = "黑";

        if (getb("wb")) {
            str = "白";
        }
        return str;
    }

    // 鼠标坐标转单元格坐标
    Point2D getXY(MouseEvent e) {
        int x = (int) Math.round((e.getX()) / Constant.cell_w);
        int y = (int) Math.round((e.getY()) / Constant.cell_w);

        return new Point2D(x, y);
    }

    // 落子后 校验
    void checkIsOver(ChessCell cell) {
        isOver(getPointStr(getLineX(cell))); // X轴
        isOver(getPointStr(getLineY(cell))); // Y轴
        isOver(getPointStr(getDiagonalR(cell))); // 左上 -> 右下
        isOver(getPointStr(getDiagonalL(cell))); // 右上 -> 左下
    }

    // 是否结束
    void isOver(String str) {
        boolean isOver = false;
        String user = "";
        if (str.contains("00000")) {
            isOver = true;
            user = "白";
        }

        if (str.contains("11111")) {
            isOver = true;
            user = "黑";
        }

        if (isOver) {
            set("isOver", true);
            System.out.println("Game Over");

            Text dqlz = geto("dqlz");
            dqlz.setFill(Color.RED);
            dqlz.setText("胜者: " + user + "，回车下一局，Esc 退出");
            dqlz.setLayoutX(50);
            dqlz.setOpacity(1);

            //getDialogService().showConfirmationBox("胜者: " + user + "\n下一局?", yes -> {
            //    if (yes) {
            //        set("isOver", false);
            //        getGameController().startNewGame();
            //    } else
            //        getGameController().exit();
            //});
        }
    }


    /**
     * 获取传入点位的类型字符串
     *
     * @param cellAll 点位列表
     * @return
     */
    String getPointStr(List<ChessCell> cellAll) {
        String collect = cellAll.stream().map(c -> getTypeStr(c)).collect(Collectors.joining(""));
        return collect;
    }

    /**
     * 获取指定棋子前后各四个
     *
     * @param cell 指定的棋子
     * @return 所有棋子的列表
     */
    List<ChessCell> getLineX(ChessCell cell) {
        List<ChessCell> chessAll = new ArrayList<>();

        int x = cell.getX();
        int y = cell.getY();

        // 左侧四个
        for (int i = 4; i > 0; i--) {
            if (x - i < 0) {
                continue;
            }
            ChessCell chessCell = cells[x - i][y];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }
        // 将指定棋子加入列表
        chessAll.add(cell);

        // 右侧四个
        for (int i = 1; i < 5; i++) {
            if (x + i > (Constant.cell_size - 1)) {
                break;
            }
            ChessCell chessCell = cells[x + i][y];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }

        return chessAll;
    }

    /**
     * 获取指定棋子上下各四个
     *
     * @param cell 指定的棋子
     * @return 垂直线上的所有棋子列表
     */
    List<ChessCell> getLineY(ChessCell cell) {
        List<ChessCell> chessAll = new ArrayList<>();

        int x = cell.getX();
        int y = cell.getY();

        for (int i = 4; i > 0; i--) {
            if (y - i < 0) {
                continue;
            }
            ChessCell chessCell = cells[x][y - i];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }
        chessAll.add(cell);

        for (int i = 1; i < 5; i++) {
            if (y + i > (Constant.cell_size - 1)) {
                break;
            }
            ChessCell chessCell = cells[x][y + i];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }

        return chessAll;
    }

    // 落子后 左上 -> 右下 点位分布
    List<ChessCell> getDiagonalR(ChessCell cell) {
        List<ChessCell> chessAll = new ArrayList<>();

        int x = cell.getX();
        int y = cell.getY();

        for (int i = 4; i > 0; i--) {
            if (x - i < 0 || y - i < 0) {
                continue;
            }
            ChessCell chessCell = cells[x - i][y - i];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }
        chessAll.add(cell);

        for (int i = 1; i < 5; i++) {
            if (x + i > (Constant.cell_size - 1) || y + i > (Constant.cell_size - 1)) {
                break;
            }
            ChessCell chessCell = cells[x + i][y + i];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }

        return chessAll;
    }

    // 落子后 右上 -> 左下 点位分布
    List<ChessCell> getDiagonalL(ChessCell cell) {
        List<ChessCell> chessAll = new ArrayList<>();

        int x = cell.getX();
        int y = cell.getY();

        for (int i = 4; i > 0; i--) {
            if (x + i > (Constant.cell_size - 1) || y - i < 0) {
                continue;
            }
            ChessCell chessCell = cells[x + i][y - i];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }
        chessAll.add(cell);

        for (int i = 1; i < 5; i++) {
            if (x - i < 0 || y + i > (Constant.cell_size - 1)) {
                break;
            }
            ChessCell chessCell = cells[x - i][y + i];
            chessCell.setSort(i);
            chessAll.add(chessCell);
        }

        return chessAll;
    }

    /**
     * AI落子
     */
    void aiRun() {
        set("nowAiIsRun", true);

        // 初始化所有棋盘点位的分数
        Arrays.stream(cells).parallel().forEach(ce ->
                Arrays.stream(ce).parallel().forEach(ChessCell::initScore)
        );

        // 获取已落子点位
        List<ChessCell> collect = Arrays.stream(cells).parallel().map(ce ->
                Arrays.stream(ce).parallel()
                        .filter(c -> c.getType() != EcEntityType.EMPTY)
                        .collect(Collectors.toList())
        ).flatMap(Collection::stream).collect(Collectors.toList());

        // 获取可得分位置（已落子周围的空点位）
        List<ChessCell> collect1 = collect.parallelStream().map(cell -> {
                    List<ChessCell> lst = new ArrayList<>();

                    lst.addAll(getLineX(cell));// X轴
                    lst.addAll(getLineY(cell)); // Y轴
                    lst.addAll(getDiagonalR(cell)); // 左上 -> 右下
                    lst.addAll(getDiagonalL(cell)); // 右上 -> 左下

                    return lst;
                }).flatMap(Collection::stream)
                .filter(c -> c.getType() == EcEntityType.EMPTY)
                .collect(Collectors.toList());

        // 评估空点位得分
        collect1.forEach(c -> c.setScore(Math.abs(calcAttackScore(c) - calcDefendS(c))));

        // 选择得分最高的棋子落子
        ChessCell chessCell = collect1.stream().max(Comparator.comparing(ChessCell::getScore)).get();

        downChess(chessCell);

        set("nowAiIsRun", false);
    }

    /**
     * 计算cell点位的进攻得分
     *
     * @param cell
     * @return 棋子的得分
     */
    int calcAttackScore(ChessCell cell) {
        cell.setType(getType());
        int score = 0;

        score += convertStrToScore(getPointStr(getLineX(cell)), AttackMap);// X轴
        score += convertStrToScore(getPointStr(getLineY(cell)), AttackMap); // Y轴
        score += convertStrToScore(getPointStr(getDiagonalR(cell)), AttackMap); // 左上 -> 右下
        score += convertStrToScore(getPointStr(getDiagonalL(cell)), AttackMap); // 右上 -> 左下

        cell.setType(EcEntityType.EMPTY);
        return score;
    }

    /**
     * 计算cell点位的防守得分
     *
     * @param cell
     * @return 防守得分
     */
    int calcDefendS(ChessCell cell) {
        cell.setType(getType());
        int score = 0;

        score += convertStrToScore(getPointStr(getLineX(cell)), DefendMap);// X轴
        score += convertStrToScore(getPointStr(getLineY(cell)), DefendMap); // Y轴
        score += convertStrToScore(getPointStr(getDiagonalR(cell)), DefendMap); // 左上 -> 右下
        score += convertStrToScore(getPointStr(getDiagonalL(cell)), DefendMap); // 右上 -> 左下

        cell.setType(EcEntityType.EMPTY);
        return score;
    }

    /**
     * 将给定字符串中包含的关键字转换为指定类型（攻击或防御）的得分
     *
     * @param str     输入字符串，可能包含关键字
     * @param typeMap 关键字与对应得分的映射表，例如Constant.AttackMap或Constant.DefendMap
     * @return 根据输入字符串中包含的关键字在映射表中计算出的总得分
     */
    int convertStrToScore(String str, Map<String, Integer> typeMap) {
        int score = 0;
        for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
            // 检查输入字符串是否包含当前关键字，如果包含则累加对应得分
            if (str.contains(entry.getKey())) {
                score += entry.getValue();
            }
        }
        return score;
    }
}
