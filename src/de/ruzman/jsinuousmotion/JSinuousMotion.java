package de.ruzman.jsinuousmotion;

import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

import de.ruzman.fx.util.Point;
import de.ruzman.fx.util.PolyBezier;
import de.ruzman.jmotion.Pause;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.TrackingBox;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;

public class JSinuousMotion extends Application implements PointMotionListener {

	private FXWorld fxWorld;
	private TrackingBox trackingBox = new TrackingBox(new Vector(180, 180, 2),
			new Vector(-100, 150, 1));
	private Group root;
	private List<Point> points = new ArrayList<>();
	private List<PolyBezier> beziers = new ArrayList<>();
	private Line line = new Line();

	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);

		Application.launch(JSinuousMotion.class, args);
	}

	public JSinuousMotion() {
		super();
	}

	public JSinuousMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		root = new Group();
		fxWorld = new FXWorld(root);
		root.getChildren().add(line);
		root.getChildren().add(fxWorld.getScore());

		final Scene scene = new Scene(root);
		scene.setFill(Color.rgb(58, 58, 58));
		scene.setCursor(Cursor.NONE);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});

		primaryStage.setTitle("JSinuousMotion");
		primaryStage.getIcons().add(new Image("file:res/img/icon.png"));
		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);
		primaryStage.show();

		final Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

		LeapApp.setMinimumHandNumber(1);
		LeapApp.setMaximumHandNumber(1);
		
		EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
			private boolean nextCircle;
			private boolean clearNext;

			public void handle(ActionEvent t) {
				if (clearNext) {
					fxWorld.clear();
					clearNext = false;
				}
				LeapApp.update();
				if (fxWorld.drawAndUpadte()) {
					scene.setRoot(new Pause(primaryStage, scene, root, timeline));
					clearNext = true;
				} else {
					if (!nextCircle && fxWorld.getCirclesSize() <= 40) {
						root.getChildren().add(fxWorld.createCircle());
						nextCircle = true;
					} else {
						nextCircle = false;
					}
				}
			}
		};

		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1.0 / 30.0), ae));
		timeline.play();

		LeapApp.getMotionRegistry().addPointMotionListener(this);
		
		new Robot().mouseMove(0, 0);
	}

	@Override
	public void pointMoved(PointEvent event) {
		Hand hand = LeapApp.getController().frame()
				.hand(event.getSource().id());
		Finger finger = hand.fingers().get(0);
		Vector position = new Vector();
		trackingBox
				.calcScreenPosition(finger.stabilizedTipPosition(), position);

		fxWorld.movePlayerTo(position.getX(), position.getY());

		Point[] stockArr = new Point[points.size()];
		points.toArray(stockArr);

		if (beziers.isEmpty()) {
			points.add(new Point(position.getX(), position.getY()));
			beziers.add(new PolyBezier((Point[]) points.toArray(stockArr)));
			root.getChildren().add(beziers.get(0));
		} else if (points.get(points.size() - 1).getDistanceTo(
				new Point(position.getX(), position.getY())) > 10) {
			points.add(new Point(position.getX(), position.getY()));
			PolyBezier bezier = new PolyBezier(stockArr);
			beziers.add(bezier);
			root.getChildren().add(bezier);

			while (beziers.size() > 4) {
				points.remove(0);
				root.getChildren().remove(beziers.get(0));
				beziers.remove(0);
				root.requestLayout();
			}

			fxWorld.getScore().setText(
					"Score: "
							+ (1 + Integer.parseInt(fxWorld.getScore()
									.getText().substring(7))));
		}
	}

	@Override
	public void pointDragged(PointEvent event) {
	}
}
