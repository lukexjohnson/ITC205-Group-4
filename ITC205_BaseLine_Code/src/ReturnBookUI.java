import java.util.Scanner;


public class ReturnBookUI {

	public static enum UIState { INITIALISED, READY, INSPECTING, COMPLETED };

	private ReturnBookControl returnBookControl;
	private Scanner input;
	private UIState state;

	
	public ReturnBookUI(ReturnBookControl returnBookControl) {
		this.returnBookControl = returnBookControl;
		input = new Scanner(System.in);
		state = UIState.INITIALISED;
		returnBookControl.setUI(this);

	}

	
	public void run() {		
		output("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
			case INITIALISED:
				break;
				
			case READY:
				String bookStr = input("Scan Book (<enter> completes): ");
				if (bookStr.length() == 0) {
				  returnBookControl.scanningComplete();
				}
				else {
					try {
						int bookId = Integer.valueOf(bookStr).intValue();
						returnBookControl.bookScanned(bookId);
					}
					catch (NumberFormatException e) {
						output("Invalid bookId");
					}
				}
				break;
				
			case INSPECTING:
				String answer = input("Is book damaged? (Y/N): ");
				boolean isDamaged = false;

				if (answer.toUpperCase().equals("Y")) {					

					isDamaged = true;
				}
				returnBookControl.dischargeLoan(isDamaged);
			
			case COMPLETED:
				output("Return processing complete");
				return;
			
			default:
				output("Unhandled state");
				throw new RuntimeException("ReturnBookUI : unhandled state :" + state);			
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
	

	public void setState(UIState state) {
		this.state = state;
	}	

	
}
