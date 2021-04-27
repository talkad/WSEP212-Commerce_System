import StaticUserInfo from "./StaticUserInfo";
import {getMessage} from "@testing-library/jest-dom/dist/utils";

class Connection{
    static connection;
    static dataFromServer;

    static setConnection(connection) {
        this.connection = connection

        this.connection.onopen = () => {
            console.log("connected to the server");
        }
        this.connection.onmessage = (message) => {
            let receivedData = JSON.parse(message);
            if(receivedData.type === "startup"){
                StaticUserInfo.setUsername(this.dataFromServer.identifier);
            }
            else if(receivedData.type === "notification"){
                alert(receivedData.message);
            }
            else{
                this.dataFromServer.push(receivedData)
            }
        }
    }

    static receiveMessage() {
        return new Promise((resolve, reject) => {
            setTimeout(function () {
                while(true){
                    if(!Connection.dataFromServer.isEmpty()){
                        return resolve(Connection.dataFromServer.shift());
                    }
                }
            }, 5000)
            reject("error");
        });
    }

    async getMessage(){
        return await Connection.receiveMessage();
    }

    static sendRegister(username, password){
        this.connection.send(JSON.stringify({
            action: "register",
            identifier: StaticUserInfo.getUsername(),
            username: username,
            pwd: password,
        }));

        const message = getMessage();
    }

    async static sendLogin(username, password){
        this.connection.send(JSON.stringify({
            action: "login",
            identifier: StaticUserInfo.getUsername(),
            username: username,
            pwd: password,
        }));

        return getMessage("login");
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
            username: username,
            storeId: storeId,
        }))
    }

    /*
    * Holds for all 3 pages in Report Pages- Need to receive information too
    */
    static sendReportRequest (functionName, adminName, storeId){
        this.connection.send(JSON.stringify({
            action: functionName,
            username: username,
            storeId: storeId,
        }))
    }


}

export default Connection;