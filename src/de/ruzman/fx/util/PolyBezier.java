package de.ruzman.fx.util;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class PolyBezier extends Group {

	private List<CubicCurve> curves;

	public PolyBezier(Point... points) {
		rebuild(points);
	}
	
	private void rebuild(Point... points) {
		if (points == null || points.length <= 2) {
			return;
		}
						
		curves = new ArrayList<>();
		
		Point[] a = new Point[points.length - 1];
		Point[] b = new Point[a.length];
		Line[] c = new Line[a.length];

		for (int i = 0; i < a.length; i++) {
			a[i] = (points[i + 1].add(points[i])).div(2);
		}

		for (int i = 0; i < a.length - 1; i++) {
			double l1 = points[i].getDistanceTo(points[i + 1]);
			double l2 = points[i + 1].getDistanceTo(points[i + 2]);

			double centroid = l1 / (l1 + l2);
			double alpha = a[i].getAngleTo(a[i + 1]);
			double distance = a[i].getDistanceTo(a[i + 1]);

			b[i] = new Point(a[i].getX() + centroid * Math.cos(alpha)
					* distance, a[i].getY() + centroid * Math.sin(alpha)
					* distance);

			c[i] = new Line(points[i + 1].getX() + a[i].getX() - b[i].getX(),
					points[i + 1].getY() + a[i].getY() - b[i].getY(),
					points[i + 1].getX() + a[i].getX() - b[i].getX()
							+ Math.cos(alpha) * distance, points[i + 1].getY()
							+ a[i].getY() - b[i].getY() + Math.sin(alpha)
							* distance);
		}
		
		curves.add(new CubicCurve(points[0].getX(), points[0].getY(), c[0]
				.getStartX(), c[0].getStartY(), points[1].getX(), points[1]
				.getY(), points[1].getX(), points[1].getY()));

		for (int i = 1; i < curves.size() - 1; i++) {
			curves.add(new CubicCurve(points[i].getX(), points[i].getY(),
					c[i - 1].getEndX(), c[i - 1].getEndY(), c[i].getStartX(),
					c[i].getStartY(), points[i + 1].getX(), points[i + 1]
							.getY()));
		}

		curves.add(new CubicCurve(points[points.length - 2].getX(),
				points[points.length - 2].getY(), c[c.length - 2].getEndX(),
				c[c.length - 2].getEndY(), points[points.length - 1].getX(),
				points[points.length - 1].getY(), points[points.length - 1]
						.getX(), points[points.length - 1].getY()));

		for (CubicCurve curve : curves) {
			curve.setStroke(Color.rgb(60, 150, 195));
			curve.setStrokeWidth(5);
			curve.setStrokeLineCap(StrokeLineCap.ROUND);
			curve.setFill(Color.TRANSPARENT);
			getChildren().add(curve);
		}
	}
}
