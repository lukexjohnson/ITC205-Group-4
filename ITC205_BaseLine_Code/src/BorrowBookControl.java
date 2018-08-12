import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {
	
	private BorrowBookUI ui;
	
	private library L;
	private Member M;
	private enum CONTROL_STATE { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private CONTROL_STATE state;
	
	private List<book> PENDING;
	private List<Loan> COMPLETED;
	private book B;
	
	
	public BorrowBookControl() {
		this.L = L.INSTANCE();
		state = CONTROL_STATE.INITIALISED;
	}
	

	public void setUI(BorrowBookUI ui) {
		if (!state.equals(CONTROL_STATE.INITIALISED)) 
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
			
		this.ui = ui;
		ui.setState(BorrowBookUI.UIState.READY);
		state = CONTROL_STATE.READY;		
	}

		
	public void cardSwiped(int memberId) {
		if (!state.equals(CONTROL_STATE.READY)) 
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
			
		M = L.getMember(memberId);
		if (M == null) {
			ui.display("Invalid memberId");
			return;
		}
		if (L.memberCanBorrow(M)) {
			PENDING = new ArrayList<>();
			ui.setState(BorrowBookUI.UIState.SCANNING);
			state = CONTROL_STATE.SCANNING; }
		else 
		{
			ui.display("Member cannot borrow at this time");
			ui.setState(BorrowBookUI.UIState.RESTRICTED); }}
	
	
	public void bookScanned(int bookId) {
		B = null;
		if (!state.equals(CONTROL_STATE.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}	
		B = L.Book(bookId);
		if (B == null) {
			ui.display("Invalid bookId");
			return;
		}
		if (!B.Available()) {
			ui.display("Book cannot be borrowed");
			return;
		}
		PENDING.add(B);
		for (book B : PENDING) {
			ui.display(B.toString());
		}
		if (L.loansRemainingForMember(M) - PENDING.size() == 0) {
			ui.display("Loan limit reached");
			completeBorrow();
		}
	}
	
	
	public void completeBorrow() {
		if (PENDING.size() == 0) {
			cancel();
		}
		else {
			ui.display("\nFinal Borrowing List");
			for (book b : PENDING) {
				ui.display(b.toString());
			}
			COMPLETED = new ArrayList<Loan>();
			ui.setState(BorrowBookUI.UIState.FINALISING);
			state = CONTROL_STATE.FINALISING;
		}
	}


	public void commitLoans() {
		if (!state.equals(CONTROL_STATE.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}	
		for (book b : PENDING) {
			Loan loan = L.issueLoan(b, M);
			COMPLETED.add(loan);			
		}
		ui.display("Completed Loan Slip");
		for (Loan loan : COMPLETED) {
			ui.display(loan.toString());
		}
		ui.setState(BorrowBookUI.UIState.COMPLETED);
		state = CONTROL_STATE.COMPLETED;
	}

	
	public void cancel() {
		ui.setState(BorrowBookUI.UIState.CANCELLED);
		state = CONTROL_STATE.CANCELLED;
	}
	
	
}
