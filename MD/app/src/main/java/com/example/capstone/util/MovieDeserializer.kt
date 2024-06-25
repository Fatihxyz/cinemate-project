package com.example.capstone.util

import com.example.capstone.model.remote.Movie
import com.google.gson.*
import java.lang.reflect.Type

class MovieDeserializer : JsonDeserializer<Movie> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Movie {
        val jsonObject = json.asJsonObject

        val movieId = try {
            jsonObject.get("movieid").asInt
        } catch (e: NumberFormatException) {
            -1
        }

        val title = try {
            jsonObject.get("title").asString
        } catch (e: Exception) {
            "Unknown Title"
        }

        val genres = try {
            jsonObject.get("genres").asString
        } catch (e: Exception) {
            "Unknown Genres"
        }

        return Movie(movieId, title, genres)
    }
}
