package de.ruzman.jmotion;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Gesture;

import de.ruzman.fx.util.TextBox;
import de.ruzman.leap.LeapApp;

public class Pause extends Group {
	private Timeline timeline;
	private Group root;
	private Scene scene;
	private Stage primaryStage;
	
	public Pause(Stage primaryStage, Scene scene, Group root, Timeline timeline) {
		this.primaryStage = primaryStage;
		this.scene = scene;
		this.root = root;
		this.timeline = timeline;
		
		Rectangle rec = new Rectangle(
				LeapApp.getDisplayWidth(),
				LeapApp.getDisplayHeight(), Color.BLACK);
		
		ImageView back = new ImageView(new Image("file:res/img/back.png"));
		back.setFitWidth(LeapApp.getDisplayWidth()/3);
		back.setFitHeight(back.getImage().getHeight()*back.getFitWidth()/back.getImage().getWidth());
		back.setTranslateX(LeapApp.getDisplayWidth()/4-back.getFitWidth()/2);
		back.setTranslateY(LeapApp.getDisplayHeight()*0.9/2-back.getFitHeight()/2);
		
		ImageView repeat = new ImageView(new Image("file:res/img/repeat.png"));
		repeat.setFitWidth(LeapApp.getDisplayWidth()/3);
		repeat.setFitHeight(repeat.getImage().getHeight()*repeat.getFitWidth()/repeat.getImage().getWidth());
		repeat.setTranslateX(LeapApp.getDisplayWidth()/4*3-repeat.getFitWidth()/2);
		repeat.setTranslateY(LeapApp.getDisplayHeight()*0.9/2-repeat.getFitHeight()/2);
		
		TextBox backText = new TextBox("BACK", repeat.getFitWidth()*0.8 , 160);
		backText.setLayoutX(LeapApp.getDisplayWidth()/4-back.getFitWidth()/2);
		backText.setLayoutY(LeapApp.getDisplayHeight()*0.9/2+repeat.getFitHeight()/2);
		backText.getText().setFont(Font.font("Segoue UI", 84));
		backText.getText().setFill(Color.rgb(240, 240, 240));
		
		TextBox repeatText = new TextBox("REPEAT", repeat.getFitWidth()*0.9 , 160);
		repeatText.setLayoutX(LeapApp.getDisplayWidth()/4*3-repeat.getFitWidth()/2);
		repeatText.setLayoutY(LeapApp.getDisplayHeight()*0.9/2+repeat.getFitHeight()/2);
		repeatText.getText().setFont(Font.font("Segoue UI", 84));
		repeatText.getText().setFill(Color.rgb(240, 240, 240));
		
		getChildren().add(root);
		getChildren().add(rec);
		getChildren().add(repeat);
		getChildren().add(back);
		getChildren().add(repeatText);
		getChildren().add(backText);
		rec.setOpacity(0.8);

		LeapApp.getController().config().setFloat("Gesture.Circle.MinRadius", 40f);
		LeapApp.getController().config().save();
		LeapApp.getController().enableGesture(Gesture.Type.TYPE_CIRCLE);
		
		timeline.stop();
		waitForAction();
	}
	
	public void waitForAction() {
		final Timeline timeline = new Timeline();
		final Pause instance = this;
		timeline.setCycleCount(Timeline.INDEFINITE);

		EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
			boolean isFirst = true;
			
			public void handle(ActionEvent t) {
				if(isFirst) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isFirst = false;
				}
				
				for (Gesture gesture : LeapApp.getController().frame()
						.gestures()) {
					if (gesture.type().equals(Gesture.Type.TYPE_CIRCLE)) {
						CircleGesture circleGesture = new CircleGesture(gesture);
						if(circleGesture.pointable().direction().angleTo(circleGesture.normal()) < Math.PI/2) {
							instance.getChildren().clear();
							LeapApp.getController().enableGesture(Gesture.Type.TYPE_CIRCLE, false);
							timeline.stop();
							instance.scene.setRoot(instance.root);
							instance.timeline.play();
						} else {
							LeapApp.getMotionRegistry().removeAllListener();
							LeapApp.getController().enableGesture(Gesture.Type.TYPE_CIRCLE, false);
							timeline.stop();
							new JMotion(primaryStage);
						}
					}
				}
			}
		};

		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1.0 / 60.0), ae));
		timeline.play();
	}
}
