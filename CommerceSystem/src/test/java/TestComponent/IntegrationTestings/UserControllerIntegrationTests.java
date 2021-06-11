package TestComponent.IntegrationTestings;

import Server.DAL.DALControllers.DALService;
import Server.DAL.DomainDTOs.UserDTO;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.StoreDiscountRule;
import Server.Domain.ShoppingManager.Predicates.BasketPredicate;
import Server.Domain.ShoppingManager.PurchaseRules.BasketPurchaseRule;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.PermissionsEnum;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import Server.Service.CommerceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class UserControllerIntegrationTests {

    @Before
    public void setUp(){
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().startDB();
        DALService.getInstance().resetDatabase();
    }

    @Test
    public void appointOwnerTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registrations
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();

        // opening the store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        // verifying requested user isn't already an owner or manager
        User tal = userController.getConnectedUsers().get("tal");
        Assert.assertFalse(tal.isOwner(storeID));
        Assert.assertFalse(tal.isManager(storeID));

        userController.appointOwner(newUserName, "tal", storeID);

        // verify the user is an owner
        tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isOwner(storeID));
    }

    @Test
    public void appointOwnerTestFailureAlreadyOwner(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registrations
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal", "kadosh");

        // opening the store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        // initial appointment as owner
        userController.appointOwner(newUserName, "tal", storeID);

        // verify the requested user is an owner
        User tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isOwner(storeID));

        // appointment as owner while already an owner
        Assert.assertTrue(userController.appointOwner(newUserName, "tal", storeID).isFailure());

        // verify post conditions
        tal = userController.getConnectedUsers().get("tal");
        Assert.assertFalse(tal.isManager(storeID));
        Assert.assertTrue(tal.isOwner(storeID));
    }

    @Test
    public void appointOwnerTestFailureAlreadyManager(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registrations
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal", "kadosh");

        // opening the store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        // initial appointment as manager
        userController.appointManager(newUserName, "tal", storeID);

        // verify the requested user is a manager
        User tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isManager(storeID));

        // appointment as owner while already a manager
        Assert.assertTrue(userController.appointOwner(newUserName, "tal", storeID).isFailure());

        // verify post conditions
        tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isManager(storeID));
        Assert.assertFalse(tal.isOwner(storeID));
    }

    @Test
    public void appointManagerTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registrations
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal", "kadosh");

        // opening the store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        // verifying requested user isn't already an owner or manager
        User tal = userController.getConnectedUsers().get("tal");
        Assert.assertFalse(tal.isOwner(storeID));
        Assert.assertFalse(tal.isManager(storeID));

        userController.appointManager(newUserName, "tal", storeID);

        // verify the user is a manager
        tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isManager(storeID));
    }

    @Test
    public void appointManagerTestFailureAlreadyOwner(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registrations
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal", "kadosh");

        // opening the store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        // initial appointment as owner
        userController.appointOwner(newUserName, "tal", storeID);

        // verify the requested user is an owner
        User tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isOwner(storeID));

        // appointment as manager while already an owner
        Assert.assertTrue(userController.appointManager(newUserName, "tal", storeID).isFailure());

        // verify post conditions
        tal = userController.getConnectedUsers().get("tal");
        Assert.assertFalse(tal.isManager(storeID));
        Assert.assertTrue(tal.isOwner(storeID));
    }

    @Test
    public void appointManagerTestFailureAlreadyManager(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registrations
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal", "kadosh");

        // opening the store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        // initial appointment as manager
        userController.appointManager(newUserName, "tal", storeID);

        // verify the requested user is a manager
        User tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isManager(storeID));

        // appointment as manager while already a manager
        Assert.assertTrue(userController.appointManager(newUserName, "tal", storeID).isFailure());

        // verify post conditions
        tal = userController.getConnectedUsers().get("tal");
        Assert.assertTrue(tal.isManager(storeID));
        Assert.assertFalse(tal.isOwner(storeID));
    }

    @Test
    public void removeManagerTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");

        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal9", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isManager(storeID));

        userController.appointManager(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isManager(storeID));

        userController.removeManagerAppointment(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isManager(storeID));
    }

    @Test
    public void removeManagerTestFailureNotAppointed(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registration
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");
        // initial login
        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal9", "kadosh");
        // open store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();
        // verify requested user isn't manager
        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isManager(storeID));
        // unsuccessful manager removal
        Assert.assertTrue(userController.removeManagerAppointment(newUserName, "tal9", storeID).isFailure());
        // verify post condition
        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isManager(storeID));
    }

    @Test
    public void removeManagerTestFailureNotAppointedOwnerByInitiator(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registration
        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");
        userController.register(initialUserName, "notTheBoss", "lol");
        // initial login
        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        login = userController.login(guest2, "notTheBoss", "lol");
        String notTheBoss = login.getResult();
        userController.login(guest, "tal9", "kadosh");
        // open store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();
        // appoint additional owner
        userController.appointOwner(newUserName, "notTheBoss", storeID);
        userController.appointManager(newUserName, "tal9", storeID);
        // verify requested user is a manager
        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isManager(storeID));
        // unsuccessful manager removal by another owner
        Assert.assertTrue(userController.removeManagerAppointment(notTheBoss, "tal9", storeID).isFailure());
        // verify post condition
        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isManager(storeID));
    }

    @Test
    public void removeManagerTestFailureIsOwner(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");

        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal9", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isOwner(storeID));

        userController.appointOwner(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isOwner(storeID));

        Assert.assertTrue(userController.removeManagerAppointment(newUserName, "tal9", storeID).isFailure());

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isOwner(storeID));
    }

    @Test
    public void removeOwnerTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");

        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal9", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isOwner(storeID));

        userController.appointOwner(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isOwner(storeID));

        userController.removeOwnerAppointment(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isOwner(storeID));
    }

    @Test
    public void removeOwnerTestFailureNotAppointed(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registration
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");
        // initial login
        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal9", "kadosh");
        // open store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();
        // verify requested user isn't owner
        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isOwner(storeID));
        // unsuccessful owner removal
        Assert.assertTrue(userController.removeOwnerAppointment(newUserName, "tal9", storeID).isFailure());
        // verify post condition
        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isOwner(storeID));
    }

    @Test
    public void removeOwnerTestFailureNotAppointedOwnerByInitiator(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // initial user registration
        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");
        userController.register(initialUserName, "notTheBoss", "lol");
        // initial login
        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        login = userController.login(guest2, "notTheBoss", "lol");
        String notTheBoss = login.getResult();
        userController.login(guest, "tal9", "kadosh");
        // open store
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();
        // appoint additional owner
        userController.appointOwner(newUserName, "notTheBoss", storeID);
        userController.appointOwner(newUserName, "tal9", storeID);
        // verify requested user is an owner
        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isOwner(storeID));
        // unsuccessful owner removal by another owner
        Assert.assertTrue(userController.removeOwnerAppointment(notTheBoss, "tal9", storeID).isFailure());
        // verify post condition
        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isOwner(storeID));
    }

    @Test
    public void removeOwnerTestFailureIsManager(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");

        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal9", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal9");
        Assert.assertFalse(tal.isManager(storeID));

        userController.appointManager(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isManager(storeID));

        Assert.assertTrue(userController.removeOwnerAppointment(newUserName, "tal9", storeID).isFailure());

        tal = userController.getConnectedUsers().get("tal9");
        Assert.assertTrue(tal.isManager(storeID));
    }

    @Test
    public void cascadingRemovalTestSuccessfulRemoval() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        String guest3 = commerceService.addGuest().getResult();
        String guest4 = commerceService.addGuest().getResult();
        String guest5 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal7", "kadosh");
        userController.register(initialUserName, "yoni7", "pis");
        userController.register(initialUserName, "jacob7", "lol");
        userController.register(initialUserName, "shaked67", "lol");
        userController.register(initialUserName, "aviad7", "lol");
        userController.register(initialUserName, "almog7", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni7", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> talLogin = userController.login(guest, "tal7", "kadosh");
        String talUserName = talLogin.getResult();
        Response<String> jacobLogin = userController.login(guest2, "jacob7", "lol");
        String jacobUserName = jacobLogin.getResult();
        userController.login(guest3, "shaked67", "lol");
        userController.login(guest4, "aviad7", "lol");
        userController.login(guest5, "almog7", "lol");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore7");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, "tal7", storeID);
        userController.appointManager(talUserName, "jacob7", storeID);
        userController.addPermission(talUserName, storeID, "jacob7", PermissionsEnum.APPOINT_MANAGER);
        userController.appointOwner(talUserName, "aviad7", storeID);
        userController.appointOwner(talUserName, "almog7", storeID);
        userController.appointManager(jacobUserName, "shaked67", storeID);

        Assert.assertTrue(userController.getConnectedUsers().get("yoni7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("tal7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("jacob7").isManager(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("aviad7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("almog7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("shaked67").isManager(storeID));

        userController.removeOwnerAppointment(yoniUserName, "tal7", storeID);

        Assert.assertTrue(userController.getConnectedUsers().get("yoni7").isOwner(storeID));
        Assert.assertFalse(userController.getConnectedUsers().get("tal7").isOwner(storeID));
        Assert.assertFalse(userController.getConnectedUsers().get("jacob7").isManager(storeID));
        Assert.assertFalse(userController.getConnectedUsers().get("aviad7").isOwner(storeID));
        Assert.assertFalse(userController.getConnectedUsers().get("almog7").isOwner(storeID));
        Assert.assertFalse(userController.getConnectedUsers().get("shaked67").isManager(storeID));
    }

    @Test
    public void cascadingRemovalTestUnsuccessfulRemoval() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        String guest3 = commerceService.addGuest().getResult();
        String guest4 = commerceService.addGuest().getResult();
        String guest5 = commerceService.addGuest().getResult();
        String guest6 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal7", "kadosh");
        userController.register(initialUserName, "yoni7", "pis");
        userController.register(initialUserName, "jacob7", "lol");
        userController.register(initialUserName, "shaked67", "lol");
        userController.register(initialUserName, "aviad7", "lol");
        userController.register(initialUserName, "almog7", "lol");
        userController.register(initialUserName, "notTheBoss", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni7", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> talLogin = userController.login(guest, "tal7", "kadosh");
        String talUserName = talLogin.getResult();
        Response<String> jacobLogin = userController.login(guest2, "jacob7", "lol");
        String jacobUserName = jacobLogin.getResult();
        Response<String> notTheBossLogin = userController.login(guest6, "notTheBoss", "lol");
        String notTheBossUserName = notTheBossLogin.getResult();
        userController.login(guest3, "shaked67", "lol");
        userController.login(guest4, "aviad7", "lol");
        userController.login(guest5, "almog7", "lol");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore7");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, "tal7", storeID);
        userController.appointManager(talUserName, "jacob7", storeID);
        userController.addPermission(talUserName, storeID, "jacob7", PermissionsEnum.APPOINT_MANAGER);
        userController.appointOwner(talUserName, "aviad7", storeID);
        userController.appointOwner(talUserName, "almog7", storeID);
        userController.appointManager(jacobUserName, "shaked67", storeID);
        userController.appointOwner(yoniUserName, "notTheBoss", storeID);

        Assert.assertTrue(userController.getConnectedUsers().get("yoni7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("tal7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("jacob7").isManager(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("aviad7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("almog7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("shaked67").isManager(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("notTheBoss").isOwner(storeID));

        Assert.assertTrue(userController.removeOwnerAppointment(notTheBossUserName, "tal7", storeID).isFailure());

        Assert.assertTrue(userController.getConnectedUsers().get("yoni7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("tal7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("jacob7").isManager(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("aviad7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("almog7").isOwner(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("shaked67").isManager(storeID));
        Assert.assertTrue(userController.getConnectedUsers().get("notTheBoss").isOwner(storeID));
    }

    @Test
    public void addPermissionTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal6", "kadosh");
        userController.register(initialUserName, "yoni6", "pis");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni6", "pis");
        String yoniUserName = yoniLogin.getResult();
        userController.login(guest, "tal6", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore6");
        int storeID = storeRes.getResult();

        userController.appointManager(yoniUserName, "tal6", storeID);

        Assert.assertFalse(userController.getConnectedUsers().get("tal6").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));

        userController.addPermission(yoniUserName, storeID, "tal6", PermissionsEnum.ADD_PRODUCT_TO_STORE);

        Assert.assertTrue(userController.getConnectedUsers().get("tal6").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));

    }

    @Test
    public void addPermissionTestFailureNotManager(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal6", "kadosh");
        userController.register(initialUserName, "yoni6", "pis");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni6", "pis");
        String yoniUserName = yoniLogin.getResult();
        userController.login(guest, "tal6", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore6");
        int storeID = storeRes.getResult();

        Assert.assertFalse(userController.getConnectedUsers().get("tal6").getStoresManaged().containsKey(storeID));
        // attempted adding permission to someone not a manager
        Assert.assertTrue(userController.addPermission(yoniUserName, storeID, "tal6", PermissionsEnum.ADD_PRODUCT_TO_STORE).isFailure());

        Assert.assertFalse(userController.getConnectedUsers().get("tal6").getStoresManaged().containsKey(storeID));

    }

    @Test
    public void addPermissionTestFailureNotPermittedToAdd(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal6", "kadosh");
        userController.register(initialUserName, "yoni6", "pis");
        userController.register(initialUserName, "bruh", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni6", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> bruhLogin = userController.login(guest2, "bruh", "lol");
        String bruhUserName = bruhLogin.getResult();
        userController.login(guest, "tal6", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore6");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, bruhUserName, storeID);
        userController.appointManager(yoniUserName, "tal6", storeID);

        Assert.assertFalse(userController.getConnectedUsers().get("tal6").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));
        // attempting to add permission to manager without permission
        Assert.assertTrue(userController.addPermission(bruhUserName, storeID, "tal6", PermissionsEnum.ADD_PRODUCT_TO_STORE).isFailure());

        Assert.assertFalse(userController.getConnectedUsers().get("tal6").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));

    }

    @Test
    public void removePermissionTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal5", "kadosh");
        userController.register(initialUserName, "yoni5", "pis");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni5", "pis");
        String yoniUserName = yoniLogin.getResult();
        userController.login(guest, "tal5", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore5");
        int storeID = storeRes.getResult();

        userController.appointManager(yoniUserName, "tal5", storeID);
        userController.addPermission(yoniUserName, storeID, "tal5", PermissionsEnum.ADD_PRODUCT_TO_STORE);

        Assert.assertTrue(userController.getConnectedUsers().get("tal5").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));

        userController.removePermission(yoniUserName, storeID, "tal5", PermissionsEnum.ADD_PRODUCT_TO_STORE);

        Assert.assertFalse(userController.getConnectedUsers().get("tal5").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));
    }

    @Test
    public void removePermissionTestFailureNotManager(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal5", "kadosh");
        userController.register(initialUserName, "yoni5", "pis");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni5", "pis");
        String yoniUserName = yoniLogin.getResult();
        userController.login(guest, "tal5", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore5");
        int storeID = storeRes.getResult();

        Assert.assertFalse(userController.getConnectedUsers().get("tal5").getStoresManaged().containsKey(storeID));

        Assert.assertTrue(userController.removePermission(yoniUserName, storeID, "tal5", PermissionsEnum.ADD_PRODUCT_TO_STORE).isFailure());

        Assert.assertFalse(userController.getConnectedUsers().get("tal5").getStoresManaged().containsKey(storeID));
    }

    @Test
    public void removePermissionTestFailureNotPermittedToRemove(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal5", "kadosh");
        userController.register(initialUserName, "yoni5", "pis");
        userController.register(initialUserName, "bruh", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni5", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> bruhLogin = userController.login(guest2, "bruh", "lol");
        String bruhUserName = bruhLogin.getResult();
        userController.login(guest, "tal5", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore5");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, bruhUserName, storeID);
        userController.appointManager(yoniUserName, "tal5", storeID);
        userController.addPermission(yoniUserName, storeID, "tal5", PermissionsEnum.ADD_PRODUCT_TO_STORE);

        Assert.assertTrue(userController.getConnectedUsers().get("tal5").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));
        // attempted to remove permission when not allowed
        Assert.assertTrue(userController.removePermission(bruhUserName, storeID, "tal5", PermissionsEnum.ADD_PRODUCT_TO_STORE).isFailure());

        Assert.assertTrue(userController.getConnectedUsers().get("tal5").getStoresManaged().get(storeID).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));
    }

    @Test
    public void getStoreWorkersDetailsTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        String guest3 = commerceService.addGuest().getResult();
        String guest4 = commerceService.addGuest().getResult();
        String guest5 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal4", "kadosh");
        userController.register(initialUserName, "yoni4", "pis");
        userController.register(initialUserName, "jacob4", "lol");
        userController.register(initialUserName, "shaked64", "lol");
        userController.register(initialUserName, "aviad4", "lol");
        userController.register(initialUserName, "almog4", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni4", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> talLogin = userController.login(guest, "tal4", "kadosh");
        String talUserName = talLogin.getResult();
        Response<String> jacobLogin = userController.login(guest2, "jacob4", "lol");
        String jacobUserName = jacobLogin.getResult();
        userController.login(guest3, "shaked64", "lol");
        userController.login(guest4, "aviad4", "lol");
        userController.login(guest5, "almog4", "lol");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, "tal4", storeID);
        userController.appointManager(talUserName, "jacob4", storeID);
        userController.addPermission(talUserName, storeID, "jacob4", PermissionsEnum.APPOINT_MANAGER);
        userController.appointOwner(talUserName, "aviad4", storeID);
        userController.appointOwner(talUserName, "almog4", storeID);
        userController.appointManager(jacobUserName, "shaked64", storeID);

        Response<List<UserDTO>> result = userController.getStoreWorkersDetails(yoniUserName, storeID);
        Assert.assertFalse(result.isFailure());
        List<UserDTO> actualUsers = result.getResult();

        List<String> users = new Vector<>();
        users.add("yoni4");
        users.add("tal4");
        users.add("jacob4");
        users.add("aviad4");
        users.add("almog4");
        users.add("shaked64");

        Assert.assertEquals(6, users.size());
        for(UserDTO user : actualUsers){
            Assert.assertTrue(users.contains(user.getName()));
        }
    }

    @Test
    public void getStoreWorkersDetailsTestFailureNoPermission(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        String guest3 = commerceService.addGuest().getResult();
        String guest4 = commerceService.addGuest().getResult();
        String guest5 = commerceService.addGuest().getResult();
        String guest6 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal4", "kadosh");
        userController.register(initialUserName, "yoni4", "pis");
        userController.register(initialUserName, "jacob4", "lol");
        userController.register(initialUserName, "shaked64", "lol");
        userController.register(initialUserName, "aviad4", "lol");
        userController.register(initialUserName, "almog4", "lol");
        userController.register(initialUserName, "bruh", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni4", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> talLogin = userController.login(guest, "tal4", "kadosh");
        String talUserName = talLogin.getResult();
        Response<String> jacobLogin = userController.login(guest2, "jacob4", "lol");
        String jacobUserName = jacobLogin.getResult();
        Response<String> bruhLogin = userController.login(guest6, "bruh", "lol");
        String bruhUserName = bruhLogin.getResult();
        userController.login(guest3, "shaked64", "lol");
        userController.login(guest4, "aviad4", "lol");
        userController.login(guest5, "almog4", "lol");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, "tal4", storeID);
        userController.appointManager(talUserName, "jacob4", storeID);
        userController.addPermission(talUserName, storeID, "jacob4", PermissionsEnum.APPOINT_MANAGER);
        userController.appointOwner(talUserName, "aviad4", storeID);
        userController.appointOwner(talUserName, "almog4", storeID);
        userController.appointManager(jacobUserName, "shaked64", storeID);

        Response<List<UserDTO>> result = userController.getStoreWorkersDetails(bruhUserName, storeID);
        Assert.assertTrue(result.isFailure());
    }

    @Test
    public void getPurchaseDetailsTestSuccess() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<Collection<PurchaseClientDTO>> response;

        String initialUserName = UserController.getInstance().addGuest().getResult();
        UserController.getInstance().register(initialUserName, "tal4", "kadosh");
        String tal = UserController.getInstance().login(initialUserName, "tal4", "kadosh").getResult();

        int storeID = UserController.getInstance().openStore(tal, "Apple").getResult();
        int productID = 65482;
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(tal, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(tal, paymentDetails, supplyDetails);

        response = UserController.getInstance().getPurchaseDetails(tal, storeID);
        Assert.assertFalse(response.isFailure());
        Assert.assertEquals(1, response.getResult().size());

        PurchaseClientDTO purchase = response.getResult().iterator().next();

        Assert.assertEquals(22500, (int)purchase.getTotalPrice());
        Assert.assertEquals(LocalDate.now().toString(), purchase.getPurchaseDate());
        Assert.assertEquals(storeID, purchase.getBasket().getStoreID());
    }

    @Test
    public void getPurchaseDetailsTestFailureNoPermissions() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<Collection<PurchaseClientDTO>> response;

        String initialUserName = UserController.getInstance().addGuest().getResult();
        String guestName2 = UserController.getInstance().addGuest().getResult();
        UserController.getInstance().register(initialUserName, "tal961", "kadosh");
        UserController.getInstance().register(initialUserName, "jacob961", "sneaky");
        String tal = UserController.getInstance().login(initialUserName, "tal961", "kadosh").getResult();
        String jacob = UserController.getInstance().login(guestName2, "jacob961", "sneaky").getResult();

        int storeID = UserController.getInstance().openStore(tal, "Apple").getResult();
        int productID = 65482;
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(tal, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(tal, paymentDetails, supplyDetails);

        response = UserController.getInstance().getPurchaseDetails(jacob, storeID);
        Assert.assertTrue(response.isFailure());
        // TODO check if we should return null on failure
    }

    @Test
    public void getUserPurchaseHistoryTestSuccess() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<List<PurchaseClientDTO>> response;

        String initialUserName = UserController.getInstance().addGuest().getResult();
        String guestName2 = UserController.getInstance().addGuest().getResult();
        UserController.getInstance().register(initialUserName, "tal4", "kadosh");
        String tal = UserController.getInstance().login(initialUserName, "tal4", "kadosh").getResult();
        String admin = UserController.getInstance().login(guestName2, "a1", "a1").getResult();

        int storeID = UserController.getInstance().openStore(tal, "Apple").getResult();
        int productID = 65482;
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(tal, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(tal, paymentDetails, supplyDetails);

        response = UserController.getInstance().getUserPurchaseHistory(admin, tal);
        Assert.assertFalse(response.isFailure());
        Assert.assertEquals(1, response.getResult().size());

        PurchaseClientDTO purchase = response.getResult().get(0);

        Assert.assertEquals(22500, (int)purchase.getTotalPrice());
        Assert.assertEquals(LocalDate.now().toString(), purchase.getPurchaseDate());
        Assert.assertEquals(storeID, purchase.getBasket().getStoreID());
    }

    @Test
    public void getUserPurchaseHistoryTestFailureNoPermissions() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<List<PurchaseClientDTO>> response;

        String initialUserName = UserController.getInstance().addGuest().getResult();
        String guestName2 = UserController.getInstance().addGuest().getResult();
        UserController.getInstance().register(initialUserName, "tal961", "kadosh");
        UserController.getInstance().register(initialUserName, "jacob961", "sneaky");
        String tal = UserController.getInstance().login(initialUserName, "tal961", "kadosh").getResult();
        String jacob = UserController.getInstance().login(guestName2, "jacob961", "sneaky").getResult();

        int storeID = UserController.getInstance().openStore(tal, "Apple").getResult();
        int productID = 65482;
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(tal, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(tal, paymentDetails, supplyDetails);

        response = UserController.getInstance().getUserPurchaseHistory(jacob, tal);
        Assert.assertTrue(response.isFailure());
        // TODO check if we should return null on failure
    }

    @Test
    public void getStorePurchaseHistoryTestSuccess() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<Collection<PurchaseClientDTO>> response;

        String initialUserName = UserController.getInstance().addGuest().getResult();
        String guestName2 = UserController.getInstance().addGuest().getResult();
        UserController.getInstance().register(initialUserName, "tal4", "kadosh");
        String tal = UserController.getInstance().login(initialUserName, "tal4", "kadosh").getResult();
        String admin = UserController.getInstance().login(guestName2, "u1", "u1").getResult();

        int storeID = UserController.getInstance().openStore(tal, "Apple").getResult();
        int productID = 65482;
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(tal, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(tal, paymentDetails, supplyDetails);

        response = UserController.getInstance().getStorePurchaseHistory(admin, storeID);
        Assert.assertFalse(response.isFailure());
        Assert.assertEquals(1, response.getResult().size());

        PurchaseClientDTO purchase = response.getResult().iterator().next();

        Assert.assertEquals(22500, (int)purchase.getTotalPrice());
        Assert.assertEquals(LocalDate.now().toString(), purchase.getPurchaseDate());
        Assert.assertEquals(storeID, purchase.getBasket().getStoreID());
    }

    @Test
    public void getStorePurchaseHistoryTestFailureNoPermissions() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<Collection<PurchaseClientDTO>> response;

        String initialUserName = UserController.getInstance().addGuest().getResult();
        String guestName2 = UserController.getInstance().addGuest().getResult();
        UserController.getInstance().register(initialUserName, "tal961", "kadosh");
        UserController.getInstance().register(initialUserName, "jacob961", "sneaky");
        String tal = UserController.getInstance().login(initialUserName, "tal961", "kadosh").getResult();
        String jacob = UserController.getInstance().login(guestName2, "jacob961", "sneaky").getResult();

        int storeID = UserController.getInstance().openStore(tal, "Apple").getResult();
        int productID = 65482;
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(tal, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(tal, paymentDetails, supplyDetails);

        response = UserController.getInstance().getStorePurchaseHistory(jacob, storeID);
        Assert.assertTrue(response.isFailure());
        // TODO check if we should return null on failure
    }
}
