#include "SocketListener.h"
#include "SocketClient.h"
#include <sys/epoll.h>
#include <sys/eventfd.h>
#include <cstdlib>
#include <zconf.h>
#include <pthread.h>

SocketListener::SocketListener() {
    if ((epoll_fd = epoll_create1(EPOLL_CLOEXEC)) == -1) {
        exit(1);
    }

    epoll_events = new epoll_event[EPOLL_BUFFER_SIZE];
    pthread_create(&thread, nullptr, SocketListener::process, this);
}

SocketListener::~SocketListener() {
    if (epoll_fd != 0) {
        close(epoll_fd);
        epoll_fd = 0;
    }

    this->interrupted = true;
}

void *SocketListener::process(void *listenerData) {
    SocketListener *instance = (SocketListener*) listenerData;

    while (!instance->interrupted) {
        instance->select();
    }

    return nullptr;
}

void SocketListener::select() {
    int eventsCount = epoll_wait(epoll_fd, epoll_events, EPOLL_BUFFER_SIZE, -1);

    for (int i = 0; i < eventsCount; i++) {
        auto client = (SocketClient *) epoll_events[i].data.ptr;
        auto events = epoll_events[i].events;

        client->handle_events(events);
    }
}
