import java.util.Date;
import java.util.concurrent.TimeUnit;
//Luke: Ready for static review

public class Calendar {

    private static Calendar self;
    private static java.util.Calendar libraryCalendar;


    private Calendar() {
        libraryCalendar = java.util.Calendar.getInstance();
    }


    public static Calendar getInstance() {
        if (self == null) {
            self = new Calendar();
        }
        return self;
    }


    public void incrementDate(int days) {
        libraryCalendar.add(java.util.Calendar.DATE, days);
    }


    public synchronized void setDate(Date date) {
        try {
            libraryCalendar.setTime(date);
            libraryCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            libraryCalendar.set(java.util.Calendar.MINUTE, 0);
            libraryCalendar.set(java.util.Calendar.SECOND, 0);
            libraryCalendar.set(java.util.Calendar.MILLISECOND, 0);
        }
        catch (Exception exception) {
        throw new RuntimeException(exception);
         }
    }


    public synchronized Date Date() {
        try {
            libraryCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            libraryCalendar.set(java.util.Calendar.MINUTE, 0);
            libraryCalendar.set(java.util.Calendar.SECOND, 0);
            libraryCalendar.set(java.util.Calendar.MILLISECOND, 0);
            return libraryCalendar.getTime();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    public synchronized Date getDueDate(int loanPeriod) {
        Date now = Date();
        libraryCalendar.add(java.util.Calendar.DATE, loanPeriod);
        Date dueDate = libraryCalendar.getTime();
        libraryCalendar.setTime(now);
        return dueDate;
    }


	public synchronized long getDaysDifference(Date targetDate) {
        long diffMillis = Date().getTime() - targetDate.getTime();
        long diffDays = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
        return diffDays;
    }
}
