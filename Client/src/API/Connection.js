import StaticUserInfo from "./StaticUserInfo";
import {getMessage} from "@testing-library/jest-dom/dist/utils";
import ProductDTO from "../JsonClasses/ProductDTO";
import Cookies from 'js-cookie'

class Connection{
    static connection;
    static dataFromServer = [];

    static setConnection(connection) {
        this.connection = connection;

        this.connection.onopen = () => {
            console.log("connected to the server");
            if(window.sessionStorage.getItem('username') !== null){
                StaticUserInfo.setUsername(window.sessionStorage.getItem('username'));
            }
            console.log("saved cookie: " + window.sessionStorage.getItem('username'));
            if(StaticUserInfo.getUsername() === '') {
                console.log("doesn't have a cookie");
                connection.send(JSON.stringify({
                    action: "startup",
                }))
            }
        }

        this.connection.onerror = (err) =>{
            console.log(err);
        }
        this.connection.onmessage = (message) => {
            let receivedData = JSON.parse(message.data);

            if(receivedData.type === "startup"){
                window.sessionStorage.setItem('username', receivedData.response.result);
                console.log("new cookie: " + receivedData.response.result);
                StaticUserInfo.setUsername(receivedData.response.result);
            }
            else if(receivedData.type === "notification"){
                alert(receivedData.message);
            }
            else{
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

    static catchResponse() {

        function sleep(ms){
            return new Promise(resolve => setTimeout(resolve, ms));
        }

        return new Promise(async (resolve, reject) => {
            let i = 0

            while(i < 5){
                if(Connection.dataFromServer.length !== 0){
                    resolve(Connection.dataFromServer.shift());
                }
                await sleep(1000);
                i++;
            }
            reject("timeout");
        });
    }

    static handleReject(error){
        alert(error);
    }

    static async getResponse(){
        return await Connection.catchResponse();
    }

    static sendRegister(username, password){
        Connection.sendMessage(Connection.connection ,JSON.stringify({
            action: "register",
            identifier: StaticUserInfo.getUsername(),
            username: username,
            pwd: password,
        }));

        return Connection.getResponse();
    }

    static sendLogin(username, password){
       Connection.sendMessage(Connection.connection ,JSON.stringify({
            action: "login",
            identifier: StaticUserInfo.getUsername(),
            username: username,
            pwd: password,
        }));

        return Connection.getResponse();
    }

    static sendSearchStoreByName(storeName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByStoreName",
            storeName: storeName,
        }));

        return Connection.getResponse();
    }

    static sendSearchProductByName(productName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByProductName",
            productName: productName,
        }));

        return Connection.getResponse();
    }

    static sendSearchProductByCategory(category){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByProductCategory",
            category: category,
        }));

        return Connection.getResponse();
    }

    static sendSearchProductByKeyword(keyword){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "searchByProductKeyword",
            keyword: keyword,
        }));

        return Connection.getResponse();
    }

    static sendAddToCart(storeID, productID){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "addToCart",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
        }));

        return Connection.getResponse();
    }

    static sendRemoveFromCart(storeID, productID){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "removeFromCart",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
        }));

        return Connection.getResponse();
    }

    static sendGetCartDetails(){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "getCartDetails",
            username: StaticUserInfo.getUsername(),
        }));

        return Connection.getResponse();
    }

    static sendUpdateProductQuantity(storeID, productID, amount){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "updateProductQuantity",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
            amount: amount,
        }));

        return Connection.getResponse();
    }

    static sendDirectPurchase(paymentDetails, supplyDetails){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "directPurchase",
            username: window.sessionStorage.getItem('username'),
            paymentDetails: paymentDetails,
            supplyDetails: supplyDetails,
        }));

        return Connection.getResponse();
    }

    static sendLogout(){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "logout",
            username: StaticUserInfo.getUsername(),
        }));

        return Connection.getResponse();
    }

    static sendOpenStore(storeName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "openStore",
            username: StaticUserInfo.getUsername(),
            storeName: storeName,
        }));

        return Connection.getResponse();
    }

    static sendAddProductReview(storeID, productID, review){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "addProductReview",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
            review: review,
        }));

        return Connection.getResponse();
    }

    static sendGetPurchaseHistory(){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: "getPurchaseHistory",
            username: StaticUserInfo.getUsername(),
        }));

        return Connection.getResponse();
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

        return Connection.getResponse();
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

        return Connection.getResponse();
    }

    static sendAddProduct (functionName, username, name, storeId, price, categories, keywords, amount){
        var categoriesVar = categories.split(',')
        var keywordsVar = keywords.split(',')
        // var DTOtoSend = new ProductDTO(name, storeId, price, categoriesVar, keywordsVar);
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: username,
            productDTO: JSON.stringify({name: name,
                                               storeID: storeId,
                                                price: price,
                                                categories: categoriesVar,
                                                keywords: keywordsVar}),
            amount: amount,
        }))

        return Connection.getResponse();
    }

    static sendDeleteProduct (functionName, username, storeId, productId, amount){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: username,
            storeID: storeId,
            productID: productId,
            amount: amount,
        }))

        return Connection.getResponse();
    }


    static sendEditProduct (functionName, username, storeId, productId, newPrice, newName){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: username,
            storeID: storeId,
            productID: productId,
            newPrice: newPrice,
            newName: newName,
        }))

        return Connection.getResponse();
    }

    static sendStorePurchaseHistory (functionName, adminName, storeId){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: adminName,
            storeID: storeId,
        }))

        return Connection.getResponse();
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

        return Connection.getResponse();
    }

    static sendGetPermissionsRequest (functionName, username, storeId){
        Connection.sendMessage(Connection.connection, JSON.stringify({
            action: functionName,
            username: username,
            storeID: storeId,
        }))

        return Connection.getResponse();
    }

}

export default Connection;