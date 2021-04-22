package Server.Communication;


import Server.Domain.CommonClasses.Response;
import com.google.gson.Gson;

public class CommerceHandler {

    private Handler handler;

    private CommerceHandler() {
        buildChainOfResponsibility();
    }

    private void buildChainOfResponsibility() {
        handler = new GuestHandler(new RegisterHandler(new StoreManagerHandler(new SystemManagerHandler(null))));
    }

    private static class CreateSafeThreadSingleton<K>
    {
        private static final CommerceHandler INSTANCE = new CommerceHandler();
    }

    public static CommerceHandler getInstance()
    {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public String handle(String input) {
        Gson gson = new Gson();

        return gson.toJson(handler.handle(input));
    }


}
