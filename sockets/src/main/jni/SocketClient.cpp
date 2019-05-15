#include "SocketClient.h"
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <utility>
#include <netdb.h>
#include <endian.h>
#include <linux/in.h>
#include <fcntl.h>
#include <cerrno>
#include <iostream>
#include <optional>
#include <zconf.h>
#include <asm/ioctls.h>
#include <sys/ioctl.h>
#include <algorithm>

SocketClient::SocketClient(std::string host, uint16_t port) {
    this->host = std::move(host);
    this->port = port;
}

SocketClient::~SocketClient() = default;

void SocketClient::connect() {
    std::optional<sockaddr_in> socket_addr = get_address();

    if (!socket_addr || !init_socket()) {
        close(true);
        return;
    }

    if (::connect(socket_fd, (sockaddr *) &socket_addr, sizeof(socket_addr)) == -1 && errno != EINPROGRESS) {
        close(true);
    } else if (!link_with_listener()) {
        close(true);
    }
}

std::optional<sockaddr_in> SocketClient::get_address() {
    hostent *hostent = gethostbyname(host.c_str());

    if (hostent != nullptr) {
        sockaddr_in socket_addr{};
        socket_addr.sin_port = htons(port),
        socket_addr.sin_family = AF_INET,
        socket_addr.sin_addr.s_addr = static_cast<in_addr_t>(*(long *) hostent->h_addr_list[0]);

        return {socket_addr};
    }

    return std::nullopt;
}

bool SocketClient::init_socket() {
    if ((socket_fd = socket(AF_INET, SOCK_STREAM | SOCK_CLOEXEC, 0)) < 0) {
        return false;
    }

    int yes = 1;
    int no = 0;

    setsockopt(socket_fd, IPPROTO_TCP, TCP_NODELAY, &yes, sizeof(int));
    setsockopt(socket_fd, SOL_SOCKET, SO_KEEPALIVE, &no, sizeof(int));
    fcntl(socket_fd, F_SETFL, fcntl(socket_fd, F_GETFL) | O_NONBLOCK);

    return true;
}

bool SocketClient::link_with_listener() {
    event_mask.data.ptr = this;
    event_mask.events = EPOLLOUT | EPOLLIN | EPOLLRDHUP | EPOLLERR | EPOLLET;

    return epoll_ctl(listener.epoll_fd, EPOLL_CTL_ADD, socket_fd, &event_mask) == 0;
}

void SocketClient::close(bool error) {
    if (!opened()) {
        return;
    }

    epoll_ctl(listener.epoll_fd, EPOLL_CTL_DEL, socket_fd, NULL);
    ::close(socket_fd);
    connected = false;
    socket_fd = -1;

    send_mutex.lock();
    sending_queue.clear();
    send_mutex.unlock();
    callback->onDisconnected(error);
}

bool SocketClient::opened() {
    return socket_fd >= 0;
}

size_t SocketClient::available() {
    if (!opened()) {
        return static_cast<size_t>(-1);
    }

    int bytes_available;
    ioctl(socket_fd, FIONREAD, &bytes_available);

    return (size_t) std::max(bytes_available, -1);
}

size_t SocketClient::read(char *&buffer, size_t count) {
    if (!opened()) {
        return static_cast<size_t>(-1);
    }

    auto bytes_read = static_cast<size_t>(recv(socket_fd, buffer, count, 0));

    if (bytes_read > 0) {
        buffer[bytes_read] = '\0';
    }

    return bytes_read;
}

void SocketClient::send(char *buffer, size_t count) {
    if (!opened()) {
        return;
    }

    Data data{
            .buffer = buffer,
            .count = count
    };

    send_mutex.lock();
    sending_queue.push_back(data);
    send_mutex.unlock();
    adjust_write_flag();
}

void SocketClient::handle_events(uint32_t events) {
    if (events & EPOLLHUP || events & EPOLLRDHUP || events & EPOLLERR) {
        close(true);
        return;
    }

    if (events & EPOLLIN) {
        if (check_errors()) {
            close(true);
            return;
        }

        callback->onReceived();
    }

    if (events & EPOLLOUT) {
        if (check_errors()) {
            close(true);
            return;
        } else if (!connected) {
            callback->onConnected();
            connected = true;
        }

        send_mutex.lock();

        for (int i = 0; i < sending_queue.size(); ++i) {
            auto data = sending_queue.begin();
            auto buffer = data->buffer;
            auto count = data->count;

            ::send(socket_fd, buffer, count, 0);
            sending_queue.pop_back();
        }

        send_mutex.unlock();
    }
}

void SocketClient::adjust_write_flag() {
    event_mask.events = EPOLLIN | EPOLLRDHUP | EPOLLERR | EPOLLET;

    if (!sending_queue.empty()) {
        event_mask.events |= EPOLLOUT;
    }

    if (epoll_ctl(listener.epoll_fd, EPOLL_CTL_MOD, socket_fd, &event_mask) != 0) {
        close(true);
    }
}

bool SocketClient::check_errors() {
    socklen_t result_len = sizeof(int);
    int result;

    return getsockopt(socket_fd, SOL_SOCKET, SO_ERROR, &result, &result_len) < 0 || result != 0;
}

