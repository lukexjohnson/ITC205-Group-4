import java.util.Scanner;

//File ready for Static review
public class FixBookUI {

    public static enum UiState {INITIALISED, READY, FIXING, COMPLETED};

    private FixBookControl control;
    private Scanner input;
    private UiState state;


    public FixBookUI(FixBookControl control) {
        this.control = control;
        input = new Scanner(System.in);
        state = UiState.INITIALISED;
        control.setUI(this);
    }


    public void setState(UiState state) {
        this.state = state;
    }


    public void runFixBook() {
        displayOutputMessage("Fix Book Use Case UI\n");

        while (true) {

            switch (state) {

            case READY:
                String bookStr = displayInputMessage("Scan Book (<enter> completes): ");
                if (bookStr.length() == 0) {
                    control.scanningComplete();
                } else {
                    try {
                        int bookId = Integer.valueOf(bookStr).intValue();
                        control.bookScanned(bookId);
                    } catch (NumberFormatException exception) {
                        displayOutputMessage("Invalid bookId");
                    }
                }
                break;

            case FIXING:
                String answer = displayInputMessage("Fix Book? (Y/N) : ");
                boolean fix = false;
                if (answer.toUpperCase().equals("Y")) {
                    fix = true;
                }
                control.fixBook(fix);
                break;

            case COMPLETED:
                displayOutputMessage("Fixing process complete");
                return;

            default:
                displayOutputMessage("Unhandled state");
                throw new RuntimeException("FixBookUI : unhandled state :" + state);
            }
        }
    }


    private String displayInputMessage(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }


    private void displayOutputMessage(Object object) {
        System.out.println(object);
    }


    public void displayMessage(Object object) {
        displayOutputMessage(object);
    }
}
