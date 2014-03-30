package de.ruzman.jmotion;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcToBuilder;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathBuilder;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import de.ruzman.fx.util.TextBox;
import de.ruzman.jdodgemotion.JDodgeMotion;
import de.ruzman.jgrabmotion.JGrabMotion;
import de.ruzman.jhandmotion.JHandMotion;
import de.ruzman.jrunmtion.JRunMotion;
import de.ruzman.jsinuousmotion.JSinuousMotion;
import de.ruzman.jtrackmotion.JTrackMotion;
import de.ruzman.jwheelmotion.JWheelMotion;
import de.ruzman.jzonemotion.JZoneMotion;
import de.ruzman.leap.LeapApp;

public class DelayProgress extends Region {
	private AnimationTimer timer;
	private double progress;

	private final TextBox textBox;
	private Timeline timeline;

	public DelayProgress(final Timeline timeline, final Stage primaryStage, final Node node, final int delay, final double centerX,
			final double centerY) {
		super();

		this.timeline = timeline;
		
		textBox = new TextBox("0 %", 140, 140);
		textBox.setLayoutX(centerX - 70);
		textBox.setLayoutY(centerY - 70);
		textBox.getText().setFont(Font.font("Segoue UI", 25));
		textBox.getText().setFill(Color.WHITE);

		getChildren().add(
				new Circle(centerX, centerY, 70, Color.rgb(106, 106, 106)));
		getChildren().add(
				createCircle(centerX, centerY, 0, progress * 360, 70,
						0, Color.rgb(130, 210, 230)));
		getChildren().add(
				createCircle(centerX, centerY, 0, progress * 360, 70,
						60, Color.rgb(60, 150, 195)));
		getChildren().add(new Path());
		getChildren().add(new Path());
		getChildren().add(textBox);

		timer = new AnimationTimer() {
			private long lasttime = 0;
			private int ms;

			@Override
			public void handle(long now) {
				if (progress >= 1.0f) {
					progress = 1.0f;
					timer.stop();
					changeScene(primaryStage, node);
				} else if (now > lasttime + 100000000 / 2) {
					ms += 5;
					progress = (float) ms / delay;
					lasttime = now;

					updateText();
					if (progress <= 0.5f) {
						getChildren().set(
								1,
								createCircle(centerX, centerY, 0,
										progress * 360, 70, 0,
										Color.rgb(130, 210, 230)));
						getChildren().set(
								2,
								createCircle(centerX, centerY, 0,
										progress * 360, 70, 60,
										Color.rgb(60, 150, 195)));
					} else {
						getChildren().set(
								3,
								createCircle(centerX, centerY, 180,
										progress * 360, 70, 0,
										Color.rgb(130, 210, 230)));
						getChildren().set(
								4,
								createCircle(centerX, centerY, 180,
										progress * 360, 70, 60,
										Color.rgb(60, 150, 195)));
					}
				}
			}
		};
		timer.start();
	}

	public void cancel() {
		timer.stop();
	}
	
	private void changeScene(Stage primaryStage, Node node) {
		LeapApp.getMotionRegistry().removeAllListener();
		timeline.stop();
		
		switch(node.getId()) {
			case "dodge": new JDodgeMotion(primaryStage); break;
			case "sinuous": new JSinuousMotion(primaryStage); break;
			case "hand": new JHandMotion(primaryStage); break;
			case "wheel": new JWheelMotion(primaryStage); break;
			case "track": new JTrackMotion(primaryStage); break;
			case "run": new JRunMotion(primaryStage); break;
			case "grab": new JGrabMotion(primaryStage); break;
			case "zone": new JZoneMotion(primaryStage); break;
		}
	}

	private void updateText() {
		textBox.setText((int) (progress * 100) + "%");
	}

	private Path createCircle(double centerX, double centerY,
			double degreeStart, double degreeEnd, double innerRadius,
			double outerRadius, Color color) {
		double angleAlpha = degreeStart * (Math.PI / 180);
		double angleAlphaNext = degreeEnd * (Math.PI / 180);

		double pointX1 = centerX + innerRadius * Math.sin(angleAlpha);
		double pointY1 = centerY - innerRadius * Math.cos(angleAlpha);

		double pointX2 = centerX + outerRadius * Math.sin(angleAlpha);
		double pointY2 = centerY - outerRadius * Math.cos(angleAlpha);

		double pointX3 = centerX + outerRadius * Math.sin(angleAlphaNext);
		double pointY3 = centerY - outerRadius * Math.cos(angleAlphaNext);

		double pointX4 = centerX + innerRadius * Math.sin(angleAlphaNext);
		double pointY4 = centerY - innerRadius * Math.cos(angleAlphaNext);

		// TODO: Old Code:
		Path path = PathBuilder
				.create()
				.fill(color)
				.stroke(color)
				.elements(
						new MoveTo(pointX1, pointY1),
						new LineTo(pointX2, pointY2),
						ArcToBuilder.create()
								.radiusX(outerRadius).radiusY(outerRadius)
								.x(pointX3).y(pointY3).sweepFlag(true).build(),
						new LineTo(pointX4, pointY4),
						ArcToBuilder.create()
								.radiusX(innerRadius).radiusY(innerRadius)
								.x(pointX1).y(pointY1).sweepFlag(false).build())
				.build();
		return path;
	}
}
