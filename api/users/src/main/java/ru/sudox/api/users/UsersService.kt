package ru.sudox.api.users

import io.reactivex.Observable
import ru.sudox.api.users.entries.UserDTO
import ru.sudox.api.users.entries.get.UsersGetResponseDTO

class UsersService {

    fun get(ids: Array<Int>): Observable<UsersGetResponseDTO> {
        return Observable.just(UsersGetResponseDTO(arrayOf(UserDTO("1", "Maxim Mityushkin", "kotlinovsky"))))
    }
}