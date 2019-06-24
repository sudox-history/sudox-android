#ifndef SSOCKETS_SOCKETMANAGER_H
#define SSOCKETS_SOCKETMANAGER_H

#include <sys/epoll.h>

#define EPOLL_BUFFER_SIZE 128

class SocketListener {
private:
    epoll_event *epoll_events;
    pthread_t thread;
    bool interrupted;

    static void *process(void *data);

    void select();

public:
    int epoll_fd;

    SocketListener();

    ~SocketListener();
};

#endif
