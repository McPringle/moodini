/*
 * Moodini
 * Copyright (C) 2016-2017 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.fihlon.moodini.server

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object PersistenceManager {

    val database: MongoDatabase

    init {
        val dbUsername = System.getenv("db_username")
        val dbPassword = System.getenv("db_password")
        val dbHost = System.getenv("db_host")
        val dbPort = System.getenv("db_port")
        val dbName = System.getenv("db_name")

        val dbURI = "mongodb://${dbUsername}:${dbPassword}@${dbHost}:${dbPort}/${dbName}"

        val client = KMongo.createClient(dbURI)

        database = client.getDatabase("moodini")
    }

    inline fun <reified T : Any> createMongoCollection(): MongoCollection<T> {
        return database.getCollection<T>()
    }

}
