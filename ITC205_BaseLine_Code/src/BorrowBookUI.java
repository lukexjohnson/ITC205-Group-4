import java.util.Scanner;
//file ready for static review


public class BorrowBookUI {

    public static enum UIState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED,
        FINALISING, COMPLETED, CANCELLED };

    private BorrowBookControl control;
    private Scanner input;
    private UIState state;


    public BorrowBookUI(BorrowBookControl control) {
        this.control = control;
        input = new Scanner(System.in);
        state = UIState.INITIALISED;
        control.setUI(this);
    }


    private String takeInput(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }


    private void output(Object object) {
        System.out.println(object);
    }


    public void setState(UIState state) {
        this.state = state;
    }


    public void runBorrowBook() {
        output("Borrow Book Use Case UI\n");

        while (true) {

            switch (state) {

            case CANCELLED:
                output("Borrowing Cancelled");
                return;

            case READY:
                String memberStr = takeInput("Swipe member card (press <enter> to cancel): ");
                if (memberStr.length() == 0) {
                    control.cancelBorrowing();
                    break;
                }
                try {
                    int memberId = Integer.valueOf(memberStr).intValue();
                    control.cardSwiped(memberId);
                } catch (NumberFormatException exception) {
                    output("Invalid Member Id");
                }
                break;

            case RESTRICTED:
                takeInput("Press <any key> to cancel");
                control.cancelBorrowing();
                break;

            case SCANNING:
                String bookStr = takeInput("Scan Book (<enter> completes): ");
                if (bookStr.length() == 0) {
                    control.completeBorrowing();
                    break;
                }
                try {
                    int bookId = Integer.valueOf(bookStr).intValue();
                    control.bookScanned(bookId);

                } catch (NumberFormatException exception) {
                    output("Invalid Book Id");
                }
                break;

            case FINALISING:
                String answer = takeInput("Commit loans? (Y/N): ");
                if (answer.toUpperCase().equals("N")) {
                    control.cancelBorrowing();
                } else {
                    control.commitLoans();
                    takeInput("Press <any key> to complete ");
                }
                break;

            case COMPLETED:
                output("Borrowing Completed");
                return;

            default:
                output("Unhandled state");
                throw new RuntimeException("BorrowBookUI : unhandled state :" + state);
            }
        }
    }


    public void display(Object object) {
        output(object);
    }
}