import json
import uuid
import time
import gevent
# from locust.events import request_success
import locust
from locust.exception import StopUser

from websocket import create_connection
import six

from locust import TaskSet, task, HttpUser

import string
import random  # define the random module

S = 10  # number of characters in the string.


class UserTaskSet(TaskSet):

    def on_start(self):
        self.ws = create_connection('ws://127.0.0.1:8080/ws')

        self.ws.send('{"action": "startup"}')
        ans = self.ws.recv()

        guest_name = json.loads(json.loads(ans)["message"])["result"]
        ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k=S))
        self.username = str(ran)

        to_send = {"action": "register", "identifier": guest_name, "username": self.username, "pwd": "123"}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans = self.ws.recv()

        locust.events.request_success.fire(
            request_type='register',
            name='test/ws/register',
            response_time=int((time.time() - start_time) * 1000),
            response_length=len(ans))

        to_send = {"action": "login", "identifier": guest_name, "username": self.username, "pwd": "123"}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)
        self.ws.recv()

        locust.events.request_success.fire(
            request_type='login',
            name='test/ws/login',
            response_time=int((time.time() - start_time) * 1000),
            response_length=len(ans))

        to_send = {"action": "openStore", "username": self.username, "storeName": "Hanut Botnim"}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans = self.ws.recv()
        store_id = json.loads(json.loads(ans)["message"])["result"]

        # ADD TO STORE -------------------------------------------------------------------------------------------------

        to_send = {"action": "addProductsToStore", "username": self.username,
                   "productDTO": json.dumps({"name": "Botnim", "storeID": store_id,
                                             "price": 8, "categories": ["snack"],
                                             "keywords": ["peanuts"]}), "amount": 1000000}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans = self.ws.recv()
        end_time = time.time()
        locust.events.request_success.fire(
            request_type='addProductsToStore',
            name='test/ws/add_products',
            response_time=int((end_time - start_time) * 1000),
            response_length=len(ans))

    def on_quit(self):
        self.ws.close()

    @task
    def purchase_history_task(self):
        for i in range(1000000):
            print(i)
            # add to cart
            to_send = {"action": "searchByProductName", "productName": "Botnim"}
            msg = json.dumps(to_send)
            self.ws.send(msg)
            ans_1 = self.ws.recv()
            print(ans_1)
            products = json.loads(json.loads(ans_1)["message"])["result"]
            if len(products) > 0:
                first_product = products[0]
                store_id = first_product["storeID"]
                product_id = first_product["productID"]

                to_send = {"action": "addToCart", "username": self.username, "storeID": store_id,
                           "productID": product_id}
                msg = json.dumps(to_send)

                start_time = time.time()
                self.ws.send(msg)

                ans_add_to_cart = self.ws.recv()
                end_time = time.time()
                locust.events.request_success.fire(
                    request_type='addToCart',
                    name='test/ws/add_to_cart',
                    response_time=int((end_time - start_time) * 1000),
                    response_length=len(ans_add_to_cart))
            else:
                start_time = time.time()
                ans_add_to_cart = self.ws.recv()
                end_time = time.time()
                locust.events.request_success.fire(
                    request_type='addToCart',
                    name='test/ws/add_to_cart',
                    response_time=int((end_time - start_time) * 1000),
                    response_length=len(ans_add_to_cart))

            # commit purchase

            to_send = {"action": "directPurchase", "username": self.username,
                       "paymentDetails": json.dumps(
                           {"card_number": "a", "month": "a", "year": "a", "holder": "a", "ccv": 111, "id": "a"}),
                       "supplyDetails": json.dumps(
                           {"name": "a", "address": "b", "city": "c", "country": "d", "zip": "e"})}
            msg = json.dumps(to_send)
            start_time = time.time()
            self.ws.send(msg)

            ans_purchase = self.ws.recv()
            end_time = time.time()
            locust.events.request_success.fire(
                request_type='directPurchase',
                name='test/ws/purchase',
                response_time=int((end_time - start_time) * 1000),
                response_length=len(ans_purchase))

        to_send = {"action": "getPurchaseHistory", "username": self.username}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans_get_history = self.ws.recv()
        purchase_history_list = json.loads(json.loads(ans_get_history)["message"])["result"]
        assert len(purchase_history_list) >= 9
        end_time = time.time()
        locust.events.request_success.fire(
            request_type='getPurchaseHistory',
            name='test/ws/show_purchase_history',
            response_time=int((end_time - start_time) * 1000),
            response_length=len(ans_get_history))
        raise StopUser()


class UserLocust(HttpUser):
    tasks = [UserTaskSet]
    min_wait = 1000000
    max_wait = 10000000
