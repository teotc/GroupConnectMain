package sg.nyp.groupconnect.entity;

public class MemberGrades {
	private String memberId;
	private String subjectId;
	private double oldGrade;
	private double newGrade;

	public MemberGrades(String memberId, String subjectId, double oldGrade,
			double newGrade) {
		super();
		this.memberId = memberId;
		this.subjectId = subjectId;
		this.oldGrade = oldGrade;
		this.newGrade = newGrade;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public double getOldGrade() {
		return oldGrade;
	}

	public void setOldGrade(double oldGrade) {
		this.oldGrade = oldGrade;
	}

	public double getNewGrade() {
		return newGrade;
	}

	public void setNewGrade(double newGrade) {
		this.newGrade = newGrade;
	}

}
