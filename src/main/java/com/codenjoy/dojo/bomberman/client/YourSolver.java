package com.codenjoy.dojo.bomberman.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;

import java.util.List;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private BigBrain brain;

    public YourSolver() {
        this.brain = new BigBrain();
    }

    // the method which should be implemented
    @Override
    public String get(Board board) {
        if (board.isMyBombermanDead()) return "";

        return this.brain.nextMove(board);
    }

    private String movesToString(List<String> moves) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (String s : moves) {
            sb.append(s).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');
        return sb.toString();
    }

    /**
     * To connect to the game server:
     * 1. Sign up on the game server. If you did everything right, you'll get to the main game board.
     * 2. Click on your name on the right hand side panel
     * 3. Copy the whole link from the browser, paste it inside below method, now you're good to go!
     */
    public static void main(String[] args) {

        String host = "3.133.109.198";
        String port = "8080";
        String playerId = "g2kzb99qhnjs217fkcyy";
        String code = "3689161262388400247";

        WebSocketRunner.runClient(
                // paste here board page url from browser after registration

                "http://" + host + ":" + port + "/codenjoy-contest/board/player/" + playerId + "?code=" + code,
                new YourSolver(),
                new Board());
    }

}
