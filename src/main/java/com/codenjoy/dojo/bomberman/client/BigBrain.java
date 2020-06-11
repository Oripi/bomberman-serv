package com.codenjoy.dojo.bomberman.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;

import java.util.*;

import static com.codenjoy.dojo.services.PointImpl.pt;

class BigBrain {

    private Dice dice = new RandomDice();

    // Logic goes here
    String nextMove(Board board) {
        Move[] moves = Move.class.getEnumConstants();
        HashMap<Move, Double> scoresMap = new HashMap<>();
        for (int i = 0; i < moves.length; i++) {
            Move m = moves[i];
            scoresMap.put(m, calculateScore(board, m));
        }
        Map.Entry<Move, Double> maxEntry = null;
        for (Map.Entry<Move, Double> entry : scoresMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        System.out.println("selected move: " + maxEntry.getKey().getValue());
        return maxEntry.getKey().getValue();
    }

    private double calculateScore(Board board, Move m) {
        System.out.println("calculateScore of move: " + m.getValue());
        Point bm = board.getBomberman();
        Point newBm = bm.copy();
        switch (m) {
            case LEFT:
            case ACT_LEFT:
                newBm.setX(bm.getX() - 1);
                break;
            case RIGHT:
            case ACT_RIGHT:
                newBm.setX(bm.getX() + 1);
                break;
            case UP:
            case ACT_UP:
                newBm.setY(bm.getY() + 1);
                break;
            case DOWN:
            case ACT_DOWN:
                newBm.setY(bm.getY() - 1);
                break;
        }

        double score = 0;
        // ----------------------Barrier---------------------
        if (board.isBarrierAt(newBm)) {
            score += -1000000f;
            System.out.println("isBarrierAt -1000000");
        }

        // ----------------------FUTURE BLASTS---------------------
        Collection<Point> futureBlasts = getFutureBlasts(board);
        double blastsScore = futureBlasts.stream().mapToDouble(fb -> {
            double dist = dist(newBm, fb);
            if (dist <= 3 && futureBlasts.contains(newBm)) {
                return -4000f * dist;
            } else {
                return 0f;
            }
        }).sum();
        System.out.println("blastsScore: " + blastsScore);
        score += blastsScore;


//        boolean isInBlastRange = futureBlasts.contains(newBm); // need to make sure that "isEqual" is implemented and filtered
//        if (isInBlastRange) {
//            score += -4000f * ; // could be the score (50) with multiplier
//            System.out.println("isInBlastRange: " + isInBlastRange + ", -1000");
//        }

        // ----------------------Enemy---------------------
        Collection<Point> otherBms = board.getOtherBombermans();
        double othersScore = otherBms.stream().mapToDouble(obm -> {
            double dist = dist(newBm, obm);
            if (dist > 0) {
                return 1000f / Math.pow(dist, 2);
            } else {
                return 1000f;
            }
        }).sum();
        System.out.println("othersScore: " + othersScore);
        score += othersScore;

        // ----------------------Monsters---------------------
        Collection<Point> monsters = board.getMeatChoppers();
        double monstersScore = monsters.stream().mapToDouble(monster -> {
            double dist = dist(newBm, monster);
            if (dist <= 2f) {
                return -50f / dist;
            } else {
                return 100f / (dist * dist);
            }
        }).sum();
        System.out.println("monstersScore: " + monstersScore);
        score += monstersScore;

        // ----------------------Walls---------------------
        Collection<Point> walls = board.getDestroyableWalls();
        double wallsScore = walls.stream().mapToDouble(wall -> {
            double dist = dist(newBm, wall);
            return (dist < 2f ? 10f : 0f);
        }).sum();
        System.out.println("wallsScore: " + wallsScore);
        score += wallsScore;


        switch(m) {
            case ACT:
            case ACT_LEFT:
            case ACT_RIGHT:
            case ACT_UP:
            case ACT_DOWN:
                score += 10;
        }

//        // ----------------------Not getting stuck---------------------
        Collection<Point> neighbours = new LinkedList<>();
        neighbours.add(new PointImpl(newBm.getX() + 1, newBm.getY()));
        neighbours.add(new PointImpl(newBm.getX() - 1, newBm.getY()));
        neighbours.add(new PointImpl(newBm.getX(), newBm.getY() + 1));
        neighbours.add(new PointImpl(newBm.getX(), newBm.getY() - 1));
        switch (m) {
            case LEFT:
            case RIGHT:
            case UP:
            case DOWN:
                neighbours.removeIf(bm::equals);
        }
        if (board.getBarriers().containsAll(neighbours)) {
            System.out.println("will be stuck -10000");
            score += -10000f;
        }

        // ----------------------Final score---------------------
        System.out.println("final score: " + score);
        return score;

    }

    private double dist(Point p1, Point p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    private Collection<Point> getFutureBlasts(Board board) {
        Collection<Point> bombs = board.getBombs();
        HashSet<Point> result = new HashSet<>();
        for (Point bomb : bombs) {
            result.add(bomb);
            result.add(pt(bomb.getX() - 1, bomb.getY()));
            result.add(pt(bomb.getX() - 2, bomb.getY()));
            result.add(pt(bomb.getX() - 3, bomb.getY()));
            result.add(pt(bomb.getX() + 1, bomb.getY()));
            result.add(pt(bomb.getX() + 2, bomb.getY()));
            result.add(pt(bomb.getX() + 3, bomb.getY()));
            result.add(pt(bomb.getX(), bomb.getY() - 1));
            result.add(pt(bomb.getX(), bomb.getY() - 2));
            result.add(pt(bomb.getX(), bomb.getY() - 3));
            result.add(pt(bomb.getX(), bomb.getY() + 1));
            result.add(pt(bomb.getX(), bomb.getY() + 2));
            result.add(pt(bomb.getX(), bomb.getY() + 3));
        }
        Collection<Point> result2 = new LinkedList<>();
        for (Point blast : result) {
            if (blast.isOutOf(board.size()) || board.getWalls().contains(blast)) {
                continue;
            }
            result2.add(blast);
        }
        return result2;
    }


}

enum Move {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    UP("UP"),
    DOWN("DOWN"),
    STOP("STOP"),
    ACT("ACT"),
    ACT_LEFT("(LEFT,ACT)"),
    ACT_RIGHT("(RIGHT,ACT)"),
    ACT_UP("(UP,ACT)"),
    ACT_DOWN("(DOWN,ACT)");

    private String value;

    Move(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}