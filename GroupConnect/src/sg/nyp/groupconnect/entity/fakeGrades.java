package sg.nyp.groupconnect.entity;

public class fakeGrades {
	private String memberId;
	private String subjectId;
	private int oldGrade;
	private int newGrade;

	public fakeGrades(String memberId, String subjectId, int oldGrade,
			int newGrade) {
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

	public int getOldGrade() {
		return oldGrade;
	}

	public void setOldGrade(int oldGrade) {
		this.oldGrade = oldGrade;
	}

	public int getNewGrade() {
		return newGrade;
	}

	public void setNewGrade(int newGrade) {
		this.newGrade = newGrade;
	}

}
