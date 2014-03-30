package de.ruzman.jwheelmotion;

import java.awt.Robot;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.leapmotion.leap.Hand;

import de.ruzman.fx.util.Point;
import de.ruzman.jmotion.Pause;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;

public class JWheelMotion extends Application implements PointMotionListener {
	private DoubleProperty wheelAngle = new SimpleDoubleProperty();

	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		Application.launch(JWheelMotion.class, args);
	}

	public JWheelMotion() {
		super();
	}

	public JWheelMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		ImageView wheel = new ImageView(new Image("file:res/img/steering.png"));
		wheel.setTranslateX(LeapApp.getDisplayWidth() / 2
				- wheel.getImage().getWidth() / 2);
		wheel.setTranslateY(LeapApp.getDisplayHeight() / 2
				- wheel.getImage().getHeight() / 2);
		wheel.rotateProperty().bind(wheelAngle);

		final Group root = new Group();
		root.getChildren().add(wheel);

		final Scene scene = new Scene(root);
		scene.setCursor(Cursor.NONE);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});

		primaryStage.setTitle("JWheelMotion");
		primaryStage.getIcons().add(new Image("file:res/img/icon.png"));
		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);
		primaryStage.show();

		LeapApp.setMinimumHandNumber(2);
		LeapApp.setMaximumHandNumber(2);
		LeapApp.getMotionRegistry().addPointMotionListener(this);

		final Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				LeapApp.update();
				if(LeapApp.getController().frame().hands().count() == 0) {
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
		Hand leftHand = LeapApp.getController().frame().hands().leftmost();
		Hand rightHand = LeapApp.getController().frame().hands().rightmost();

		Point point = new Point(leftHand.stabilizedPalmPosition().getX(),
				leftHand.stabilizedPalmPosition().getY());
		Point point2 = new Point(rightHand.stabilizedPalmPosition().getX(),
				rightHand.stabilizedPalmPosition().getY());

		wheelAngle.set(180 - Math.toDegrees(point2.getAngleTo(point)));
	}

	@Override
	public void pointDragged(PointEvent event) {

	}
}
