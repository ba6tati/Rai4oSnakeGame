package com.snakegame;

import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class TurnProcessor {
    private final SnakeNodeManager snakeNodeManager = new SnakeNodeManager();
    private final MainScene mainScene;

    private final ImageView apple;
    private final Parent head;
    private final CounterController counterController;
    private final SnakeHeadController headController;

    public TurnProcessor(MainScene mainScene) {
        this.mainScene = mainScene;
        this.headController = this.mainScene.getHeadController();
        snakeNodeManager.setRoot(mainScene.getRoot());
        apple = this.mainScene.getApple();
        head = this.mainScene.getHead();
        counterController = this.mainScene.getCounterController();
    }
    private boolean isAppleEaten() {
        return (head.getLayoutX() == apple.getLayoutX() && head.getLayoutY() == apple.getLayoutY());
    }
    private boolean isCollidingWithBorder(){
        return head.getLayoutX() < 0 || head.getLayoutX() >= 600 || head.getLayoutY() < 80 || head.getLayoutY() >= 680;
    }
    private boolean containsPos(double[] pos) {
        return snakeNodeManager.getPositions().stream()
                .anyMatch(arr -> Arrays.equals(arr, pos));
    }

    private void changeAppleLocation() {
        Random rand = new Random();
        double x = rand.nextInt(9) * 60;
        double y = rand.nextInt(1, 10) * 60 + 20;

        while (containsPos(new double[]{x, y})) {
            x = rand.nextInt(9) * 60;
            y = rand.nextInt(1, 10) * 60 + 20;
        }
        apple.setLayoutX(x);
        apple.setLayoutY(y);
    }
    public boolean processTurn() throws IOException {

        head.setLayoutX(Math.round((head.getLayoutX() + 60 * headController.getX()) / 10.0f)*10);
        head.setLayoutY(Math.round((head.getLayoutY() + 60 * headController.getY()) / 10.0f)*10);

        snakeNodeManager.addFirstPosition(new double[]{head.getLayoutX(), head.getLayoutY()});
        snakeNodeManager.setCurrHeadDirection(headController.getDir());

        if (!snakeNodeManager.updateNodes() || isCollidingWithBorder()){
            head.setLayoutX(head.getLayoutX() - 60 * headController.getX());
            head.setLayoutY(head.getLayoutY() - 60 * headController.getY());
            mainScene.gameOver();
            return false;
        }

        if (isAppleEaten()) {
            changeAppleLocation();
            counterController.updateScore();
            snakeNodeManager.spawnNode();
        }
        return true;
    }

}
