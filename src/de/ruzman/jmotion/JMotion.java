package de.ruzman.jmotion;

import java.awt.Robot;
import java.net.URL;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;

public class JMotion extends Application implements PointMotionListener {
	private Robot robot;
	
	public static void main(String[] args) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		Application.launch(JMotion.class, args);
	}
	
	public JMotion() {}
	
	public JMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final Timeline timeline = new Timeline();
		
		try {
			robot = new Robot();
			
			FXMLLoader loader = new FXMLLoader(new URL("file:res/fxml/JMotion.fxml"));
			VBox vbox = (VBox) loader.load();
			CostumCursor cursor = new CostumCursor(primaryStage, timeline);
			Scene scene = new Scene(new StackPane(vbox, cursor));
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					System.exit(0);	
				}
			});
			
			GridPane gridPane = (GridPane) vbox.getChildren().get(1);
			gridPane.setPrefHeight(LeapApp.getDisplayHeight()-150);
			
			if(LeapApp.getDisplayHeight() >= 1000) {
				Button sin = new Button("JSinuousMotion");
				sin.setId("sinuous");
				sin.setPrefSize(350, 250);
				gridPane.add(sin, 0, 2);
				
				Button track = new Button("JTrackMotion");
				track.setId("track");
				track.setPrefSize(350, 250);
				gridPane.add(track, 1, 2);
			}
			
			cursor.registerScene(scene);
			cursor.registerButtons(gridPane);
			
			primaryStage.setTitle("JMotion");
			primaryStage.getIcons().add(new Image("file:res/img/icon.png"));			
			primaryStage.setScene(scene);
			primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			primaryStage.setFullScreen(true);
			primaryStage.show();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        timeline.setCycleCount(Timeline.INDEFINITE);

        LeapApp.setMinimumHandNumber(0);
		LeapApp.setMaximumHandNumber(1);
		LeapApp.getMotionRegistry().addPointMotionListener(this);
		
        EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
		        LeapApp.update();
           }
        };
 
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0/60.0), ae));
        timeline.play();
        
		new Robot().mouseMove(0, 0);
	}
	
	@Override
	public void pointMoved(PointEvent event) {
		robot.mouseMove((int) event.getX(), (int) event.getY());
	}

	@Override
	public void pointDragged(PointEvent event) {
		// Do nothing.
	}
}
