package de.ruzman.jhandmotion;

import java.awt.Robot;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.ruzman.jmotion.Pause;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointListener;

public class JHandMotion extends Application implements PointListener {
	private FXWorld fxWorld;
	private Map<Integer, Boolean> hands = new HashMap<>();

	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);

		Application.launch(JHandMotion.class, args);
	}

	public JHandMotion() {
		super();
	}

	public JHandMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		final Group root = new Group();
		fxWorld = new FXWorld(root);

		final Scene scene = new Scene(root);
		scene.setCursor(Cursor.NONE);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});
		
		primaryStage.setTitle("JHandMotion");
		primaryStage.getIcons().add(new Image("file:res/img/icon.png"));
		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);
		primaryStage.show();

		LeapApp.setMinimumHandNumber(0);
		LeapApp.setMaximumHandNumber(2);
		LeapApp.getMotionRegistry().addPointListener(this);

		final Random random = new Random();
		final Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
			private int count = 0;
			private boolean isFirst = true;

			public void handle(ActionEvent t) {
				if(isFirst) {
					fxWorld.resetScore();
					isFirst = false;
				}
				
				LeapApp.update();
				fxWorld.drawAndUpadte();
				count = ++count % 50;
				if (count == 49) {
					if (fxWorld.canCreateMore() && random.nextBoolean()) {
						fxWorld.createHand(true);
					}
					if (fxWorld.canCreateMore() && random.nextBoolean()) {
						fxWorld.createHand(false);
					}
				}
				if (fxWorld.isOver()) {
					scene.setRoot(new Pause(primaryStage, scene, root, timeline));
					isFirst = true;
				}
			}
		};
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1.0 / 60.0), ae));
		timeline.play();
        
		new Robot().mouseMove(0, 0);
	}

	@Override
	public void zoneChanged(PointEvent event) {
		if (!hands.containsKey(event.getSource().id())) {
			hands.put(event.getSource().id(), false);
		}

		if (!hands.get(event.getSource().id()) && event.isInClickZone()) {
			if ((event.getX() < LeapApp.getDisplayWidth() / 2 && fxWorld
					.checkLeft()) || fxWorld.checkRight()) {
				fxWorld.addScore(1);
			}
			hands.put(event.getSource().id(), true);
		} else if (!event.isInClickZone()) {
			hands.put(event.getSource().id(), false);
		}
	}
}
