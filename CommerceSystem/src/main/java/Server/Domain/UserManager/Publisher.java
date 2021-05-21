package Server.Domain.UserManager;

import Server.DAL.PublisherDTO;
import Server.Domain.CommonClasses.Pair;
import Server.Service.DataObjects.ReplyMessage;
import Server.Service.Notifier;
import Server.Service.Notify;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Publisher{

    private Map<Integer, List<String>> storeSubscribers;
    private UserController userController;
    private Notify notifier;

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
    public void notify(String name, ReplyMessage msg) {
        System.out.println("server notifies to " + name + " the message " + msg.getMessage());
        notifier.notify(name, msg);
    }

    /**
     * All store owners will be notified on some event
     * @param storeID - the store id the event occurred
     * @param msg - the message that will be sent
     */
    public void notify(int storeID, ReplyMessage msg) {
        List<String> users = storeSubscribers.get(storeID);
        User user;

        if(users == null)
            return;

        for(String username: users){

            if(userController.isConnected(username))
            {
                System.out.println("notification sent to " + username + ": " + msg.getMessage());

                notify(username, msg);
            }
            else {
                System.out.println("notification added to pending |" + username + ": " + msg.getMessage());
                user = userController.getUserByName(username);
                user.addPendingMessage(msg);
            }

        }
    }

    public void subscribe(Integer storeID, String username) {
        storeSubscribers.putIfAbsent(storeID, new Vector<>());
        storeSubscribers.get(storeID).add(username);
    }

    public void unsubscribe(Integer storeID, String username) {
        if(storeSubscribers.containsKey(storeID))
            storeSubscribers.get(storeID).remove(username);
    }

    // inject the mock notifier for testing - no one should use this function!
    public void setNotifier(Notify notifier){
        this.notifier = notifier;
    }

    public PublisherDTO toDTO(){
        //TODO may need to make thread-safe
        List<Pair<Integer, List<String>>> subscribers = new Vector<>();
        for(int storeID : this.storeSubscribers.keySet()){
            subscribers.add(new Pair<>(storeID, new Vector<>(this.storeSubscribers.get(storeID))));
        }

        return new PublisherDTO(subscribers);
    }

    public void loadFromDTO(PublisherDTO publisherDTO){
        //TODO may need to make thread-safe
        List<Pair<Integer, List<String>>> subscribers = publisherDTO.getStoreSubscribers();
        if(subscribers != null){
            for(Pair<Integer, List<String>> pair : subscribers){
                this.storeSubscribers.put(pair.getFirst(), new Vector<>(pair.getSecond()));
            }
        }
    }

}
