; Declare external functions
declare i32 @socket(i32, i32, i32)
declare i32 @bind(i32, i8*, i32)
declare i32 @listen(i32, i32)
declare i32 @accept(i32, i8*, i32*)
declare i32 @close(i32)
declare i32 @recv(i32, i8*, i32, i32)
declare i32 @send(i32, i8*, i32, i32)
declare i32 @WSAStartup(i16, i8*)
declare i32 @printf(i8*, ...)

@str_init_winsock = private unnamed_addr constant [25 x i8] c"Initializing Winsock...\0A\00"
@str_socket_creation = private unnamed_addr constant [20 x i8] c"Creating socket...\0A\00"
@str_bind_socket = private unnamed_addr constant [19 x i8] c"Binding socket...\0A\00"
@str_listen_socket = private unnamed_addr constant [21 x i8] c"Listening socket...\0A\00"
@str_accept_conn = private unnamed_addr constant [29 x i8] c"Waiting for a connection...\0A\00"
@str_comm_with_client = private unnamed_addr constant [30 x i8] c"Communicating with client...\0A\00"
@str_success = private unnamed_addr constant [16 x i8] c"Server running\0A\00"
@str_error = private unnamed_addr constant [19 x i8] c"An error occurred\0A\00"
@str_received_message = private unnamed_addr constant [22 x i8] c"Received message: %s\0A\00"

; Define the main function
define i32 @main() {
entry:
    ; Initialize Winsock
    call i32 @printf(i8* @str_init_winsock)
    %ws_result = call i32 @WSAStartup(i16 u0x0202, i8* %wsa_data)
    %cmp_ws_result = icmp ne i32 %ws_result, 0
    br i1 %cmp_ws_result, label %error, label %setup_addr

setup_addr:
           ; Allocate memory for sockaddr_in structure
           %addr = alloca { i16, i16, i32, [8 x i8] }, align 4

           ; Set the address family (AF_INET)
           %addr_family = getelementptr inbounds { i16, i16, i32, [8 x i8] }, { i16, i16, i32, [8 x i8] }* %addr, i32 0, i32 0
           store i16 2, i16* %addr_family

           ; Set the port (8080), in network byte order
           %addr_port = getelementptr inbounds { i16, i16, i32, [8 x i8] }, { i16, i16, i32, [8 x i8] }* %addr, i32 0, i32 1
           store i16 36895, i16* %addr_port  ; 36895 = 0x901F (network byte order for 8080)

    ; Set the IP address (e.g., INADDR_ANY, which is 0.0.0.0)
    %addr_inaddr = getelementptr inbounds { i16, i16, i32, [8 x i8] }, { i16, i16, i32, [8 x i8] }* %addr, i32 0, i32 2
    store i32 0, i32* %addr_inaddr

    ; Zero out the rest of the structure (sin_zero)
    %addr_zero = getelementptr inbounds { i16, i16, i32, [8 x i8] }, { i16, i16, i32, [8 x i8] }* %addr, i32 0, i32 3
    call void @llvm.memset.p0i8.i32(i8* %addr_zero, i8 0, i32 8, i32 1, i1 false)

    ; Creating socket
    call i32 @printf(i8* @str_socket_creation)
    %sockfd = call i32 @socket(i32 2, i32 1, i32 6)
    %cmp = icmp slt i32 %sockfd, 0
    br i1 %cmp, label %error, label %bind_socket

bind_socket:
    ; Bind the socket
    call i32 @printf(i8* @str_bind_socket)
    %bind_result = call i32 @bind(i32 %sockfd, i8* %addr, i32 16)
    %cmp_bind = icmp slt i32 %bind_result, 0
    br i1 %cmp_bind, label %error, label %listen_socket

listen_socket:
    ; Listen for incoming connections
    call i32 @printf(i8* @str_listen_socket)
    %listen_result = call i32 @listen(i32 %sockfd, i32 10)
    %cmp_listen = icmp slt i32 %listen_result, 0
    br i1 %cmp_listen, label %error, label %accept_conn

accept_conn:
    ; Accept an incoming connection
    call i32 @printf(i8* @str_accept_conn)
    %clientfd = call i32 @accept(i32 %sockfd, i8* null, i32* null)
    %cmp_accept = icmp slt i32 %clientfd, 0
    br i1 %cmp_accept, label %error, label %communicate

communicate:
    ; Print message indicating communication with client
    call i32 @printf(i8* @str_comm_with_client)

    ; Allocate space for receiving data
    %recv_buffer = alloca [1024 x i8], align 4

    ; Receive data from the client
    %recv_len = call i32 @recv(i32 %clientfd, i8* %recv_buffer, i32 1024, i32 0)

    ; Print the received data
    %recv_msg = getelementptr inbounds [1024 x i8], [1024 x i8]* %recv_buffer, i32 0, i32 0
    call i32 @printf(i8* @str_received_message, i8* %recv_msg)

    ; Echo the received data back to the client
    call i32 @send(i32 %clientfd, i8* %recv_buffer, i32 %recv_len, i32 0)

    ; Close the client socket
    call i32 @close(i32 %clientfd)

    ; Close the server socket
    call i32 @close(i32 %sockfd)

    ; Indicate success
    call i32 @printf(i8* @str_success)
    ret i32 0

error:
    ; Handle error
    call i32 @printf(i8* @str_error)
    ret i32 1
}

; Declaration of llvm.memset intrinsic
declare void @llvm.memset.p0i8.i32(i8* nocapture, i8, i32, i32, i1)
