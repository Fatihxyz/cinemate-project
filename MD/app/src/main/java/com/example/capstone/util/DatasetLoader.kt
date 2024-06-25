package com.example.capstone.util

import android.content.Context
import com.example.capstone.data.local.Movie
import com.opencsv.CSVReader
import java.io.InputStreamReader

object DatasetLoader {

    fun loadMovies(context: Context): List<Movie> {
        val movies = mutableListOf<Movie>()
        val inputStream = context.assets.open("movies.csv")
        val reader = CSVReader(InputStreamReader(inputStream))

        var nextLine = reader.readNext()
        while (nextLine != null) {
            if (nextLine[0] == "movieId") {
                nextLine = reader.readNext()
                continue
            }
            val movieId = nextLine[0].toInt()
            val title = nextLine[1]
            val genres = nextLine[2]
            movies.add(Movie(movieId, title, genres))
            nextLine = reader.readNext()
        }
        reader.close()
        return movies
    }

}
