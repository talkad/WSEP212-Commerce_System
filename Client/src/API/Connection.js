import StaticUserInfo from "./StaticUserInfo";
import {getMessage} from "@testing-library/jest-dom/dist/utils";
import ProductDTO from "../JsonClasses/ProductDTO";
import Cookies from 'js-cookie'

class Connection{
    static connection;
    static dataFromServer = [];
    static offerNotifications = [];

    static setConnection(connection) {
        this.connection = connection;

        this.connection.onopen = () => {
            console.log("connected to the server");
            console.log("saved cookie: " + window.sessionStorage.getItem('username'));
            if(window.sessionStorage.getItem('username') === null) {
                console.log("doesn't have a cookie");
                connection.send(JSON.stringify({
                    action: "startup",
                }))
            }
            else{
                StaticUserInfo.setUsername(window.sessionStorage.getItem('username'));
                connection.send(JSON.stringify({
                    action: "reconnection",
                    username: window.sessionStorage.getItem('username'),
                }))
            }
        }

        this.connection.onerror = (err) =>{
            console.log(err);
        }

        this.connection.onmessage = (message) => {
            let receivedData = JSON.parse(message.data);
            console.log(receivedData);

            if(receivedData.type === "startup"){
                //console.log(receivedData.message().result);
                const inner_parse = JSON.parse(receivedData.message);
                window.sessionStorage.setItem('username', inner_parse.result);
                console.log("new cookie: " + inner_parse.result);
                StaticUserInfo.setUsername(inner_parse.result);
            }
            else if(receivedData.type === "notification"){
                alert(receivedData.message);
            }
            else if(receivedData.type === "reactiveNotification"){

                Connection.offerNotifications.push(receivedData);

            }
            else if(receivedData.type === "response"){
                Connection.dataFromServer.push(receivedData);
            }
        }
    }

    static waitForOpenConnection = (socket) => {
        return new Promise((resolve, reject) => {
            const maxNumberOfAttempts = 10
            const intervalTime = 200 //ms

            let currentAttempt = 0
            const interval = setInterval(() => {
                if (currentAttempt > maxNumberOfAttempts - 1) {
                    clearInterval(interval)
                    reject(new Error('Maximum number of attempts exceeded'))
                } else if (socket.readyState === socket.OPEN) {
                    clearInterval(interval)
                    resolve()
                }
                currentAttempt++
            }, intervalTime)
        })
    }

    static sendMessage = async (socket, msg) => {
        if (socket.readyState !== socket.OPEN) {
            try {
                await Connection.waitForOpenConnection(socket)
                socket.send(msg)
            } catch (err) { console.error(err) }
        } else {
            socket.send(msg)
        }
    }

    static catchOfferNotification() {

        function sleep(ms){
            return new Promise(resolve => setTimeout(resolve, ms));
        }

        return new Promise(async (resolve, reject) => {
            while(true){
                if(Connection.offerNotifications.length !== 0){

                    resolve(Connection.offerNotifications.shift());
                }
                await sleep(5000);
            }
        });
    }

    static async getOfferNotification(){
        return await Connection.catchOfferNotification();
    }


    static searchAndReturn(action){
        for(let i=0; i < Connection.dataFromServer.length; i++){
            if(Connection.dataFromServer[i] !== undefined && Connection.dataFromServer[i].action === action){
                return i;
            }
        }

        return -1;
    }

    static catchResponse(action) {

        function sleep(ms){
            return new Promise(resolve => setTimeout(resolve, ms));
        }

        return new Promise(async (resolve, reject) => {
            let i = 0

            while(i < 5){
                if(Connection.dataFromServer.length !== 0){
                    let index = this.searchAndReturn(action);

                    if(index !== -1){
                        let message = Connection.dataFromServer[index];
                        //delete Connection.dataFromServer[index];
                        resolve(JSON.parse(message.message));
                    }

                }
                await sleep(1000);
                i++;
            }
            reject("timeout");
        });
    }

    static handleReject(error){
        alert(error);
        window.history.back();
    }

    static async getResponse(action){
        return await Connection.catchResponse(action);
    }

    static sendRegister(username, password){
        Connection.sendMessage(Connection.connection ,JSON.stringify({
            action: "register",
            identifier: window.sessionStorage.getItem('username'),
            username: username,
            pwd: password,
        }));

        return Connection.getResponse("register");
    }

    static sendLogin(username, password){
       Connection.sendMessage(Connection.connection ,JSON.stringify({
            action: "login",
            identifier: window.sessionStorage.getItem('username'),
            username: username,
            pwd: password,
        }));

        return Connection.getResponse("login");
    }

    static sendSearchStoreByName(storeName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByStoreName",
            storeName: storeName,
        }));

        return Connection.getResponse("searchByStoreName");
    }

    static sendSearchProductByName(productName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByProductName",
            productName: productName,
        }));

        return Connection.getResponse("searchByProductName");
    }

    static sendSearchProductByCategory(category){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByProductCategory",
            category: category,
        }));

        return Connection.getResponse("searchByProductCategory");
    }

    static sendSearchProductByKeyword(keyword){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByProductKeyword",
            keyword: keyword,
        }));

        return Connection.getResponse("searchByProductKeyword");
    }

    static sendAddToCart(storeID, productID){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "addToCart",
            username: window.sessionStorage.getItem('username'),
            storeID: storeID,
            productID: productID,
        }));

        return Connection.getResponse("addToCart");
    }

    static sendOfferPrice(productID, storeID, priceOffer){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "bidOffer",
            username: window.sessionStorage.getItem('username'),
            productID: productID,
            storeID: storeID,
            priceOffer: priceOffer,
        }));

        return Connection.getResponse("bidOffer");
    }

    static sendManagerOfferReply(offeringUsername, productID, storeID, bidReply){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "bidManagerReply",
            username: window.sessionStorage.getItem('username'),
            offeringUsername: offeringUsername,
            productID: productID,
            storeID: storeID,
            bidReply: bidReply,
        }));

        return Connection.getResponse("bidManagerReply");
    }

    static sendBuyOffer(productID, storeID, paymentDetails, supplyDetails){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "bidUserReply",
            username: window.sessionStorage.getItem('username'),
            productID: productID,
            storeID: storeID,
            paymentDetails: JSON.stringify(paymentDetails),
            supplyDetails: JSON.stringify(supplyDetails),
        }));

        return Connection.getResponse("bidUserReply");
    }

    static sendRemoveFromCart(storeID, productID){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "removeFromCart",
            username: window.sessionStorage.getItem('username'),
            storeID: storeID,
            productID: productID,
        }));

        return Connection.getResponse("removeFromCart");
    }

    static sendGetCartDetails(){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "getCartDetails",
            username: window.sessionStorage.getItem('username'),
        }));

        return Connection.getResponse("getCartDetails");
    }

    static sendUpdateProductQuantity(storeID, productID, amount){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "updateProductQuantity",
            username: window.sessionStorage.getItem('username'),
            storeID: storeID,
            productID: productID,
            amount: amount,
        }));

        return Connection.getResponse("updateProductQuantity");
    }

    static sendDirectPurchase(paymentDetails, supplyDetails){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "directPurchase",
            username: window.sessionStorage.getItem('username'),
            paymentDetails: JSON.stringify(paymentDetails),
            supplyDetails: JSON.stringify(supplyDetails),
        }));

        return Connection.getResponse("directPurchase");
    }

    static sendLogout(){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "logout",
            username: window.sessionStorage.getItem('username'),
        }));

        return Connection.getResponse("logout");
    }

    static sendOpenStore(storeName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "openStore",
            username: window.sessionStorage.getItem('username'),
            storeName: storeName,
        }));

        return Connection.getResponse("openStore");
    }

    static sendStoreOwned(){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "getStoreOwned",
            username: window.sessionStorage.getItem('username'),
        }))

        return Connection.getResponse("getStoreOwned");
    }

    static sendAddProductReview(storeID, productID, review){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "addProductReview",
            username: window.sessionStorage.getItem('username'),
            storeID: storeID,
            productID: productID,
            review: review,
        }));

        return Connection.getResponse("addProductReview");
    }

    static sendGetPurchaseHistory(){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "getPurchaseHistory",
            username: window.sessionStorage.getItem('username'),
        }));

        return Connection.getResponse("getPurchaseHistory");
    }

    // static sendAppointManager (functionName, appointerName, appointeeName, storeId){
    //     this.connection.send(JSON.stringify({
    //         action: functionName,
    //         appointerName: appointerName,
    //         appointeeName: appointeeName,
    //         storeId: storeId,
    //     }))
    // }

    /*
    Holds for all 4 Appointments Pages
     */
    static sendAppoints (functionName, appointerName, appointeeName, storeId){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            appointerName: appointerName,
            appointeeName: appointeeName,
            storeID: storeId,
        }))

        return Connection.getResponse(functionName);
    }

    //ADD TO SERVICE FIRST!
    // static sendDiscount (){
    //     this.connection.send(JSON.stringify({
    //         action: functionName,
    //     }))
    // }

    /*
    Holds for 2 Permission Pages
     */
    static sendPermission (functionName, permitting, storeId, permitted, permissions){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            permitting: permitting,
            storeID: storeId,
            permitted: permitted,
            permission: permissions,
        }))

        return Connection.getResponse(functionName);
    }

    static sendAddProduct (functionName, username, name, storeId, price, categories, keywords, amount){
        var categoriesVar = categories.split(',')
        var keywordsVar = keywords.split(',')
        // var DTOtoSend = new ProductDTO(name, storeId, price, categoriesVar, keywordsVar);
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: window.sessionStorage.getItem('username'),
            productDTO: JSON.stringify({name: name,
                                               storeID: storeId,
                                                price: price,
                                                categories: categoriesVar,
                                                keywords: keywordsVar}),
            amount: amount,
        }))

        return Connection.getResponse(functionName);
    }

    static sendDeleteProduct (functionName, username, storeId, productId, amount){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: window.sessionStorage.getItem('username'),
            storeID: storeId,
            productID: productId,
            amount: amount,
        }))

        return Connection.getResponse(functionName);
    }


    static sendEditProduct (functionName, username, storeId, productId, newPrice, newName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: window.sessionStorage.getItem('username'),
            storeID: storeId,
            productID: productId,
            newPrice: newPrice,
            newName: newName,
        }))

        return Connection.getResponse(functionName);
    }

    static sendStorePurchaseHistory (functionName, adminName, storeId){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: adminName,
            storeID: storeId,
        }))

        return Connection.getResponse(functionName);
    }

    /*
    * Holds for all 3 pages in Report Pages- Need to receive information too
    */
    static sendReportRequest (functionName, adminName, storeId){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: adminName,
            storeID: storeId,
        }))

        return Connection.getResponse(functionName);
    }

    static sendGetPermissionsRequest (functionName, username, storeId){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: window.sessionStorage.getItem('username'),
            storeID: storeId,
        }))

        return Connection.getResponse(functionName);
    }

}

export default Connection;