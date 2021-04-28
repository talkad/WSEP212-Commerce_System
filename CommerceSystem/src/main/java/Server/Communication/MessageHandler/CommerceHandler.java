package Server.Communication.MessageHandler;


import Server.Domain.CommonClasses.Response;
import com.google.gson.Gson;

import java.util.Properties;

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

    public Response<?> handle(String input) {
        Gson gson = new Gson();
        Properties data = gson.fromJson(input, Properties.class);

        if(data == null)
            return new Response<>(false, true, "Not a valid Get Request - may be the handshake");

        return handler.handle(input);
    }


}
