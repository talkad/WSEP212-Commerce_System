package Server.DAL;

public class CacheCleaner implements Runnable{
    public void run(){
        while(true) {
            DALService.getInstance().cleanCache();

            try {
                Thread.sleep(2*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
