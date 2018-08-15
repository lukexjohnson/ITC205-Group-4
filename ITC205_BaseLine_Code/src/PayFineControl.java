public class PayFineControl {
	
  //File ready for Static review
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	
	private PayFineUI ui;
	private ControlState state;
	private Library library;
	private Member member;


	public PayFineControl() {
		this.library = library.getInstance();
		state = ControlState.INITIALISED;
	}
	
	
	public void setUI(PayFineUI ui) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl:"
					+ " cannot call setUI except in INITIALISED state");
		}	
		this.ui = ui;
		ui.setState(PayFineUI.UiState.READY);
		state = ControlState.READY;		
	}

	
	public void cardSwiped(int memberId) {
		if (!state.equals(ControlState.READY)) {
			throw new RuntimeException("PayFineControl:"
					+ " cannot call cardSwiped except in READY state");
		}	
		member = library.getMember(memberId);
		
		if (member == null) {
			ui.display("Invalid Member Id");
			return;
		}
		ui.display(member.toString());
		ui.setState(PayFineUI.UiState.PAYING);
		state = ControlState.PAYING;
	}
	
	
	public void cancel() {
		ui.setState(PayFineUI.UiState.CANCELLED);
		state = ControlState.CANCELLED;
	}

	
	public double payFine(double amount) {
		if (!state.equals(ControlState.PAYING)) {
			throw new RuntimeException("PayFineControl:"
					+ " cannot call payFine except in PAYING state");
		}	
		double change = member.payFine(amount);
		if (change > 0) {
			ui.display(String.format("Change: $%.2f", change));
		}
		ui.display(member.toString());
		ui.setState(PayFineUI.UiState.COMPLETED);
		state = ControlState.COMPLETED;
		return change;
	}
	
}
