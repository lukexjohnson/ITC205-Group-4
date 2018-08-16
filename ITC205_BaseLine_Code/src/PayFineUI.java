import java.util.Scanner;

//File ready for Static review
public class PayFineUI {

    public static enum UiState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };

    private PayFineControl control;
    private Scanner input;
    private UiState state;


    public PayFineUI(PayFineControl control) {
        this.control = control;
        input = new Scanner(System.in);
        state = UiState.INITIALISED;
        control.setUI(this);
    }


    public void setState(UiState state) {
        this.state = state;
    }


    public void runPayFine() {
        displayOutputMessage("Pay Fine Use Case UI\n");

        while (true) {

            switch (state) {

            case READY:
                String memberStr = displayInputMessage("Swipe member card (press <enter> to cancel): ");
                if (memberStr.length() == 0) {
                    control.cancelPayingFine();
                    break;
                }
                try {
                    int memberId = Integer.valueOf(memberStr).intValue();
                    control.cardSwiped(memberId);
                } catch (NumberFormatException exception) {
                    displayOutputMessage("Invalid memberId");
                }
                break;

            case PAYING:
                double amount = 0;
                String amountStr = displayInputMessage("Enter amount (<Enter> cancels) : ");
                if (amountStr.length() == 0) {
                    control.cancelPayingFine();
                    break;
                }
                try {
                    amount = Double.valueOf(amountStr).doubleValue();
                } catch (NumberFormatException exception) {
                }
                if (amount <= 0) {
                    displayOutputMessage("Amount must be positive");
                    break;
                }
                control.payFine(amount);
                break;

            case CANCELLED:
                displayOutputMessage("Pay Fine process cancelled");
                return;

            case COMPLETED:
                displayOutputMessage("Pay Fine process complete");
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
