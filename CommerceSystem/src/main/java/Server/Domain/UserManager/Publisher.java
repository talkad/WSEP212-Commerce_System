package Server.Domain.UserManager;

import Server.DAL.DALService;
import Server.DAL.PairDTOs.IntStringListPair;
import Server.DAL.PublisherDTO;
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
        this.storeSubscribers = new ConcurrentHashMap<>();
        userController = UserController.getInstance();
        notifier = Notifier.getInstance();

        PublisherDTO publisherDTO = DALService.getInstance().getPublisher();
        if(publisherDTO != null){
            this.loadFromDTO(publisherDTO);
        }
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
        notifier.notify(name, msg);
    }

    /**
     * All store owners will be notified on some event
     * @param storeID - the store id the event occurred
     * @param msg - the message that will be sent
     */
    public void notify(PermissionsEnum perm, int storeID, ReplyMessage msg) {
        List<String> users = storeSubscribers.get(storeID);
        User user;

        if(users == null)
            return;

        for(String name: users)
            System.out.println(name);

        for(String username: users){

            user = userController.getUserByName(username);

            if(perm == null || (user.getStoresOwned().contains(storeID) || user.getStoresManaged().get(storeID).contains(perm))) {

                if (userController.isConnected(username)) {
                    System.out.println("notification sent to " + username + ": " + msg.getMessage());

                    notify(username, msg);
                } else {
                    user.addPendingMessage(msg);
                }

            }

        }
    }

    public void subscribe(Integer storeID, String username) {
        storeSubscribers.putIfAbsent(storeID, new Vector<>());
        storeSubscribers.get(storeID).add(username);
        DALService.getInstance().savePublisher(this.toDTO());
    }

    public void unsubscribe(Integer storeID, String username) {
        if(storeSubscribers.containsKey(storeID)) {
            storeSubscribers.get(storeID).remove(username);
            DALService.getInstance().savePublisher(this.toDTO());
        }
    }

    // inject the mock notifier for testing - no one should use this function!
    public void setNotifier(Notify notifier){
        this.notifier = notifier;
    }

    public PublisherDTO toDTO(){
        //TODO may need to make thread-safe
        List<IntStringListPair> subscribers = new Vector<>();
        for(int storeID : this.storeSubscribers.keySet()){
            subscribers.add(new IntStringListPair(storeID, new Vector<>(this.storeSubscribers.get(storeID))));
        }

        return new PublisherDTO(subscribers);
    }

    public void loadFromDTO(PublisherDTO publisherDTO){
        //TODO may need to make thread-safe
        List<IntStringListPair> subscribers = publisherDTO.getStoreSubscribers();
        if(subscribers != null){
            for(IntStringListPair pair : subscribers){
                this.storeSubscribers.put(pair.getFirst(), new Vector<>(pair.getSecond()));
            }
        }
    }

}
