package com.zc.fivechess;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 * 2023-11-24
 * zhangxl
 */
public class Constant {
    public static int top = 40;
    public static int left = 25;
    public static int right = 25;
    public static int bottom = 25;
    public static int cell_size = 19;
    public static int cell_w = 15;

    // 0表示白子，1表示黑子，_表示空位
    public static Map<String, Integer> AttackMap = new HashMap<>();
    public static Map<String, Integer> DefendMap = new HashMap<>();

    static {
        // 初始化攻击策略Map
        initAttackMap();

        // 初始化防御策略Map
        initDefendMap();
    }

    private static void initAttackMap() {
        AttackMap.put("11111", 9999999);
        AttackMap.put("_1111_", 100000);

        AttackMap.put("1111__", 80000);
        AttackMap.put("111_1_", 80000);
        AttackMap.put("11_11_", 80000);
        AttackMap.put("1_111_", 80000);
        AttackMap.put("__1111", 80000);
        AttackMap.put("_1_111", 80000);
        AttackMap.put("_11_11", 80000);
        AttackMap.put("_111_1", 80000);

        AttackMap.put("01111_", 80000);
        AttackMap.put("0111_1", 80000);
        AttackMap.put("011_11", 80000);
        AttackMap.put("01_111", 80000);
        AttackMap.put("0_1111", 80000);
        AttackMap.put("_11110", 80000);
        AttackMap.put("1_1110", 80000);
        AttackMap.put("11_110", 80000);
        AttackMap.put("111_10", 80000);
        AttackMap.put("1111_0", 80000);

        AttackMap.put("1_1_1_1", 50000);
        AttackMap.put("1_11_1", 60000);
        AttackMap.put("11_11", 70000);

        AttackMap.put("_111_", 50000);

        AttackMap.put("__111", 25000);
        AttackMap.put("1__11", 25000);
        AttackMap.put("11__1", 25000);
        AttackMap.put("111__", 25000);
        AttackMap.put("1_11_", 25000);
        AttackMap.put("11_1_", 25000);

        AttackMap.put("0111__", 25000);
        AttackMap.put("__1110", 25000);

        AttackMap.put("11___", 5);
        AttackMap.put("_11__", 100);
        AttackMap.put("__11_", 100);
        AttackMap.put("___11", 5);

        AttackMap.put("01____", 5);
        AttackMap.put("0_1___", 1);
        AttackMap.put("0__1__", 1);
        AttackMap.put("0___1_", 1);
        AttackMap.put("0____1", 1);

        AttackMap.put("0_111", 0);
        AttackMap.put("01_11", 0);
        AttackMap.put("011_1", 0);
        AttackMap.put("0111_", 0);
        AttackMap.put("_0111", 0);
        AttackMap.put("00111", 0);
        AttackMap.put("1110_", 0);
        AttackMap.put("111_0", 0);
        AttackMap.put("11100", 0);
        AttackMap.put("01___0", 0);
        AttackMap.put("0_1__0", 0);
        AttackMap.put("0__1_0", 0);
        AttackMap.put("0___10", 0);
    }

    private static void initDefendMap() {
        DefendMap.put("10000", 200000);
        DefendMap.put("01000", 200000);
        DefendMap.put("00100", 200000);
        DefendMap.put("00010", 200000);
        DefendMap.put("00001", 200000);

        DefendMap.put("_0001_", 70000);
        DefendMap.put("_0010_", 70000);
        DefendMap.put("_0100_", 70000);
        DefendMap.put("_1000_", 70000);

        DefendMap.put("_0001", 60000);
        DefendMap.put("_0010", 60000);
        DefendMap.put("_0100", 60000);
        DefendMap.put("_1000", 60000);

        DefendMap.put("_100_", 40000);
        DefendMap.put("_010_", 40000);
        DefendMap.put("_001_", 40000);

        DefendMap.put("011_011", 15000);
        DefendMap.put("_01_01_", 10000);

        DefendMap.put("0_11_1_0", 20000);
        DefendMap.put("0_111_0", 25000);

        DefendMap.put("01___", 3);
        DefendMap.put("_01__", 3);
        DefendMap.put("__01_", 3);
        DefendMap.put("___01", 3);

        DefendMap.put("10___", 3);
        DefendMap.put("_10__", 3);
        DefendMap.put("__10_", 3);
        DefendMap.put("___10", 3);
    }
}
