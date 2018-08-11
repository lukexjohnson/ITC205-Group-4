import java.util.ArrayList;
import java.util.List;


public class BorrowBookControl {

    private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

    private BorrowBookUI ui;
    private library library;
    private member member;
    private ControlState state;
    private book currentBook;
    
    private List<book> pendingBooks;
    private List<loan> completedLoans;
    


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
            pendingBooks = new ArrayList<>();
            ui.setState(BorrowBookUI.UIState.SCANNING);
            state = ControlState.SCANNING;
        } else {
            ui.display("Member cannot borrow at this time");
            ui.setState(BorrowBookUI.UIState.RESTRICTED);
        }
    }


    public void Scanned(int bookId) {
        currentBook = null;
        if (!state.equals(ControlState.SCANNING)) {
            throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
        }
        currentBook = library.Book(bookId);
        if (currentBook == null) {
            ui.display("Invalid bookId");
            return;
        }
        if (!currentBook.Available()) {
            ui.display("Book cannot be borrowed");
            return;
        }
        pendingBooks.add(currentBook);
        for (book currentBook : pendingBooks) {
            ui.display(currentBook.toString());
        }
        if (library.loansRemainingForMember(member) - pendingBooks.size() == 0) {
            ui.display("Loan limit reached");
            Complete();
        }
    }


    public void Complete() {
        if (pendingBooks.size() == 0) {
            cancelLoans();
        } else {
            ui.display("\nFinal Borrowing List");
            for (book currentBook : pendingBooks) {
                ui.display(currentBook.toString());
            }
            completedLoans = new ArrayList<loan>();
            ui.setState(BorrowBookUI.UIState.FINALISING);
            state = ControlState.FINALISING;
        }
    }


    public void commitLoans() {
        if (!state.equals(ControlState.FINALISING)) {
            throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
        }
        for (book currentBook : pendingBooks) {
            loan loan = library.issueLoan(currentBook, member);
            completedLoans.add(loan);
        }
        ui.display("Completed Loan Slip");
        for (loan loan : completedLoans) {
            ui.display(loan.toString());
        }
        ui.setState(BorrowBookUI.UIState.COMPLETED);
        state = ControlState.COMPLETED;
    }


    public void cancelLoans() {
        ui.setState(BorrowBookUI.UIState.CANCELLED);
        state = ControlState.CANCELLED;
    }
}
