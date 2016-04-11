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
import afester.javafx.svg.SvgLoader;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;


@Example(desc = "Game example: Driving Car", 
         cat  = "FranzXaver")
public class DrivingCar extends Application {

    private static enum Direction {
        NONE, LEFT,RIGHT, UP, DOWN
    }

    private Rectangle border;
    private Group car;

    // background scene
    private Group scene;
    double sceneWidth;
    double sceneHeight;
    double viewHeight;
    double sceneSpeed = 1.0;

    private Direction currentDirection = Direction.NONE;
    private static final double SPEED = 8.0; // 12.0;


    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage stage) {

        // setup the scene (road)
        InputStream svgFile = getClass().getResourceAsStream("scene.svg");
        SvgLoader loader = new SvgLoader();
        scene = loader.loadSvg(svgFile);
        sceneWidth = scene.getBoundsInParent().getWidth();
        sceneHeight = scene.getBoundsInParent().getHeight();
        viewHeight = sceneHeight / 2;
        scene.setTranslateY(-viewHeight + 10);
        scene.setTranslateX(10);

        // load the car
        svgFile = getClass().getResourceAsStream("Bmw_Z_Top_View_clip_art.svg");
        car = loader.loadSvg(svgFile);
        car.setTranslateY(100);
        car.setTranslateX(200);

        // the border overlays the background scene
        border = new Rectangle(5, 5, sceneWidth, viewHeight + 10);
        border.setFill(null);
        border.setStroke(new Color(0x64 / 255F, 0x83 / 255F, 0x9d / 255F, 1.0));
        border.setStrokeWidth(10);

        // setup timeline  duration is the complete cycle (100000 / 400 = 250 msec!)
        final Duration oneFrameAmt = Duration.millis(1000 / 60);
        final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, e-> handleFrame());
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(oneFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // JavaFX boilerplate - setup stage
        Group mainGroup = new Group();
        mainGroup.getChildren().addAll(scene, car, border);
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
          case UP   : currentDirection = Direction.UP;
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

    
    private Bounds rectBounds;
    private Bounds areaBounds;

    private void handleFrame() {
        //long x1 = System.currentTimeMillis();

        // Update geometric bounds of all objects
        rectBounds = car.getBoundsInParent();
        areaBounds = scene.getBoundsInParent();

        handlePlatform();

        handleScene();
    }

    private void handleScene() {
        double pos = scene.getTranslateY();
        if (pos > 9) {
            pos = -viewHeight + 10;
        } else {
            pos += sceneSpeed;
        }
        scene.setTranslateY(pos);
    }

    private void handlePlatform() {

        switch (currentDirection) {
          case NONE : break;

          case LEFT :
          {
              double newPos = car.getTranslateX() - SPEED;
              if (newPos < areaBounds.getMinX()) {
                  newPos = areaBounds.getMinX();
              }
              car.setTranslateX(newPos);
          }
              break;

          case RIGHT: 
          {
              double newPos = car.getTranslateX() + SPEED;
              if (newPos > areaBounds.getMaxX() - rectBounds.getWidth()) {
                  newPos = areaBounds.getMaxX() - rectBounds.getWidth();
              }
              car.setTranslateX(newPos);
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
}
