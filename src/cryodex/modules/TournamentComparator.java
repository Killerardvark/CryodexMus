package cryodex.modules;

import java.util.Comparator;

public abstract class TournamentComparator<T> implements Comparator<T> {

	protected int compareInt(int a, int b) {
		if (a == b) {
			return 0;
		} else if (a > b) {
			return -1;
		} else {
			return 1;
		}
	}

	protected int compareDouble(double a, double b) {
		if (a == b) {
			return 0;
		} else if (a > b) {
			return -1;
		} else {
			return 1;
		}
	}
}
