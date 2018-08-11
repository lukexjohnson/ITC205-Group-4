import java.util.ArrayList;
import java.util.List;


public class BorrowBookControl {

    private BorrowBookUI ui;

    private library library;
    private member member;

    private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

    private ControlState state;

    private List<book> PENDING;
    private List<loan> COMPLETED;
    private book B;


    public BorrowBookControl() {
        this.library = library.INSTANCE();
        state = ControlState.INITIALISED;
    }


    public void setUI(BorrowBookUI ui) {
        if (!state.equals(ControlState.INITIALISED))
            throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

        this.ui = ui;
        ui.setState(BorrowBookUI.UIState.READY);
        state = ControlState.READY;
    }


    public void Swiped(int memberId) {
        if (!state.equals(ControlState.READY))
            throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

        member = library.getMember(memberId);
        if (member == null) {
            ui.display("Invalid memberId");
            return;
        }
        if (library.memberCanBorrow(member)) {
            PENDING = new ArrayList<>();
            ui.setState(BorrowBookUI.UIState.SCANNING);
            state = ControlState.SCANNING;
        } else {
            ui.display("Member cannot borrow at this time");
            ui.setState(BorrowBookUI.UIState.RESTRICTED);
        }
    }


    public void Scanned(int bookId) {
        B = null;
        if (!state.equals(ControlState.SCANNING)) {
            throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
        }
        B = library.Book(bookId);
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
        if (library.loansRemainingForMember(member) - PENDING.size() == 0) {
            ui.display("Loan limit reached");
            Complete();
        }
    }


    public void Complete() {
        if (PENDING.size() == 0) {
            cancel();
        } else {
            ui.display("\nFinal Borrowing List");
            for (book b : PENDING) {
                ui.display(b.toString());
            }
            COMPLETED = new ArrayList<loan>();
            ui.setState(BorrowBookUI.UIState.FINALISING);
            state = ControlState.FINALISING;
        }
    }


    public void commitLoans() {
        if (!state.equals(ControlState.FINALISING)) {
            throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
        }
        for (book b : PENDING) {
            loan loan = library.issueLoan(b, member);
            COMPLETED.add(loan);
        }
        ui.display("Completed Loan Slip");
        for (loan loan : COMPLETED) {
            ui.display(loan.toString());
        }
        ui.setState(BorrowBookUI.UIState.COMPLETED);
        state = ControlState.COMPLETED;
    }


    public void cancel() {
        ui.setState(BorrowBookUI.UIState.CANCELLED);
        state = ControlState.CANCELLED;
    }
}
