package com.sudox.protocol.models

import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random

class JsonModelTest : Assert() {

    @Test
    fun testReadResponse_error() {
        val errorCode = Random.nextInt(1, Integer.MAX_VALUE)
        val jsonObject = JSONObject().apply { put("error", errorCode) }
        val jsonModel = Mockito.spy(JsonModel::class.java)

        // Testing
        jsonModel.readResponse(jsonObject)

        // Verifying
        assertEquals(errorCode, jsonModel.error)
        assertEquals(0, jsonModel.response)
    }

    @Test
    fun testReadResponse_success_response_code() {
        val responseCode = Random.nextInt(1, Integer.MAX_VALUE)
        val jsonObject = JSONObject().apply { put("response", responseCode) }
        val jsonModel = Mockito.spy(JsonModel::class.java)

        // Testing
        jsonModel.readResponse(jsonObject)

        // Verifying
        assertEquals(-1, jsonModel.error)
        assertEquals(responseCode, jsonModel.response)
    }

    @Test
    fun testReadResponse_success_response_json_object() {
        val jsonObject = JSONObject().apply { put("message", "Test") }
        val jsonModel = Mockito.spy(JsonModel::class.java)

        // Testing
        Mockito.doNothing().`when`(jsonModel).fromJSON(jsonObject)
        jsonModel.readResponse(jsonObject)
        Mockito.verify(jsonModel).fromJSON(jsonObject)
    }

    @Test
    fun testIsSuccess_success() {
        val random = Random.nextInt(1, Integer.MAX_VALUE)
        val jsonObject = JSONObject().apply { put("response", random) }
        val jsonModel = Mockito.spy(JsonModel::class.java)

        // Testing
        jsonModel.readResponse(jsonObject)

        // Verifying
        assertTrue(jsonModel.isSuccess())
    }

    @Test
    fun testIsSuccess_error() {
        val errorCode = Random.nextInt(1, Integer.MAX_VALUE)
        val jsonObject = JSONObject().apply { put("error", errorCode) }
        val jsonModel = Mockito.spy(JsonModel::class.java)

        // Testing
        jsonModel.readResponse(jsonObject)

        // Verifying
        assertFalse(jsonModel.isSuccess())
    }

    @Test
    fun testIsSuccess_error_and_success() {
        val jsonModel = Mockito.spy(JsonModel::class.java)
        val random = Random.nextInt(1, Integer.MAX_VALUE)
        val jsonObject = JSONObject().apply {
            put("error", random)
            put("response", random)
        }

        // Testing
        jsonModel.readResponse(jsonObject)

        // Verifying
        assertFalse(jsonModel.isSuccess())
    }
}