package com.company;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Mj mj = new Mj(2);

        Player player_a = new Player();
        Player player_b = new Player();
        Player player_c = new Player();
        Player player_d = new Player();

        List<Player> playerList = Arrays.asList(player_a, player_b, player_c, player_d);

        mj.begin(playerList);
        mj.play(playerList);
    }
}
