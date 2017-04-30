package menezes.paulo.safe.util;

import java.util.Comparator;

import android.util.Pair;

import menezes.paulo.safe.entity.Place;

public class PlaceAlertComparator implements Comparator<Pair<Integer, Place>> {
	@Override
	public int compare(Pair<Integer, Place> lhs, Pair<Integer, Place> rhs) {
		return lhs.first - rhs.first;
	}
}
