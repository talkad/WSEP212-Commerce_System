class StaticUserInfo{
    static username = '';
    static StoreId = '';

    static getUsername() { return this.username.toString() };
    static getStoreId() { return this.StoreId.toString() };


    static setUsername(username) { this.username = username };
    static setStoreId(storeId) { this.StoreId = storeId };
}

export default StaticUserInfo;