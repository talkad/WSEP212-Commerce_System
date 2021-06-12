package Server.Domain.UserManager;


import Server.DAL.DALControllers.DALProxy;
import Server.DAL.DomainDTOs.DailyCountersDTO;
import Server.Service.DataObjects.ReplyMessage;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Statistics {
    private LocalDate currentDate;
    private ReadWriteLock lock;
    private AtomicInteger dailyGuestCounter;
    private AtomicInteger dailyRegisteredCounter;
    private AtomicInteger dailyManagerCounter;
    private AtomicInteger dailyOwnerCounter;
    private AtomicInteger dailyAdminCounter;

    private Statistics()
    {
        this.lock = new ReentrantReadWriteLock();
        initStats();
    }

    private void initStats(){
        this.currentDate = LocalDate.now();
        DailyCountersDTO counters = DALProxy.getInstance().getDailyCounters(this.currentDate);

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

    public void incDailyGuestCounter() {
        lock.writeLock().lock();
        checkDateToUpdate();
        dailyGuestCounter.incrementAndGet();
        saveCounters();
        lock.writeLock().unlock();

        if(UserController.getInstance().isConnected("a1")){
            Gson gson = new Gson();
            Publisher.getInstance().notify("a1", new ReplyMessage("liveUpdate", gson.toJson(getCounters()), "dailyStatistics"));
        }
    }

    public void incDailyRegisteredCounter() {
        lock.writeLock().lock();
        checkDateToUpdate();
        dailyRegisteredCounter.incrementAndGet();
        saveCounters();
        lock.writeLock().unlock();

        if(UserController.getInstance().isConnected("a1")){
            Gson gson = new Gson();
            Publisher.getInstance().notify("a1", new ReplyMessage("liveUpdate", gson.toJson(getCounters()), "dailyStatistics"));
        }
    }

    public void incDailyManagerCounter() {
        lock.writeLock().lock();
        checkDateToUpdate();
        dailyManagerCounter.incrementAndGet();
        saveCounters();
        lock.writeLock().unlock();

        if(UserController.getInstance().isConnected("a1")){
            Gson gson = new Gson();
            Publisher.getInstance().notify("a1", new ReplyMessage("liveUpdate", gson.toJson(getCounters()), "dailyStatistics"));
        }
    }

    public void incDailyOwnerCounter() {
        lock.writeLock().lock();
        checkDateToUpdate();
        dailyOwnerCounter.incrementAndGet();
        saveCounters();
        lock.writeLock().unlock();

        if(UserController.getInstance().isConnected("a1")){
            Gson gson = new Gson();
            Publisher.getInstance().notify("a1", new ReplyMessage("liveUpdate", gson.toJson(getCounters()), "dailyStatistics"));
        }
    }

    public void incDailyAdminCounter() {
        lock.writeLock().lock();
        checkDateToUpdate();
        dailyAdminCounter.incrementAndGet();
        saveCounters();
        lock.writeLock().unlock();

        if(UserController.getInstance().isConnected("a1")){
            Gson gson = new Gson();
            Publisher.getInstance().notify("a1", new ReplyMessage("liveUpdate", gson.toJson(getCounters()), "dailyStatistics"));
        }
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

    private void checkDateToUpdate(){
        if(this.currentDate.compareTo(LocalDate.now()) < 0){
            currentDate = LocalDate.now();
            this.dailyGuestCounter.set(0);
            this.dailyRegisteredCounter.set(0);
            this.dailyManagerCounter.set(0);
            this.dailyOwnerCounter.set(0);
            this.dailyAdminCounter.set(0);
        }
    }

    private void saveCounters(){
        DailyCountersDTO dailyCountersDTO = new DailyCountersDTO(   this.currentDate.toString(),
                this.dailyGuestCounter.get(),
                this.dailyRegisteredCounter.get(),
                this.dailyManagerCounter.get(),
                this.dailyOwnerCounter.get(),
                this.dailyAdminCounter.get());

        DALProxy.getInstance().saveCounters(dailyCountersDTO);
    }

    private List<String> getCounters(){

        return Arrays.asList("Guest: " + getDailyGuestCounter().get(),
                "Registered: " + getDailyRegisteredCounter().get(),
                "Manager: " + getDailyManagerCounter().get(),
                "Owner: " + getDailyOwnerCounter().get(),
                "Admin: " + getDailyAdminCounter().get()
        );

    }

    protected void zeroCounters(){
        this.dailyGuestCounter.set(0);
        this.dailyRegisteredCounter.set(0);
        this.dailyManagerCounter.set(0);
        this.dailyOwnerCounter.set(0);
        this.dailyAdminCounter.set(0);
    }
}
