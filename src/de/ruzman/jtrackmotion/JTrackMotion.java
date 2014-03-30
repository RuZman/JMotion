
package de.ruzman.jtrackmotion;

import java.awt.Robot;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

import de.ruzman.jmotion.Pause;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.TrackingBox;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointListener;
import de.ruzman.leap.event.PointMotionListener;
 
public class JTrackMotion extends Application implements PointListener, PointMotionListener {	
	private Group root = new Group();
	private Map<Integer, Map<Integer, Sphere>> fingers = new HashMap<>();
	
	private TrackingBox trackingBox;
	private Text handCount;
	private Text fingerCount;
	
	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		Application.launch(JTrackMotion.class, args);
	}

	public JTrackMotion() {
		super();
	}

	public JTrackMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
     	handCount = new Text(20, 30, "Hände: 0");
    	handCount.setFill(Color.WHITE);
    	handCount.setFont(Font.font("Segoue UI", 24));
    	handCount.setFontSmoothingType(FontSmoothingType.LCD);
    	
    	fingerCount = new Text(20, 65, "Finger: 0");
    	fingerCount.setFill(Color.WHITE);
    	fingerCount.setFont(Font.font("Segoue UI", 24));
    	fingerCount.setFontSmoothingType(FontSmoothingType.LCD);
		
		AmbientLight ambientLight = new AmbientLight(Color.WHITE);
		root.getChildren().add(ambientLight);
		root.getChildren().add(fingerCount);
		root.getChildren().add(handCount);
    	
        final Scene scene = new Scene(root);
		scene.setCursor(Cursor.NONE);
        scene.setFill(Color.rgb(58, 58, 58));
        scene.setCamera(new PerspectiveCamera(false));
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});

		primaryStage.setTitle("JTrackMotion");
		primaryStage.getIcons().add(new Image("file:res/img/icon.png"));
		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);
		primaryStage.show();

		LeapApp.setMaximumHandNumber(1);
		LeapApp.setMaximumHandNumber(10);
        LeapApp.getMotionRegistry().addPointMotionListener(this);
        LeapApp.getMotionRegistry().addPointListener(this);
                
		final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {       	
            public void handle(ActionEvent t) {
			       LeapApp.update();
			       if(updateHandCount() == 0) {
			    	   scene.setRoot(new Pause(primaryStage, scene, root, timeline));
			       }
			       updateFingerCount();
           }
        };
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0/60.0), ae));
        timeline.play();
        
        trackingBox = new TrackingBox(new Vector(600,400,200));
		new Robot().mouseMove(0, 0);
  	}

	@Override
	public void pointMoved(PointEvent event) {
		Hand hand = LeapApp.getController().frame().hand(event.getSource().id());
		
		if(!fingers.containsKey(hand.id())) {
			fingers.put(hand.id(), new HashMap<Integer, Sphere>());
		}
		
		cleanFinger(hand.id(), hand);
		
		Vector position = new Vector();
		Sphere sphere;
		
		for(Finger finger: hand.fingers()) {
			if(!fingers.get(hand.id()).containsKey(finger.id())) {
				sphere = new Sphere(20);
		      
				PhongMaterial material = new PhongMaterial();
		        material.setDiffuseColor(Color.WHITE);
		        material.setSpecularColor(Color.rgb(58, 58, 58));
				sphere.setMaterial(material);
				
				root.getChildren().add(sphere);
				
				fingers.get(hand.id()).put(finger.id(), sphere);
			} else {
				sphere = fingers.get(hand.id()).get(finger.id());
			}
			
			trackingBox.calcScreenPosition(finger.stabilizedTipPosition(), position);
			
			sphere.setTranslateX(position.getX());
			sphere.setTranslateY(position.getY());
			sphere.setTranslateZ((1-position.getZ())/100);
		}		
	}

	private void updateFingerCount() {
		int count = LeapApp.getController().frame().fingers().count();
		fingerCount.setText("Finger: " + count);
	}
	
	private int updateHandCount() {
		int count = LeapApp.getController().frame().hands().count();
		handCount.setText("Hände: " + count);
		return count;
	}
	
	@Override
	public void pointDragged(PointEvent event) {}

	@Override
	public void zoneChanged(PointEvent event) {
		if(event.leftViewPort()) {
			if(fingers.get(event.getSource().id()) != null) {
				Hand hand = LeapApp.getController().frame().hand(event.getSource().id());
				cleanFinger(event.getSource().id(), hand);
			}
		}
	}
	
	private void cleanFinger(int id, Hand hand) {
		Iterator<Map.Entry<Integer, Sphere>> itEntry = fingers.get(id).entrySet().iterator();
		
		while(itEntry.hasNext()) {
			Map.Entry<Integer, Sphere> entry = itEntry.next();
			
			if(!hand.finger(entry.getKey()).isValid()) {
				root.getChildren().remove(entry.getValue());
				itEntry.remove();
			}
			
		}
	}
}