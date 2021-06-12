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
        # ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k=S))
        self.username = guest_name

        # to_send = {"action": "register", "identifier": guest_name, "username": self.username, "pwd": "123"}
        # msg = json.dumps(to_send)
        # start_time = time.time()
        # self.ws.send(msg)
        #
        # ans = self.ws.recv()
        #
        # locust.events.request_success.fire(
        #     request_type='register',
        #     name='test/ws/register',
        #     response_time=int((time.time() - start_time) * 1000),
        #     response_length=len(ans))
        #
        # to_send = {"action": "login", "identifier": guest_name, "username": self.username, "pwd": "123"}
        # msg = json.dumps(to_send)
        # start_time = time.time()
        # self.ws.send(msg)
        # self.ws.recv()
        #
        # locust.events.request_success.fire(
        #     request_type='login',
        #     name='test/ws/login',
        #     response_time=int((time.time() - start_time) * 1000),
        #     response_length=len(ans))

    def on_quit(self):
        self.ws.close()

    @task(1)
    def be_on(self):
        to_send = {"action": "getCartDetails", "username": self.username}
        msg = json.dumps(to_send)
        start_time = time.time()
        self.ws.send(msg)

        ans = self.ws.recv()
        is_failure = json.loads(json.loads(ans)["message"])["result"]
        assert not is_failure
        end_time = time.time()
        locust.events.request_success.fire(
            request_type='getCartDetails',
            name='test/ws/show_cart',
            response_time=int((end_time - start_time) * 1000),
            response_length=len(ans))


class UserLocust(HttpUser):
    tasks = [UserTaskSet]
    min_wait = 0
    max_wait = 100
