package de.ruzman.leap;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

import de.ruzman.leap.event.LeapEvent;
import de.ruzman.leap.event.LeapListener;
import de.ruzman.leap.event.PointEvent.Zone;
import de.ruzman.leap.event.PointListener;
import de.ruzman.leap.event.PointMotionListener;

public class MotionRegistry implements LeapListener {	
	protected ConcurrentMap<Integer, ExtendedHand> hands;
	protected List<PointListener> pointListeners;
	protected List<PointMotionListener> pointMotionListeners;
	
	private AWTDispatcher awtDispatcher;

	public MotionRegistry() {
		hands = new ConcurrentHashMap<>(10);
		pointListeners = new CopyOnWriteArrayList<>();
		pointMotionListeners = new CopyOnWriteArrayList<>();
	}
	
	public void update(Frame frame) {
		for(ExtendedHand extendedHand: hands.values()) {
			if(frame.hand(extendedHand.id()).id() == -1) {
				extendedHand.destroy();
				hands.remove(extendedHand.id());
			}
		}
		
		ExtendedHand extendedHand;
		int handCount = 0;
		
		for(Hand hand: frame.hands()) {
			extendedHand = hands.get(hand.id());
			
			if(extendedHand == null && hands.size() < LeapApp.getMaximumHandNumber()) {
				TrackingBox trackingBox = new TrackingBox();
				
				switch(LeapApp.getMode()) {
					case DYNAMIC_ONE_SIDE:
							Vector zone = new Vector();
							trackingBox.calcZone(hand.palmPosition(), zone);
							trackingBox = TrackingBox.buildOneSideTrackingBox(zone.getX() < 0);
						break;
					default:
						break;
				}
				
				extendedHand = new ExtendedHand(hand.id(), trackingBox);
				extendedHand.setClickZone(EnumSet.of(Zone.BACK));
				
				for(PointListener pointListener: pointListeners) {
					extendedHand.addPointListener(pointListener);
				}
				for(PointMotionListener pointMotionListener: pointMotionListeners) {
					extendedHand.addPointMotionListener(pointMotionListener);
				}
				
				hands.put(hand.id(), extendedHand);
			}

			if(extendedHand != null && handCount++ < LeapApp.getMaximumHandNumber()) {
				extendedHand.update(frame, frame.hand(extendedHand.id()).palmPosition(),
						frame.hand(extendedHand.id()).stabilizedPalmPosition());
			}
		}
	}
	
	public void clear() {
		hands.clear();
	}
	
	public void addPointListener(PointListener pointListener) {
		pointListeners.add(pointListener);
		
		for(AbstractPoint abstractPoint: hands.values()) {
			abstractPoint.addPointListener(pointListener);
		}		
	}
	
	public void addPointMotionListener(PointMotionListener pointMotionListener) {
		pointMotionListeners.add(pointMotionListener);

		for(AbstractPoint abstractPoint: hands.values()) {
			abstractPoint.addPointMotionListener(pointMotionListener);
		}
	}
	
	public void removePointListener(PointListener pointListener) {
		pointListeners.add(pointListener);
		
		for(AbstractPoint abstractPoint: hands.values()) {
			abstractPoint.removePointListener(pointListener);
		}		
	}
	
	public void removePointMotionListener(PointMotionListener pointMotionListener) {
		pointMotionListeners.remove(pointMotionListener);
		
		for(AbstractPoint abstractPoint: hands.values()) {
			abstractPoint.removePointMotionListener(pointMotionListener);
		}
	}
	
	@Override
	public void update(LeapEvent event) {
		update(event.getFrame());
		
		ExtendedHand extendedHand;
		for(Hand hand: event.getFrame().hands()) {
			
			extendedHand = hands.get(hand.id());
			
			if(extendedHand != null) {
				extendedHand.updateEvents(event.getFrame());
			}
		}
	}

	@Override
	public void statusChanged(LeapEvent event) {
		clear();
	}

	protected void setAWTDispatcher(AWTDispatcher awtDispatcher) {
		if(this.awtDispatcher == null) {
			this.awtDispatcher = awtDispatcher;
			addPointListener(awtDispatcher);
			addPointMotionListener(awtDispatcher);
		}
	}

	public synchronized void removeAllListener() {		
		pointListeners.clear();
		pointMotionListeners.clear();
		
		hands.clear();
	}
	
	public int handCount() {
		return hands.size();
	}
}
