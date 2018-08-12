import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
public class Library implements Serializable {

    private static final String LIBRARY_FILE = "library.obj";
    private static final int LOAN_LIMIT = 2;
    private static final int LOAN_PERIOD = 2;
    private static final double FINE_PER_DAY = 1.0;
    private static final double MAX_FINES_OWED = 5.0;
    private static final double DAMAGE_FEE = 2.0;

    private static Library self;
    private int bookId;
    private int memberId;
    private int libraryId;
    private Date loadDate;

    private Map<Integer, book> catalog;
    private Map<Integer, Member> members;
    private Map<Integer, Loan> loans;
    private Map<Integer, Loan> currentLoans;
    private Map<Integer, book> damagedBooks;


    private Library() {
        catalog = new HashMap<>();
        members = new HashMap<>();
        loans = new HashMap<>();
        currentLoans = new HashMap<>();
        damagedBooks = new HashMap<>();
        bookId = 1;
        memberId = 1;
        libraryId = 1;
    }


    public static synchronized Library getInstance() {
        if (self == null) {
            Path path = Paths.get(LIBRARY_FILE);
            if (Files.exists(path)) {
                try (ObjectInputStream lof 
                    = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {

                    self = (Library) lof.readObject();
                    Calendar.getInstance().setDate(self.loadDate);
                    lof.close();
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            } else
                self = new Library();
        }
        return self;
    }


    public static synchronized void saveToLibraryFile() {
        if (self != null) {
            self.loadDate = Calendar.getInstance().Date();
            try (ObjectOutputStream lof 
                = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
                lof.writeObject(self);
                lof.flush();
                lof.close();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }


    public int getBookId() {
        return bookId;
    }


    public int getMemberId() {
        return memberId;
    }


    private int nextBookId() {
        return bookId++;
    }


    private int nextMemberId() {
        return memberId++;
    }


    private int nextLibraryId() {
        return libraryId++;
    }


    public List<Member> getMembers() {
        return new ArrayList<Member>(members.values());
    }


    public List<book> getBooks() {
        return new ArrayList<book>(catalog.values());
    }


    public List<Loan> getCurrentLoans() {
        return new ArrayList<Loan>(currentLoans.values());
    }


    public Member addMember(String lastName, String firstName, String email, int phoneNo) {
        Member member = new Member(lastName, firstName, email, phoneNo, nextMemberId());
        members.put(member.getMemberId(), member);
        return member;
    }


    public book addBook(String author, String title, String callNo) {
        book book = new book(author, title, callNo, nextBookId());
        catalog.put(book.ID(), book);
        return book;
    }


    public Member getMember(int memberId) {
        if (members.containsKey(memberId)) {
            return members.get(memberId);
        }
        return null;
    }


    public book getBook(int bookId) {
        if (catalog.containsKey(bookId)) {
            return catalog.get(bookId);
        }
        return null;
    }


    public int getLoanLimit() {
        return LOAN_LIMIT;
    }


    public boolean memberCanBorrow(Member member) {
        if (member.getNumberOfCurrentLoans() == LOAN_LIMIT) {
            return false;
        }
        if (member.getFinesOwed() >= MAX_FINES_OWED) {
            return false;
        }
        for (Loan loan : member.getLoans()) {
            if (loan.isOverDue()) {
                return false;
            }
        }
        return true;
    }


    public int loansRemainingForMember(Member member) {
        return LOAN_LIMIT - member.getNumberOfCurrentLoans();
    }


    public Loan issueLoan(book book, Member member) {
        Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
        int libraryId = nextLibraryId();
        Loan loan = new Loan(libraryId, book, member, dueDate);
        member.takeOutLoan(loan);
        book.Borrow();
        loans.put(loan.getId(), loan);
        currentLoans.put(book.ID(), loan);
        return loan;
    }


    public Loan getLoanByBookId(int bookId) {
        if (currentLoans.containsKey(bookId)) {
            return currentLoans.get(bookId);
        }
        return null;
    }


    public double calculateOverDueFine(Loan loan) {
        if (loan.isOverDue()) {
            Date loanDueDate = loan.getDueDate();
            long daysOverDue = Calendar.getInstance().getDaysDifference(loanDueDate);
            double fine = daysOverDue * FINE_PER_DAY;
            return fine;
        }
        return 0.0;
    }


    public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
        Member member = currentLoan.getMember();
        book book = currentLoan.getBook();

        double overDueFine = calculateOverDueFine(currentLoan);
        member.addFine(overDueFine);

        member.dischargeLoan(currentLoan);
        book.Return(isDamaged);
        if (isDamaged) {
            member.addFine(DAMAGE_FEE);
            damagedBooks.put(book.ID(), book);
        }
        currentLoan.dischargeLoan();
        currentLoans.remove(book.ID());
    }


    public void checkCurrentLoans() {
        for (Loan loan : currentLoans.values()) {
            loan.checkOverDue();
        }
    }


    public void repairBook(book currentBook) {
        int currentBookId = currentBook.ID();
        if (damagedBooks.containsKey(currentBookId)) {
            currentBook.Repair();
            damagedBooks.remove(currentBookId);
        } else {
            throw new RuntimeException("Library: repairBook: book is not damaged");
        }
    }
}
