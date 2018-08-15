import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

//File ready for Static review
@SuppressWarnings("serial")
public class Loan implements Serializable {
	
	public static enum LoanState { CURRENT, OVER_DUE, DISCHARGED };
	
	private int loanID;
	private Member member;
	private Book book;
	private Date dueDate;
	private LoanState state;


	public Loan(int loanId, Book book, Member member, Date dueDate) {

		this.loanID = loanId;
		this.book = book;
		this.member = member;
		this.dueDate = dueDate;
		this.state = LoanState.CURRENT;
	}

	
	public void checkOverDue() {
		if (state == LoanState.CURRENT &&
			Calendar.getInstance().Date().after(dueDate)) {
			this.state = LoanState.OVER_DUE;			
		}
	}

	
	public boolean isOverDue() {
		return state == LoanState.OVER_DUE;
	}

	
	public Integer getId() {
		return loanID;
	}


	public Date getDueDate() {
		return dueDate;
	}
	
	
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder buildString = new StringBuilder();
		buildString.append("Loan:  ").append(loanID).append("\n")
		  .append("  Borrower ").append(member.getMemberId()).append(" : ")
		  .append(member.getLastName()).append(", ").append(member.getFirstName()).append("\n")
		  .append("  Book ").append(book.id()).append(" : " )
		  .append(book.title()).append("\n")
		  .append("  DueDate: ").append(simpleDateFormat.format(dueDate)).append("\n")
		  .append("  State: ").append(state);		
		return buildString.toString();
	}


	public Member getMember() {
		return member;
	}


	public Book getBook() {
		return book;
	}


	public void dischargeLoan() {
		state = LoanState.DISCHARGED;		
	}

}
