package sg.nyp.groupconnect.entity;

import java.util.Comparator;

public class DistanceSorter implements Comparator<GrpRoomListExt> {

	@Override
	public int compare(GrpRoomListExt a, GrpRoomListExt b) {
		int returnVal = 0;

		if (a.getDistance() < b.getDistance()) {
			returnVal = -1;
		} else if (a.getDistance() > b.getDistance()) {
			returnVal = 1;
		} else if (a.getDistance() == b.getDistance()) {
			returnVal = 0;
		}
		return returnVal;
	}
}
