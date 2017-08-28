/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package afester.javafx.examples.animation.game;

import afester.javafx.examples.Example;
import afester.javafx.tools.KeyStateManager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


@Example(desc = "Game example: Bouncing ball", 
         cat  = "FranzXaver")
public class BouncingBall extends Application {

    private static enum Direction { NONE, LEFT,RIGHT, UP, DOWN }

    private Rectangle rect = new Rectangle(60, 8);
    private Circle ball = new Circle(10);
    private Rectangle border;

    double sceneWidth = 640;
    //double sceneHeight = 480;
    double viewHeight = 480;
    double sceneSpeed = 1.0;

    private Direction currentDirection = Direction.NONE;
    private static final double SPEED = 8.0; // 12.0;
    private static final double STARTANGLE_VARIANCE = Math.toRadians(45);   // +/- 45deg

    // ball vector
    private double ballAngle = Math.toRadians(300); // 315);
    private double ballSpeed = 4.0;
    double dx = 0;
    double dy = 0;

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }


    @Override
    public void start(Stage stage) {

        dx = ballSpeed * Math.cos(ballAngle);
        dy = ballSpeed * Math.sin(ballAngle);

        // create rectangle to move
        rect.setFill(Color.BLACK);
        rect.setTranslateY(200);
        rect.setTranslateX(180);

        // the border overlays the background scene
        border = new Rectangle(5, 5, sceneWidth, viewHeight + 10);
        border.setFill(null);
        border.setStroke(new Color(0x64 / 255F, 0x83 / 255F, 0x9d / 255F, 1.0));
        border.setStrokeWidth(10);

        // create ball
        resetBall();

        // setup timeline  duration is the complete cycle (100000 / 400 = 250 msec!)
        final Duration oneFrameAmt = Duration.millis(1000 / 60);
        final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, e-> handleFrame());
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(oneFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // JavaFX boilerplate - setup stage
        Group mainGroup = new Group();
        mainGroup.getChildren().addAll(ball, rect, border);
        Scene mainScene = new Scene(mainGroup, sceneWidth + 10, viewHeight + 10);

        KeyStateManager km = new KeyStateManager(mainScene);
        km.setOnKeyChangeEvent( e -> handleKey(e.getKeyCode()) );

        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();
    }

    private void handleKey(KeyCode key) {
        if (key == null) {
            currentDirection = Direction.NONE;
            return;
        }

        switch (key) {
          case UP : currentDirection = Direction.UP;  
            break;

          case DOWN : currentDirection = Direction.DOWN;
            break;

          case LEFT : currentDirection = Direction.LEFT;
            break;

          case RIGHT: currentDirection = Direction.RIGHT;
            break;

          case SPACE : // system.err.println("ACTION");
              break;

          default : break;
        }
    }

    
    /**
     * Calculates the exit angle for the bouncing ball.
     * Rationale:
     * <ul>
     *   <li>We only need to consider two quadrants (180deg) - otherwise the
     *       ball moves away from the barrier</li>
     *   <li>The angle of the barrier is subtracted and then re-added</li>
     *   <li>For the reference (barrier) angle, we also only need to consider
     *       two quadrants (0 - 180deg) - the value is properly calculated!  
     * </ul> 
     * @param old   The current angle of the ball.
     * @param reference The angle of the barrier which bounces the ball.
     *
     * @return The new angle of the ball.
     */
    private double calculateExitAngle(double old, double reference) {
        double normAngle = old - reference;
        if (normAngle < 0) {
            normAngle = normAngle + 2 * Math.PI;
        }

        double newAngle = 2 * Math.PI - normAngle;
        return newAngle + reference;
    }

    private boolean isExploding = false;

    private void resetBall() {
        ball.setFill(Color.BLUE);
        ball.setTranslateX(border.getX() + border.getWidth() / 2);

        double min = Math.toRadians(270) - STARTANGLE_VARIANCE;
        double max = Math.toRadians(270) + STARTANGLE_VARIANCE;
        ballAngle = Math.random() * (max - min) + min;
        dx = ballSpeed * Math.cos(ballAngle);
        dy = ballSpeed * Math.sin(ballAngle);

        // Note: translation point of circle is its middle point!
        ball.setTranslateY(border.getY() + ball.getRadius());
    }


    private Bounds ballBounds;
    private Bounds rectBounds;
    private Bounds areaBounds;

    private void handleFrame() {
        //long x1 = System.currentTimeMillis();

        // Update geometric bounds of all objects
        ballBounds = ball.getBoundsInParent();
        rectBounds = rect.getBoundsInParent();
        areaBounds = border.getBoundsInParent();

        handlePlatform();

        if (isExploding) {
            handleExplode();
        } else {
            advanceBall();
        }

        //handleScene();

        //long x2 = System.currentTimeMillis();
        // system.err.println(x2 - x1);
    }


    private void handlePlatform() {

        switch (currentDirection) {
          case NONE : 
              break;

          case LEFT : {
              double newPos = rect.getTranslateX() - SPEED;
              if (newPos < areaBounds.getMinX()) {
                  newPos = areaBounds.getMinX();
              }
              rect.setTranslateX(newPos);
          }
              break;

          case RIGHT: 
          {
              double newPos = rect.getTranslateX() + SPEED;
              if (newPos > areaBounds.getMaxX() - rectBounds.getWidth()) {
                  newPos = areaBounds.getMaxX() - rectBounds.getWidth();
              }
              rect.setTranslateX(newPos);
          }
              break;

          case UP :
              sceneSpeed += 0.1;
              if (sceneSpeed > 10) {
                  sceneSpeed = 10;
              }
              break;

          case DOWN :
              sceneSpeed -= 0.1;
              if (sceneSpeed < 0) {
                  sceneSpeed = 0;
              }
              break;

          default : break;
        }
    }


    private void handleExplode() {
        if (ballBounds.getMinY() > areaBounds.getMaxY()) {
            isExploding = false;
            resetBall();
        } else {
            ball.setTranslateX(ball.getTranslateX() + dx);
            ball.setTranslateY(ball.getTranslateY() - dy);
        }
    }


    private void advanceBall() {
        double refAngle = 0;
        boolean doBounce = false;
        double jitter = 0;

        if (ballBounds.getMaxY() >= rectBounds.getMinY()) {
            if (   ballBounds.getMaxX() < rectBounds.getMinX() 
                || ballBounds.getMinX() > rectBounds.getMaxX()) {
                isExploding = true;
                ball.setFill(Color.RED);
            } else {
                // calculate a value between -0.5 and 0.5 which corresponds to the
                // position within the platform where the ball bounces
                double pos = (ball.getTranslateX() - rect.getTranslateX()) / rect.getWidth();
                if (pos < 0) {
                    pos = 0;
                }
                if (pos > 1) {
                    pos = 1;
                }
                pos = pos - 0.5;

                // system.err.printf("%s\n", pos);
                jitter = pos;
                doBounce = true;
            }
        }

        if (   ballBounds.getMinY() <= areaBounds.getMinY()
            || ballBounds.getMaxY() >= areaBounds.getMaxY()) {   // this must never happen!
            doBounce = true;
        } else if (   ballBounds.getMinX() <= areaBounds.getMinX() 
                   || ballBounds.getMaxX() >= areaBounds.getMaxX()) {
            doBounce = true;
            refAngle = Math.PI / 2;
        }

        if (doBounce) {
            ballAngle = calculateExitAngle(ballAngle, refAngle) + jitter;
            dx = ballSpeed * Math.cos(ballAngle);
            dy = ballSpeed * Math.sin(ballAngle);
        }

        ball.setTranslateX(ball.getTranslateX() + dx);
        ball.setTranslateY(ball.getTranslateY() - dy);
    }

}
