import java.util.Scanner;


public class BorrowBookUi {
	
	public static enum UiState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, 
		FINALISING, COMPLETED, CANCELLED };

	private BorrowBookControl control;
	private Scanner input;
	private UiState state;

	
	public BorrowBookUi(BorrowBookControl control) {
		this.control = control;
		input = new Scanner(System.in);
		state = UiState.INITIALISED;
		control.setUi(this);
	}

	
	private String input(String prompt) {
		System.out.print(prompt);
		return input.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}
	
			
	public void setState(UiState state) {
		this.state = state;
	}

	
	public void run() {
		output("Borrow Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {			
			
			case CANCELLED:
				output("Borrowing Cancelled");
				return;

				
			case READY:
				String memberStr = input("Swipe member card (press <enter> to cancel): ");
				if (memberStr.length() == 0) {
					control.cancel();
					break;
				}
				try {
					int memberId = Integer.valueOf(memberStr).intValue();
					control.Swiped(memberId);
				}
				catch (NumberFormatException exception) {
					output("Invalid Member Id");
				}
				break;

				
			case RESTRICTED:
				input("Press <any key> to cancel");
				control.cancel();
				break;
			
				
			case SCANNING:
				String bookStr = input("Scan Book (<enter> completes): ");
				if (bookStr.length() == 0) {
					control.Complete();
					break;
				}
				try {
					int bookId = Integer.valueOf(bookStr).intValue();
					control.Scanned(bookId);
					
				} catch (NumberFormatException exception) {
					output("Invalid Book Id");
				} 
				break;
					
				
			case FINALISING:
				String answer = input("Commit loans? (Y/N): ");
				if (answer.toUpperCase().equals("N")) {
					control.cancel();
					
				} else {
					control.commitLoans();
					input("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				output("Borrowing Completed");
				return;
	
				
			default:
				output("Unhandled state");
				throw new RuntimeException("BorrowBookUi : unhandled state :" + state);			
			}
		}		
	}


	public void display(Object object) {
		output(object);		
	}
}
