package ru.sudox.api.system

import io.reactivex.Observable
import ru.sudox.api.system.entries.gettime.SystemGetTimeResponseDTO

class SystemService {

    /**
     * Запрашивает время на сервере.
     *
     * @return Observable на который прилетит ответ от сервера.
     */
    fun getTime(): Observable<SystemGetTimeResponseDTO> {
        return Observable.just(SystemGetTimeResponseDTO(System.currentTimeMillis()))
    }
}