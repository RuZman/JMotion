package de.ruzman.jdodgemotion;

import static de.ruzman.box2d.util.Util.HEIGHT;
import static de.ruzman.box2d.util.Util.WIDTH;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import de.ruzman.box2d.util.Util;
import de.ruzman.fx.util.TextBox;
import de.ruzman.leap.LeapApp;

public class Box2DWorld {    
    private World world;
    
    private Group root;
    private TextBox textBox;
    
    private Circle player;
    private List<Body> circles;
    private Body square;
    
    public Box2DWorld(Group root) {
    	this.root = root;
    	
    	world = new World(new Vec2());
    	circles = new ArrayList<>();
    	
    	init();
    }
    
    private void init() {
		textBox = new TextBox("0", 400, 140);
		textBox.setLayoutX(LeapApp.getDisplayWidth()/2 - 200);
		textBox.setLayoutY(LeapApp.getDisplayHeight()/2 - 70);
		textBox.getText().setFont(Font.font("Segoue UI", 84));
		textBox.getText().setFill(Color.rgb(155, 155, 155));
		
		TextBox textBox2 = new TextBox("Score", 400, 140);
		textBox2.setLayoutX(LeapApp.getDisplayWidth()/2 - 200);
		textBox2.setLayoutY(LeapApp.getDisplayHeight()/2 - 160);
		textBox2.getText().setFont(Font.font("Segoue UI", 84));
		textBox2.getText().setFill(Color.rgb(155, 155, 155));
    	
		root.getChildren().add(textBox2);
    	root.getChildren().add(textBox);
    	root.getChildren().add(createPlayer());
    	root.getChildren().add(createRectangle(5, 5));
		
        initWall(-1, 0, 1, HEIGHT);
        initWall(0, -1, WIDTH+1, 1);
        initWall(WIDTH+1, 0, 1, HEIGHT);
        initWall(0, HEIGHT, WIDTH+1, 1);
    }
	
	public boolean drawAndUpadte() {	
		Rectangle rectangle = ((Rectangle) square.getUserData()); 
		
		if(Circle.intersect(player, rectangle).getLayoutBounds().getWidth() != -1) {
			root.getChildren().remove(rectangle);
			root.getChildren().add(createRectangle(5, 5));
			
			Random random = new Random();
			Circle radius = new Circle(player.getCenterX(), player.getCenterY(), 200);			
			Circle newCircle = new Circle(random.nextFloat()*LeapApp.getDisplayWidth(),
	        		random.nextFloat()*LeapApp.getDisplayHeight(), 20);
			
			while(Circle.intersect(radius, newCircle).getLayoutBounds().getWidth() >= 0) {
				newCircle.setCenterX(random.nextFloat()*LeapApp.getDisplayWidth());
				newCircle.setCenterY(random.nextFloat()*LeapApp.getDisplayHeight());
			}
			
			root.getChildren().add(createCircle(
					Util.pixelToMeterX((float) newCircle.getCenterX()),
					Util.pixelToMeterY((float) newCircle.getCenterY())));
			
			world.destroyBody(square);
			textBox.setText("" + (Integer.parseInt(textBox.getText().getText()) + 1));
		}
		
	    Circle circle2;
	    Iterator<Body> bodies = circles.iterator();
		while(bodies.hasNext()) {
			Body body = bodies.next();
			
			if(!body.isActive()) {
				textBox.setText("0");
				root.getChildren().remove(((Circle) body.getUserData()));
				bodies.remove();
				continue;
			}
			
			circle2 = (Circle) body.getUserData();
			circle2.centerXProperty().set(Util.meterToPixelX(body.getPosition().x));
			circle2.centerYProperty().set(Util.meterToPixelY(body.getPosition().y));
			
			if(Circle.intersect(player, circle2).getLayoutBounds().getWidth() != -1) {				
				for(Body body2: circles) {
					body2.setActive(false);
					world.destroyBody(body2);
				}				
				return true;
			}
		}
		
		return false;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void movePlayerTo(float x, float y) {
		player.setCenterX(x);
		player.setCenterY(y);
	}
    
	private Circle createPlayer() {
		player = new Circle(
				Util.meterToPixelX(0),
				Util.meterToPixelY(0),
				Util.meterToPixelX(3));
		player.setFill(Color.rgb(60, 150, 195));
		
		return player;		
	}
	
	private Circle createCircle(float x, float y) {
		Random random = new Random();
		
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(x, y);
        
		CircleShape shape = new CircleShape();
		shape.m_radius = 2f;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0;
		fixtureDef.restitution = 0.8f;
		fixtureDef.filter.groupIndex = -1;

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setLinearVelocity(new Vec2(5+random.nextInt(50), 3+random.nextInt(50)));
		
		Circle circle = new Circle(
				Util.meterToPixelX(body.getPosition().x),
				Util.meterToPixelY(body.getPosition().y),
				Util.meterToPixelX(body.getFixtureList().getShape().m_radius));
		circle.setFill(Color.rgb(175, 70, 55));
		
		body.setUserData(circle);
		circles.add(body);
		
		return circle;		
	}
	
    private Rectangle createRectangle(float w, float h) {
    	Random random = new Random();
    	
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(5+random.nextFloat()*(WIDTH-7), 5+random.nextFloat()*(HEIGHT-7));
        
        Vec2[] vertices = {
            new Vec2(0, 0),
            new Vec2(w, 0),
            new Vec2(w, h),
            new Vec2(0, h),
        };
        
        PolygonShape shape = new PolygonShape();
        shape.set(vertices, vertices.length);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
		fixtureDef.filter.groupIndex = -1;
        
        Body body = world.createBody(bodyDef); 
        body.createFixture(fixtureDef);
        
        Rectangle rectangle = new Rectangle(
        		Util.meterToPixelX(body.getPosition().x),
        		Util.meterToPixelY(body.getPosition().y),
        		Util.meterToPixelX(w),
        		Util.meterToPixelX(h));
		rectangle.setFill(Color.rgb(60, 150, 195));
		
		body.setUserData(rectangle);
		square = body;
		
        return rectangle;
    }
	
    private void initWall(float x, float y, float w, float h) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(x, y);
        
        Vec2[] vertices = {
            new Vec2(0, 0),
            new Vec2(w, 0),
            new Vec2(w, h),
            new Vec2(0, h),
        };
        
        PolygonShape wallShape = new PolygonShape();
        wallShape.set(vertices, vertices.length);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = wallShape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0;
		fixtureDef.restitution = 1;
        
        Body body = world.createBody(bodyDef); 
        body.createFixture(fixtureDef);
    }
}
