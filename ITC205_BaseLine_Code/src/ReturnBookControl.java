//Luke: Ready for static review

public class ReturnBookControl {

    private enum ReturnBookControlState { INITIALISED, READY, INSPECTING };

    private ReturnBookUI ui;
    private ReturnBookControlState state;
    private Library library;
    private Loan currentLoan;


    public ReturnBookControl() {
        this.library = Library.getInstance();
        state = ReturnBookControlState.INITIALISED;
    }


    public void setUI(ReturnBookUI ui) {
        if (!state.equals(ReturnBookControlState.INITIALISED)) {
            throw new RuntimeException("ReturnBookControl: cannot call setUI "
                + "except in INITIALISED state");
        }
        this.ui = ui;
        ui.setState(ReturnBookUI.UIState.READY);
        state = ReturnBookControlState.READY;
    }


    public void bookScanned(int bookId) {
        if (!state.equals(ReturnBookControlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call "
                + "bookScanned except in READY state");
        }
        Book currentBook = library.getBook(bookId);
        if (currentBook == null) {
            ui.displayMessage("Invalid Book Id");
            return;
        }
        if (!currentBook.isOnLoan()) {
            ui.displayMessage("Book has not been borrowed");
            return;
        }
        currentLoan = library.getLoanByBookId(bookId);	
        double overDueFine = 0.0;
        if (currentLoan.isOverDue()) {
            overDueFine = library.calculateOverDueFine(currentLoan);
        }
        ui.displayMessage("Inspecting");
        ui.displayMessage(currentBook.toString());
        ui.displayMessage(currentLoan.toString());

        if (currentLoan.isOverDue()) {
            ui.displayMessage(String.format("\nOverdue fine : $%.2f", overDueFine));
        }
        ui.setState(ReturnBookUI.UIState.INSPECTING);
        state = ReturnBookControlState.INSPECTING;
    }


    public void scanningComplete() {
        if (!state.equals(ReturnBookControlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call "
                + "scanningComplete except in READY state");
        }
        ui.setState(ReturnBookUI.UIState.COMPLETED);
    }


    public void dischargeLoan(boolean isDamaged) {
        if (!state.equals(ReturnBookControlState.INSPECTING)) {
            throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan "
                + "except in INSPECTING state");
        }
        library.dischargeLoan(currentLoan, isDamaged);
        currentLoan = null;
        ui.setState(ReturnBookUI.UIState.READY);
        state = ReturnBookControlState.READY;
    }
}
