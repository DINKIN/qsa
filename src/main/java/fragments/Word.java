package fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import geometry.Point;
import pdb.Residue;
import statistics.Statistics;

/**
 *
 * @author Antonin Pavelka
 */
public class Word implements Serializable, WordInterface {

	private static final long serialVersionUID = 1L;
	private Residue[] residues_;
	private List<Double> intDist_ = new ArrayList<>();
	private Point center;
	private int id;

	public Word(int id, List<Residue> residues) {
		residues_ = new Residue[residues.size()];
		residues.toArray(residues_);
		computeInternalDistances();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	private void computeInternalDistances() {
		for (int x = 0; x < residues_.length; x++) {
			for (int y = 0; y < x; y++) {
				Point a = residues_[x].getPosition();
				Point b = residues_[y].getPosition();
				intDist_.add(a.distance(b));
			}
		}
	}

	public int seqDist(Word other) {
		int d = Integer.MAX_VALUE;
		for (Residue x : residues_) {
			for (Residue y : other.residues_) {
				int diff = Math.abs(x.getIndex().getSequenceNumber() - y.getIndex().getSequenceNumber());
				if (diff < d) {
					d = diff;
				}
			}
		}
		return d;
	}

	public boolean isInContact(Word other, double threshold) {
		Residue a1 = residues_[0];
		Residue a2 = residues_[residues_.length - 1];
		Residue b1 = other.residues_[0];
		Residue b2 = other.residues_[residues_.length - 1];

		int n1 = a1.getId().getSequenceNumber();
		int n2 = a2.getId().getSequenceNumber();
		int m1 = b1.getId().getSequenceNumber();
		int m2 = b2.getId().getSequenceNumber();
		if ((n1 <= m1 && m1 <= n2) || (n1 <= m2 && m2 <= n2) || (m1 <= n1 && n1 <= m2) || (m1 <= n2 && n2 <= m2)) {
			return false;
		} else {
			for (int x = 0; x < residues_.length; x++) {
				for (int y = 0; y < other.residues_.length; y++) {
					double d = residues_[x].distance(other.residues_[y]);
					if (d <= threshold) {
						return true;
					}
				}
			}
			// return (a1.distance(b1) <= threshold && a2.distance(b2) <=
			// threshold)
			// || (a1.distance(b2) <= threshold && a2.distance(b1) <=
			// threshold);
		}
		return false;
	}

	public double shapeDifference(Word other) {
		return Statistics.difference(intDist_, other.intDist_);
	}

	public Point[] getPoints() {
		Point[] points = new Point[residues_.length];
		for (int i = 0; i < residues_.length; i++) {
			points[i] = residues_[i].getPosition();
		}
		return points;
	}

	public Residue[] getResidues() {
		return residues_;
	}

	public Point getCenter() {
		if (center == null) {
			Point sum = new Point(0, 0, 0);
			Point[] ps = getPoints();
			for (Point p : ps) {
				sum = sum.plus(p);
			}
			center = sum.divide(ps.length);
		}
		return center;
	}

	public void print() {
		for (Residue r : residues_) {
			System.out.print(r.getId().toString() + " ");
		}
		System.out.println();
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		Word other = (Word) o;
		return id == other.id;
	}

	public String toString() {
		return Integer.toString(id);
	}
}
