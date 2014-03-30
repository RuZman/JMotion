package de.ruzman.jhandmotion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import de.ruzman.fx.util.TextBox;
import de.ruzman.leap.LeapApp;

public class FXWorld {    
   
    private Group root;
    private TextBox textBox;
    
    private Rectangle rightSide;
    private Rectangle leftSide;
    
    private List<ImageView> imageViews = new ArrayList<>();
    
    private Image imageHand = new Image("file:res/img/hand2.png");
    private Image imageHand3 = new Image("file:res/img/hand3.png");
    private Image imageHand4 = new Image("file:res/img/hand4.png");
    
    private int created;
    private int score;
    
    public FXWorld(Group root) {
    	this.root = root;    	
    	init();
    }
    
    private void init() {
		textBox = new TextBox("0 / 20", 400, 140);
		textBox.setLayoutX(LeapApp.getDisplayWidth()/2 - 200);
		textBox.setLayoutY(LeapApp.getDisplayHeight()/2 - 70);
		textBox.getText().setFont(Font.font("Segoue UI", 84));
		textBox.getText().setFill(Color.rgb(155, 155, 155));
		
		TextBox textBox2 = new TextBox("Score", 400, 140);
		textBox2.setLayoutX(LeapApp.getDisplayWidth()/2 - 200);
		textBox2.setLayoutY(LeapApp.getDisplayHeight()/2 - 160);
		textBox2.getText().setFont(Font.font("Segoue UI", 84));
		textBox2.getText().setFill(Color.rgb(155, 155, 155));
    	
    	rightSide = new Rectangle(0, LeapApp.getDisplayHeight()/3*2, LeapApp.getDisplayWidth()/2, imageHand.getHeight());
    	rightSide.setFill(Color.GAINSBORO);
    	leftSide = new Rectangle(LeapApp.getDisplayWidth()/2, LeapApp.getDisplayHeight()/3*2, LeapApp.getDisplayWidth()/2, imageHand.getHeight());
    	leftSide.setFill(Color.GAINSBORO);
		
		root.getChildren().add(textBox2);
    	root.getChildren().add(textBox);
    	root.getChildren().add(rightSide);
    	root.getChildren().add(leftSide);
    }
	
	public void drawAndUpadte() {
		Iterator<ImageView> itImageView = imageViews.iterator();
		
		while(itImageView.hasNext()) {
			ImageView imageView = itImageView.next();
			if(imageView.getTranslateY() > LeapApp.getDisplayHeight()/3*2+imageHand.getHeight()) {
				if(!imageView.getImage().equals(imageHand4)) {
					imageView.setImage(imageHand3);
					addScore(0);
				}
				
            	itImageView.remove();
			}			
		}
	}
	
	public void createHand(boolean isRight) {
    	final double centerX = (imageHand.getWidth()+LeapApp.getDisplayWidth())/2;
    	final double posX = centerX+(isRight?15:-imageHand.getWidth()-15);
    	
    	final ImageView imageView = new ImageView(imageHand);
    	imageView.setLayoutX(posX);
    	imageView.setLayoutY(-imageHand.getHeight());
    	    	
    	Path path = new Path();
    	path.getElements().add(new MoveTo(0, 0));
    	path.getElements().add(new LineTo(0, LeapApp.getDisplayHeight()+imageHand.getHeight()*2));
    	
    	PathTransition pathTransition = new PathTransition();
    	pathTransition.setDuration(Duration.millis(4000));
    	pathTransition.setPath(path);
    	pathTransition.setNode(imageView);
    	pathTransition.play();
    	pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
            	root.getChildren().remove(imageView);
            }
        });
    	
    	root.getChildren().add(imageView);
    	imageViews.add(imageView);
		created++;
	}

	public boolean checkRight() {
		Iterator<ImageView> itImageView = imageViews.iterator();
		boolean isRight = false;
		
		while(itImageView.hasNext()) {
			ImageView imageView = itImageView.next();
			
			if(imageView.getTranslateY() > LeapApp.getDisplayHeight()/3*2
				&& imageView.getImage().equals(imageHand)
				&& imageView.getLayoutX() > LeapApp.getDisplayWidth()/2) {
				imageView.setImage(imageHand4);
				isRight = true;
			}			
		}
		
		return isRight;
	}
	
	public boolean checkLeft() {
		Iterator<ImageView> itImageView = imageViews.iterator();
		boolean isLeft = false;
		
		while(itImageView.hasNext()) {
			ImageView imageView = itImageView.next();
			
			if(imageView.getTranslateY() > LeapApp.getDisplayHeight()/3*2
				&& imageView.getImage().equals(imageHand)
				&& imageView.getLayoutX() < LeapApp.getDisplayWidth()/2) {
				imageView.setImage(imageHand4);
				isLeft = true;
			}			
		}
		
		return isLeft;
	}
	
	public boolean canCreateMore() {
		if(created < 20) {
			return true;
		}
		return false;
	}
	
	public boolean isOver() {
		return !canCreateMore() && imageViews.size() == 0;
	}
	
	public void resetScore() {
		created = 0;
		score = 0;
		textBox.getText().setText(score + " / 20");
	}
	
	public void addScore(int deltaScore) {
		score += deltaScore;
		textBox.getText().setText(score + " / 20");
	}
}