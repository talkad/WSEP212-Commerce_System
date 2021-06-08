import json
import uuid
import time
import gevent

from websocket import create_connection
import six

from locust import TaskSet, task
import socketio

# from locust.events import request_success


class LocustUser(TaskSet):
    def on_start(self):
        sio = socketio.Client()
        url = "ws://127.0.0.1:8080/ws"
        sio.connect(url, transports="polling")

        self.ws = sio
        self.user_id = sio.sid
        body = '{"session_request", {"session_id": "' + self.user_id + '"}}'

        self.ws.emit(body)

    @task(1)
    def say_hello(self):
        start_at = time.time()
        body = {"message": "Hello", "customData": {"language": "en"}, "session_id": self.user_id}
