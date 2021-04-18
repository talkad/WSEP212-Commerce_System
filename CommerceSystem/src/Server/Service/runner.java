package Server.Service;

import Server.Domain.CommonClasses.Response;

public class runner {
    public static void main(String[] args){
        CommerceService commerceService = CommerceService.getInstance();

        Response<Boolean> res = new Response<>(false, true, "err msg");


    }
}
