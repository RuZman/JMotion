package de.ruzman.jzonemotion;

import java.awt.Robot;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import de.ruzman.jmotion.Pause;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.TrackingBox;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointEvent.Zone;
import de.ruzman.leap.event.PointListener;

public class JZoneMotion extends Application implements PointListener {	
	private Map<String, Image> images;
	private ObjectProperty<Image> image = new SimpleObjectProperty<>();
	
	public static void main(String args[]) {
		LeapApp.init(false);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		Application.launch(JZoneMotion.class, args);
	}

	public JZoneMotion() {
		super();
	}

	public JZoneMotion(Stage primaryStage) {
		try {
			start(primaryStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		images = new HashMap<>();
		images.put("NO", new Image("file:res/img/NO.png"));
		images.put("000", new Image("file:res/img/000.png"));
		images.put("001", new Image("file:res/img/001.png"));
		images.put("010", new Image("file:res/img/010.png"));
		images.put("011", new Image("file:res/img/011.png"));
		images.put("100", new Image("file:res/img/100.png"));
		images.put("101", new Image("file:res/img/101.png"));
		images.put("110", new Image("file:res/img/110.png"));
		images.put("111", new Image("file:res/img/111.png"));
		
		image.set(images.get("NO"));
		
		ImageView imageView = new ImageView();
		imageView.imageProperty().bind(image);
		
		final Group root = new Group(imageView);
		root.setTranslateX(LeapApp.getDisplayWidth()/2 - imageView.getImage().getWidth()/2);
		root.setTranslateY(LeapApp.getDisplayHeight()/2 - imageView.getImage().getHeight()/2);
		
		final Scene scene = new Scene(root);
		scene.setCursor(Cursor.NONE);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.exit(0);	
			}
		});

		primaryStage.setTitle("JZoneMotion");
		primaryStage.getIcons().add(new Image("file:res/img/icon.png"));
		primaryStage.setScene(scene);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);
		primaryStage.show();

		LeapApp.setMinimumHandNumber(1);
		LeapApp.setMaximumHandNumber(1);
        LeapApp.getMotionRegistry().addPointListener(this);
        
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
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0/60.0), ae));
        timeline.play();
        
		new Robot().mouseMove(0, 0);
  	}


	@Override
	public void zoneChanged(PointEvent event) {
		event.getSource().setTrackingBox(TrackingBox.buildOneSideTrackingBox(false));
		
		if(event.isInsideTrackingBox()) {
			image.setValue(getImage(event.isInZone(Zone.LEFT), 
					event.isInZone(Zone.DOWN), 
					event.isInZone(Zone.BACK)));
		} else {
			image.setValue(images.get("NO"));
		}
	}
	
	private Image getImage(boolean isLeft, boolean isDown, boolean isBack) {
		return images.get("" + (isLeft ? 1 : 0) + (isDown ? 1 : 0)  + (isBack ? 1 : 0));
	}
}
