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
    private int PhoneNo;
    private int memberId;
    private double fines;

    private Map<Integer, Loan> LNS;


    public Member(String lastName, String firstName, String email, int phoneNo, int id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.PhoneNo = phoneNo;
        this.memberId = id;

        this.LNS = new HashMap<>();
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Member:  ").append(memberId).append("\n").append("  Name:  ").append(lastName).append(", ").append(firstName)
                .append("\n").append("  Email: ").append(email).append("\n").append("  Phone: ").append(PhoneNo).append("\n")
                .append(String.format("  Fines Owed :  $%.2f", fines)).append("\n");

        for (Loan loan : LNS.values()) {
            sb.append(loan).append("\n");
        }
        return sb.toString();
    }


    public int getId() {
        return memberId;
    }


    public List<Loan> getLoans() {
        return new ArrayList<Loan>(LNS.values());
    }


    public int getNumberOfCurrentLoans() {
        return LNS.size();
    }


    public double getFinesOwed() {
        return fines;
    }


    public void takeOutLoan(Loan loan) {
        if (!LNS.containsKey(loan.getId())) {
            LNS.put(loan.getId(), loan);
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
        if (LNS.containsKey(loan.getId())) {
            LNS.remove(loan.getId());
        } else {
            throw new RuntimeException("No such loan held by member");
        }
    }
}
