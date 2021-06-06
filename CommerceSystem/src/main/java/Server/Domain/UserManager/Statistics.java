package Server.Domain.UserManager;

import Server.DAL.DALService;
import Server.DAL.DailyCountersDTO;
import Server.DAL.PublisherDTO;
import Server.Service.Notifier;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;

public class Statistics {
    private LocalDate currentDate;
    private ReadWriteLock dateLock;
    private AtomicInteger dailyGuestCounter;
    private AtomicInteger dailyRegisteredCounter;
    private AtomicInteger dailyManagerCounter;
    private AtomicInteger dailyOwnerCounter;
    private AtomicInteger dailyAdminCounter;

    private Statistics()
    {
        initStats();
    }

    private void initStats(){
        this.currentDate = LocalDate.now();
        DailyCountersDTO counters = DALService.getInstance().getDailyCounters(this.currentDate);

        this.dailyGuestCounter = new AtomicInteger(counters.getGuestCounter());
        this.dailyRegisteredCounter = new AtomicInteger(counters.getRegisteredCounter());
        this.dailyManagerCounter = new AtomicInteger(counters.getManagerCounter());
        this.dailyOwnerCounter = new AtomicInteger(counters.getOwnerCounter());
        this.dailyAdminCounter = new AtomicInteger(counters.getAdminCounter());
    }

    private static class CreateSafeThreadSingleton {
        private static final Statistics INSTANCE = new Statistics();
    }

    public static Statistics getInstance() {
        return Statistics.CreateSafeThreadSingleton.INSTANCE;
    }


    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public ReadWriteLock getDateLock() {
        return dateLock;
    }

    public void incDailyGuestCounter() {
        dailyGuestCounter.incrementAndGet() ;
    }

    public void incDailyRegisteredCounter() {
        dailyRegisteredCounter.incrementAndGet();
    }

    public void incDailyManagerCounter() {
         dailyManagerCounter.incrementAndGet();
    }

    public void incDailyOwnerCounter() {
         dailyOwnerCounter.incrementAndGet();
    }

    public void incDailyAdminCounter() {
         dailyAdminCounter.incrementAndGet();
    }

    public AtomicInteger getDailyGuestCounter() {
        return dailyGuestCounter;
    }

    public AtomicInteger getDailyRegisteredCounter() {
        return dailyRegisteredCounter;
    }

    public AtomicInteger getDailyManagerCounter() {
        return dailyManagerCounter;
    }

    public AtomicInteger getDailyOwnerCounter() {
        return dailyOwnerCounter;
    }

    public AtomicInteger getDailyAdminCounter() {
        return dailyAdminCounter;
    }

    public void checkDateToUpdate(){
        if(this.currentDate.compareTo(LocalDate.now()) < 0){
            currentDate = LocalDate.now();
            this.dailyGuestCounter.set(0);
            this.dailyRegisteredCounter.set(0);
            this.dailyManagerCounter.set(0);
            this.dailyOwnerCounter.set(0);
            this.dailyAdminCounter.set(0);
        }
    }

    public void saveCounters(){
        DailyCountersDTO dailyCountersDTO = new DailyCountersDTO(   this.currentDate.toString(),
                this.dailyGuestCounter.get(),
                this.dailyRegisteredCounter.get(),
                this.dailyManagerCounter.get(),
                this.dailyOwnerCounter.get(),
                this.dailyAdminCounter.get());

        DALService.getInstance().saveCounters(dailyCountersDTO);
    }

}
