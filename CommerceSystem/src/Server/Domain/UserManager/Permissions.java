package Server.Domain.UserManager;

public enum Permissions {

    /** Guest **/
    REGISTER,                  /* Use Case: 2.3 */

    /** Registered User **/
    //LOGIN,                     /* Use Case: 2.4 */
    LOGOUT,                    /* Use Case: 3.1 */
    OPEN_STORE,                /* Use Case: 3.2 */
    REVIEW_PRODUCT,            /* Use Case: 3.3 */

    /** Store Owner **/
    //MANAGE_PRODUCTS,           /* Use Case: 4.1 - includes 4.1.1, 4.1.2, 4.1.3 */
    ADD_PRODUCT_TO_STORE,
    REMOVE_PRODUCT_FROM_STORE,
    UPDATE_PRODUCT_PRICE,
    MANAGE_DISCOUNTS,           /* Use Cases: 4.2.1, 4.2.2 */
    MANAGE_DISCOUNTS_POLICY,    /* Use Cases: 4.2.3, 4.2.4 */
    MANAGE_PURCHASE_POLICY,     /* Use Cases: 4.2.5, 4.2.6 */
    MANAGE_PURCHASE_TYPE,       /* Use Cases: 4.2.7, 4.2.8 */
    APPOINT_OWNER,              /* Use Case: 4.3 */
    REMOVE_OWNER_APPOINTMENT,   /* Use Case: 4.4 */
    APPOINT_MANAGER,            /* Use Case: 4.5 */
    EDIT_PERMISSION,             /* Use Case: 4.6 */
    REMOVE_MANAGER_APPOINTMENT, /* Use Case: 4.7 */
    RECEIVE_STORE_WORKER_INFO,         /* Use Case: 4.9 */
    RECEIVE_STORE_HISTORY,      /* Use Case: 4.11 */

    /** Store Manager **/
    /* NOTE: Store manager will have permissions 4.9 and 4.10 immediately after he is appointed.
     *  all other permissions are taken from store owner above, depends on which permissions
     *  are given him by his appointer  */

    /** System Manager **/
    RECEIVE_GENERAL_HISTORY    /* Use Case: 6.4 */
}
