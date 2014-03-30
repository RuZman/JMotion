package de.ruzman.jgrabmotion;

import java.awt.Robot;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.leapmotion.leap.Hand;

import de.ruzman.fx.util.TextBox;
import de.ruzman.jmotion.Pause;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;

public class JGrabMotion extends Application implements PointMotionListener {
	private static final Image hand5 = new Image("file:res/img/hand5.png");
	private static final Image hand6 = new Image("file:res/img/hand6.png");
	
	private static final Image grab0 = new Image("file:res/img/grab0.png");
	private static final Image grab1 = new Image("file:res/img/grab1.png");
	
	private ObjectProperty<Image> image = new SimpleObjectProperty<>(grab0);
	private ObjectProperty<Image> inputImage = new SimpleObjectProperty<>();
	
	private DoubleProperty handX = new SimpleDoubleProperty();
	private DoubleProperty handY = new SimpleDoubleProperty();
	
	private ImageView grabView;
	private ImageView hand;
	
	private Rectangle recStar;
	private Rectangle recHelix;
	private Rectangle recTriangle;
	
	private boolean isGrabbing;
	private int prevFingerCount;
	private int inputNumber;
	
	private int score;
	
	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		Application.launch(JGrabMotion.class, args);
	}

	public JGrabMotion() {
		super();
	}

	public JGrabMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		grabView = new ImageView();
		grabView.imageProperty().bind(image);
		grabView.setTranslateX(LeapApp.getDisplayWidth() / 2
				- grabView.getImage().getWidth() / 2);
		grabView.setTranslateY(LeapApp.getDisplayHeight() / 2
				- grabView.getImage().getHeight() / 2);

		setRandomInputImage();
		
		ImageView input = new ImageView();
		input.imageProperty().bind(inputImage);
		input.translateXProperty().bind(grabView.translateXProperty());
		input.translateYProperty().bind(grabView.translateYProperty());
		
		hand = new ImageView(hand5);
		hand.translateXProperty().bind(handX);
		hand.translateYProperty().bind(handY);	
		
		recStar = new Rectangle(LeapApp.getDisplayWidth()/4, LeapApp.getDisplayHeight()/4);
		recStar.setFill(Color.rgb(240, 240, 240));
		recStar.setTranslateX(LeapApp.getDisplayWidth() - recStar.getWidth());
		recStar.setTranslateY(LeapApp.getDisplayHeight()/3);
		
		recTriangle = new Rectangle(LeapApp.getDisplayWidth()/4, LeapApp.getDisplayHeight()/5);
		recTriangle.setFill(Color.rgb(240, 240, 240));
		recTriangle.setTranslateX(LeapApp.getDisplayWidth()/2-recTriangle.getWidth()/2);
		recTriangle.setTranslateY(LeapApp.getDisplayHeight()-recTriangle.getHeight());
		
		recHelix = new Rectangle(LeapApp.getDisplayWidth()/4, LeapApp.getDisplayHeight()/4);
		recHelix.setFill(Color.rgb(240, 240, 240));
		recHelix.setTranslateY(LeapApp.getDisplayHeight()/3);
		
		TextBox textTriangle = new TextBox("Dreieck", recTriangle.getWidth(), recTriangle.getHeight());
		textTriangle.setLayoutX(recTriangle.getTranslateX());
		textTriangle.setLayoutY(recTriangle.getTranslateY());
		textTriangle.getText().setFont(Font.font("Segoue UI", 84));
		textTriangle.getText().setFill(Color.rgb(155, 155, 155));
		
		TextBox textHelix = new TextBox("Spirale", recHelix.getWidth(), recHelix.getHeight());
		textHelix.setLayoutX(recHelix.getTranslateX());
		textHelix.setLayoutY(recHelix.getTranslateY());
		textHelix.getText().setFont(Font.font("Segoue UI", 84));
		textHelix.getText().setFill(Color.rgb(155, 155, 155));
		
		TextBox textStar = new TextBox("Stern", recStar.getWidth(), recStar.getHeight());
		textStar.setLayoutX(recStar.getTranslateX());
		textStar.setLayoutY(recStar.getTranslateY());
		textStar.getText().setFont(Font.font("Segoue UI", 84));
		textStar.getText().setFill(Color.rgb(155, 155, 155));
		
		final TextBox textTime = new TextBox("Time: 40", LeapApp.getDisplayWidth(), 120);
		textTime.getText().setFont(Font.font("Segoue UI", 84));
		textTime.getText().setFill(Color.rgb(155, 155, 155));
		
		final TextBox textScore = new TextBox("Score: 0", LeapApp.getDisplayWidth(), 100);
		textScore.setLayoutY(100);
		textScore.getText().setFont(Font.font("Segoue UI", 84));
		textScore.getText().setFill(Color.rgb(155, 155, 155));
		
		final Group root = new Group();
		root.getChildren().add(recTriangle);
		root.getChildren().add(recHelix);
		root.getChildren().add(recStar);
		root.getChildren().add(textTriangle);
		root.getChildren().add(textHelix);
		root.getChildren().add(textStar);
		root.getChildren().add(textTime);
		root.getChildren().add(textScore);
		root.getChildren().add(input);
		root.getChildren().add(grabView);
		root.getChildren().add(hand);

		final Scene scene = new Scene(root);
		scene.setCursor(Cursor.NONE);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});
		
		primaryStage.setTitle("JGrabMotion");
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
			boolean isFirst = true;
			long endTime;
			
			public void handle(ActionEvent t) {
				if(isFirst) {
					endTime = System.currentTimeMillis()+40000;
					isFirst = false;
					score = 0;
				}
				
				LeapApp.update();
				if(System.currentTimeMillis() > endTime) {
					scene.setRoot(new Pause(primaryStage, scene, root, timeline));
					isFirst = true;
				} else {
					long time = (endTime-System.currentTimeMillis())/1000;
					textTime.setText("Time: " + time);
					textScore.setText("Score: " + score);
				}
			}
		};
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1.0 / 60.0), ae));
		timeline.play();
		
		new Robot().mouseMove(0, 0);
	}

	private void setRandomInputImage() {
		inputNumber = new Random().nextInt(3);
		inputImage.setValue(new Image("file:res/img/input" + inputNumber + ".png"));
	}

	@Override
	public void pointMoved(PointEvent event) {
		handX.set(event.getX() - hand.getImage().getWidth()/2);
		handY.set(event.getY() - hand.getImage().getHeight()/2);
		
		Hand leapHand = LeapApp.getController().frame().hand(event.getSource().id()); 
		
		if(leapHand.fingers().count() <= 1) {
			hand.setImage(hand6);
		} else {
			hand.setImage(hand5);
		}
		
		if((prevFingerCount >= 2 || isGrabbing) && leapHand.fingers().count() <= 1
				&& grabView.getBoundsInParent().intersects(hand.getBoundsInParent())) {
			grabView.setTranslateX(event.getX() - grabView.getImage().getWidth() / 2);
			grabView.setTranslateY(event.getY() - grabView.getImage().getHeight() / 2);
			image.set(grab1);
			isGrabbing = true;
		} else if(isGrabbing) {
			grabView.setTranslateX(LeapApp.getDisplayWidth() / 2
					- grabView.getImage().getWidth() / 2);
			grabView.setTranslateY(LeapApp.getDisplayHeight() / 2
					- grabView.getImage().getHeight() / 2);
			isGrabbing = false;
			image.set(grab0);
						
			if((recTriangle.getBoundsInParent().intersects(hand.getBoundsInParent())
				&& inputNumber == 0) || (recHelix.getBoundsInParent().intersects(hand.getBoundsInParent())
						&& inputNumber == 1) || (recStar.getBoundsInParent().intersects(hand.getBoundsInParent())
								&& inputNumber == 2))  {
				score++;
			}
			setRandomInputImage();
		}
		
		prevFingerCount = leapHand.fingers().count();
	}

	@Override
	public void pointDragged(PointEvent event) {}
}
