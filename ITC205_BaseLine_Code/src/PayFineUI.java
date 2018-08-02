import java.util.Scanner;


public class PayFineUI {


	public static enum UIState{ INITIALISED, READY, PAYING, COMPLETED, CANCELLED };

	private PayFineControl payFineControl;
	private Scanner input;
	private UIState state;

	public payFineUI(PayFineControl payFineControl) {
		this.payFineControl = payFineControl;
		input = new Scanner(System.in);
		state = UIState.INITIALISED;
		payFineControl.setUI(this);
	}
	
	public void setState(UIState state) {
		this.state = state;
	}

	public void run() {
		output("Pay Fine Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
			case READY:
				String memberStr = input("Swipe member card (press <enter> to cancel): ");
				if (memberStr.length() == 0) {
					payFineControl.cancel();
					break;
				}
				try {
					int memberId = Integer.valueOf(memberStr).intValue();
					payFineControl.cardSwiped(memberId);
				}
				catch (NumberFormatException e) {
					output("Invalid memberId");
				}
				break;
				
			case PAYING:
				double amount = 0;
				String amountStr = input("Enter amount (<Enter> cancels) : ");
				if (amountStr.length() == 0) {
					payFineControl.cancel();
					break;
				}
				try {
					amount = Double.valueOf(amountStr).doubleValue();
				}
				catch (NumberFormatException e) {}
				if (amount <= 0) {
					output("Amount must be positive");
					break;
				}
				payFineControl.payFine(amount);
				break;
								
			case CANCELLED:
				output("Pay Fine process cancelled");
				return;

			case COMPLETED:
				output("Pay Fine process complete");
				return;

			default:
				output("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + state);			
			
			}		
		}		
	}

	private String input(String prompt) {
		System.out.print(prompt);
		return input.nextLine();
	}	
		
	private void output(Object outputObject) {
		System.out.println(outputObject);
	}	

	public void display(Object displayObject) {
		output(displayObject);
	}
}
