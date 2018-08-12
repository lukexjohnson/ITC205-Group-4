import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
public class Member implements Serializable {

    private String lastName;
    private String firstName;
    private String email;
    private int phoneNo;
    private int memberId;
    private double fines;

    private Map<Integer, Loan> currentLoans;


    public Member(String lastName, String firstName, String email, int phoneNo, int memberId) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.memberId = memberId;

        this.currentLoans = new HashMap<>();
    }


    public String toString() {
        StringBuilder buildString = new StringBuilder();
        buildString.append("Member:  ").append(memberId).append("\n").append("  Name:  ")
            .append(lastName).append(", ").append(firstName).append("\n").append("  Email: ")
            .append(email).append("\n").append("  Phone: ").append(phoneNo).append("\n")
            .append(String.format("  Fines Owed :  $%.2f", fines)).append("\n");

        for (Loan loan : currentLoans.values()) {
            buildString.append(loan).append("\n");
        }
        return buildString.toString();
    }


    public int getMemberId() {
        return memberId;
    }


    public List<Loan> getLoans() {
        return new ArrayList<Loan>(currentLoans.values());
    }


    public int getNumberOfCurrentLoans() {
        return currentLoans.size();
    }


    public double getFinesOwed() {
        return fines;
    }


    public void takeOutLoan(Loan loan) {
        int loanId = loan.getId();
        if (!currentLoans.containsKey(loanId)) {
            currentLoans.put(loanId, loan);
        } else {
            throw new RuntimeException("Duplicate loan added to member");
        }
    }


    public String getLastName() {
        return lastName;
    }


    public String getFirstName() {
        return firstName;
    }


    public void addFine(double fine) {
        fines += fine;
    }


    public double payFine(double amount) {
        if (amount < 0) {
            throw new RuntimeException("Member.payFine: amount must be positive");
        }
        double change = 0;
        if (amount > fines) {
            change = amount - fines;
            fines = 0;
        } else {
            fines -= amount;
        }
        return change;
    }


    public void dischargeLoan(Loan loan) {
        int loanId = loan.getId();
        if (currentLoans.containsKey(loanId)) {
            currentLoans.remove(loanId);
        } else {
            throw new RuntimeException("No such loan held by member");
        }
    }
}
