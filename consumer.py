# !/usr/bin/env python

import socket
import select
import errno
import sys

IP = "127.0.0.1" # CAMBIAR POR IP DE AWS
#IP = "3.15.232.180"
# PORT = 1234 # CAMBIAR POR PUERTO DE AWS
PORT = 5555

my_queue = input("Queue: ")
my_x = input("Exchange: ")
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((IP, PORT))
client_socket.setblocking(False)

queue = "(" + my_x + "," + my_queue + ")\n"
queue = queue.encode('utf-8')
client_socket.send(queue)

while True:
	# send n as ack
	n = input("Input any character for next message or q for quit: ")
	if (n == 'q'):
		client_socket.close()
		break
	else:
		n = 'n\n'
		n = n.encode('utf-8')
		ack = client_socket.send(n)
	try:
		if not ack:
			print("connection closed by the server")
			sys.exit()

		# receive from queue
		message = client_socket.recv(1024).decode('utf-8')

		print(f"{my_queue} > {message}")
	except IOError as e:
		if e.errno != errno.EAGAIN and e.errno != errno.EWOULDBLOCK:
			print('Reading error: ', str(e))
			sys.exit()
		continue
	except Exception as e:
		print('General error: ', str(e))
		sys.exit()