package com.company;

import java.util.*;

public class Mj {

    public static final String[] number = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九"};

    public static final String[] color = new String[]{"筒", "索", "萬"};

    public static final String[] special = new String[]{"東風", "南風", "西風", "北風", "紅中", "發財", "白板",
            "春天", "夏天", "秋天", "冬天", "梅花", "蘭花", "竹子", "菊花"};


    private List<String> default_brand;
    private final List<String> sorted_brand = new ArrayList<>();
    private final int method;
    private int gang_num = 0;
    private List<String> ghost_brand;

    // 牌山的初始化
    public Mj(int method) {
        // 川麻
        this.method = method;
        if (this.method == 1) {
            this.default_brand = new ArrayList<>();
            for (int i = 1; i <= color.length; i++) {
                for (int j = 1; j <= number.length; j++) {
                    for (int k = 1; k <= 4; k++) {
                        String val = i + String.valueOf(j);
                        this.default_brand.add(val);
                    }
                }
            }
            Collections.shuffle(this.default_brand);
        }
        // 广麻
        if (this.method == 2) {
            this.default_brand = new ArrayList<>();
            for (int i = 1; i <= color.length; i++) {
                for (int j = 1; j <= number.length; j++) {
                    for (int k = 1; k <= 4; k++) {
                        String val = i + String.valueOf(j);
                        this.default_brand.add(val);
                    }
                }
            }
            for (int i = 1; i <= 7; i++) {
                for (int k = 1; k <= 4; k++) {
                    String val = "4" + i;
                    this.default_brand.add(val);
                }
            }
            Collections.shuffle(this.default_brand); // 将默认排序打乱
        }
//        printDefault_brand();
    }

    public void printDefault_brand() {
        System.out.println("牌山");
        format_display(default_brand);
    }

    public List<String> getDefault_brand() {
        return this.default_brand;
    }

    public void printSorted_brand() {
        System.out.println("发牌顺序牌山");
        format_display(this.sorted_brand);
        System.out.println();
    }

    // 测试用走后门设置鬼牌
    public void setGhost_brand(List<String> ghost_brand) {
        this.ghost_brand = ghost_brand;
    }

    // 获取丢骰子后的牌山
    public List<String> getSorted_brand() {
        return this.sorted_brand;
    }

    // 调整牌山顺序
    public void setSorted_brand(int a, int b) {
        if (this.method == 1) {
            this.sorted_brand.addAll(this.default_brand.subList(13 * a - b, this.default_brand.size())); // 看大
            this.sorted_brand.addAll(this.default_brand.subList(0, 13 * a - b)); // 摸小
            Collections.reverse(this.sorted_brand); // 将牌放到队尾
//          printSorted_brand();
        } else if (this.method == 2) {
            this.sorted_brand.addAll(this.default_brand.subList(17 * a - b, this.default_brand.size())); // 看大
            this.sorted_brand.addAll(this.default_brand.subList(0, 17 * a - b)); // 摸小
            Collections.reverse(this.sorted_brand); // 将牌放到队尾
//            printSorted_brand();
        }

    }

    // 初始发牌
    public void begin(List<Player> playerList) {
        int dice_a = (int) (Math.random() * 6 + 1);
        int dice_b = (int) (Math.random() * 6 + 1);
        System.out.println("投掷结果为 " + dice_a + ", " + dice_b);

        // 定庄
//        int banker = (dice_a + dice_b) % 4;
//        if (banker == 0) banker = 4;
//        System.out.println("庄为" + banker + "号位");

        int direction = Math.max(dice_a, dice_b); // 摸哪边
        int distance = Math.min(dice_a, dice_b); // 摸几栋
        if (direction > 4) {
            direction -= 4;
        }
        setSorted_brand(direction, distance); // 看大摸小
        if (this.method == 2) { // 如果是广麻，增加翻牌定鬼环节
            confirm_ghost();
        }

        // 发牌
        int index = 0;
        while (index < 3) {
            playerList.forEach(player -> {
                player.setHand(this.sorted_brand.subList(0, 4));
                this.sorted_brand.subList(0, 4).clear();
            });
            index += 1;
        } // 每个玩家拿4栋牌
        playerList.forEach(player -> {
            player.setHand(this.sorted_brand.subList(0, 1));
            this.sorted_brand.subList(0, 1).clear();
            System.out.print("玩家" + (playerList.indexOf(player) + 1) + " 初始手牌 ");
            format_display(player.getHand());
        }); // 每人再补充一张成为13张
    }

    // 开始游戏
    public void play(List<Player> playerList) {
        int flag = 0; // 当前位置
        get_brand(playerList, flag);
    }

    // 翻牌确定鬼
    public void confirm_ghost() {
        Collections.reverse(this.sorted_brand);
        List<String> ghost_flag_brand = Collections.singletonList(this.sorted_brand.get(4)); // 用subList和get都会报java.util.ConcurrentModificationException
        this.sorted_brand.subList(4, 5).clear(); // 鬼牌指示牌为倒数第三栋的上面那张，鬼牌为指示牌+1，用大圈，并且排除在可摸牌山之外
        Collections.reverse(this.sorted_brand);
        String flag_brand = ghost_flag_brand.get(0);
        StringBuilder g_brand = new StringBuilder();
        if (flag_brand.charAt(0) == '4') { // 如果是字牌，就只有7类
            if (flag_brand.charAt(1) == '7') { // 如果是47-白板，则鬼牌为东风
                g_brand.append("41");
            } else { // 如果不是47-白板，则鬼牌为+1
                g_brand.append("4");
                g_brand.append((char) (flag_brand.charAt(1) + 1));
            }
        } else { // 如果是普通花色，则有9类
            if (flag_brand.charAt(1) == '9') { // 如果是9万，9筒，9索，则鬼牌为1万，1筒，1索
                g_brand.append(flag_brand.charAt(0));
                g_brand.append('1');
            } else { // 如果不是，则鬼牌为+1
                g_brand.append(flag_brand.charAt(0));
                g_brand.append((char) (flag_brand.charAt(1) + 1));
            }
        }
        this.ghost_brand = new ArrayList<>(Collections.singletonList(g_brand.toString()));
        System.out.println("鬼牌指示牌为 " + format(ghost_flag_brand) + "，加一后鬼牌确定为 " + format(this.ghost_brand));
    }

    // 玩家摸牌
    public void get_brand(List<Player> playerList, int flag) {
        while (this.sorted_brand.size() != 0) {
            List<String> current_brand = Collections.singletonList(this.sorted_brand.get(0)); // 用subList和get都会报java.util.ConcurrentModificationException
            this.sorted_brand.subList(0, 1).clear(); // 从牌顶摸一张
            System.out.print("牌山还剩 " + this.sorted_brand.size() + " 张牌，" + ((method == 2) ? ("鬼牌是 " + format(this.ghost_brand) + " ， ") : "")
                    + "玩家" + (flag + 1) + " 本轮摸到的牌为 ");
            format_display(current_brand);

            Player current_player = playerList.get(flag);
            current_player.setHand(current_brand);
            System.out.print("玩家" + (flag + 1) + " 当前手牌为 ");
            format_display(current_player.getHand());
            System.out.println("当前手牌顺序提示：  1    2     3    4    5    6     7    8    9   10    11   12   13    14");
            System.out.println("当前 玩家" + (flag + 1) + " 展示区为 " + list_format(current_player.getShow()));
            flag = player_operate_brand(playerList, current_player, current_brand, flag);
            // 结束条件：1.某位玩家宣布胡牌，2.牌山的牌被摸完
            if (flag == -1) { // 如果返回-1，则宣布游戏结束
                break;
            }
            flag += 1;
            if (flag > 3) {
                flag = 0;
            }
        }
        System.out.println("游戏结束");
    }

    // 玩家对手牌进行操作
    public int player_operate_brand(List<Player> playerList, Player current_player, List<String> current_brand, int flag) {
        Map<String, Integer> gang_map = check_self_gang(current_player.getHand(), current_player.getShow(), current_player); // 检查玩家当前手牌能否暗杠加杠
        int choice = choose_action(gang_map.size());
        switch (choice) {
            case 1:// 出牌弃牌
                flag = player_forgive_discard(playerList, current_player, flag);
                break;
            case 2: // 暗杠或补杠
                flag = operate_own_gang(playerList, flag, current_brand.get(0), gang_map);
                break;
            case 3: // 胡牌
                flag = end(playerList, current_player);
                break;
        }
        return flag;
    }

    // 玩家弃牌
    public int player_forgive_discard(List<Player> playerList, Player current_player, int flag) {
        System.out.print("请选择要弃牌的牌张（从1开始）：");
        String forgive = current_player.setDiscard(input_num(current_player.getHand().size()));
        System.out.print("丢弃的手牌为 ");
        format_display(Collections.singletonList(forgive));
        System.out.print("玩家" + (flag + 1) + " 当前的手牌为 ");
        format_display(current_player.getHand());
        System.out.println("当前弃牌区及展示区为");
        playerList.forEach(player -> {
            System.out.println("玩家" + (playerList.indexOf(player) + 1) + " 弃牌区");
            format_display(player.getDiscard());
            System.out.println("玩家" + (playerList.indexOf(player) + 1) + " 碰杠区\n" + list_format(player.getShow()));
        });
        System.out.println("****************************************************************************************");
        return check_peng_or_gang(playerList, current_player, forgive, flag); // 玩家打出牌后检查其他玩家手牌是否能开碰或开杠
    }

    // 检查是否能胡牌（仅限自摸）
    public boolean check_win(Player current_player, List<String> current_brand, List<List<String>> show_brand) {
        Set<String> brand_set = new HashSet<>(current_brand); // 手牌去重，用来验证12张落地的情况
        Map<String, Integer> player_hand_map = current_player.getHandSize(); // 获取手牌中每个牌的数量
        List<String> brand_no_ghost = new ArrayList<>(current_brand); // 复制一份手牌的备份
        int ghost_num = 0;
        if (method == 2) {
            ghost_num = player_hand_map.getOrDefault(ghost_brand.get(0), 0); // 获取手牌中鬼的数量
            brand_no_ghost.removeAll(ghost_brand); // 将手牌中的鬼移除
        }
        // 判断大相公和小相公，两种情况都不能胡牌，只有2，5，8，11，14张牌的时候能胡牌
        if ((current_brand.size() + 1) % 3 != 0) {
            return false;
        }
        // 判断四鬼胡，仅限广麻
        if (method == 2 && ghost_num == 4) {
            return true;
        }
        // 判断七对
        if (current_brand.size() == 14 && check_seven_pairs(ghost_num, brand_no_ghost)) {
            return true;
        }
        // 判断十三幺，仅限广麻
        if (method == 2 && current_brand.size() == 14 && checkShisanyao(ghost_num, brand_no_ghost)) {
            return true;
        }
        // 判断碰碰胡12张落地（大对子金钩钓）
        if (current_brand.size() == 2 && brand_set.size() == 1 && show_brand.size() == 4) {
            return true;
        }
        // 判断碰碰胡12张落地并且摸到鬼
        if (method == 2 && current_brand.size() == 2 && show_brand.size() == 4 && current_brand.contains(this.ghost_brand.get(0))) {
            return true;
        }
        // 其他情况的判断
        // 依次删除一对牌做将，其余牌全部成扑则可胡
        for (int i = 0; i < brand_no_ghost.size() - 1; i++) {
            if (i > 0 && brand_no_ghost.get(i).equals(brand_no_ghost.get(i - 1))) {
                // 和上一次是同样的牌，避免重复计算
                continue;
            }
            // 如果下一张牌还没有到结尾并且当前牌和下一张牌相同、或者鬼牌数量仍大于0
            if ((i + 1 < brand_no_ghost.size() && brand_no_ghost.get(i).equals(brand_no_ghost.get(i + 1))) || ghost_num > 0) {
                // 找到对子、或是用一张癞子拼出的对子
                List<String> brand_cp = new ArrayList<>(brand_no_ghost); // 复制一个临时数组
                int pu_ghost = ghost_num; // 复制一个临时鬼牌数量
                // 将当前牌从列表中去除，并且如果下一张牌也一样，则一起清除，作为将牌，否则去掉一张鬼牌作为将牌的补充
                brand_cp.subList(i, i + 1).clear();
                if (brand_cp.get(i).equals(brand_no_ghost.get(i))) {
                    brand_cp.subList(i, i + 1).clear();
                } else {
                    pu_ghost -= 1;
                }
                // 将剩下的牌进行验证看是否能够组成刻子或者顺子
                if (isPu(brand_cp, pu_ghost)) {
                    return true;
                }
            }
        }
        // 如果所有的牌都是单牌，则尝试用两个鬼做将，看其他的牌是否能够组成刻子或顺子
        return ghost_num >= 2 && isPu(brand_no_ghost, ghost_num - 2);
    }

    // 胡牌时检查是否能组成顺子或刻子
    public boolean isPu(List<String> brands, int ghost_num) {
        // 如果已经没有牌了，则证明全部已经配对完成
        if (brands.size() == 0) {
            return true;
        }
        // 先找完所有的顺子
        // 若第一张是顺子中的一张
        // 不计字牌，字牌只能是刻子，不能是顺子
        if (Integer.parseInt(brands.get(0)) < 40) {
            for (int first = Integer.parseInt(brands.get(0)) - 2; first <= Integer.parseInt(brands.get(0)); first++) {
                if (first % 10 > 7 || ghost_num == 0 && first < Integer.parseInt(brands.get(0))) {
                    // 剪枝：顺子第一张牌不会大于7点、无赖子情况下顺子第一张只能用手上的牌
                    continue;
                }
                int shun_count = 0; // 计算顺子数量
                // 如果3个连着的数字在这里都存在，则能组成一个顺子
                for (int i = 0; i < 3; i++) {
                    if (brands.contains(String.valueOf(first + i))) {
                        shun_count += 1;
                    }
                }
                // 如果组成一个顺子的元素等于3或者不足以组成一个顺子，但是加上现有的鬼牌后能够大于等于3组成一个顺子
                if (shun_count == 3 || shun_count + ghost_num >= 3) {
                    // 找到包含第一张牌的顺子
                    List<String> brands_tmp = new ArrayList<>(brands);
                    int ghost_num_tmp = ghost_num;
                    // 遍历列表，假如存在该元素，则将该元素从列表中删除，否则减少一张鬼牌，作为顺子缺失元素的代替
                    for (int i = 0; i < 3; i++) {
                        int delete_pos = brands_tmp.indexOf(String.valueOf(first + i));
                        if (delete_pos >= 0) {
                            brands_tmp.subList(delete_pos, delete_pos + 1).clear();
                        } else {
                            ghost_num_tmp -= 1;
                        }
                    }
                    // 递归调用
                    if (isPu(brands_tmp, ghost_num_tmp)) {
                        return true;
                    }
                }
            }
        }

        // 若第一张是刻子中的一张
        int ke_count = 1;
        int ke_brand = Integer.parseInt(brands.get(0));
        // 假如第二张和第三张都和第一张牌一样
        if (brands.size() > 1 && Integer.parseInt(brands.get(1)) == ke_brand) {
            ke_count += 1;
        }
        if (brands.size() > 2 && Integer.parseInt(brands.get(2)) == ke_brand) {
            ke_count += 1;
        }
        // 同顺子
        if (ke_count == 3 || ke_count + ghost_num >= 3) {
            // 找到包含第一张牌的顺子
            List<String> brands_tmp = new ArrayList<>(brands);
            int ghost_num_tmp = ghost_num;
            for (int i = 0; i < 3; i++) {
                int delete_pos = brands_tmp.indexOf(String.valueOf(ke_brand));
                if (delete_pos >= 0) {
                    brands_tmp.subList(delete_pos, delete_pos + 1).clear();
                } else {
                    ghost_num_tmp -= 1;
                }
            }
            return isPu(brands_tmp, ghost_num_tmp); // 迭代调用
        }
        // 找完所有的顺子和刻子假如都不能结束则返回false
        return false;
    }

    // 检查十三幺是否能够胡牌
    public boolean checkShisanyao(int ghost_num, List<String> brand_no_ghost) {
        int request = 0; // 鬼牌数量需求
        String[] model = new String[]{"11", "19", "21", "29", "31", "39", "41", "42", "43", "44", "45", "46", "47"};
        List<String> model_list = new ArrayList<>(Arrays.asList(model));
        Set<String> brand_no_ghost_set = new HashSet<>(brand_no_ghost);
        // 利用set去重的特性，将将牌变为1个，假如没有鬼，则肯定剩下13张，如果遍历完都能一一对应上（set只剩0张）则能够胡牌
        // 假如有鬼，牌肯定是少于13张，那么有多少个没对应上就有多少个request鬼牌需求，假如鬼牌能满足需求就能胡牌
        for (String i : model_list) {
            if (brand_no_ghost_set.contains(i)) {
                brand_no_ghost_set.remove(i);
            } else {
                request += 1;
            }
        }
        return request - ghost_num <= 0; // 如果需要补充的牌减去鬼数量小于等于0，则证明十三幺能胡牌
    }

    // 检查七对是否能够胡牌（包含豪七）
    public boolean check_seven_pairs(int ghost_num, List<String> brand_no_ghost) {
        int index = 0;
        int single = 0;
        // 遍历数组，如果是能两两配对的，则index+2，否则+1
        while (index < brand_no_ghost.size() - 1) {
            if (brand_no_ghost.get(index).equals(brand_no_ghost.get(index + 1))) {
                index += 2;
            } else {
                // 如果最后两个都不一样，则单牌+2
                if (index + 2 >= brand_no_ghost.size()) {
                    index += 2;
                    single += 2;
                } else {
                    index += 1;
                    single += 1;
                }
            }
        }
        return single - ghost_num <= 0; // 如果单牌数量减去鬼数量小于等于0，则证明七对能配对成功
    }

    // 玩家宣布胡牌
    public int end(List<Player> playerList, Player current_player) {
        boolean isWin = check_win(current_player, current_player.getHand(), current_player.getShow());
        System.out.println("玩家" + (playerList.indexOf(current_player) + 1) + " 宣布和牌，该玩家手牌为 "
                + format(current_player.getHand()) + " ，碰杠区牌为 " + list_format(current_player.getShow())
                + "\n和牌结果为 " + (isWin ? "自摸" : "诈和"));
        System.out.println("玩家" + (playerList.indexOf(current_player) + 1) + (isWin ? " 胜利" : " 诈和赔三家"));
        return -1;
    }

    // 选择别人打出来的牌是否要开碰或开杠
    public int check_peng_or_gang(List<Player> playerList, Player current_player, String forgive, int flag) {
        int index = check_other_gang(playerList, current_player, forgive); // 检查是否能开碰或开杠
        if (index != -1) {
            System.out.print("请 玩家" + ((index + 1) % 5) + " 选择是否要开碰或开杠（1=开碰，2=开杠，3=跳过）：");
            int c = input_num_restrict(3, index);
            if (c == 1) {
                // 选择开碰
                flag = operate_peng(playerList, current_player, index, forgive);
            } else if (c == 2) {
                // 选择开杠
                flag = operate_gang(playerList, index - 5, forgive);
            } else {
                System.out.println("过");
                return flag;
            }
        }
        return flag;
    }

    // 检查自己摸到牌能否暗杠或加杠，返回能杠的牌的列表
    public Map<String, Integer> check_self_gang(List<String> current_hand_brand, List<List<String>> show_brand, Player current_player) {
        Map<String, Integer> player_hand_map = current_player.getHandSize();
        Map<String, Integer> res = new HashMap<>(); // key-value: 牌:1=能暗杠；2=能补杠
        if (this.sorted_brand.size() == 0) { // 最后一张牌不允许杠
            return res;
        }
        player_hand_map.forEach((key, value) -> { // 遍历手牌如果有同样4张的就可以暗杠
            if (value == 4) {
                res.put(key, 1);
            }
        });
        for (List<String> b : show_brand) { // 如果手牌中有1张是在碰杠区存在的牌就可以补杠
            if (current_hand_brand.contains(b.get(0))) {
                res.put(b.get(0), 2);
            }
        }
        return res;
    }

    // 检查某一玩家打牌后是否有人能点杠或点碰
    public int check_other_gang(List<Player> playerList, Player current_player, String brand) {
        for (Player player : playerList) {
            // 防止自己手里有刻子，打出来后自己又碰的情况出现，不能碰自己的牌
            if (player == current_player) {
                continue;
            }
            // 如果存在能碰牌的人，则返回0-3，如果存在能杠牌的人，则返回5-8，否则返回-1，选择+5而不是+4是因为后面要system.out
            Map<String, Integer> player_hand_map = player.getHandSize();
            if (player_hand_map.getOrDefault(brand, -1) == 2) {
                return playerList.indexOf(player);
            } else if (player_hand_map.getOrDefault(brand, -1) == 3 && this.sorted_brand.size() != 0) {
                return playerList.indexOf(player) + 5;
            }
        }
        return -1;
    }

    // 玩家碰牌操作
    public int operate_peng(List<Player> playerList, Player discard_player, int index, String forgive) {
        index %= 5; // 防止index越界
        Player operator_player = playerList.get(index); // 获取当前操作的玩家下标
        discard_player.removeDiscard(); // 将该玩家的手牌移除
        operator_player.setShow(forgive, 1); // 设置该玩家的碰杠区牌
        System.out.print("玩家" + (index + 1) + " 选择开碰，当前手牌为 ");
        format_display(playerList.get(index).getHand());
        System.out.println("当前手牌顺序提示：          1     2    3    4    5    6     7    8    9    10   11");
        System.out.println("当前 玩家" + (index + 1) + " 展示区为 " + list_format(operator_player.getShow()));
        return player_forgive_discard(playerList, operator_player, index); // 碰牌完成后要丢弃手牌
    }

    // 玩家杠牌操作
    public int operate_gang(List<Player> playerList, int index, String brand) {
        index %= 5; // 防止index越界
        Player operator_player = playerList.get(index);
        operator_player.setShow(brand, 2);
        List<String> current_brand = get_brand_after_gang(operator_player);
        System.out.print("玩家" + ((index + 1) % 5) + " 选择开杠，从牌墙末尾摸到的牌为 " + format(current_brand)
                + " ，牌山还剩 " + this.sorted_brand.size() + " 张牌\n" + "玩家" + ((index + 1) % 5) + " 当前手牌为 ");
        format_display(operator_player.getHand());
        System.out.println("当前手牌顺序提示：  1    2     3    4    5    6     7    8    9    10   11");
        System.out.println("当前 玩家" + ((index + 1) % 5) + " 展示区为 " + list_format(operator_player.getShow()));
        return player_operate_brand(playerList, operator_player, current_brand, index); // 杠牌完成后选择接下来的操作
    }

    // 玩家自摸暗杠或加杠操作
    public int operate_own_gang(List<Player> playerList, int index, String brand, Map<String, Integer> canGang) {
        Player operator_player = playerList.get(index);
        List<String> blind_gang = new ArrayList<>(); // 暗杠
        List<String> add_gang = new ArrayList<>(); // 补杠
        // 遍历列表，因为可能同时有多个元素可以杠
        canGang.forEach((key, value) -> {
            if (value == 1) {
                blind_gang.add(key);
            } else if (value == 2) {
                add_gang.add(key);
            }
        });
        String gang_brand = null;
        // 当只有1个可以杠时，就直接杠，不用问
        if (canGang.size() == 1) {
            if (blind_gang.size() == 1) {
                operator_player.setShow(blind_gang.get(0), 2);
                gang_brand = blind_gang.get(0);
            } else if (add_gang.size() == 1) {
                operator_player.setShow(add_gang.get(0), 3);
                gang_brand = add_gang.get(0);
            }
        } else {
            List<String> canGang_list = new ArrayList<>(canGang.keySet());
            System.out.print("当前可暗杠或加杠的牌为 ");
            format_display(canGang_list);
            System.out.println("顺序提示：             1    2    3    4");
            System.out.print("请选择要暗杠或加杠的牌：");
            int input = input_num(canGang_list.size()) - 1;
            if (blind_gang.contains(canGang_list.get(input))) {
                operator_player.setShow(canGang_list.get(input), 2);
            } else if (add_gang.contains(canGang_list.get(input))) {
                operator_player.setShow(canGang_list.get(input), 3);
            }
        }
        List<String> current_brand = get_brand_after_gang(operator_player);
        System.out.print("玩家" + ((index + 1) % 5) + " 选择暗杠/加杠 " + format(new ArrayList<>(Collections.singleton(gang_brand)))
                + " ，从牌墙末尾摸到的牌为 " + format(current_brand) + " ，牌山还剩 " + this.sorted_brand.size() + " 张牌\n"
                + "玩家" + ((index + 1) % 5) + " 当前手牌为 ");
        format_display(operator_player.getHand());
        System.out.println("当前手牌顺序提示：  1    2     3    4    5    6     7    8    9   10    11");
        System.out.println("当前 玩家" + ((index + 1) % 5) + " 展示区为 " + list_format(operator_player.getShow()));
        return player_operate_brand(playerList, operator_player, current_brand, index);
    }

    // 玩家杠后摸牌操作
    public List<String> get_brand_after_gang(Player operator_player) {
        List<String> current_brand;
        if (this.gang_num == 4 && this.method == 2) { // 如果是广麻，并且已经开了4个杠，那么计数器就+1，因为倒数第三栋上面的牌是鬼牌指示牌，不能被摸，这样就不会摸错牌
            this.gang_num += 1;
        }

        Collections.reverse(this.sorted_brand);
        if (this.gang_num % 2 == 0) {
            current_brand = Collections.singletonList(this.sorted_brand.get(1)); // 用subList和get都会报java.util.ConcurrentModificationException
            this.sorted_brand.subList(1, 2).clear(); // 从牌尾摸一张，摸上面的那个
        } else {
            current_brand = Collections.singletonList(this.sorted_brand.get(0)); // 用subList和get都会报java.util.ConcurrentModificationException
            this.sorted_brand.subList(0, 1).clear(); // 从牌尾摸一张，摸下面的那个
        }
        Collections.reverse(this.sorted_brand);
        this.gang_num += 1;
        operator_player.setHand(current_brand);
        return current_brand;
    }


    // 玩家选择操作
    public int choose_action(int canGang) {
        System.out.println("请选择要进行的操作");
        System.out.println("1.弃牌");
        System.out.println("2.暗杠/加杠");
        System.out.println("3.和牌");
        System.out.print("请输入：");
        return input_num_restrict(3, canGang != 0 ? 4 : 1);
    }

    // 检查输入的数据是否符合规则
    public int input_num(int max_input) {
        Scanner input = new Scanner(System.in);
        int num;
        while (true) {
            try {
                num = input.nextInt();
                if (num > max_input || num <= 0) {
                    throw new Exception();
                }
                return num;
            } catch (Exception e) {
                System.out.println("输入错误，请重新输入");
                input = new Scanner(System.in);
            }
        }
    }

    // 检查输入的数据是否符合规则,防止不能杠而输入杠
    public int input_num_restrict(int max_input, int restrict) {
        Scanner input = new Scanner(System.in);
        int num;
        while (true) {
            try {
                num = input.nextInt();
                if (num > max_input || num <= 0 || (restrict < 4 && num == 2)) {
                    throw new Exception();
                }
                return num;
            } catch (Exception e) {
                System.out.println("输入错误或不能开杠，请重新输入");
                input = new Scanner(System.in);
            }
        }
    }


    // 将数字可视化
    public List<String> format_display(List<String> list) {
        List<String> output = new ArrayList<>();
        list.forEach(p -> {
            StringBuilder name = new StringBuilder();
            int num = p.charAt(1) - '0';
            int tp = p.charAt(0) - '0';
            if (tp < 4) {
                name.append(number[num - 1]);
                name.append(color[tp - 1]);
            } else if (tp == 4) {
                name.append(special[num - 1]);
            } else if (tp == 5) {
                name.append(special[num + 7 - 1]);
            }
            output.add(name.toString());
        });
        // 将鬼牌移动到第一位，仅限广麻
//        if (method == 2) {
//            // 获取鬼牌数量
//            Long ghost_num = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).getOrDefault(ghost_brand.get(0), 0L);
//            if (output.contains(format(ghost_brand.get(0)))) {
//                int index = output.indexOf(format(ghost_brand.get(0)));
//                output.subList(index, (int) (index + ghost_num)).clear();
//                for (int i = 0; i < ghost_num; i++) {
//                    output.add(0, format(ghost_brand.get(0)));
//                }
//            }
//        }

        System.out.println(output);
        return output;
    }

    // 将数字可视化
    public void list_format_display(List<List<String>> list) {
        if (list.size() == 0) {
            System.out.println(new ArrayList<Integer>());
        }
        list.forEach(this::format_display);
    }

    // 将数字列表可视化
    public List<List<String>> list_format(List<List<String>> list) {
        List<List<String>> output = new ArrayList<>();
//        List<String> temp = new ArrayList<>();
        list.forEach(l -> {
            output.add(format(l));
        });
        return output;
    }

    // 将数字可视化
    public List<String> format(List<String> list) {
        List<String> output = new ArrayList<>();
        list.forEach(p -> {
            StringBuilder name = new StringBuilder();
            int num = p.charAt(1) - '0';
            int tp = p.charAt(0) - '0';
            if (tp < 4) {
                name.append(number[num - 1]);
                name.append(color[tp - 1]);
            } else if (tp == 4) {
                name.append(special[num - 1]);
            } else if (tp == 5) {
                name.append(special[num + 7 - 1]);
            }
            output.add(name.toString());
        });
        return output;
    }

    // 将数字可视化
    public String format(String str) {
        StringBuilder res = new StringBuilder();
        int num = str.charAt(1) - '0';
        int tp = str.charAt(0) - '0';
        if (tp < 4) {
            res.append(number[num - 1]);
            res.append(color[tp - 1]);
        } else if (tp == 4) {
            res.append(special[num - 1]);
        } else if (tp == 5) {
            res.append(special[num + 7 - 1]);
        }
        return res.toString();
    }

}
