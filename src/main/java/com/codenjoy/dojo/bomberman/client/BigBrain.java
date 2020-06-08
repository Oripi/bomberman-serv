package com.codenjoy.dojo.bomberman.client;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.RandomDice;

import java.util.LinkedList;
import java.util.List;

class BigBrain {

    private Dice dice = new RandomDice();

    // Logic goes here
    List<String> nextMoves(Board board) {
        List<String> moves = new LinkedList<>();
        moves.add(Direction.valueOf(this.dice.next(6)).toString());
        return moves;
    }
}
