import json
import uuid
import time
import gevent
# from locust.events import request_success
import locust

from websocket import create_connection
import six

from locust import TaskSet, task, HttpUser

import string
import random  # define the random module

S = 10  # number of characters in the string.


# call random.choices() string module to find the string in Uppercase + numeric data.


# from locust.events import request_success


# ws_client = ws = create_connection('ws://127.0.0.1:8080/ws')


class UserTaskSet(TaskSet):
    def on_start(self):
        self.store_count = 0
        self.registered_count = 0
        self.purchase_count = 0

        self.ws = create_connection('ws://127.0.0.1:8080/ws')

        self.ws.send('{"action": "startup"}')
        ans = self.ws.recv()

        guest_name = json.loads(json.loads(ans)["message"])["result"]
        self.username = "MainUser"

        # REGISTER -----------------------------------------------------------------------------------------------------

        to_send = {"action": "register", "identifier": guest_name, "username": self.username, "pwd": "123"}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)
        ans = self.ws.recv()

        # LOGIN --------------------------------------------------------------------------------------------------------

        to_send = {"action": "login", "identifier": guest_name, "username": self.username, "pwd": "123"}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)
        self.ws.recv()

        # OPEN STORE ---------------------------------------------------------------------------------------------------

        to_send = {"action": "openStore", "username": self.username, "storeName": "Hanut Botnim"}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans = self.ws.recv()
        store_id = json.loads(json.loads(ans)["message"])["result"]

        # ADD TO STORE -------------------------------------------------------------------------------------------------

        to_send = {"action": "addProductsToStore",
                   "username": self.username,
                   "productDTO": {"name": "Botnim",
                                  "storeID": store_id,
                                  "price": 100,
                                  "categories": ["Botnim"],
                                  "keywords": ["Botnim"]
                                  },
                   "amount": 10000000}

        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans = self.ws.recv()

    def on_quit(self):
        self.ws.close()

    @task
    def persistence(self):
        if self.registered_count < 10000:
            temp_connection = create_connection('ws://127.0.0.1:8080/ws')
            temp_connection.send('{"action": "startup"}')

            ans = temp_connection.recv()
            guest_name = json.loads(json.loads(ans)["message"])["result"]
            ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k=S))
            username = str(ran)

            # REGISTER -------------------------------------------------------------------------------------------------

            to_send = {"action": "register", "identifier": guest_name, "username": username, "pwd": "123"}
            msg = json.dumps(to_send)
            start_time = time.time()
            temp_connection.send(msg)

            ans = temp_connection.recv()
            locust.events.request_success.fire(
                request_type='register_persistence',
                name='test/ws/register_persistence',
                response_time=int((time.time() - start_time) * 1000),
                response_length=len(ans))

            # CHECKING STORE COUNT -------------------------------------------------------------------------------------

            if self.store_count < 1000:
                # LOGIN ------------------------------------------------------------------------------------------------

                to_send = {"action": "login", "identifier": guest_name, "username": username, "pwd": "123"}
                msg = json.dumps(to_send)
                start_time = time.time()
                temp_connection.send(msg)

                ans = temp_connection.recv()
                locust.events.request_success.fire(
                    request_type='login_persistence',
                    name='test/ws/login_persistence',
                    response_time=int((time.time() - start_time) * 1000),
                    response_length=len(ans))

                # OPEN STORE -------------------------------------------------------------------------------------------

                ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k=S))
                store_name = str(ran)

                to_send = {"action": "openStore", "username": username, "storeName": store_name}
                msg = json.dumps(to_send)
                start_time = time.time()
                temp_connection.send(msg)

                ans = temp_connection.recv()
                store_id = json.loads(json.loads(ans)["message"])["result"]
                locust.events.request_success.fire(
                    request_type='open_store_persistence',
                    name='test/ws/open_store_persistence',
                    response_time=int((time.time() - start_time) * 1000),
                    response_length=len(ans))

                self.store_count += 1
                product_count = 0

                while product_count < 1000:
                    # ADDING PRODUCT -----------------------------------------------------------------------------------
                    ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k=S))
                    product_name = str(ran)

                    to_send = {"action": "addProductsToStore",
                               "productDTO": {"name": product_name,
                                              "storeID": store_id,
                                              "price": 100,
                                              "categories": [product_name],
                                              "keywords": [product_name]
                                              },
                               "username": username}

                    msg = json.dumps(to_send)
                    start_time = time.time()
                    temp_connection.send(msg)

                    ans = temp_connection.recv()
                    locust.events.request_success.fire(
                        request_type='add_product_persistence',
                        name='test/ws/add_product_persistence',
                        response_time=int((time.time() - start_time) * 1000),
                        response_length=len(ans))

                    product_count += 1

                if self.purchase_count < 1000000:
                    self.purchase_count += 100
                    current_user_purchase_count = 0

                    while current_user_purchase_count < 100:

                        to_send = {"action": "searchByProductName", "productName": "Bamba Nugat"}
                        msg = json.dumps(to_send)
                        temp_connection.send(msg)
                        new_user_ans = temp_connection.recv()

                        products = json.loads(json.loads(new_user_ans)["message"])["result"]
                        if len(products) > 0:
                            first_product = products[0]
                            store_id = json.loads(first_product)["storeID"]
                            product_id = json.loads(first_product)["productID"]

                            to_send = {"action": "addToCart", "username": username,
                                       "storeID": store_id, "productID": product_id}
                            msg = json.dumps(to_send)

                            start_time = time.time()
                            temp_connection.send(msg)

                            new_user_ans = temp_connection.recv()
                            end_time = time.time()
                            locust.events.request_success.fire(
                                request_type='addToCart_persistence',
                                name='test/ws/addToCart_persistence',
                                response_time=int((end_time - start_time) * 1000),
                                response_length=len(new_user_ans))
                        else:
                            start_time = time.time()
                            end_time = time.time()
                            locust.events.request_success.fire(
                                request_type='addToCart_persistence',
                                name='test/ws/addToCart_persistence',
                                response_time=int((end_time - start_time) * 1000),
                                response_length=len(new_user_ans))

                        # commit purchase
                        to_send = {"action": "directPurchase", "username": username,
                                   "paymentDetails": json.dumps(
                                       {"card_number": "a", "month": "a", "year": "a", "holder": "a", "ccv": "a",
                                        "id": "a"}),
                                   "supplyDetails": json.dumps(
                                       {"name": "a", "address": "b", "city": "c", "country": "d", "zip": "e"})}
                        msg = json.dumps(to_send)
                        start_time = time.time()
                        temp_connection.send(msg)

                        new_user_ans = temp_connection.recv()
                        end_time = time.time()
                        locust.events.request_success.fire(
                            request_type='directPurchase_persistence',
                            name='test/ws/directPurchase_persistence',
                            response_time=int((end_time - start_time) * 1000),
                            response_length=len(new_user_ans))

                        current_user_purchase_count += 1

                    to_send = {"action": "getPurchaseHistory", "username": username}
                    msg = json.dumps(to_send)
                    start_time = time.time()
                    temp_connection.send(msg)

                    purchase_history = json.loads(json.loads(ans)["message"])["result"]

                    if len(purchase_history) == 100:
                        end_time = time.time()
                        locust.events.request_success.fire(
                            request_type='purchase_history_persistence',
                            name='test/ws/purchase_history_persistence',
                            response_time=int((end_time - start_time) * 1000),
                            response_length=len(ans))
                    else:
                        assert False

            temp_connection.close()
            self.registered_count += 1


class UserLocust(HttpUser):
    tasks = [UserTaskSet]
    min_wait = 0
    max_wait = 100
