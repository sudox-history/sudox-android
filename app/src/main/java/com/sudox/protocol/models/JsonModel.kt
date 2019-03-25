package com.sudox.protocol.models

import org.json.JSONObject

abstract class JsonModel {

    var error: Int = -1
    var response: Int = 0

    /**
     * Читает ответ от сервера, определяет его формат и если все правильно, вызывает
     * соответствующий метод для его парсинга.
     *
     * @param jsonObject - обьект ответа.
     */
    fun readResponse(jsonObject: JSONObject) {
        if (!jsonObject.has("error")) {
            val value = jsonObject.opt("response")

            // Parse ...
            if (value is Int) {
                response = value
            } else if (value == null) {
                fromJSON(jsonObject)
            }
        } else {
            error = jsonObject.optInt("error")
        }
    }

    /**
     * Преобразовавает обьект в формат JSON-обьекта и возвращает его.
     */
    open fun toJSON(): JSONObject? = null

    /**
     * Преобразовывает данные из JSON и записывает обьект.
     *
     * @param jsonObject - JSON-обьект для чтения.
     */
    open fun fromJSON(jsonObject: JSONObject) {}

    /**
     * Возвращает статус запроса.
     *
     * Если:
     * true - запрос выполнен успешно,
     * false - возникла ошибка.
     */
    fun isSuccess(): Boolean = error == -1 || response == 1
}