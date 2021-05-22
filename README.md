# WSEP212---Commerce_System
Workshop for software engineering 3rd year project


###Initialization files info:

####Configuration file: configfile.json
- Provided in JSON format as a JSON object that contains all relevant information for system init.
  
    - Contains distant DataBase location & credentials to access it.
      > During the init process the system tries to connect to the DataBase at the provided location
      with the provided username and password, upon success continues to next step and upon failure
      terminates the system with the appropriate error as output.
    - Contains external payment system & supply system location.
      > During the init process the system tries to connect to the payment system and supply system at
      the provided location, upon success continues to next step and upon failure terminates the system
      with the appropriate error as output.
    - Contains credentials of admin user created upon system init.
      > During the init process the system tries to register the initial admin user using the provided
      username and password in the configuration file, upon success admin user is created and
      init process is complete and system is up and running and system terminates with appropriate
      error as output otherwise.
    
####State init file: initfile.txt
- Provided in txt format as commands separated by a ';' character, the following commands
are supported in the file:
  
> register(username, password) - Registers a user with the provided username and password.

> login(username, password) - Attempts to log in the system with the provided username and password.
 
> openStore(username, storename) - Creates a store owned the user with the provided username, and the store is named according to the storename provided.
 
> addProductsToStore(productName, storeID, price, categories, keywords, amount) - adds a product with the provided productName, price, categories, keywords and amount to a store with an ID of storeID.
   >* categories and keywords must be provided as a list of strings:
   >    * Each string must be within quotation marks (" ")
   >    * The strings must be separated with a comma and space (, )
   >    * The list of strings will be provided inside square parentheses ([ ])
   > 
   >   i.e : ["string", "string2", "string3"]
         
> appointStoreManager(username, storeID) - Appoints the user with the provided username as a manager in a store with an ID of storeID.
 
> addPermission(storeID, username, permissionType) - Adds the provided permission to user with the provided username at store with an ID of storeID.
 
> logout(username) - Logs out the user with the provided username out of the system.