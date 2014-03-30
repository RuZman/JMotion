package de.ruzman.jrunmtion;

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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.leapmotion.leap.Hand;

import de.ruzman.fx.util.Point;
import de.ruzman.fx.util.TextBox;
import de.ruzman.jmotion.Pause;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;

public class JRunMotion extends Application implements PointMotionListener {	
	private Group root;
	
    private Image skalaImage = new Image("file:res/img/skala.png");
    private ImageView skala = new ImageView(skalaImage);
    private Rectangle rectangle;
    private TextBox textBox;
    
    private double percent;
    private boolean isUp;
    private long startTime;
	
	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		Application.launch(JRunMotion.class, args);
	}

	public JRunMotion() {
		super();
	}

	public JRunMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		rectangle = new Rectangle(0, skalaImage.getHeight()*0.95, Color.rgb(60, 150, 195));
		
		skala.setTranslateX(LeapApp.getDisplayWidth()/2 - skalaImage.getWidth()/2);
		skala.setTranslateY(LeapApp.getDisplayHeight()/2 - skalaImage.getHeight()/2);
		
		textBox = new TextBox("0.000 sec.", skalaImage.getWidth(), 120);
		textBox.setLayoutX(LeapApp.getDisplayWidth()/2 - skalaImage.getWidth()/2);
		textBox.setLayoutY(LeapApp.getDisplayHeight()/2 + skalaImage.getHeight()/2);
		textBox.getText().setFont(Font.font("Segoue UI", 84));
		textBox.getText().setFill(Color.rgb(155, 155, 155));
		
		root = new Group();		
		root.getChildren().add(rectangle);
		root.getChildren().add(skala);
		root.getChildren().add(textBox);
		
		final Scene scene = new Scene(root);
		scene.setCursor(Cursor.NONE);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});
		
		primaryStage.setTitle("JRunMotion");
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
			    
			    if(startTime != 0) {
				    long sec = (System.currentTimeMillis() - startTime)/1000;
				    long mili = (System.currentTimeMillis() - startTime)%1000;
				    	   
				    textBox.setText(sec + "." + mili + " sec.");
			    }
			    
			    if(percent >= 1) {
			    	percent = 0;
			    	startTime = 0;
					scaleRetangle();
			    	scene.setRoot(new Pause(primaryStage, scene, root, timeline));
			    }
           }
        };
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0/60.0), ae));
        timeline.play();
        
		new Robot().mouseMove(0, 0);
  	}

	private void scaleRetangle() {
		rectangle.setWidth(percent*skalaImage.getWidth()*0.993);
		
		rectangle.setTranslateX(LeapApp.getDisplayWidth()/2 - skalaImage.getWidth()/2*0.995);
		rectangle.setTranslateY(LeapApp.getDisplayHeight()/2 - rectangle.getHeight()/2);
	}

	@Override
	public void pointMoved(PointEvent event) {		
		Hand leftHand = LeapApp.getController().frame().hands().leftmost();
		Hand rightHand = LeapApp.getController().frame().hands().rightmost();		
		
		Point point = new Point(leftHand.stabilizedPalmPosition().getX(), leftHand.stabilizedPalmPosition().getY());
		Point point2 = new Point(rightHand.stabilizedPalmPosition().getX(), rightHand.stabilizedPalmPosition().getY());
		
		if(percent < 1 && ((isUp && point.getY() > point2.getY() && point.getY()-point2.getY() >= 80)
				|| (!isUp && point2.getY() > point.getY() && point2.getY()-point.getY() >= 80))) {
			if(percent == 0.0) {
				startTime = System.currentTimeMillis();
			}
			isUp = !isUp;
			percent += 0.01;
			scaleRetangle();
		}
	}

	@Override
	public void pointDragged(PointEvent event) {

	}
}
