package com.codenjoy.dojo.bomberman.client;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.RandomDice;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

import java.util.LinkedList;
import java.util.List;

class BigBrain {

    private Dice dice = new RandomDice();

    // Logic goes here
    List<String> nextMoves(Board board) {

        try (Graph g = new Graph()) {
            final String value = "Hello from " + TensorFlow.version();

            // Construct the computation graph with a single operation, a constant
            // named "MyConst" with a value "value".
            try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
                // The Java API doesn't yet include convenience functions for adding operations.
                g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
            }

            // Execute the "MyConst" operation in a Session.
            try (Session s = new Session(g);
                 // Generally, there may be multiple output tensors,
                 // all of them must be closed to prevent resource leaks.
                 Tensor output = s.runner().fetch("MyConst").run().get(0)) {
                System.out.println(new String(output.bytesValue(), "UTF-8"));
            }
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.toString());
        }




        List<String> moves = new LinkedList<>();
        moves.add(Direction.valueOf(this.dice.next(6)).toString());
        return moves;
    }
}
