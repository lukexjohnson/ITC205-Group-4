import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Main {

    private static Scanner takeInput;
    private static Library library;
    private static String libraryMenu;
    private static Calendar calendar;
    private static SimpleDateFormat simpleDateFormat;

    private static String getMenu() {
        StringBuilder buildString = new StringBuilder();

        buildString.append("\nLibrary Main Menu\n\n").append("  M  : add member\n").append("  LM : list members\n")
                .append("\n").append("  B  : add book\n").append("  LB : list books\n").append("  FB : fix books\n")
                .append("\n").append("  L  : take out a loan\n").append("  R  : return a loan\n")
                .append("  LL : list loans\n").append("\n").append("  P  : pay fine\n").append("\n")
                .append("  T  : increment date\n").append("  Q  : quit\n").append("\n").append("Choice : ");

        return buildString.toString();
    }


    public static void main(String[] args) {
        try {
            takeInput = new Scanner(System.in);
            library = library.getInstance(); // change library.INSTANCE to Library.INSTANCE
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (Member member : library.getMembers()) {
                output(member);
            }
            output(" ");
            for (Book book : library.getBooks()) {
                output(book);
            }

            libraryMenu = getMenu();

            boolean e = false;

            while (!e) {

                output("\n" + simpleDateFormat.format(calendar.Date()));
                String c = input(libraryMenu);

                switch (c.toUpperCase()) {

                case "M":
                    addMember();
                    break;

                case "LM":
                    listMembers();
                    break;

                case "B":
                    addBook();
                    break;

                case "LB":
                    listBooks();
                    break;

                case "FB":
                    fixBooks();
                    break;

                case "L":
                    borrowBook();
                    break;

                case "R":
                    returnBook();
                    break;

                case "LL":
                    listCurrentLoans();
                    break;

                case "P":
                    payFine();
                    break;

                case "T":
                    incrementDate();
                    break;

                case "Q":
                    e = true;
                    break;

                default:
                    output("\nInvalid option\n");
                    break;
                }

                Library.saveToLibraryFile();
            }
        } catch (RuntimeException e) {
            output(e);
        }
        output("\nEnded\n");
    }


    private static void payFine() {
        new PayFineUI(new PayFineControl()).runPayFine();
    }


    private static void listCurrentLoans() {
        output("");
        for (Loan loan : library.getCurrentLoans()) {
            output(loan + "\n");
        }
    }

    
    private static void listBooks() {
        output("");
        for (Book book : library.getBooks()) {
            output(book + "\n");
        }
    }

    private static void listMembers() {
        output("");
        for (Member member : library.getMembers()) {
            output(member + "\n");
        }
    }


    private static void borrowBook() {
        new BorrowBookUI(new BorrowBookControl()).run();
    }


    private static void returnBook() {
        new ReturnBookUI(new ReturnBookControl()).run();
    }


    private static void fixBooks() {
        new FixBookUI(new FixBookControl()).runFixBook();
    }


    private static void incrementDate() {
        try {
            int days = Integer.valueOf(input("Enter number of days: ")).intValue();
            calendar.incrementDate(days);
            library.checkCurrentLoans();
            output(simpleDateFormat.format(calendar.Date()));

        } catch (NumberFormatException e) {
            output("\nInvalid number of days\n");
        }
    }


    private static void addBook() {
        String author = input("Enter author: ");
        String title = input("Enter title: ");
        String callNo = input("Enter call number: ");
        Book book = library.addBook(author, title, callNo);
        output("\n" + book + "\n");
    }


    private static void addMember() {
        try {
            String lastName = input("Enter last name: ");
            String firstName = input("Enter first name: ");
            String email = input("Enter email: ");
            int phoneNo = Integer.valueOf(input("Enter phone number: ")).intValue();
            Member member = library.addMember(lastName, firstName, email, phoneNo);
            output("\n" + member + "\n");

        } catch (NumberFormatException e) {
            output("\nInvalid phone number\n");
        }
    }

    private static String input(String prompt) {
        System.out.print(prompt);
        return takeInput.nextLine();
    }

    private static void output(Object outputObject) {
        System.out.println(outputObject);
    }
}
