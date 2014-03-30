package de.ruzman.jsinuousmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import de.ruzman.fx.util.Point;
import de.ruzman.leap.LeapApp;

public class FXWorld {        
    private Group root;
    
    private Circle player;
    private List<Circle> circles;
    private List<Object[]> snake;
    private Text score;
    
    public FXWorld(Group root) {
    	this.root = root;
    	
    	circles = new ArrayList<>();
    	snake = new ArrayList<>();
    	
    	root.getChildren().add(createPlayer());
    	
    	score = new Text(20, 30, "Score: 0");
    	score.setFill(Color.WHITE);
    	score.setFont(Font.font("Segoue UI", 24));
    	score.setFontSmoothingType(FontSmoothingType.LCD);
    }
	
	public boolean drawAndUpadte() {			
	    for(Circle circle: circles) {
	    	if(Circle.intersect(player, circle).getLayoutBounds().getWidth() != -1) {
	    		root.getChildren().removeAll(circles);
	    		circles.clear();
	    		return true;
	    	}
	    }
	    
	    return false;
	}
	
	public int getCirclesSize() {
		return circles.size();
	}
	
	public List<Object[]> getSnake() {
		return snake;
	}
	
	public void movePlayerTo(float x, float y) {
		player.setCenterX(x);
		player.setCenterY(y);
	}
    
	private Circle createPlayer() {
		player = new Circle(0,0, 10,
				Color.rgb(60, 150, 195));
		
		return player;		
	}
	
	public Text getScore() {
		return score;
	}
	
	public Circle createCircle() {		
		final Random random = new Random();
		final Circle circle;
		final Path path = new Path();
		final Point toPoint;
		
		float trans = random.nextFloat();
		float radius = 6+random.nextFloat()*3;
		
		if(random.nextBoolean()) {
			circle = new Circle(trans * LeapApp.getDisplayWidth(),
					-radius, radius);
			
	    	path.getElements().add(new MoveTo(circle.getCenterX(), circle.getCenterY()));
	    	path.getElements().add(new LineTo(-circle.getRadius(), LeapApp.getDisplayHeight()*trans));
	    	toPoint = new Point(-circle.getRadius(), LeapApp.getDisplayHeight()*trans);
		} else {
			circle = new Circle(LeapApp.getDisplayWidth()+radius,
					trans*LeapApp.getDisplayHeight(), radius);
			
			path.getElements().add(new MoveTo(circle.getCenterX(), circle.getCenterY()));
			path.getElements().add(new LineTo(LeapApp.getDisplayWidth() * trans, LeapApp.getDisplayHeight()+circle.getRadius()));
			toPoint = new Point(LeapApp.getDisplayWidth() * trans, LeapApp.getDisplayHeight()+circle.getRadius());
		}
		
		circle.setFill(Color.RED);
		
    	PathTransition pathTransition = new PathTransition();
    	
    	pathTransition.setDuration(Duration.millis(2*toPoint.getDistanceTo(new Point(circle.getCenterX(), circle.getCenterY()))));
    	pathTransition.setPath(path);
    	pathTransition.setNode(circle);
    	pathTransition.play();
    	pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	circles.remove(circle);
            	root.getChildren().remove(circle);
            }
        });
		    	
    	circles.add(circle);
    	
		return circle;		
	}

	public void clear() {
    	score.setText("Score: 0");
	}
}
