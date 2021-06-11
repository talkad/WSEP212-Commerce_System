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

    def on_quit(self):
        self.ws.close()

    @task(1)
    def purchase(self):
        to_send = {"action": "directPurchase", "username": self.username,
                   "paymentDetails": json.dumps(
                       {"card_number": "a", "month": "a", "year": "a", "holder": "a", "ccv": 984, "id": "a"}),
                   "supplyDetails": json.dumps({"name": "a", "address": "b", "city": "c", "country": "d", "zip": "e"})}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans = self.ws.recv()
        end_time = time.time()
        locust.events.request_success.fire(
            request_type='directPurchase',
            name='test/ws/purchase',
            response_time=int((end_time - start_time) * 1000),
            response_length=len(ans))


class UserLocust(HttpUser):
    tasks = [UserTaskSet]
    min_wait = 0
    max_wait = 100
