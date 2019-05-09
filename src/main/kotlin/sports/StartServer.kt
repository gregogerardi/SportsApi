package sports

import com.mongodb.MongoClient
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.shiro.ShiroAuth
import io.vertx.ext.auth.shiro.ShiroAuthOptions
import io.vertx.ext.auth.shiro.ShiroAuthRealmType
import io.vertx.ext.web.Router
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import sports.controller.SportsController
import sports.controller.restApiServer
import sports.controller.setUpAuthentication
import sports.model.repository.SportsMongoDB
import sports.services.SportsService

//this constants's section should be in a .config file and use a reader to load every parameter. For this simple test api we leave them here

const val SSL_CONFIG = false
const val USERS_AND_PASSWORDS_PATH =
    "D:\\bibliotecas\\Documentos\\apiServer\\src\\main\\resources\\vertx-users.properties"
const val PROPERTIES_PATH = "properties_path"
const val DATABASE_NAME = "exampleDatabase-v1"
const val LOCAL_HOST = "localhost"
const val DATABASE_PORT = 27017
const val SERVER_PORT = 8080
const val PASSWORD_FOR_KEYSTORE = "key1234"
const val PATH_TO_KEYSTORE = "D:\\bibliotecas\\Documentos\\apiServer\\src\\main\\resources\\keystore.jks"

fun main() {
    val vertx = Vertx.vertx()
    vertx.restApiServer(
        SERVER_PORT, LOCAL_HOST, SportsController.initRouter(
            Router.router(vertx).setUpAuthentication(
                vertx, ShiroAuth.create(
                    vertx, ShiroAuthOptions().setType(ShiroAuthRealmType.PROPERTIES)
                        .setConfig(JsonObject().put(PROPERTIES_PATH, USERS_AND_PASSWORDS_PATH))
                )
            )
            , SportsService(
                SportsMongoDB(
                    KMongo.createClient(LOCAL_HOST, DATABASE_PORT).getDatabase(DATABASE_NAME).withCodecRegistry(
                        CodecRegistries.fromRegistries(
                            MongoClient.getDefaultCodecRegistry(),
                            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
                        )
                    ).getCollection()
                )
            )
        ), SSL_CONFIG
    )
}
