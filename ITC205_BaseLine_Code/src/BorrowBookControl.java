import java.util.ArrayList;
import java.util.List;
//file ready for static review

public class BorrowBookControl {


    private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING,
        IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

    private BorrowBookUI ui;
    private Library library;
    private Member member;
    private ControlState state;
    private Book currentBook;
    
    private List<Book> pendingBooks;
    private List<Loan> completedLoans;


    public BorrowBookControl() {
        this.library = library.getInstance();
        state = ControlState.INITIALISED;
    }


    public void setUI(BorrowBookUI ui) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("BorrowBookControl:"
                + " cannot call setUI except in INITIALISED state");
        }
        this.ui = ui;
        ui.setState(BorrowBookUI.UIState.READY);
        state = ControlState.READY;
    }


    public void cardSwiped(int memberId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("BorrowBookControl:"
                + " cannot call cardSwiped except in READY state");
        }
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


    public void bookScanned(int bookId) {
        currentBook = null;
        if (!state.equals(ControlState.SCANNING)) {
            throw new RuntimeException("BorrowBookControl:"
                + " cannot call bookScanned except in SCANNING state");
        }
        currentBook = library.getBook(bookId);
        if (currentBook == null) {
            ui.display("Invalid bookId");
            return;
        }
        if (!currentBook.available()) {
            ui.display("Book cannot be borrowed");
            return;
        }
        pendingBooks.add(currentBook);
        for (Book currentBook : pendingBooks) {
            ui.display(currentBook.toString());
        }
        int booksRemaining = library.loansRemainingForMember(member) - pendingBooks.size();
        if (booksRemaining == 0) {
            ui.display("Loan limit reached");
            completeBorrowing();
        }
    }


    public void completeBorrowing() {
        int pendingBooksSize = pendingBooks.size();
        if (pendingBooksSize == 0) {
            cancelBorrowing();
        } else {
            ui.display("\nFinal Borrowing List");
            for (Book currentBook : pendingBooks) {
                ui.display(currentBook.toString());
            }
            completedLoans = new ArrayList<Loan>();
            ui.setState(BorrowBookUI.UIState.FINALISING);
            state = ControlState.FINALISING;
        }
    }


    public void commitLoans() {
        if (!state.equals(ControlState.FINALISING)) {
            throw new RuntimeException("BorrowBookControl:"
                + " cannot call commitLoans except in FINALISING state");
        }
        for (Book currentBook : pendingBooks) {
            Loan loan = library.issueLoan(currentBook, member);
            completedLoans.add(loan);
        }
        ui.display("Completed Loan Slip");
        for (Loan loan : completedLoans) {
            ui.display(loan.toString());
        }
        ui.setState(BorrowBookUI.UIState.COMPLETED);
        state = ControlState.COMPLETED;
    }


    public void cancelBorrowing() {
        ui.setState(BorrowBookUI.UIState.CANCELLED);
        state = ControlState.CANCELLED;
    }
}
