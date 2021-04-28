class StaticUserInfo{
    static username = 'some user..';
    static StoreId = 'some store id..';

    static getUsername() { return this.username.toString() };
    static getStoreId() { return this.StoreId.toString() };


    static setUsername(username) { this.username = username };
    static setStoreId(storeId) { this.StoreId = storeId };
}

export default StaticUserInfo;