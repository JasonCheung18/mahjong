package com.company;

import java.util.Arrays;
import java.util.Collections;

public class Test {
    public static void main(String[] args) {
        Player player_test = new Player();
        Mj mj = new Mj(2);
        mj.setGhost_brand(Collections.singletonList("24"));
        String[] test_brand = new String[] {"11", "11", "11", "12", "13", "14", "15", "16", "17", "18", "19", "19", "19", "13"};
        String[] test_show = new String[] {};
        player_test.setHand(Arrays.asList(test_brand));
        player_test.setShow_cheat(Arrays.asList(test_show));
        System.out.println(mj.check_win(player_test, player_test.getHand(), player_test.getShow()));
    }
}
