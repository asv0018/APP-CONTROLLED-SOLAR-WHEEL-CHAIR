import socket
import json

HOSTNAME = str(socket.gethostname())
IP_ADDRESS = str(socket.gethostbyname(HOSTNAME))
# DEFINE THE PORT NUMBER
PORT = 1337
print("THE IP ADDRESS OF THIS DESKTOP NAMED "+HOSTNAME+" IS "+IP_ADDRESS)
print("THE PORT NUMBER IS "+str(PORT))
while True:
    print("MANOHAR I AM STARTING A NEW SESSION")
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((IP_ADDRESS, PORT))
    print("WAITING FOR A CLIENT TO CONNECT")
    s.listen(1)
    conn, addr = s.accept()
    print("CLIENT CONNECTED")
    while 1:
        data = conn.recv(1024)
        print("RECEIVED DATA : ")
        data = data.decode("utf-8")
        print(data)
        print("\n")
        if not data:
            break
        
    conn.close()
    print("CLIENT DISCONNECTED")
    
print("EXITING THE SCRIPT")

    
