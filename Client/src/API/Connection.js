import StaticUserInfo from "./StaticUserInfo";
import {getMessage} from "@testing-library/jest-dom/dist/utils";

class Connection{
    static connection;
    static dataFromServer = [];
    static readyMessages = [];

    static setConnection(connection) {
        this.connection = connection;

        this.connection.onopen = () => {
            console.log("connected to the server");
            connection.send(JSON.stringify({
                action: "startup",
            }))
        }

        this.connection.onerror = (err) =>{
            console.log(err);
        }
        this.connection.onmessage = (message) => {
            let receivedData = JSON.parse(message.data);
            if(receivedData.type === "startup"){
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

    static catchResponse() {
        function sleep(ms){
            return new Promise(resolve => setTimeout(resolve, ms));
        }

        return new Promise(async (resolve, reject) => {
            let i = 0

            while(i < 5){
                if(Connection.dataFromServer.length !== 0){
                    return resolve(Connection.dataFromServer.shift());
                }
                await sleep(1000);
                i++;
            }
            reject("timeout");
        });
    }

    static async getResponse(){
        function successCallback(result){
            console.log(result);
            if(result.response.isFailure === true){
                alert(result.response.errMsg);
            }
            else{
                Connection.readyMessages.push(result);
            }
        }

        function failureCallback(error){
            alert(error);
        }

        await Connection.catchResponse().then(successCallback, failureCallback);

        console.log("hello there");

        if(Connection.readyMessages.length === 0){
            return Connection.readyMessages.shift();
        }
        else{
            return null;
        }
    }

    static sendRegister(username, password){
        console.log(StaticUserInfo.getUsername());
        this.connection.send(JSON.stringify({
            action: "register",
            identifier: StaticUserInfo.getUsername(),
            username: username,
            pwd: password,
        }));

        return Connection.getResponse();
    }

    static sendLogin(username, password){
        this.connection.send(JSON.stringify({
            action: "login",
            identifier: StaticUserInfo.getUsername(),
            username: username,
            pwd: password,
        }));

        return Connection.getResponse();
    }

    static sendSearchStoreByName(storeName){
        this.connection.send(JSON.stringify({
            action: "searchByStoreName",
            storeName: storeName,
        }));
    }

    static sendSearchProductByName(productName){
        this.connection.send(JSON.stringify({
            action: "searchByProductName",
            productName: productName,
        }));
    }

    static sendSearchProductByCategory(category){
        this.connection.send(JSON.stringify({
            action: "searchByProductCategory",
            category: category,
        }));
    }

    static sendSearchProductByKeyword(keyword){
        this.connection.send(JSON.stringify({
            action: "searchByProductKeyword",
            keyword: keyword,
        }));
    }

    static sendAddToCart(storeID, productID){
        this.connection.send(JSON.stringify({
            action: "addToCart",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
        }));
    }

    static sendRemoveFromCart(storeID, productID){
        this.connection.send(JSON.stringify({
            action: "removeFromCart",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
        }));
    }

    static sendGetCartDetails(){
        this.connection.send(JSON.stringify({
            action: "getCartDetails",
            username: StaticUserInfo.getUsername(),
        }));
    }

    static sendUpdateProductQuantity(storeID, productID, amount){
        this.connection.send(JSON.stringify({
            action: "updateProductQuantity",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
            amount: amount,
        }));
    }

    static sendDirectPurchase(backAccount, location){
        this.connection.send(JSON.stringify({
            action: "directPurchase",
            username: StaticUserInfo.getUsername(),
            backAccount: backAccount,
            location: location,
        }));
    }

    static sendLogout(){
        this.connection.send(JSON.stringify({
            action: "logout",
            username: StaticUserInfo.getUsername(),
        }));
    }

    static sendOpenStore(storeName){
        this.connection.send(JSON.stringify({
            action: "openStore",
            username: StaticUserInfo.getUsername(),
            storeName: storeName,
        }));
    }

    static sendAddProductReview(storeID, productID, review){
        this.connection.send(JSON.stringify({
            action: "addProductReview",
            username: StaticUserInfo.getUsername(),
            storeID: storeID,
            productID: productID,
            review: review,
        }));
    }

    static sendGetPurchaseHistory(){
        this.connection.send(JSON.stringify({
            action: "getPurchaseHistory",
            username: StaticUserInfo.getUsername(),
        }));
    }

    static sendAppointManager (functionName, appointerName, appointeeName, storeId){
        this.connection.send(JSON.stringify({
            action: functionName,
            appointerName: appointerName,
            appointeeName: appointeeName,
            storeId: storeId,
        }))
    }

    /*
    Holds for all 4 Appointments Pages
     */
    static sendAppoints (functionName, appointerName, appointeeName, storeId){
        this.connection.send(JSON.stringify({
            action: functionName,
            appointerName: appointerName,
            appointeeName: appointeeName,
            storeId: storeId,
        }))
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
        this.connection.send(JSON.stringify({
            action: functionName,
            permitting: permitting,
            storeId: storeId,
            permitted: permitted,
            permissions: permissions,
        }))
    }

    static sendDeleteProduct (functionName, username, storeId, productId, amount){
        this.connection.send(JSON.stringify({
            action: functionName,
            username: username,
            storeId: storeId,
            productId: productId,
            amount: amount,
        }))
    }

    static sendEditProduct (functionName, username, storeId, productId, newPrice, newName){
        this.connection.send(JSON.stringify({
            action: functionName,
            username: username,
            storeId: storeId,
            productId: productId,
            newPrice: newPrice,
            newName: newName,
        }))
    }

    static sendStorePurchaseHistory (functionName, adminName, storeId){
        this.connection.send(JSON.stringify({
            action: functionName,
            username: adminName,
            storeId: storeId,
        }))
    }

    /*
    * Holds for all 3 pages in Report Pages- Need to receive information too
    */
    static sendReportRequest (functionName, adminName, storeId){
        this.connection.send(JSON.stringify({
            action: functionName,
            username: adminName,
            storeId: storeId,
        }))
    }


}

export default Connection;