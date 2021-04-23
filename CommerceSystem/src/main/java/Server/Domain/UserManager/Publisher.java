package Server.Domain.UserManager;

import Server.Communication.WSS.Notifier;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Publisher{

    private Map<Integer, Vector<String>> storeSubscribers;
    private UserController userController;
    private Notifier notifier;

    private Publisher()
    {
        storeSubscribers = new ConcurrentHashMap<>();
        userController = UserController.getInstance();
        notifier = Notifier.getInstance();
    }

    private static class CreateSafeThreadSingleton {
        private static final Publisher INSTANCE = new Publisher();
    }

    public static Publisher getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    /**
     * when logged in, a user will receive its pending messages
     * @param name - name of the user to be notified
     * @param msg - the message that will be sent
     */
    public void notify(String name, String msg) {
        notifier.notify(name, msg);
    }

    /**
     * All store owners will be notified on some event
     * @param storeID - the store id the event occurred
     * @param msg - the message that will be sent
     */
    public void notify(int storeID, String msg) {
        Vector<String> users = storeSubscribers.get(storeID);
        User user;

        if(users == null)
            return;

        for(String username: users){

            if(userController.isConnected(username))
            {
                notify(username, msg);
            }
            else {
                user = userController.getUserByName(username);
                user.addPendingMessage(msg);
            }

        }
    }

    public void subscribe(Integer storeID, String username) {
        storeSubscribers.putIfAbsent(storeID, new Vector<>());
        storeSubscribers.get(storeID).add(username);
    }
}
