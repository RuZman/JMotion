package de.ruzman.jmotion;

import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CostumCursor extends Pane {
	private ImageView defaultImage;
	private Rectangle rectangle;
	private Stage primaryStage;
	
	private Timeline timeline;
	
	public CostumCursor(Stage primaryStage, Timeline timeline) {
		this.primaryStage = primaryStage;
		this.timeline = timeline;
		
		defaultImage = new ImageView(new Image("file:res/img/cursor.png"));
		rectangle = new Rectangle(350, 200, Color.rgb(239, 239, 239));
		
		setMouseTransparent(true);
		getChildren().add(defaultImage);
	}
	
	public void registerScene(Scene scene) {
		scene.setCursor(Cursor.NONE);
		scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
			 @Override public void handle(MouseEvent event) {
				 defaultImage.setX(event.getSceneX());
				 defaultImage.setY(event.getSceneY());
			 }
		});
	}
	
	public void registerButtons(GridPane gridPane) {		
		for(Node node: gridPane.getChildren()) {
			if( node instanceof Button) {
				final Button button = (Button) node;
				button.setOnMouseEntered(new EventHandler<MouseEvent>() {
					 @Override public void handle(MouseEvent event) {
						 rectangle.setX(button.getBoundsInParent().getMinX());
						 rectangle.setY(button.getBoundsInParent().getMinY()+150);
						 
						 getChildren().set(0, rectangle);
						 getChildren().add(new DelayProgress(timeline, primaryStage, button, 150, 
								 rectangle.getBoundsInParent().getMinX() + rectangle.getWidth()/2, 
								 rectangle.getBoundsInParent().getMinY() + rectangle.getHeight()/2));
						 return;
					 }
				});
				button.setOnMouseExited(new EventHandler<MouseEvent>() {
					 @Override public void handle(MouseEvent event) {
						 DelayProgress delayProgress = (DelayProgress) getChildren().get(1);
						 delayProgress.cancel();
						 
						 getChildren().clear();
						 getChildren().add(defaultImage);
						 return;
					 }
				});
			}
		}
	}
}
