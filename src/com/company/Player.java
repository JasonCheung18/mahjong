package com.company;

import com.sun.javafx.scene.control.skin.IntegerFieldSkin;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Player {
    private final List<String> hand = new ArrayList<>();  // 手牌
    public List<List<String>> show = new ArrayList<>(); // 碰杠牌
    public List<String> discard = new ArrayList<>(); // 弃牌

    public Player(){

    }

    public void setHand(List<String> hand) {
        this.hand.addAll(hand);
    }

    public List<String> getHand() {
        Collections.sort(hand);
        return hand;
    }

    // 获取手牌中每个牌的类型及数量
    public Map<String, Integer> getHandSize() {
        Map<String, Integer> hand_map = new HashMap<>();
        hand.forEach(h -> {
            if (!hand_map.containsKey(h)) {
                hand_map.put(h, 1);
            } else {
                hand_map.put(h, hand_map.get(h) + 1);
            }
        });
        return hand_map;
    }

    public List<List<String>> getShow() {
        return show;
    }

    // 设置玩家碰杠牌后手牌及碰杠区牌
    public void setShow(String cards, int choice) {
        List<String> shows = new ArrayList<>();
        if (choice == 1) { // 碰
            this.hand.remove(cards); // 不能用removeAll，可能有刻子但是只选择了碰牌
            this.hand.remove(cards);
            shows.add(cards);
            shows.add(cards);
            shows.add(cards);
            this.show.add(shows);
        } else if (choice == 2) { // 明杠及暗杠
            this.hand.removeAll(Collections.singleton(cards));
            shows.add(cards);
            shows.add(cards);
            shows.add(cards);
            shows.add(cards);
            this.show.add(shows);
        } else if (choice == 3) {  // 加杠
            this.hand.remove(cards);
            for (List<String> list : this.show) {
                if (list.get(0).equals(cards)) {
                    list.add(cards);
                }
            }
        }
    }

    // 测试用走后门设置碰杠区
    public void setShow_cheat(List<String> list) {
        this.show.add(list);
    }

    public List<String> getDiscard() {
        return discard;
    }

    // 玩家选择弃牌后将牌丢入弃牌区
    public String setDiscard(int position) {
        String forgive = hand.get(position - 1);
        hand.remove(position - 1);
        discard.add(forgive);
        return forgive;
    }

    // 假如有人选择碰杠，则将该玩家刚刚在弃牌区的牌移除
    public void removeDiscard() {
        discard.remove(discard.size() - 1);
    }

}
