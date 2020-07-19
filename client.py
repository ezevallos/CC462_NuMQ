# !/usr/bin/env python

import socket
import select
import errno
import sys

HEADER_LENGTH = 10

# IP = "127.0.0.1" # CAMBIAR POR IP DE AWS
IP = "3.15.232.180"
# PORT = 1234 # CAMBIAR POR PUERTO DE AWS
PORT = 5555

my_queue = input("Queue: ")
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((IP, PORT))
client_socket.setblocking(False)

queue = my_queue.encode('utf-8')
queue_header = f"{len(queue):<{HEADER_LENGTH}}".encode('utf-8')
client_socket.send(queue_header + queue)

while True:
	message = input(f"{my_queue} > ")
	try:
		while True:
			queue_header = client_socket.recv(HEADER_LENGTH)
			if not len(queue):
				print("connection closed by the server")
				sys.exit()
			queue_length = int(queue_length.decode('utf-8').strip())
			queue = client_socket.recv(queue_length).decode('utf-8')

			message_header = client_socket.recv(HEADER_LENGTH)
			message_length = int(message_header.decode('utf-8').strip())
			message = client_socket.recv(message_length).decode('utf-8')

			print(f"{queue} > {message}")
	except IOError as e:
		if e.errno != eerno.EAGAIN and e.errno != errno.EWOULDBLOCK:
			print('Reading error: ', str(e))
			sys.exit()
		continue
	
	except Exception as e:
		print('General error: ', str(e))
		sys.exit()
