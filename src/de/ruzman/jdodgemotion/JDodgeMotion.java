package de.ruzman.jdodgemotion;

import java.awt.Robot;

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
import de.ruzman.leap.event.PointMotionListener;

public class JDodgeMotion extends Application implements PointMotionListener {

	private Box2DWorld box2DWorld;

	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		Application.launch(JDodgeMotion.class, args);
	}

	public JDodgeMotion() {}

	public JDodgeMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		final Group root = new Group();
		box2DWorld = new Box2DWorld(root);

		final Scene scene = new Scene(root);
		scene.setCursor(Cursor.NONE);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});
		
		primaryStage.setTitle("JDodgeMotion");
		primaryStage.getIcons().add(new Image("file:res/img/icon.png"));
		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);
		primaryStage.show();

		LeapApp.setMinimumHandNumber(1);
		LeapApp.setMaximumHandNumber(1);
		LeapApp.getMotionRegistry().addPointMotionListener(this);
		
		final Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

		EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				LeapApp.update();
				box2DWorld.getWorld().step(1f / 60, 8, 3);
				if (box2DWorld.drawAndUpadte()) {
					scene.setRoot(new Pause(primaryStage, scene, root, timeline));
				}
			}
		};

		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1.0 / 60.0), ae));
		timeline.play();

		new Robot().mouseMove(0, 0);
	}

	@Override
	public void pointMoved(PointEvent event) {
		box2DWorld.movePlayerTo(event.getX(), event.getY());
	}

	@Override
	public void pointDragged(PointEvent event) {
	}
}
