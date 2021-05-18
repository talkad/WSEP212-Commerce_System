class StaticUserInfo{
    static username = '';
    static StoreId = '';
    static userStores = [];

    static getUsername() { return this.username };
    static getStoreId() { return this.StoreId };
    static getPermissions() { return this.StoreId };
    static getUserStores() { return this.userStores };

    static setUsername(username) { this.username = username };
    static setStoreId(storeId) { this.StoreId = storeId };
    static setPermissions(storeId) { this.StoreId = storeId };
    static setUserStores(userStores) { this.userStores = userStores };
}

export default StaticUserInfo;