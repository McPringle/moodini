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

import com.mongodb.MongoClientOptions
import com.mongodb.MongoClientURI
import com.mongodb.WriteConcern
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object PersistenceManager {

    val database: MongoDatabase

    init {
        val db_username = System.getenv("db_username")
        val db_password = System.getenv("db_password")
        val db_host = System.getenv("db_host")
        val db_port = System.getenv("db_port")
        val db_name = System.getenv("db_name")

        val mongoClientOptions = MongoClientOptions.builder()
                .connectTimeout(10_000)
                .socketTimeout(10_000)
                .writeConcern(WriteConcern.W1)
        val db_uri = MongoClientURI("mongodb://${db_username}:${db_password}@${db_host}:${db_port}/${db_name}",
                mongoClientOptions)

        val client = KMongo.createClient(db_uri)

        database = client.getDatabase("moodini")
    }

    inline fun <reified T : Any> createMongoCollection(): MongoCollection<T> {
        return database.getCollection<T>()
    }

}
