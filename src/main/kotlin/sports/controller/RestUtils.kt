package sports.controller

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.net.JksOptions
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BasicAuthHandler
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import sports.PASSWORD_FOR_KEYSTORE
import sports.PATH_TO_KEYSTORE
import java.util.*
import kotlin.reflect.KClass

val GSON = Gson()
const val MISSING_PARAMETER = "Missing parameter"
private const val SERVER_LISTENING_AT = "Server listening at"


fun Vertx.restApiServer(port: Int, host: String, router: Router, sslEnable : Boolean) =
    createHttpServer(
        HttpServerOptions().setKeyStoreOptions(
            JksOptions()
                .setPassword(PASSWORD_FOR_KEYSTORE)
                .setPath(PATH_TO_KEYSTORE)
        ).setSsl(sslEnable)
    ).requestHandler(router)
        .listen(port, host) {
            if (it.succeeded()) println("$SERVER_LISTENING_AT $port")
            else println(it.cause())
        }

fun Router.setUpAuthentication(vertx: Vertx, authProvider: AuthProvider): Router {
    post().remove().handler(CookieHandler.create()).handler(
        SessionHandler.create(LocalSessionStore.create(vertx)).setAuthProvider(authProvider)
    ).handler(BasicAuthHandler.create(authProvider))
    return this
}

fun Route.awaitBody() = handler(BodyHandler.create())

fun <T> RoutingContext.bodyAs(clazz: KClass<out Any>): Optional<T> {
    return try {
        Optional.of(GSON.fromJson<T>(bodyAsString, clazz.java))
    } catch (e: JsonSyntaxException) {
        response().setStatusCode(HTTP_WRONG_REQUEST).end()
        Optional.empty()
    }
}

fun RoutingContext.param(paramName: String): Optional<String> {
    val param = request().getParam(paramName)
    if (param.isBlank()) {
        //This should never happen. Vertx only call a handler for a given paramenter if it's present
        response().setStatusCode(HTTP_WRONG_REQUEST).end("$MISSING_PARAMETER $paramName")
        return Optional.empty()
    }
    return Optional.of(param)
}

fun <T> RoutingContext.handleFuture(
    future: Future<T>,
    successAction: RoutingContext.(result: T) -> Unit,
    failAction: RoutingContext.(cause: Throwable) -> Unit
) {
    future.setHandler {
        if (it.succeeded()) successAction(it.result())
        else failAction(it.cause())
    }
}
