package Server.Communication.MessageHandler;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountRules.*;
import Server.Domain.ShoppingManager.Predicates.*;
import Server.Domain.ShoppingManager.PurchaseRules.*;
import Server.Service.CommerceService;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class SystemManagerHandler extends  Handler{

    private CommerceService service;

    public SystemManagerHandler(Handler nextHandler){
        super(nextHandler);
        this.service = CommerceService.getInstance();
    }

    @Override
    public Response<?> handle(String input) {
        Response<?> response;
        Gson gson = new Gson();
        Properties data = gson.fromJson(input, Properties.class);

        String action = data.getProperty("action");

        switch (action) {
            case "getUserPurchaseHistory" -> {
                String adminName = data.getProperty("adminName");
                String username = data.getProperty("username");

                response = service.getUserPurchaseHistory(adminName, username);
            }
            case "getStorePurchaseHistory" ->{
                String adminName = data.getProperty("adminName");
                String storeID = data.getProperty("storeID");

                response = service.getStorePurchaseHistory(adminName, Integer.parseInt(storeID));
            }
            case "getTotalSystemRevenue" -> {
                String username = data.getProperty("username");

                response = service.getTotalSystemRevenue(username);
            }
            case "addDiscountRule" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String discountRuleStr = data.getProperty("discountRule");

                DiscountRule discountRule = parseDiscountRule(discountRuleStr);

                response = service.addDiscountRule(username, Integer.parseInt(storeID), discountRule);
            }
            case "addPurchaseRule" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String purchaseRuleStr = data.getProperty("purchaseRule");

                PurchaseRule purchaseRule = parsePurchaseRule(purchaseRuleStr);

                response = service.addPurchaseRule(username, Integer.parseInt(storeID), purchaseRule);
            }
            case "removeDiscountRule" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String discountID = data.getProperty("discountRuleID");

                response = service.removeDiscountRule(username, Integer.parseInt(storeID), Integer.parseInt(discountID));
            }
            case "removePurchaseRule" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String purchaseRuleID = data.getProperty("purchaseRuleID");

                response = service.removePurchaseRule(username, Integer.parseInt(storeID), Integer.parseInt(purchaseRuleID));
            }

            case "getDailyStatistics" -> {
                String adminName = data.getProperty("adminName");
                String date = data.getProperty("date");

                response = service.getDailyStatistics(adminName, LocalDate.parse(date));
            }
            case "isAdmin" -> {
                String username = data.getProperty("username");

                response = service.isAdmin(username);
            }
            default -> response = new Response<>(false, true, "INVALID INPUT: "+input);  // end of the chain of responsibility
        }

        return response;
    }

    private PurchaseRule parsePurchaseRule(String purchaseRuleStr) {
        Gson gson = new Gson();
        Properties data = gson.fromJson(purchaseRuleStr, Properties.class);
        String type = data.getProperty("type");

        PurchaseRule rule = null;

        switch (type){
            case "BasketPurchaseRule" -> {
                String basketPredicate = data.getProperty("basketPredicate");

                rule = new BasketPurchaseRule(parseBasketPredicate(basketPredicate));
            }
            case "CategoryPurchaseRule" -> {
                String categoryPredicate = data.getProperty("categoryPredicate");

                rule = new CategoryPurchaseRule(parseCategoryPredicate(categoryPredicate));
            }
            case "ProductPurchaseRule" -> {
                String productPredicate = data.getProperty("productPredicate");

                rule = new ProductPurchaseRule(parseProductPredicate(productPredicate));
            }
            case "AndCompositionPurchaseRule" -> {
                String policyRules = data.getProperty("policyRules");

                String[] ruleList = parseList(policyRules);
                List<PurchaseRule> rules = new LinkedList<>();

                for(String purchaseRule: ruleList) {
                    PurchaseRule x = parsePurchaseRule(purchaseRule);
                    rules.add(x);
                }
                rule = new AndCompositionPurchaseRule(rules);
            }

            case "OrCompositionPurchaseRule" -> {
                String policyRules = data.getProperty("policyRules");

                String[] ruleList = parseList(policyRules);
                List<PurchaseRule> rules = new LinkedList<>();

                for(String purchaseRule: ruleList)
                    rules.add(parsePurchaseRule(purchaseRule));
                rule = new OrCompositionPurchaseRule(rules);
            }

            case "ConditioningCompositionPurchaseRule" -> {
                String conditions = data.getProperty("predicates");
                String impliedConditions = data.getProperty("impliedPredicates");

                String[] condStrList = parseList(conditions);
                String[] impliedCondStrList = parseList(impliedConditions);
                String pred;
                List<Predicate> condList = new LinkedList<>();
                List<Predicate> impliedCondList = new LinkedList<>();

                for(String s : condStrList){
                    data = gson.fromJson(s, Properties.class);
                    if(s.contains("basketPredicate")){
                        pred = data.getProperty("basketPredicate");
                        condList.add(parseBasketPredicate(pred));
                    }
                    else if(s.contains("categoryPredicate")){
                        pred = data.getProperty("categoryPredicate");
                        condList.add(parseCategoryPredicate(pred));
                    }
                    else if(s.contains("productPredicate")){
                        pred = data.getProperty("productPredicate");
                        condList.add(parseProductPredicate(pred));
                    }
                    else
                        throw new IllegalArgumentException("Invalid predicate type provided to purchase rule");
                }

                for(String s : impliedCondStrList){
                    data = gson.fromJson(s, Properties.class);
                    if(s.contains("basketPredicate")){
                        pred = data.getProperty("basketPredicate");
                        impliedCondList.add(parseBasketPredicate(pred));
                    }
                    else if(s.contains("categoryPredicate")){
                        pred = data.getProperty("categoryPredicate");
                        impliedCondList.add(parseCategoryPredicate(pred));
                    }
                    else if(s.contains("productPredicate")){
                        pred = data.getProperty("productPredicate");
                        impliedCondList.add(parseProductPredicate(pred));
                    }
                    else
                        throw new IllegalArgumentException("Invalid predicate type provided to purchase rule");
                }

                rule = new ConditioningCompositionPurchaseRule(condList, impliedCondList);
            }
        }
        return rule;
    }

    private DiscountRule parseDiscountRule(String discountRuleStr) {
        Gson gson = new Gson();
        Properties data = gson.fromJson(discountRuleStr, Properties.class);
        String type = data.getProperty("type");

        DiscountRule rule = null;

        switch (type){
            case "CategoryDiscountRule" -> {
                String category = data.getProperty("category");
                String discount = data.getProperty("discount");
                rule = new CategoryDiscountRule(category, Double.parseDouble(discount));
            }
            case "StoreDiscountRule" -> {
                String discount = data.getProperty("discount");

                rule = new StoreDiscountRule(Double.parseDouble(discount));
            }
            case "ProductDiscountRule" -> {
                String productID = data.getProperty("productID");
                String discount = data.getProperty("discount");

                rule = new ProductDiscountRule(Integer.parseInt(productID), Double.parseDouble(discount));
            }
            case "ConditionalCategoryDiscountRule" -> {
                String category = data.getProperty("category");
                String discount = data.getProperty("discount");
                String categoryPredicate = data.getProperty("categoryPredicate");

                rule = new ConditionalCategoryDiscountRule(category, Double.parseDouble(discount), parseCategoryPredicate(categoryPredicate));
            }
            case "ConditionalStoreDiscountRule" -> {
                String discount = data.getProperty("discount");
                String storePredicate = data.getProperty("storePredicate");

                rule = new ConditionalStoreDiscountRule(Double.parseDouble(discount), parseStorePredicate(storePredicate));
            }
            case "ConditionalProductDiscountRule" -> {
                String productID = data.getProperty("productID");
                String discount = data.getProperty("discount");
                String productPredicate = data.getProperty("productPredicate");

                rule = new ConditionalProductDiscountRule(Integer.parseInt(productID), Double.parseDouble(discount), parseProductPredicate(productPredicate));
            }
            case "AndCompositionDiscountRule" -> {
                String category = data.getProperty("category");
                String discount = data.getProperty("discount");
                String policyRules = data.getProperty("policyRules");

                String[] ruleList = parseList(policyRules);
                List<DiscountRule> rules = new LinkedList<>();

                for(String discountRule: ruleList)
                    rules.add(parseDiscountRule(discountRule));

                rule = new AndCompositionDiscountRule(category, Double.parseDouble(discount), rules);
            }
            case "MaximumCompositionDiscountRule" -> {
                String policyRules = data.getProperty("policyRules");

                String[] ruleList = parseList(policyRules);
                List<DiscountRule> rules = new LinkedList<>();

                for(String discountRule: ruleList)
                    rules.add(parseDiscountRule(discountRule));

                rule = new MaximumCompositionDiscountRule(rules);
            }
            case "OrCompositionDiscountRule" -> {
                String discount = data.getProperty("discount");
                String policyRules = data.getProperty("policyRules");
                String category = data.getProperty("category");

                String[] ruleList = parseList(policyRules);
                List<DiscountRule> rules = new LinkedList<>();

                for(String discountRule: ruleList)
                    rules.add(parseDiscountRule(discountRule));

                rule = new OrCompositionDiscountRule(category, Double.parseDouble(discount), rules);
            }
            case "SumCompositionDiscountRule" -> {
                String policyRules = data.getProperty("policyRules");

                String[] ruleList = parseList(policyRules);
                List<DiscountRule> rules = new LinkedList<>();

                for(String discountRule: ruleList)
                    rules.add(parseDiscountRule(discountRule));

                rule = new MaximumCompositionDiscountRule(rules);
            }
            case "TermsCompositionDiscountRule" -> {
                String category = data.getProperty("category");
                String discount = data.getProperty("discount");
                String predicates = data.getProperty("predicates");
                String[] predStrList = parseList(predicates);
                String pred;
                List<Predicate> predList = new LinkedList<>();
                for(String s : predStrList){
                    data = gson.fromJson(s, Properties.class);
                    if(s.contains("storePredicate")){
                        pred = data.getProperty("storePredicate");
                        predList.add(parseStorePredicate(pred));
                    }
                    else if(s.contains("categoryPredicate")){
                        pred = data.getProperty("categoryPredicate");
                        predList.add(parseCategoryPredicate(pred));
                    }
                    else if(s.contains("productPredicate")){
                        pred = data.getProperty("productPredicate");
                        predList.add(parseProductPredicate(pred));
                    }
                    else
                        throw new IllegalArgumentException("Invalid predicate type provided to discount rule");
                }

                rule = new TermsCompositionDiscountRule(Double.parseDouble(discount), category,  predList);

            }
            case "XorCompositionDiscountRule" -> {
                String discount = data.getProperty("discount");
                String policyRules = data.getProperty("policyRules");
                String xorResolveType = data.getProperty("xorResolveType");

                String[] ruleList = parseList(policyRules);
                List<DiscountRule> rules = new LinkedList<>();

                for(String discountRule: ruleList)
                    rules.add(parseDiscountRule(discountRule));

                rule = new XorCompositionDiscountRule(Double.parseDouble(discount), rules, XorResolveType.valueOf(xorResolveType));
            }
        }

        return rule;
    }

    private CategoryPredicate parseCategoryPredicate(String categoryPredicate){
        Gson gson = new Gson();
        Properties data = gson.fromJson(categoryPredicate, Properties.class);
        String category = data.getProperty("category");
        String minUnits = data.getProperty("minUnits");
        String maxUnits = data.getProperty("maxUnits");

        return new CategoryPredicate(category, Integer.parseInt(minUnits), Integer.parseInt(maxUnits));
    }

    private StorePredicate parseStorePredicate(String storePredicate){
        Gson gson = new Gson();
        Properties data = gson.fromJson(storePredicate, Properties.class);
        String minUnits = data.getProperty("minUnits");
        String maxUnits = data.getProperty("maxUnits");
        String minPrice = data.getProperty("minPrice");

        return new StorePredicate(Integer.parseInt(minUnits), Integer.parseInt(maxUnits), Double.parseDouble(minPrice));
    }

    private ProductPredicate parseProductPredicate(String storePredicate){
        Gson gson = new Gson();
        Properties data = gson.fromJson(storePredicate, Properties.class);
        String minUnits = data.getProperty("minUnits");
        String maxUnits = data.getProperty("maxUnits");
        String productID = data.getProperty("productID");

        return new ProductPredicate(Integer.parseInt(productID), Integer.parseInt(minUnits), Integer.parseInt(maxUnits));
    }

    private BasketPredicate parseBasketPredicate(String basketPredicate){
        Gson gson = new Gson();
        Properties data = gson.fromJson(basketPredicate, Properties.class);
        String minUnits = data.getProperty("minUnits");
        String maxUnits = data.getProperty("maxUnits");
        String minPrice = data.getProperty("minPrice");

        return new BasketPredicate(Integer.parseInt(minUnits), Integer.parseInt(maxUnits), Double.parseDouble(minPrice));
    }

    private String[] parseList(String listStr){
        //String str = listStr.substring(1, listStr.length() - 1);

        return listStr.split(", ");
    }
}
