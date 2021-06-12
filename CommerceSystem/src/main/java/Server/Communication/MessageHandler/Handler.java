package Server.Communication.MessageHandler;


import Server.Domain.CommonClasses.Response;

public abstract class Handler {

    private Handler nextHandler;

    public Handler(Handler nextHandler){
        this.nextHandler = nextHandler;
    }

    public Response<?> handle(String input){
        Response<?> response;

        if(nextHandler != null) {
            try{
                response = nextHandler.handle(input);
                return response;
            }catch(Exception e){
//                e.printStackTrace();
                new Response<>(false, true, "Handler found but an error occurred");
            }
        }

        return new Response<>(false, true, "Invalid input: "+ input);
    }
}
